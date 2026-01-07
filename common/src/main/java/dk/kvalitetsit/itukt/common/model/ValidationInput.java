package dk.kvalitetsit.itukt.common.model;


import java.util.List;
import java.util.Optional;

public record ValidationInput(
        String personId,
        Actor createdBy,
        Optional<Actor> reportedBy,
        List<Integer> skippedErrorCodes,
        int citizenAge,
        long drugId,
        String indicationCode,
        Optional<List<ExistingDrugMedication>> existingDrugMedication) {
    /**
     * Represents the actor who either created or reported the prescription
     * @param id Unique ID of the actor
     * @param specialityCode Example values: ortopædkirurg, sygeplejeske etc.
     * @param department the department of which the actor is associated
     */
   public record Actor (String id, Optional<String> specialityCode, Optional<Department> department) {
        public Actor(String id) {
            this(id, Optional.empty(), Optional.empty());
        }
    }
}