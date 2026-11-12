package com.logistpulse.chain;

import com.logistpulse.inventory.Inventory;

public class StockHandler extends ValidationHandler {
    private final Inventory inv;

    public StockHandler(Inventory inv) { this.inv = inv; }

    @Override
    protected ValidationResult check(ReservationRequest r) {
        int avail = inv.totalAvailable(r.sku());
        return avail >= r.qty()
                ? ValidationResult.accepted()
                : ValidationResult.rejected("Stock insuficiente: disponible " + avail + ", pedido " + r.qty());
    }
}
