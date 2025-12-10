package dk.kvalitetsit.itukt.common.model;


import java.util.List;
import java.util.Optional;

public record ValidationInput(
        String personId,
        Actor createdById,
        Optional<Actor> reportedBy,
        List<Integer> skippedErrorCodes,
        int citizenAge,
        long drugId,
        String indicationCode,
        Department.Identifier organisationId,
        Optional<List<ExistingDrugMedication>> existingDrugMedication
) {
   public record Actor (String id, Optional<Department> department) { }
}