package dk.kvalitetsit.itukt.validation.service.model;

import java.util.ArrayList;

public record Department(SOR sor, ArrayList<Speciality> specialities) {
}
