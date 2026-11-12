package com.logistpulse.invoker;

import com.logistpulse.command.Command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/** EMISOR (Invoker): solo dispara comandos; no conoce la lógica de inventario. */
public class CommandInvoker {
    private final Deque<Command> history = new ArrayDeque<>();
    private final Set<String> processedKeys = new HashSet<>();   // idempotencia
    private final Ledger ledger;

    public CommandInvoker(Ledger ledger) { this.ledger = ledger; }

    public boolean submit(String actor, String idempotencyKey, Command cmd) {
        if (!processedKeys.add(idempotencyKey)) {
            ledger.log(actor, cmd.name(), "DUPLICADO", "clave " + idempotencyKey + " ya procesada");
            return false;
        }
        try {
            cmd.execute();
            history.push(cmd);
            ledger.log(actor, cmd.name(), "OK", cmd.describe());
            return true;
        } catch (RuntimeException e) {
            processedKeys.remove(idempotencyKey);                 // permite reintento
            ledger.log(actor, cmd.name(), "RECHAZADO", e.getMessage());
            return false;
        }
    }

    public boolean undoLast(String actor) {
        if (history.isEmpty()) return false;
        Command cmd = history.pop();
        cmd.undo();
        ledger.log(actor, cmd.name(), "COMPENSADO", "inverso de: " + cmd.describe());
        return true;
    }
}
