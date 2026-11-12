package com.logistpulse.model;

public class Location {
    public final String name;
    public final int storageCapacity;      // unidades que caben almacenadas
    public final int reservationCapacity;  // unidades reservables a la vez

    public Location(String name, int storageCapacity, int reservationCapacity) {
        this.name = name;
        this.storageCapacity = storageCapacity;
        this.reservationCapacity = reservationCapacity;
    }
}
