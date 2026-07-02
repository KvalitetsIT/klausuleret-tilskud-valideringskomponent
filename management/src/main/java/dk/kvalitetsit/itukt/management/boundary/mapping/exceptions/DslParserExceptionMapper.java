package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.BadRequestException;
import dk.kvalitetsit.itukt.management.exceptions.*;

public class DslParserExceptionMapper implements Mapper<DslParserException, BadRequestException> {
    private static final String INVALID_DSL = "Ugyldig klausulbetingelse. ";

    @Override
    public BadRequestException map(DslParserException dslParserException) {
        return switch (dslParserException) {
            case IncompleteDslException ignored ->
                    new BadRequestException("Ufærdig klausulbetingelse");
            case UnexpectedValueException e ->
                    new BadRequestException(INVALID_DSL + "Uventet værdi: " + e.getValue());
            case UnexpectedAgeValueException e ->
                    new BadRequestException(INVALID_DSL + "Uventet værdi for alder: " + e.getValue());
            case UnexpectedEmptyMultiValueConditionException ignored ->
                    new BadRequestException(INVALID_DSL + "Betingelse med flere værdier må ikke være tom");
            case UnexpectedExistingDrugMedicationKeysException e ->
                    new BadRequestException(INVALID_DSL + "Eksisterende lægemiddel kan kun have værdier for: " +
                            String.join(", ", e.getValidKeys()));
        };
    }
}
