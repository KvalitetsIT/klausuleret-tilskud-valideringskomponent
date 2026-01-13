package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

public enum Identifier {
    EXISTING_DRUG_MEDICATION("EKSISTERENDE_LÆGEMIDDEL"),
    INDICATION("INDIKATION"),
    AGE("ALDER"),
    ATC_CODE("ATC"),
    FORM_CODE("FORM"),
    ROUTE("ROUTE"),
    DOCTOR_SPECIALITY("LÆGESPECIALE"),
    DEPARTMENT_SPECIALITY("AFDELINGSSPECIALE");

    private final String value;

    Identifier(String value) {
        this.value = value;
    }

    public static Identifier from(String s) {
        for (Identifier id : Identifier.values()) {
            if (id.value.equalsIgnoreCase(s)) { // case-insensitive match
                return id;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + s);
    }

    @Override
    public String toString() {
        return value;
    }
}


