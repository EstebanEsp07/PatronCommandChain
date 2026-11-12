package com.logistpulse.command;

import com.logistpulse.inventory.Inventory;

public class TransferCommand implements Command {
    private final Inventory inv;
    private final String from, to;
    private final int qty;

    public TransferCommand(Inventory inv, String from, String to, int qty) {
        this.inv = inv;
        this.from = from;
        this.to = to;
        this.qty = qty;
    }

    @Override public String name() { return "Transfer"; }
    @Override public void execute() { inv.transfer(from, to, qty); }
    @Override public void undo() { inv.transfer(to, from, qty); }
    @Override public String describe() { return from + " -> " + to + " qty=" + qty; }
}
