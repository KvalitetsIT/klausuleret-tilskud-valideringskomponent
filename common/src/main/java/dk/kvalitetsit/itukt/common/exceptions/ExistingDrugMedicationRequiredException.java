package dk.kvalitetsit.itukt.common.exceptions;

public class ExistingDrugMedicationRequiredException extends RuntimeException {
    public ExistingDrugMedicationRequiredException() {
        super("Existing drug medication is required for this operation");
    }
}
