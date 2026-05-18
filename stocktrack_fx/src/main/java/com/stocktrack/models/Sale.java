package com.stocktrack.models;

import java.time.LocalDate;

public class Sale {
    private String id;
    private double shares;
    private double salePrice;
    private double buyPrice;
    private LocalDate date;
    private AccountType accountType;

    public Sale(String id, double shares, double salePrice, double buyPrice,
                LocalDate date, AccountType accountType) {
        this.id = id;
        this.shares = shares;
        this.salePrice = salePrice;
        this.buyPrice = buyPrice;
        this.date = date;
        this.accountType = accountType;
    }

    public String getId() { return id; }
    public double getShares() { return shares; }
    public double getSalePrice() { return salePrice; }
    public double getBuyPrice() { return buyPrice; }
    public LocalDate getDate() { return date; }
    public AccountType getAccountType() { return accountType; }

    public double getGain() { return shares * (salePrice - buyPrice); }

    public double getTax() {
        double gain = getGain();
        if (gain <= 0) return 0;
        if (accountType == AccountType.FRIE_MIDLER) {
            return gain <= 61000 ? gain * 0.27 : 61000 * 0.27 + (gain - 61000) * 0.42;
        }
        return gain * accountType.getFlatRate();
    }

    public double getNetGain() { return getGain() - getTax(); }

    public String toCsv() {
        return id + ";" + shares + ";" + salePrice + ";" + buyPrice + ";"
                + date + ";" + accountType.name();
    }

    public static Sale fromCsv(String line) {
        String[] p = line.split(";");
        return new Sale(p[0], Double.parseDouble(p[1]), Double.parseDouble(p[2]),
                Double.parseDouble(p[3]), LocalDate.parse(p[4]),
                AccountType.valueOf(p[5]));
    }
}
