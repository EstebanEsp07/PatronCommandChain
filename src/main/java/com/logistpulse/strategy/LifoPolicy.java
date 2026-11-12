package com.logistpulse.strategy;

import com.logistpulse.model.Lot;

import java.util.Comparator;
import java.util.List;

/** LIFO (Last In, First Out): sale primero el lote que ingresó más recientemente. */
public class LifoPolicy implements PickingPolicy {
    @Override public String name() { return "LIFO"; }

    @Override
    public List<Lot> sort(List<Lot> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparing((Lot l) -> l.receivedAt).reversed())
                .toList();
    }
}
