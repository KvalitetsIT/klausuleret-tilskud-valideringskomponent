package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.management.boundary.ErrorMessages;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.dsl2expression.DslParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public enum Identifier {
    EXISTING_DRUG_MEDICATION("EKSISTERENDE_LÆGEMIDDEL"),
    INDICATION("INDIKATION"),
    AGE("ALDER"),
    ATC_CODE("ATC"),
    FORM_CODE("FORM"),
    ROUTE("ROUTE"),
    DOCTOR_SPECIALITY("LÆGESPECIALE"),
    DEPARTMENT_SPECIALITY("AFDELINGSSPECIALE");

    private static final Logger logger = LoggerFactory.getLogger(Identifier.class);
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
        logger.debug("Unknown identifier: {}. Available identifiers: {}", s, Arrays.toString(Identifier.values()));
        throw new DslParserException(ErrorMessages.unexpectedValue(s));
    }

    @Override
    public String toString() {
        return value;
    }
}


