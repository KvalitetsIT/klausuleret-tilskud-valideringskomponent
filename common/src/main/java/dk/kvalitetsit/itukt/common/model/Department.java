package dk.kvalitetsit.itukt.common.model;

import java.util.Optional;
import java.util.Set;


/**
 * This model represents a department in a simple form.
 * </br>
 * Notice, that the department may as well be an organization
 *
 * @param sor          The sor code pointing to the department/organisation
 * @param shak         The shak code pointing to the department/organisation
 * @param specialities The distinct specialities assigned to the department/organisation
 */
public record Department(Optional<Identifier.SHAK> shak, Optional<Identifier.SOR> sor, Set<Speciality> specialities) {
    public Department {
        java.util.Objects.requireNonNull(shak);
        java.util.Objects.requireNonNull(sor);
        java.util.Objects.requireNonNull(specialities);
    }

    public Department(Identifier id, Set<Speciality> specialities) {
        this(
                id instanceof Identifier.SHAK shakId ? Optional.of(shakId) : Optional.empty(),
                id instanceof Identifier.SOR sorId ? Optional.of(sorId) : Optional.empty(),
                specialities
        );
    }

    public Department withSpecialities(Set<Speciality> specialities) {
        return new Department(this.shak, this.sor, specialities);
    }


    public sealed interface Identifier permits Identifier.SHAK, Identifier.SOR {

        String code();

        record SHAK(String code) implements Identifier {
        }

        record SOR(String code) implements Identifier {
        }
    }

    public record Speciality(String name) {
    }
}
