package com.logistpulse.model;

import java.time.LocalDate;

public class Lot {
    public final String id, sku, location;
    public final LocalDate expiry;
    public LotState state;
    public int onHand, reserved;

    public Lot(String id, String sku, String location, int onHand, LocalDate expiry, LotState state) {
        this.id = id;
        this.sku = sku;
        this.location = location;
        this.onHand = onHand;
        this.expiry = expiry;
        this.state = state;
    }

    public int available() { return onHand - reserved; }
}
