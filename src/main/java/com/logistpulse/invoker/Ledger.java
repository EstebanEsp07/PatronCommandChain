package com.logistpulse.invoker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Registro de auditoría de decisiones. */
public class Ledger {
    private final List<String> entries = new ArrayList<>();

    public void log(String actor, String command, String status, String detail) {
        entries.add(String.format("%s | %-12s | %-9s | %-10s | %s",
                LocalDateTime.now().withNano(0), actor, command, status, detail));
    }

    public List<String> entries() { return List.copyOf(entries); }

    public void print() { entries.forEach(e -> System.out.println("   " + e)); }
}
