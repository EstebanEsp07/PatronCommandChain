package com.logistpulse.model;

import java.time.LocalDate;

public class Lot {
    public final String id, sku, location;
    public final LocalDate expiry;
    public final LocalDate receivedAt;   // fecha de ingreso al almacén (FIFO / LIFO)
    public LotState state;
    public int onHand, reserved;

    public Lot(String id, String sku, String location, int onHand,
               LocalDate expiry, LotState state, LocalDate receivedAt) {
        this.id = id;
        this.sku = sku;
        this.location = location;
        this.onHand = onHand;
        this.expiry = expiry;
        this.state = state;
        this.receivedAt = receivedAt;
    }

    /** Constructor anterior: asume que el lote ingresó hoy. */
    public Lot(String id, String sku, String location, int onHand, LocalDate expiry, LotState state) {
        this(id, sku, location, onHand, expiry, state, LocalDate.now());
    }

    public int available() { return onHand - reserved; }
}
