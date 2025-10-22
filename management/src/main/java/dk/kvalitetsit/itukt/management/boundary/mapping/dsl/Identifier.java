package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

enum Identifier {
    EXISTING_DRUG_MEDICATION("EKSISTERENDE_LÆGEMIDDEL"),
    INDICATION("INDIKATION"),
    AGE("ALDER"),
    ATC_CODE("ATC"),
    FORM_CODE("FORM"),
    ROUTE("ROUTE");

    private final String value;

    Identifier(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}


