package com.logistpulse.command;

import com.logistpulse.inventory.Inventory;

public class AdjustCommand implements Command {
    private final Inventory inv;
    private final String lotId;
    private final int delta;

    public AdjustCommand(Inventory inv, String lotId, int delta) {
        this.inv = inv;
        this.lotId = lotId;
        this.delta = delta;
    }

    @Override public String name() { return "Adjust"; }
    @Override public void execute() { inv.adjust(lotId, delta); }
    @Override public void undo() { inv.adjust(lotId, -delta); }
    @Override public String describe() { return "lote=" + lotId + " delta=" + delta; }
}
