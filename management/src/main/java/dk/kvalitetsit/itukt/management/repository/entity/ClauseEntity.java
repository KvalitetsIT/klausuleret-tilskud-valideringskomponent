package dk.kvalitetsit.itukt.management.repository.entity;

import java.util.UUID;


public sealed interface ClauseEntity permits ClauseEntity.NotPersisted, ClauseEntity.Persisted {
    String name();

    String errorMessage();

    ExpressionEntity expression();

    record Persisted(Long id,
                     UUID uuid,
                     String name,
                     Integer errorCode,
                     String errorMessage,
                     ExpressionEntity.Persisted expression
    )
            implements ClauseEntity{

    }

    record NotPersisted(
            String name,
            ExpressionEntity.NotPersisted expression,
            String errorMessage
    )
            implements ClauseEntity {
    }
}
