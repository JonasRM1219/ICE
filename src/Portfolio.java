import java.util.ArrayList;

public class Portfolio {
    private ArrayList<Holding> holdings;

    public Portfolio() {
        this.holdings = new ArrayList<>();
    }

    public ArrayList<Holding> getHoldings() {
        return holdings;
    }

    public void addHolding(Holding holding) {
        holdings.add(holding);
    }

    public void removeHolding(String id) {
        holdings.removeIf(h -> h.getId().equals(id));
    }

    public Holding findById(String id) {
        for (Holding h : holdings) {
            if (h.getId().equals(id)) return h;
        }
        return null;
    }

    public Holding findByTicker(String ticker) {
        for (Holding h : holdings) {
            if (h.getTicker().equalsIgnoreCase(ticker)) return h;
        }
        return null;
    }

    // ── Samlede beregninger ───────────────────────────────────────────────────

    public double getTotalValue() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getCurrentValue();
        }
        return total;
    }

    public double getTotalCost() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getCostBasis();
        }
        return total;
    }

    public double getTotalGain() {
        return getTotalValue() - getTotalCost();
    }

    public double getTotalTax() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.estimatedTax();
        }
        return total;
    }

    public double getTotalDividends() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getTotalDividends();
        }
        return total;
    }

    public double getTotalReturn() {
        double total = 0;
        for (Holding h : holdings) {
            total += h.getTotalReturn();
        }
        return total;
    }

    public boolean isEmpty() {
        return holdings.isEmpty();
    }

    @Override
    public String toString() {
        if (holdings.isEmpty()) return "Porteføljen er tom.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-20s %8s %12s %12s %10s%n",
            "Ticker", "Navn", "Stk", "Investeret", "Værdi", "Gevinst"));
        sb.append("-".repeat(72)).append("\n");
        for (Holding h : holdings) {
            sb.append(String.format("%-6s %-20s %8.0f %10.2f kr %10.2f kr %+9.2f kr%n",
                h.getTicker(), h.getName(), h.getShares(),
                h.getCostBasis(), h.getCurrentValue(), h.getUnrealizedGain()));
        }
        sb.append("-".repeat(72)).append("\n");
        sb.append(String.format("%-28s %10.2f kr %10.2f kr %+9.2f kr%n",
            "TOTAL", getTotalCost(), getTotalValue(), getTotalGain()));
        sb.append(String.format("Estimeret skat ved salg: %.2f kr%n", getTotalTax()));
        sb.append(String.format("Samlet afkast inkl. udbytte og salg: %.2f kr%n", getTotalReturn()));
        return sb.toString();
    }
}
