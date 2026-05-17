import java.io.*;
import java.util.ArrayList;

public class StorageService {
    private static final String HOLDINGS_FILE = "data/holdings.csv";
    private static final String SALES_FILE = "data/sales.csv";
    private static final String DIVIDENDS_FILE = "data/dividends.csv";

    public static void savePortfolio(Portfolio portfolio) {
        // Gem holdings
        try (PrintWriter pw = new PrintWriter(new FileWriter(HOLDINGS_FILE))) {
            for (Holding h : portfolio.getHoldings()) {
                pw.println(h.toCsv());
            }
        } catch (IOException e) {
            System.out.println("Fejl ved gemning af holdings: " + e.getMessage());
        }

        // Gem salg — format: holdingId;salgsdata
        try (PrintWriter pw = new PrintWriter(new FileWriter(SALES_FILE))) {
            for (Holding h : portfolio.getHoldings()) {
                for (Sale s : h.getSales()) {
                    pw.println(h.getId() + ";" + s.toCsv());
                }
            }
        } catch (IOException e) {
            System.out.println("Fejl ved gemning af salg: " + e.getMessage());
        }

        // Gem udbytte — format: holdingId;udbyttedata
        try (PrintWriter pw = new PrintWriter(new FileWriter(DIVIDENDS_FILE))) {
            for (Holding h : portfolio.getHoldings()) {
                for (Dividend d : h.getDividends()) {
                    pw.println(h.getId() + ";" + d.toCsv());
                }
            }
        } catch (IOException e) {
            System.out.println("Fejl ved gemning af udbytte: " + e.getMessage());
        }
    }

    public static Portfolio loadPortfolio() {
        Portfolio portfolio = new Portfolio();
        new File("data").mkdirs();

        // Indlæs holdings
        try (BufferedReader br = new BufferedReader(new FileReader(HOLDINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    portfolio.addHolding(Holding.fromCsv(line));
                }
            }
        } catch (FileNotFoundException e) {
            // Første gang appen køres — ingen fil endnu
        } catch (IOException e) {
            System.out.println("Fejl ved indlæsning af holdings: " + e.getMessage());
        }

        // Indlæs salg
        try (BufferedReader br = new BufferedReader(new FileReader(SALES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    String holdingId = line.substring(0, line.indexOf(";"));
                    String saleData = line.substring(line.indexOf(";") + 1);
                    Holding h = portfolio.findById(holdingId);
                    if (h != null) {
                        h.addSale(Sale.fromCsv(saleData));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // Ingen salg endnu
        } catch (IOException e) {
            System.out.println("Fejl ved indlæsning af salg: " + e.getMessage());
        }

        // Indlæs udbytte
        try (BufferedReader br = new BufferedReader(new FileReader(DIVIDENDS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    String holdingId = line.substring(0, line.indexOf(";"));
                    String dividendData = line.substring(line.indexOf(";") + 1);
                    Holding h = portfolio.findById(holdingId);
                    if (h != null) {
                        h.addDividend(Dividend.fromCsv(dividendData));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // Ingen udbytte endnu
        } catch (IOException e) {
            System.out.println("Fejl ved indlæsning af udbytte: " + e.getMessage());
        }

        return portfolio;
    }

    public static void deleteAll() {
        new File(HOLDINGS_FILE).delete();
        new File(SALES_FILE).delete();
        new File(DIVIDENDS_FILE).delete();
        System.out.println("Al data slettet.");
    }
}
