package com.logistpulse.chain;

import com.logistpulse.inventory.Inventory;
import com.logistpulse.model.Lot;
import com.logistpulse.model.LotState;

import java.time.LocalDate;
import java.util.Optional;

public class LotHandler extends ValidationHandler {
    private final Inventory inv;

    public LotHandler(Inventory inv) { this.inv = inv; }

    @Override
    protected ValidationResult check(ReservationRequest r) {
        Optional<Lot> opt = inv.findLot(r.lotId());
        if (opt.isEmpty())
            return ValidationResult.rejected("Lote inexistente: " + r.lotId());
        Lot l = opt.get();
        if (!l.sku.equals(r.sku()))
            return ValidationResult.rejected("El lote " + l.id + " no corresponde al SKU " + r.sku());
        if (l.state == LotState.CUARENTENA)
            return ValidationResult.rejected("Lote " + l.id + " en cuarentena");
        if (l.state == LotState.VENCIDO || l.expiry.isBefore(LocalDate.now()))
            return ValidationResult.rejected("Lote " + l.id + " vencido");
        if (l.available() < r.qty())
            return ValidationResult.rejected("Lote " + l.id + " sin unidades suficientes");
        return ValidationResult.accepted();
    }
}
