package com.logistpulse.chain;

import com.logistpulse.inventory.Inventory;
import com.logistpulse.model.Location;
import com.logistpulse.model.Lot;

public class CapacityHandler extends ValidationHandler {
    private final Inventory inv;

    public CapacityHandler(Inventory inv) { this.inv = inv; }

    @Override
    protected ValidationResult check(ReservationRequest r) {
        Lot l = inv.findLot(r.lotId()).orElseThrow();
        Location loc = inv.location(l.location);
        return inv.reservedAt(loc.name) + r.qty() <= loc.reservationCapacity
                ? ValidationResult.accepted()
                : ValidationResult.rejected("Capacidad de reserva excedida en " + loc.name);
    }
}
