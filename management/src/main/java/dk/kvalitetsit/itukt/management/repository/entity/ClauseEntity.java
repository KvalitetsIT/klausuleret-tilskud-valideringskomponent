package dk.kvalitetsit.itukt.management.repository.entity;

import dk.kvalitetsit.itukt.common.repository.core.State;

import java.util.UUID;


public sealed interface ClauseEntity extends State<ClauseEntity> permits ClauseEntity.NotPersisted, ClauseEntity.Persisted {
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
            implements ClauseEntity, State.Persisted<ClauseEntity> {

    }

    record NotPersisted(
            String name,
            ExpressionEntity.NotPersisted expression,
            String errorMessage
    )
            implements ClauseEntity, State.NotPersisted<ClauseEntity> {
    }
}
