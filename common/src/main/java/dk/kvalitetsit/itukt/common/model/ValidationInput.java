package dk.kvalitetsit.itukt.common.model;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;
import static java.util.Optional.empty;

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