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

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    // Til CSV gemning: id;amount;date
    public String toCsv() {
        return id + ";" + amount + ";" + date;
    }

    // Indlæs fra CSV
    public static Dividend fromCsv(String line) {
        String[] parts = line.split(";");
        return new Dividend(parts[0], Double.parseDouble(parts[1]), LocalDate.parse(parts[2]));
    }

    @Override
    public String toString() {
        return String.format("Udbytte: %.2f kr (%s)", amount, date);
    }
}
