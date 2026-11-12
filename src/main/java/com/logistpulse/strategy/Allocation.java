package com.logistpulse.strategy;

/** Parte de un pedido que se toma de un lote concreto. */
public record Allocation(String lotId, int qty) {}
