package com.logistpulse;

import com.logistpulse.chain.CapacityHandler;
import com.logistpulse.chain.LotHandler;
import com.logistpulse.chain.ReservationRequest;
import com.logistpulse.chain.SkuHandler;
import com.logistpulse.chain.StockHandler;
import com.logistpulse.chain.ValidationHandler;
import com.logistpulse.command.ReserveCommand;
import com.logistpulse.inventory.Inventory;
import com.logistpulse.invoker.CommandInvoker;
import com.logistpulse.invoker.Ledger;
import com.logistpulse.model.Location;
import com.logistpulse.model.Lot;
import com.logistpulse.model.LotState;
import com.logistpulse.strategy.Allocation;
import com.logistpulse.strategy.FefoPolicy;
import com.logistpulse.strategy.FifoPolicy;
import com.logistpulse.strategy.LifoPolicy;
import com.logistpulse.strategy.Picking;
import com.logistpulse.strategy.PickingPolicy;

import java.time.LocalDate;
import java.util.List;

/** CLIENTE: compara las tres políticas y luego reserva con Command + Chain. */
public class StrategyDemo {
    public static void main(String[] args) {
        Inventory inv = new Inventory();
        inv.addSku("LECHE-1L");
        inv.addLocation(new Location("A1", 1000, 200));
        LocalDate hoy = LocalDate.now();

        //                      id     sku         loc   qty  vence             estado                  ingresó
        inv.addLot(new Lot("L-A", "LECHE-1L", "A1", 40, hoy.plusDays(5),  LotState.DISPONIBLE, hoy.minusDays(30)));
        inv.addLot(new Lot("L-B", "LECHE-1L", "A1", 40, hoy.plusDays(40), LotState.DISPONIBLE, hoy.minusDays(10)));
        inv.addLot(new Lot("L-C", "LECHE-1L", "A1", 40, hoy.plusDays(20), LotState.DISPONIBLE, hoy.minusDays(2)));
        inv.addLot(new Lot("L-D", "LECHE-1L", "A1", 40, hoy.minusDays(1), LotState.VENCIDO,    hoy.minusDays(60))); // se excluye
        inv.addLot(new Lot("L-E", "LECHE-1L", "A1", 40, hoy.plusDays(30), LotState.CUARENTENA, hoy.minusDays(5)));  // se excluye

        Picking picking = new Picking(inv, new FefoPolicy());

        System.out.println("== Mismo pedido (70 uds), tres políticas ==");
        List<PickingPolicy> policies = List.of(new FefoPolicy(), new FifoPolicy(), new LifoPolicy());
        for (PickingPolicy p : policies) {
            picking.setPolicy(p);                          // cambio en tiempo de ejecución
            System.out.printf("   %-4s -> %s%n", p.name(), picking.plan("LECHE-1L", 70));
        }

        // Integración con Command + Chain: el plan FEFO se convierte en comandos Reserve
        ValidationHandler chain = new SkuHandler(inv);
        chain.setNext(new StockHandler(inv))
             .setNext(new LotHandler(inv))
             .setNext(new CapacityHandler(inv));
        Ledger ledger = new Ledger();
        CommandInvoker invoker = new CommandInvoker(ledger);

        picking.setPolicy(new FefoPolicy());
        List<Allocation> plan = picking.plan("LECHE-1L", 70);
        int i = 1;
        for (Allocation a : plan) {
            invoker.submit("agente-IA", "pick-" + i++,
                    new ReserveCommand(inv, chain, new ReservationRequest("LECHE-1L", a.lotId(), a.qty())));
        }

        System.out.println("\n== Estado tras reservar con FEFO ==");
        inv.print();
        System.out.println("\n== Ledger ==");
        ledger.print();
    }
}
