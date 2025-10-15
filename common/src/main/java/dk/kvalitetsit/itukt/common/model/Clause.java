package dk.kvalitetsit.itukt.common.model;


import dk.kvalitetsit.itukt.common.repository.core.State;

import java.util.UUID;

public sealed interface Clause extends State<Clause> permits Clause.NotPersisted, Clause.Persisted {
    String name();

    Expression expression();

    record Persisted(Long id,
                     UUID uuid,
                     String name,
                     Error error,
                     Expression.Persisted expression
    )
            implements Clause, State.Persisted<Clause> {

    }

    record NotPersisted(
            String name,
            Expression.NotPersisted expression,
            String errorMessage
    )
            implements Clause, State.NotPersisted<Clause> {
    }

}

