package dk.kvalitetsit.itukt.common.model;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;
import static java.util.Optional.empty;


/**
 * Represents the complete set of input parameters required for a validation process related to a specific person and a drug medication context.
 *
 * @param personId The unique identifier for the person being validated.
 * @param skippedErrorCodes A list of specific error codes (as integers) that should be deliberately ignored during the validation process.
 * @param citizenAge The current age of the citizen/person, used for age-related validation rules.
 * @param drugId The unique identifier of the drug being validated.
 * @param indicationCode A string code representing the medical reason or indication for which the drug is being used.
 * @param existingDrugMedication An optional list of existing drug medications the citizen is already taking.
 */


public record ValidationInput(
        String personId,
        CreatedBy createdBy,
        Optional<ReportedBy> reportedBy,
        List<Integer> skippedErrorCodes,
        int citizenAge,
        long drugId,
        String indicationCode,
        Optional<List<ExistingDrugMedication>> existingDrugMedication) {
    /**
     * Represents the actor who wants to create a prescription
     * @param id Unique ID of the actor
     * @param specialityCode Example values: ortopædkirurg, sygeplejeske etc.
     */
        public record CreatedBy(String id, Optional<String> specialityCode) {
            public CreatedBy(String id) { this(id, empty()); }
            public CreatedBy(String id, String specialityCode) { this(id, of(specialityCode)); }
        }

        /**
         * Represents the actor who reports a prescription
         * @param id Unique ID of the actor
         * @param specialityCode Example values: ortopædkirurg, sygeplejeske etc.
         */
        public record ReportedBy(String id, Optional<String> specialityCode) {
            public ReportedBy(String id) { this(id, empty()); }
            public ReportedBy(String id, String specialityCode) { this(id, of(specialityCode)); }
        }
}