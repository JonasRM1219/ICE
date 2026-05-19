package com.stocktrack.services;

import com.stocktrack.models.*;

import java.io.*;

public class StorageService {
    private static final String DIR = "data/";
    private static final String HOLDINGS_FILE = DIR + "holdings.csv";
    private static final String SALES_FILE    = DIR + "sales.csv";
    private static final String DIVIDENDS_FILE = DIR + "dividends.csv";

    public static void savePortfolio(Portfolio portfolio) {
        new File(DIR).mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(HOLDINGS_FILE))) {
            for (Holding h : portfolio.getHoldings()) pw.println(h.toCsv());
        } catch (IOException e) { System.err.println("Fejl ved gem holdings: " + e.getMessage()); }

        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE))) {
            for (Holding h : portfolio.getHoldings())
                for (Sale s : h.getSales()) pw.println(h.getId() + ";" + s.toCsv());
        } catch (IOException e) { System.err.println("Fejl ved gem salg: " + e.getMessage()); }

        try (PrintWriter pw = new PrintWriter(new FileWriter(DIVIDENDS_FILE))) {
            for (Holding h : portfolio.getHoldings())
                for (Dividend d : h.getDividends()) pw.println(h.getId() + ";" + d.toCsv());
        } catch (IOException e) { System.err.println("Fejl ved gem udbytte: " + e.getMessage()); }
    }

    public static Portfolio loadPortfolio() {
        Portfolio portfolio = new Portfolio();
        new File(DIR).mkdirs();

        try (BufferedReader br = new BufferedReader(new FileReader(HOLDINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null)
                if (!line.isBlank()) portfolio.addHolding(Holding.fromCsv(line));
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) { System.err.println("Fejl ved indlæs holdings: " + e.getMessage()); }

        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                int sep = line.indexOf(";");
                Holding h = portfolio.findById(line.substring(0, sep));
                if (h != null) h.addSale(Sale.fromCsv(line.substring(sep + 1)));
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) { System.err.println("Fejl ved indlæs salg: " + e.getMessage()); }

        try (BufferedReader br = new BufferedReader(new FileReader(DIVIDENDS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                int sep = line.indexOf(";");
                Holding h = portfolio.findById(line.substring(0, sep));
                if (h != null) h.addDividend(Dividend.fromCsv(line.substring(sep + 1)));
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) { System.err.println("Fejl ved indlæs udbytte: " + e.getMessage()); }

        return portfolio;
    }

    public static void deleteAll() {
        new File(HOLDINGS_FILE).delete();
        new File(SALES_FILE).delete();
        new File(DIVIDENDS_FILE).delete();
    }
}
