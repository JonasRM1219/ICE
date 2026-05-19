public class Main {
    public static void main(String[] args) {
        // Indlæs gemt data ved opstart
        Portfolio portfolio = Stor
        ageService.loadPortfolio();

        // Start tekstbaseret menu
        Menu menu = new Menu(portfolio);
        menu.start();
    }
}
