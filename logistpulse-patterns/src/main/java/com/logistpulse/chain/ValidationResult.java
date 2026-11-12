package com.logistpulse.chain;

public record ValidationResult(boolean ok, String reason) {
    public static ValidationResult accepted() { return new ValidationResult(true, "OK"); }
    public static ValidationResult rejected(String why) { return new ValidationResult(false, why); }
}
