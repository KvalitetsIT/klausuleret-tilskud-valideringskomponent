package dk.kvalitetsit.itukt.common.model;

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
public record Department(Identifier.SHAK shak, Identifier.SOR sor, Set<Speciality> specialities) {
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
