package dk.kvalitetsit.itukt.common.model;

/**
 * A simple container of the data required by the {@link ExistingDrugMedicationConditionExpression}.
 * @param atcCode Anatomical Therapeutic Chemical (ATC) code of the drug; e.g. "C03CA01" represents Furosemide.
 * @param formCode A code representing the pharmaceutical form of the drug; e.g., "tablet", "capsule", "injection".
 * @param routeOfAdministrationCode A code representing how the drug is administered; e.g., "oral", "intravenous", "subcutaneous".
 */
public record ExistingDrugMedication(String atcCode, String formCode, String routeOfAdministrationCode) {
}
