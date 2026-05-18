package com.stocktrack.models;

import java.util.ArrayList;

public class Portfolio {
    private ArrayList<Holding> holdings = new ArrayList<>();

    public ArrayList<Holding> getHoldings() { return holdings; }

    public void addHolding(Holding h) { holdings.add(h); }

    public void removeHolding(String id) {
        holdings.removeIf(h -> h.getId().equals(id));
    }

    public Holding findById(String id) {
        return holdings.stream().filter(h -> h.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean isEmpty() { return holdings.isEmpty(); }

    public double getTotalValue() {
        return holdings.stream().mapToDouble(Holding::getCurrentValue).sum();
    }

    public double getTotalCost() {
        return holdings.stream().mapToDouble(Holding::getCostBasis).sum();
    }

    public double getTotalGain() { return getTotalValue() - getTotalCost(); }

    public double getTotalTax() {
        return holdings.stream().mapToDouble(Holding::estimatedTax).sum();
    }

    public double getTotalDividends() {
        return holdings.stream().mapToDouble(Holding::getTotalDividends).sum();
    }

    public double getTotalSaleGain() {
        return holdings.stream().mapToDouble(Holding::getTotalSaleGain).sum();
    }

    public double getTotalReturn() {
        return holdings.stream().mapToDouble(Holding::getTotalReturn).sum();
    }
}
