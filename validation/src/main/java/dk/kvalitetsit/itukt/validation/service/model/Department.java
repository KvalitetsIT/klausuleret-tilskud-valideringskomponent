package dk.kvalitetsit.itukt.validation.service.model;

import java.util.Set;

public record Department(DepartmentIdentifier departmentIdentifier, Set<Speciality> specialities) {
}
