public enum AccountType {
    FRIE_MIDLER("Frie midler"),
    AKTIESPAREKONTO("Aktiesparekonto"),
    PENSION("Pension");

    private final String label;

    AccountType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
