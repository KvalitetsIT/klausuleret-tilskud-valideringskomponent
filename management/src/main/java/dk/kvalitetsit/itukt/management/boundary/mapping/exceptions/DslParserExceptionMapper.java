package dk.kvalitetsit.itukt.management.boundary.mapping.exceptions;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.BadRequestApiException;
import dk.kvalitetsit.itukt.management.exceptions.*;

public class DslParserExceptionMapper implements Mapper<DslParserException, BadRequestApiException> {
    private static final String INVALID_DSL = "Ugyldig klausulbetingelse. ";

    @Override
    public BadRequestApiException map(DslParserException dslParserException) {
        return switch (dslParserException) {
            case IncompleteDslException ignored ->
                    new BadRequestApiException("Ufærdig klausulbetingelse");
            case UnexpectedValueException e ->
                    new BadRequestApiException(INVALID_DSL + "Uventet værdi: " + e.getValue());
            case UnexpectedAgeValueException e ->
                    new BadRequestApiException(INVALID_DSL + "Uventet værdi for alder: " + e.getValue());
            case UnexpectedEmptyMultiValueConditionException ignored ->
                    new BadRequestApiException(INVALID_DSL + "Betingelse med flere værdier må ikke være tom");
            case UnexpectedExistingDrugMedicationKeysException e ->
                    new BadRequestApiException(INVALID_DSL + "Eksisterende lægemiddel kan kun have værdier for: " +
                            String.join(", ", e.getValidKeys()));
        };
    }
}
