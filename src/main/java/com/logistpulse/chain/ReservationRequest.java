package com.logistpulse.chain;

public record ReservationRequest(String sku, String lotId, int qty) {}
