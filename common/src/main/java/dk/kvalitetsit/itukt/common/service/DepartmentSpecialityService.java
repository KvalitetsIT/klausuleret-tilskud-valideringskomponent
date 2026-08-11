package dk.kvalitetsit.itukt.common.service;

import dk.kvalitetsit.itukt.common.model.Department;

import java.util.Optional;
import java.util.Set;

public interface DepartmentSpecialityService {
    Set<Department.Speciality> getSpecialities();
    Optional<Department.Speciality> getSpeciality(String specialityName);
}
