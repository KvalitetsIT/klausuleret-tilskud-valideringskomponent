package dk.kvalitetsit.itukt.management.repository.entity;

import dk.kvalitetsit.itukt.common.model.Clause;

import java.util.Optional;
import java.util.Set;

public class ClauseQuery {
    private Optional<String> name = Optional.empty();
    private Set<Clause.Status> statuses = Set.of();
    private boolean withoutChildren = false;
    private boolean withoutPrimaryChildren = false;

    public ClauseQuery name(String name) {
        this.name = Optional.of(name);
        return this;
    }

    public ClauseQuery statuses(Clause.Status ... statuses) {
        this.statuses = Set.of(statuses);
        return this;
    }

    public ClauseQuery withoutChildren() {
        this.withoutChildren = true;
        return this;
    }

    public ClauseQuery withoutPrimaryChildren() {
        this.withoutPrimaryChildren = true;
        return this;
    }

    public Optional<String> getName() {
        return name;
    }

    public Set<Clause.Status> getStatuses() {
        return statuses;
    }

    public boolean isWithoutChildren() {
        return withoutChildren;
    }

    public boolean isWithoutPrimaryChildren() {
        return withoutPrimaryChildren;
    }
}
