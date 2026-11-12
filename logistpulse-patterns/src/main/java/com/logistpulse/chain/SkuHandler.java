package com.logistpulse.chain;

import com.logistpulse.inventory.Inventory;

public class SkuHandler extends ValidationHandler {
    private final Inventory inv;

    public SkuHandler(Inventory inv) { this.inv = inv; }

    @Override
    protected ValidationResult check(ReservationRequest r) {
        return inv.hasSku(r.sku())
                ? ValidationResult.accepted()
                : ValidationResult.rejected("SKU inexistente: " + r.sku());
    }
}
