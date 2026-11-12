package com.logistpulse.strategy;

import com.logistpulse.model.Lot;

import java.util.Comparator;
import java.util.List;

/** FIFO (First In, First Out): sale primero el lote que ingresó antes. */
public class FifoPolicy implements PickingPolicy {
    @Override public String name() { return "FIFO"; }

    @Override
    public List<Lot> sort(List<Lot> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparing((Lot l) -> l.receivedAt))
                .toList();
    }
}
