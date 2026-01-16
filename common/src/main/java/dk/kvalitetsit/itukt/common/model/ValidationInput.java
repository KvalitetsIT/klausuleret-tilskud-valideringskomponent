package dk.kvalitetsit.itukt.common.model;


import java.util.List;
import java.util.Optional;


/**
 * Represents the complete set of input parameters required for a validation process related to a specific person and a drug medication context.
 *
 * @param personId               The unique identifier for the person being validated.
 * @param skippedErrorCodes      A list of specific error codes (as integers) that should be deliberately ignored during the validation process.
 * @param citizenAge             The current age of the citizen/person, used for age-related validation rules.
 * @param drugId                 The unique identifier of the drug being validated.
 * @param indicationCode         A string code representing the medical reason or indication for which the drug is being used.
 * @param existingDrugMedication An optional list of existing drug medications the citizen is already taking.
 * @param elementPath            A unique identifier for the validation, that must be included in validation errors.
 */
public record ValidationInput(
        String personId,
        Actor createdBy,
        Optional<Actor> reportedBy,
        List<Integer> skippedErrorCodes,
        int citizenAge,
        long drugId,
        String indicationCode,
        Optional<List<ExistingDrugMedication>> existingDrugMedication,
        String elementPath) {
    /**
     * Represents the actor who either created or reported the prescription
     *
     * @param id             Unique ID of the actor
     * @param specialityCode Example values: ortopædkirurg, sygeplejeske etc.
     * @param department     the department of which the actor is associated
     */
    public record Actor(String id, Optional<String> specialityCode, Optional<Department> department) {
        public Actor(String id) {
            this(id, Optional.empty(), Optional.empty());
        }
    }
}