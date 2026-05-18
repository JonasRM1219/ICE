package com.stocktrack.models;

import java.time.LocalDate;
import java.util.ArrayList;

public class Holding {
    private String id;
    private String ticker;
    private String name;
    private double shares;
    private double avgBuyPrice;
    private AccountType accountType;
    private LocalDate buyDate;
    private double currentPrice;
    private ArrayList<Sale> sales = new ArrayList<>();
    private ArrayList<Dividend> dividends = new ArrayList<>();

    public Holding(String id, String ticker, String name, double shares,
                   double avgBuyPrice, AccountType accountType, LocalDate buyDate) {
        this.id = id;
        this.ticker = ticker;
        this.name = name;
        this.shares = shares;
        this.avgBuyPrice = avgBuyPrice;
        this.accountType = accountType;
        this.buyDate = buyDate;
        this.currentPrice = avgBuyPrice;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────
    public String getId() { return id; }
    public String getTicker() { return ticker; }
    public String getName() { return name; }
    public double getShares() { return shares; }
    public double getAvgBuyPrice() { return avgBuyPrice; }
    public AccountType getAccountType() { return accountType; }
    public LocalDate getBuyDate() { return buyDate; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double p) { this.currentPrice = p; }
    public ArrayList<Sale> getSales() { return sales; }
    public ArrayList<Dividend> getDividends() { return dividends; }

    // ── Calculations ─────────────────────────────────────────────────────────
    public double getCostBasis() { return shares * avgBuyPrice; }
    public double getCurrentValue() { return shares * currentPrice; }
    public double getUnrealizedGain() { return getCurrentValue() - getCostBasis(); }
    public double getUnrealizedGainPct() {
        return getCostBasis() == 0 ? 0 : (getUnrealizedGain() / getCostBasis()) * 100;
    }

    public double estimatedTax() {
        double gain = getUnrealizedGain();
        if (gain <= 0) return 0;
        if (accountType == AccountType.FRIE_MIDLER) {
            return gain <= 61000 ? gain * 0.27 : 61000 * 0.27 + (gain - 61000) * 0.42;
        }
        return gain * accountType.getFlatRate();
    }

    public double getTotalDividends() {
        return dividends.stream().mapToDouble(Dividend::getAmount).sum();
    }

    public double getTotalSaleGain() {
        return sales.stream().mapToDouble(Sale::getGain).sum();
    }

    public double getTotalReturn() {
        return getUnrealizedGain() + getTotalSaleGain() + getTotalDividends();
    }

    public void addSale(Sale s) { sales.add(s); }
    public void addDividend(Dividend d) { dividends.add(d); }

    // ── CSV ──────────────────────────────────────────────────────────────────
    public String toCsv() {
        return id + ";" + ticker + ";" + name + ";" + shares + ";"
                + avgBuyPrice + ";" + accountType.name() + ";" + buyDate + ";" + currentPrice;
    }

    public static Holding fromCsv(String line) {
        String[] p = line.split(";");
        Holding h = new Holding(p[0], p[1], p[2],
                Double.parseDouble(p[3]), Double.parseDouble(p[4]),
                AccountType.valueOf(p[5]), LocalDate.parse(p[6]));
        h.setCurrentPrice(Double.parseDouble(p[7]));
        return h;
    }
}
