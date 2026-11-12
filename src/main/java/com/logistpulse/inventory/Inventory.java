package com.logistpulse.inventory;

import com.logistpulse.model.Location;
import com.logistpulse.model.Lot;
import com.logistpulse.model.LotState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** RECEPTOR (Receiver): contiene la lógica de negocio real. */
public class Inventory {
    private final Set<String> catalog = new HashSet<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Lot> lots = new LinkedHashMap<>();

    public void addSku(String sku) { catalog.add(sku); }
    public void addLocation(Location l) { locations.put(l.name, l); }
    public void addLot(Lot l) { lots.put(l.id, l); }

    // --- consultas (las usa la cadena) ---
    public boolean hasSku(String sku) { return catalog.contains(sku); }
    public Optional<Lot> findLot(String id) { return Optional.ofNullable(lots.get(id)); }
    public Location location(String name) { return locations.get(name); }

    public int totalAvailable(String sku) {
        return lots.values().stream()
                .filter(l -> l.sku.equals(sku) && l.state == LotState.DISPONIBLE)
                .mapToInt(Lot::available).sum();
    }

    public int reservedAt(String location) {
        return lots.values().stream()
                .filter(l -> l.location.equals(location))
                .mapToInt(l -> l.reserved).sum();
    }

    public int storedAt(String location) {
        return lots.values().stream()
                .filter(l -> l.location.equals(location))
                .mapToInt(l -> l.onHand).sum();
    }

    // --- operaciones de negocio (las invocan los comandos) ---
    public void reserve(String lotId, int qty) {
        Lot l = require(lotId);
        if (qty <= 0 || l.available() < qty)
            throw new IllegalStateException("No hay disponibilidad suficiente en " + lotId);
        l.reserved += qty;
    }

    public void release(String lotId, int qty) {
        Lot l = require(lotId);
        if (qty <= 0 || l.reserved < qty)
            throw new IllegalStateException("No se puede liberar más de lo reservado en " + lotId);
        l.reserved -= qty;
    }

    public void adjust(String lotId, int delta) {
        Lot l = require(lotId);
        if (l.onHand + delta < l.reserved)
            throw new IllegalStateException("El ajuste deja el stock por debajo de lo reservado en " + lotId);
        l.onHand += delta;
    }

    public void transfer(String fromId, String toId, int qty) {
        Lot from = require(fromId), to = require(toId);
        if (!from.sku.equals(to.sku))
            throw new IllegalStateException("Los lotes pertenecen a SKUs distintos");
        if (qty <= 0 || from.available() < qty)
            throw new IllegalStateException("Stock disponible insuficiente en origen " + fromId);
        Location dest = locations.get(to.location);
        if (!from.location.equals(to.location) && storedAt(dest.name) + qty > dest.storageCapacity)
            throw new IllegalStateException("Capacidad de almacenamiento excedida en " + dest.name);
        from.onHand -= qty;
        to.onHand += qty;
    }

    private Lot require(String id) {
        Lot l = lots.get(id);
        if (l == null) throw new IllegalStateException("Lote inexistente: " + id);
        return l;
    }

    public void print() {
        lots.values().forEach(l -> System.out.printf(
                "   %-6s sku=%-8s loc=%-4s onHand=%-3d reservado=%-3d estado=%s%n",
                l.id, l.sku, l.location, l.onHand, l.reserved, l.state));
    }
}
