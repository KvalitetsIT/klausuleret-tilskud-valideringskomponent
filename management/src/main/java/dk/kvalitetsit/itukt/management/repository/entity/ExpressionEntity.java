package dk.kvalitetsit.itukt.management.repository.entity;

import dk.kvalitetsit.itukt.common.model.BinaryOperator;
import dk.kvalitetsit.itukt.common.model.Field;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.common.repository.core.State;

public sealed interface ExpressionEntity extends State<ExpressionEntity> {

    ExpressionType type();

    sealed interface Persisted extends ExpressionEntity, State.Persisted<ExpressionEntity> {

        Long id();

        record StringCondition(Long id, Field field, String value) implements ExpressionEntity.Persisted {
            @Override
            public ExpressionType type() {
                return ExpressionType.STRING_CONDITION;
            }
        }

        record NumberCondition(Long id, Field field, Operator operator,
                               int value) implements ExpressionEntity.Persisted {
            @Override
            public ExpressionType type() {
                return ExpressionType.NUMBER_CONDITION;
            }
        }


        record BinaryExpression(Long id, ExpressionEntity.Persisted left, BinaryOperator operator,
                                ExpressionEntity.Persisted right) implements ExpressionEntity.Persisted {
            @Override
            public ExpressionType type() {
                return ExpressionType.BINARY;
            }
        }


        record ExistingDrugMedicationCondition(Long id, String atcCode, String formCode,
                                               String routeOfAdministrationCode) implements ExpressionEntity.Persisted {
            @Override
            public ExpressionType type() {
                return ExpressionType.EXISTING_DRUG_MEDICATION;
            }
        }
    }

    sealed interface NotPersisted extends ExpressionEntity, State.NotPersisted<ExpressionEntity> {
        record StringConditionEntity(Field field, String value) implements ExpressionEntity.NotPersisted {
            @Override
            public ExpressionType type() {
                return ExpressionType.STRING_CONDITION;
            }
        }

        record NumberCondition(Field field, Operator operator, int value)
                implements ExpressionEntity.NotPersisted {
            @Override
            public ExpressionType type() {
                return ExpressionType.NUMBER_CONDITION;
            }
        }

        record BinaryExpression(ExpressionEntity.NotPersisted left, BinaryOperator operator,
                                ExpressionEntity.NotPersisted right) implements ExpressionEntity.NotPersisted {
            @Override
            public ExpressionType type() {
                return ExpressionType.BINARY;
            }
        }

        record ExistingDrugMedicationCondition(String atcCode, String formCode, String routeOfAdministrationCode)
                implements ExpressionEntity.NotPersisted {
            @Override
            public ExpressionType type() {
                return ExpressionType.EXISTING_DRUG_MEDICATION;
            }
        }
    }
}

