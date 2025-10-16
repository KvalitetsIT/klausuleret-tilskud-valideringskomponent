package dk.kvalitetsit.itukt.common.model;

import dk.kvalitetsit.itukt.common.exceptions.ExistingDrugMedicationRequiredException;

public sealed interface Condition extends Expression {

    record Indication(String requiredValue) implements Condition {
        @Override
        public boolean validates(ValidationInput validationInput) {
            return requiredValue.equals(validationInput.indicationCode());
        }
    }

    record Age(Operator operator, int value) implements Condition {
        @Override
        public boolean validates(ValidationInput validationInput) {
            var intValue = validationInput.citizenAge();
            return switch (operator) {
                case EQUAL -> intValue == this.value;
                case GREATER_THAN_OR_EQUAL_TO -> intValue >= this.value;
                case LESS_THAN_OR_EQUAL_TO -> intValue <= this.value;
                case GREATER_THAN -> intValue > this.value;
                case LESS_THAN -> intValue < this.value;
            };
        }
    }

    record ExistingDrugMedication(String atcCode, String formCode,
                                  String routeOfAdministrationCode) implements Condition {

        @Override
        public boolean validates(ValidationInput validationInput) {
            var existingDrugMedication = validationInput.existingDrugMedication()
                    .orElseThrow(ExistingDrugMedicationRequiredException::new);
            return existingDrugMedication.stream().anyMatch(this::itemMatches);
        }

        private boolean itemMatches(ExistingDrugMedication value) {
            return value instanceof ExistingDrugMedication(
                    String atcCodeValue, String formCodeValue, String routeOfAdministrationCodeValue
            ) &&
                    valueMatchesCondition(atcCodeValue, this.atcCode) &&
                    valueMatchesCondition(formCodeValue, this.formCode) &&
                    valueMatchesCondition(routeOfAdministrationCodeValue, this.routeOfAdministrationCode);
        }

        private boolean valueMatchesCondition(String value, String condition) {
            return condition.equals(value) || condition.equals("*");
        }

    }
}