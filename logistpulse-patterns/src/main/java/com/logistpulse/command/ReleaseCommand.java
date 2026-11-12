package com.logistpulse.command;

import com.logistpulse.inventory.Inventory;

public class ReleaseCommand implements Command {
    private final Inventory inv;
    private final String lotId;
    private final int qty;

    public ReleaseCommand(Inventory inv, String lotId, int qty) {
        this.inv = inv;
        this.lotId = lotId;
        this.qty = qty;
    }

    @Override public String name() { return "Release"; }
    @Override public void execute() { inv.release(lotId, qty); }
    @Override public void undo() { inv.reserve(lotId, qty); }
    @Override public String describe() { return "lote=" + lotId + " qty=" + qty; }
}
