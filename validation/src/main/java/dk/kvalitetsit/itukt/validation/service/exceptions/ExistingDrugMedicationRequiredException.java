package dk.kvalitetsit.itukt.validation.service.exceptions;

public class ExistingDrugMedicationRequiredException extends RuntimeException {
    public ExistingDrugMedicationRequiredException() {
        super("Existing drug medication is required for this operation");
    }
}
