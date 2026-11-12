package com.logistpulse.chain;

/** Manejador base: interfaz común + enlace al siguiente eslabón. */
public abstract class ValidationHandler {
    private ValidationHandler next;

    /** Permite encadenar: a.setNext(b).setNext(c) */
    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    public final ValidationResult handle(ReservationRequest req) {
        ValidationResult r = check(req);
        if (!r.ok()) return r;                               // el eslabón corta la cadena
        return (next == null) ? ValidationResult.accepted() : next.handle(req);
    }

    protected abstract ValidationResult check(ReservationRequest req);
}
