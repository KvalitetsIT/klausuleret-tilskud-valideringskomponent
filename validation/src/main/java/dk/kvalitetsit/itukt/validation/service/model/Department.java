package dk.kvalitetsit.itukt.validation.service.model;

import java.util.List;

public record Department(SOR sor, List<Speciality> specialities) {
}
