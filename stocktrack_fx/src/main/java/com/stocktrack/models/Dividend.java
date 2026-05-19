package com.stocktrack.models;

import java.time.LocalDate;

public class Dividend {
    private String id;
    private double amount;
    private LocalDate date;

    public Dividend(String id, double amount, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.date = date;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    public String toCsv() {
        return id + ";" + amount + ";" + date;
    }

    public static Dividend fromCsv(String line) {
        String[] p = line.split(";");
        return new Dividend(p[0], Double.parseDouble(p[1]), LocalDate.parse(p[2]));
    }
}
