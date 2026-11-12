package com.logistpulse.command;

import com.logistpulse.chain.ReservationRequest;
import com.logistpulse.chain.ValidationHandler;
import com.logistpulse.chain.ValidationResult;
import com.logistpulse.inventory.Inventory;

public class ReserveCommand implements Command {
    private final Inventory inv;                // receptor
    private final ValidationHandler chain;      // cadena inyectada por el cliente
    private final ReservationRequest req;

    public ReserveCommand(Inventory inv, ValidationHandler chain, ReservationRequest req) {
        this.inv = inv;
        this.chain = chain;
        this.req = req;
    }

    @Override public String name() { return "Reserve"; }

    @Override
    public void execute() {
        ValidationResult v = chain.handle(req);
        if (!v.ok()) throw new IllegalStateException(v.reason());
        inv.reserve(req.lotId(), req.qty());
    }

    @Override public void undo() { inv.release(req.lotId(), req.qty()); }

    @Override
    public String describe() {
        return "sku=" + req.sku() + " lote=" + req.lotId() + " qty=" + req.qty();
    }
}
