public class TaxCalculator {

    // Progressionsgrænse 2024
    private static final double GRÆNSE_UGIFT = 61000;
    private static final double GRÆNSE_GIFT = 122000;

    // Beregn skat baseret på gevinst og kontotype
    public static double calculate(double gain, AccountType type) {
        if (gain <= 0) return 0;

        switch (type) {
            case FRIE_MIDLER:
                return calculateProgressive(gain, false);
            case AKTIESPAREKONTO:
                return gain * 0.17;
            case PENSION:
                return gain * 0.153;
            default:
                return 0;
        }
    }

    // Progressiv beskatning for frie midler (27% / 42%)
    public static double calculateProgressive(double gain, boolean gift) {
        double grænse = gift ? GRÆNSE_GIFT : GRÆNSE_UGIFT;
        if (gain <= grænse) {
            return gain * 0.27;
        } else {
            return grænse * 0.27 + (gain - grænse) * 0.42;
        }
    }

    // Beregn estimeret skat ved salg af hele positionen
    public static double estimatedTaxOnSale(double gain, AccountType type, boolean gift) {
        if (type == AccountType.FRIE_MIDLER) {
            return calculateProgressive(gain, gift);
        }
        return calculate(gain, type);
    }
}
