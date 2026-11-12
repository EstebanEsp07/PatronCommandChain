package com.logistpulse.strategy;

import com.logistpulse.inventory.Inventory;
import com.logistpulse.model.Lot;
import com.logistpulse.model.LotState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CONTEXTO: conoce el inventario y delega el orden de los lotes en la política activa,
 * sin saber si es FEFO, FIFO o LIFO.
 */
public class Picking {
    private final Inventory inv;
    private PickingPolicy policy;

    public Picking(Inventory inv, PickingPolicy policy) {
        this.inv = inv;
        this.policy = policy;
    }

    /** Cambia el algoritmo en tiempo de ejecución. */
    public void setPolicy(PickingPolicy policy) { this.policy = policy; }

    public PickingPolicy policy() { return policy; }

    /**
     * Propone de qué lotes tomar las unidades. No modifica el inventario: las reservas
     * se hacen después con ReserveCommand (que pasa por la cadena de validación).
     */
    public List<Allocation> plan(String sku, int qty) {
        List<Lot> candidates = inv.lotsOf(sku).stream()
                .filter(this::eligible)
                .toList();

        List<Allocation> plan = new ArrayList<>();
        int remaining = qty;
        for (Lot lot : policy.sort(candidates)) {
            if (remaining == 0) break;
            int take = Math.min(lot.available(), remaining);
            plan.add(new Allocation(lot.id, take));
            remaining -= take;
        }
        if (remaining > 0)
            throw new IllegalStateException("Stock insuficiente para " + sku
                    + ": faltan " + remaining + " unidades (política " + policy.name() + ")");
        return plan;
    }

    private boolean eligible(Lot l) {
        return l.state == LotState.DISPONIBLE
                && !l.expiry.isBefore(LocalDate.now())
                && l.available() > 0;
    }
}
