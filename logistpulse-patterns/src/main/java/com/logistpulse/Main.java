package com.logistpulse;

import com.logistpulse.chain.CapacityHandler;
import com.logistpulse.chain.LotHandler;
import com.logistpulse.chain.ReservationRequest;
import com.logistpulse.chain.SkuHandler;
import com.logistpulse.chain.StockHandler;
import com.logistpulse.chain.ValidationHandler;
import com.logistpulse.command.AdjustCommand;
import com.logistpulse.command.ReleaseCommand;
import com.logistpulse.command.ReserveCommand;
import com.logistpulse.command.TransferCommand;
import com.logistpulse.inventory.Inventory;
import com.logistpulse.invoker.CommandInvoker;
import com.logistpulse.invoker.Ledger;
import com.logistpulse.model.Location;
import com.logistpulse.model.Lot;
import com.logistpulse.model.LotState;

import java.time.LocalDate;

/** CLIENTE: arma receptor, cadena e invocador, y dispara los comandos. */
public class Main {
    public static void main(String[] args) {
        // 1) Receptor con datos de prueba
        Inventory inv = new Inventory();
        inv.addSku("LECHE-1L");
        inv.addSku("YOGUR-200");
        inv.addLocation(new Location("A1", 500, 100));
        inv.addLocation(new Location("B1", 150, 50));
        LocalDate hoy = LocalDate.now();
        inv.addLot(new Lot("L-001", "LECHE-1L", "A1", 80, hoy.plusDays(10), LotState.DISPONIBLE));
        inv.addLot(new Lot("L-002", "LECHE-1L", "A1", 40, hoy.minusDays(2), LotState.VENCIDO));
        inv.addLot(new Lot("L-003", "YOGUR-200", "A1", 60, hoy.plusDays(20), LotState.CUARENTENA));
        inv.addLot(new Lot("L-004", "LECHE-1L", "B1", 30, hoy.plusDays(15), LotState.DISPONIBLE));

        // 2) Cadena: SKU -> stock -> lote -> capacidad
        ValidationHandler chain = new SkuHandler(inv);
        chain.setNext(new StockHandler(inv))
             .setNext(new LotHandler(inv))
             .setNext(new CapacityHandler(inv));

        // 3) Invocador con ledger
        Ledger ledger = new Ledger();
        CommandInvoker invoker = new CommandInvoker(ledger);

        System.out.println("== Estado inicial ==");
        inv.print();

        // 4) Mismo canal para humanos y agentes de IA
        invoker.submit("operario-01", "req-1", new ReserveCommand(inv, chain, new ReservationRequest("LECHE-1L", "L-001", 20)));
        invoker.submit("agente-IA",   "req-2", new ReserveCommand(inv, chain, new ReservationRequest("LECHE-1L", "L-002", 5)));   // lote vencido
        invoker.submit("agente-IA",   "req-3", new ReserveCommand(inv, chain, new ReservationRequest("YOGUR-200", "L-003", 5)));   // sin stock disponible (cuarentena)
        invoker.submit("operario-01", "req-4", new ReserveCommand(inv, chain, new ReservationRequest("QUESO-X", "L-001", 1)));     // SKU inexistente
        invoker.submit("operario-01", "req-1", new ReserveCommand(inv, chain, new ReservationRequest("LECHE-1L", "L-001", 20)));   // duplicado
        invoker.submit("supervisor",  "req-5", new AdjustCommand(inv, "L-001", -5));
        invoker.submit("supervisor",  "req-6", new TransferCommand(inv, "L-001", "L-004", 30));
        invoker.submit("operario-01", "req-7", new ReleaseCommand(inv, "L-001", 10));
        invoker.undoLast("operario-01");   // compensa el Release

        System.out.println("\n== Estado final ==");
        inv.print();
        System.out.println("\n== Ledger ==");
        ledger.print();
    }
}
