package com.logistpulse.strategy;

import com.logistpulse.model.Lot;

import java.util.List;

/** INTERFAZ ESTRATEGIA: define en qué orden se consumen los lotes. */
public interface PickingPolicy {
    String name();

    /** Devuelve los lotes candidatos ordenados por prioridad de picking (sin modificar la lista original). */
    List<Lot> sort(List<Lot> candidates);
}
