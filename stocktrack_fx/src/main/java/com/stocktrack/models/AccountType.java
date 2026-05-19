package com.stocktrack.models;

public enum AccountType {
    FRIE_MIDLER("Frie midler", 0),
    AKTIESPAREKONTO("Aktiesparekonto", 0.17),
    PENSION("Pension", 0.153);

    private final String label;
    private final double flatRate;

    AccountType(String label, double flatRate) {
        this.label = label;
        this.flatRate = flatRate;
    }

    public String getLabel() { return label; }
    public double getFlatRate() { return flatRate; }

    @Override
    public String toString() { return label; }
}
