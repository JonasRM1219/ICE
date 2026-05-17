import java.time.LocalDate;

public class Sale {
    private String id;
    private double shares;
    private double salePrice;
    private double buyPrice;
    private LocalDate date;
    private AccountType accountType;

    public Sale(String id, double shares, double salePrice, double buyPrice, LocalDate date, AccountType accountType) {
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

    public double getGain() {
        return shares * (salePrice - buyPrice);
    }

    public double getTax() {
        return TaxCalculator.calculate(getGain(), accountType);
    }

    public double getNetGain() {
        return getGain() - getTax();
    }

    // Til CSV gemning: id;shares;salePrice;buyPrice;date;accountType
    public String toCsv() {
        return id + ";" + shares + ";" + salePrice + ";" + buyPrice + ";" + date + ";" + accountType.name();
    }

    public static Sale fromCsv(String line) {
        String[] parts = line.split(";");
        return new Sale(
            parts[0],
            Double.parseDouble(parts[1]),
            Double.parseDouble(parts[2]),
            Double.parseDouble(parts[3]),
            LocalDate.parse(parts[4]),
            AccountType.valueOf(parts[5])
        );
    }

    @Override
    public String toString() {
        return String.format("Salg: %.0f stk til %.2f kr | Gevinst: %.2f kr | Skat: %.2f kr | Netto: %.2f kr (%s)",
            shares, salePrice, getGain(), getTax(), getNetGain(), date);
    }
}
