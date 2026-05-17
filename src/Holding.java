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
    private ArrayList<Sale> sales;
    private ArrayList<Dividend> dividends;

    public Holding(String id, String ticker, String name, double shares,
                   double avgBuyPrice, AccountType accountType, LocalDate buyDate) {
        this.id = id;
        this.ticker = ticker;
        this.name = name;
        this.shares = shares;
        this.avgBuyPrice = avgBuyPrice;
        this.accountType = accountType;
        this.buyDate = buyDate;
        this.currentPrice = avgBuyPrice; // Standard: brug købspris til vi henter live kurs
        this.sales = new ArrayList<>();
        this.dividends = new ArrayList<>();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getTicker() { return ticker; }
    public String getName() { return name; }
    public double getShares() { return shares; }
    public double getAvgBuyPrice() { return avgBuyPrice; }
    public AccountType getAccountType() { return accountType; }
    public LocalDate getBuyDate() { return buyDate; }
    public double getCurrentPrice() { return currentPrice; }
    public ArrayList<Sale> getSales() { return sales; }
    public ArrayList<Dividend> getDividends() { return dividends; }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    // ── Beregninger ───────────────────────────────────────────────────────────

    public double getCostBasis() {
        return shares * avgBuyPrice;
    }

    public double getCurrentValue() {
        return shares * currentPrice;
    }

    public double getUnrealizedGain() {
        return getCurrentValue() - getCostBasis();
    }

    public double getUnrealizedGainPct() {
        if (getCostBasis() == 0) return 0;
        return (getUnrealizedGain() / getCostBasis()) * 100;
    }

    public double estimatedTax() {
        return TaxCalculator.calculate(getUnrealizedGain(), accountType);
    }

    public double getTotalDividends() {
        double total = 0;
        for (Dividend d : dividends) {
            total += d.getAmount();
        }
        return total;
    }

    public double getTotalSaleGain() {
        double total = 0;
        for (Sale s : sales) {
            total += s.getGain();
        }
        return total;
    }

    public double getTotalReturn() {
        return getUnrealizedGain() + getTotalSaleGain() + getTotalDividends();
    }

    // ── Tilføj salg og udbytte ────────────────────────────────────────────────

    public void addSale(Sale sale) {
        sales.add(sale);
    }

    public void addDividend(Dividend dividend) {
        dividends.add(dividend);
    }

    // ── CSV ───────────────────────────────────────────────────────────────────

    // Format: id;ticker;name;shares;avgBuyPrice;accountType;buyDate;currentPrice
    public String toCsv() {
        return id + ";" + ticker + ";" + name + ";" + shares + ";" +
               avgBuyPrice + ";" + accountType.name() + ";" + buyDate + ";" + currentPrice;
    }

    public static Holding fromCsv(String line) {
        String[] parts = line.split(";");
        Holding h = new Holding(
            parts[0],
            parts[1],
            parts[2],
            Double.parseDouble(parts[3]),
            Double.parseDouble(parts[4]),
            AccountType.valueOf(parts[5]),
            LocalDate.parse(parts[6])
        );
        h.setCurrentPrice(Double.parseDouble(parts[7]));
        return h;
    }

    @Override
    public String toString() {
        return String.format(
            "[%s] %s | %.0f stk | Købt: %.2f kr | Nu: %.2f kr | Gevinst: %.2f kr (%.1f%%) | Est. skat: %.2f kr | Konto: %s",
            ticker, name, shares, avgBuyPrice, currentPrice,
            getUnrealizedGain(), getUnrealizedGainPct(), estimatedTax(), accountType
        );
    }
}
