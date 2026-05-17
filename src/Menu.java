import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class Menu {
    private Portfolio portfolio;
    private Scanner scanner;

    public Menu(Portfolio portfolio) {
        this.portfolio = portfolio;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("\n=== STOCKTRACK ===");
        while (true) {
            printMainMenu();
            String valg = scanner.nextLine().trim();
            switch (valg) {
                case "1": visPortefolje(); break;
                case "2": tilfoejAktie(); break;
                case "3": registrerSalg(); break;
                case "4": registrerUdbytte(); break;
                case "5": visAfkast(); break;
                case "6": visHistorik(); break;
                case "7": sletAktie(); break;
                case "0":
                    StorageService.savePortfolio(portfolio);
                    System.out.println("Data gemt. Farvel!");
                    return;
                default:
                    System.out.println("Ugyldigt valg.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Vis portefølje");
        System.out.println("2. Tilføj aktie");
        System.out.println("3. Registrér salg");
        System.out.println("4. Registrér udbytte");
        System.out.println("5. Vis samlet afkast");
        System.out.println("6. Vis salgshistorik");
        System.out.println("7. Slet aktie");
        System.out.println("0. Gem og afslut");
        System.out.print("Vælg: ");
    }

    private void visPortefolje() {
        System.out.println("\n=== PORTEFØLJE ===");
        if (portfolio.isEmpty()) {
            System.out.println("Ingen aktier endnu. Tilføj en aktie med valg 2.");
            return;
        }
        System.out.println(portfolio);
    }

    private void tilfoejAktie() {
        System.out.println("\n=== TILFØJ AKTIE ===");

        System.out.print("Ticker (fx AAPL, NOVO-B.CO): ");
        String ticker = scanner.nextLine().trim().toUpperCase();

        System.out.print("Navn (fx Apple Inc): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = ticker;

        System.out.print("Antal aktier: ");
        double shares = parseDouble(scanner.nextLine());

        System.out.print("Gennemsnitlig købspris (kr): ");
        double buyPrice = parseDouble(scanner.nextLine());

        System.out.println("Kontotype:");
        System.out.println("  1. Frie midler (27% / 42%)");
        System.out.println("  2. Aktiesparekonto (17%)");
        System.out.println("  3. Pension (15,3%)");
        System.out.print("Vælg: ");
        AccountType type = switch (scanner.nextLine().trim()) {
            case "2" -> AccountType.AKTIESPAREKONTO;
            case "3" -> AccountType.PENSION;
            default -> AccountType.FRIE_MIDLER;
        };

        Holding holding = new Holding(
            UUID.randomUUID().toString(),
            ticker, name, shares, buyPrice, type, LocalDate.now()
        );

        portfolio.addHolding(holding);
        StorageService.savePortfolio(portfolio);
        System.out.println("✓ " + ticker + " tilføjet til porteføljen.");
    }

    private void registrerSalg() {
        System.out.println("\n=== REGISTRÉR SALG ===");
        Holding h = vælgHolding();
        if (h == null) return;

        System.out.print("Antal solgte aktier: ");
        double shares = parseDouble(scanner.nextLine());

        System.out.print("Salgspris pr. aktie (kr): ");
        double salePrice = parseDouble(scanner.nextLine());

        Sale sale = new Sale(
            UUID.randomUUID().toString(),
            shares, salePrice, h.getAvgBuyPrice(),
            LocalDate.now(), h.getAccountType()
        );

        h.addSale(sale);
        StorageService.savePortfolio(portfolio);

        System.out.printf("✓ Salg registreret.%n");
        System.out.printf("  Gevinst: %.2f kr | Skat: %.2f kr | Netto: %.2f kr%n",
            sale.getGain(), sale.getTax(), sale.getNetGain());
    }

    private void registrerUdbytte() {
        System.out.println("\n=== REGISTRÉR UDBYTTE ===");
        Holding h = vælgHolding();
        if (h == null) return;

        System.out.print("Udbyttebeløb (kr): ");
        double amount = parseDouble(scanner.nextLine());

        Dividend dividend = new Dividend(UUID.randomUUID().toString(), amount, LocalDate.now());
        h.addDividend(dividend);
        StorageService.savePortfolio(portfolio);

        System.out.printf("✓ Udbytte på %.2f kr registreret for %s.%n", amount, h.getTicker());
    }

    private void visAfkast() {
        System.out.println("\n=== SAMLET AFKAST ===");
        if (portfolio.isEmpty()) { System.out.println("Ingen aktier."); return; }

        System.out.printf("Urealiseret gevinst:     %10.2f kr%n", portfolio.getTotalGain());
        System.out.printf("Realiseret gevinst:      %10.2f kr%n",
            portfolio.getHoldings().stream().mapToDouble(Holding::getTotalSaleGain).sum());
        System.out.printf("Udbytte modtaget:        %10.2f kr%n", portfolio.getTotalDividends());
        System.out.println("-".repeat(40));
        System.out.printf("Samlet afkast:           %10.2f kr%n", portfolio.getTotalReturn());
        System.out.printf("Estimeret skat ved salg: %10.2f kr%n", portfolio.getTotalTax());
    }

    private void visHistorik() {
        System.out.println("\n=== SALGSHISTORIK ===");
        boolean harSalg = false;
        for (Holding h : portfolio.getHoldings()) {
            if (!h.getSales().isEmpty()) {
                System.out.println("\n" + h.getTicker() + " - " + h.getName());
                for (Sale s : h.getSales()) {
                    System.out.println("  " + s);
                }
                harSalg = true;
            }
        }
        if (!harSalg) System.out.println("Ingen salg registreret endnu.");
    }

    private void sletAktie() {
        System.out.println("\n=== SLET AKTIE ===");
        Holding h = vælgHolding();
        if (h == null) return;
        portfolio.removeHolding(h.getId());
        StorageService.savePortfolio(portfolio);
        System.out.println("✓ " + h.getTicker() + " slettet.");
    }

    private Holding vælgHolding() {
        if (portfolio.isEmpty()) {
            System.out.println("Ingen aktier i porteføljen.");
            return null;
        }
        System.out.println("Vælg aktie:");
        ArrayList<Holding> list = portfolio.getHoldings();
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("  %d. %s - %s%n", i + 1, list.get(i).getTicker(), list.get(i).getName());
        }
        System.out.print("Nummer: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < list.size()) return list.get(idx);
        } catch (NumberFormatException ignored) {}
        System.out.println("Ugyldigt valg.");
        return null;
    }

    private double parseDouble(String input) {
        try {
            return Double.parseDouble(input.replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
