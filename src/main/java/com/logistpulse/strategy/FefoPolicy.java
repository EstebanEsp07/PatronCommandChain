package com.logistpulse.strategy;

import com.logistpulse.model.Lot;

import java.util.Comparator;
import java.util.List;

/** FEFO (First Expired, First Out): sale primero el lote que vence antes. */
public class FefoPolicy implements PickingPolicy {
    @Override public String name() { return "FEFO"; }

    @Override
    public List<Lot> sort(List<Lot> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparing((Lot l) -> l.expiry)
                        .thenComparing(l -> l.receivedAt))   // desempate: el más antiguo
                .toList();
    }
}
