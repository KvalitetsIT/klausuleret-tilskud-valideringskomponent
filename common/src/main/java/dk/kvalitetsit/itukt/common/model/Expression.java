package dk.kvalitetsit.itukt.common.model;

import dk.kvalitetsit.itukt.common.repository.core.State;

sealed public interface Expression extends State<Expression> permits Condition, Expression.NotPersisted, Expression.Persisted {

    boolean validates(ValidationInput validationInput);

    sealed interface Persisted extends Expression, State.Persisted<Expression> permits Persisted.Binary, Persisted.Condition {
        record Condition(Long id, dk.kvalitetsit.itukt.common.model.Condition condition) implements Expression.Persisted {
            @Override
            public boolean validates(ValidationInput validationInput) {
                return condition.validates(validationInput); // Delegate validation
            }
        }

        record Binary(
                Long id,
                Expression.Persisted left,
                BinaryOperator operator,
                Expression.Persisted right
        ) implements Expression.Persisted {

            @Override
            public boolean validates(ValidationInput validationInput) {
                return switch (operator) {
                    case AND -> left.validates(validationInput) && right.validates(validationInput);
                    case OR -> left.validates(validationInput) || right.validates(validationInput);
                };
            }

        }

    }

    sealed interface NotPersisted extends Expression, State.NotPersisted<Expression> permits NotPersisted.Binary, NotPersisted.Condition {
        record Condition(dk.kvalitetsit.itukt.common.model.Condition condition) implements Expression.NotPersisted {
            @Override
            public boolean validates(ValidationInput validationInput) {
                return condition.validates(validationInput); // Delegate validation
            }
        }

        record Binary(
                Expression.NotPersisted left,
                BinaryOperator operator,
                Expression.NotPersisted right
        ) implements Expression.NotPersisted {

            @Override
            public boolean validates(ValidationInput validationInput) {
                return switch (operator) {
                    case AND -> left.validates(validationInput) && right.validates(validationInput);
                    case OR -> left.validates(validationInput) || right.validates(validationInput);
                };
            }

        }

    }


}
