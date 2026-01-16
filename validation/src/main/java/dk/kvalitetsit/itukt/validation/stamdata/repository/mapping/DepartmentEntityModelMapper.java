package dk.kvalitetsit.itukt.validation.stamdata.repository.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DepartmentEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class DepartmentEntityModelMapper implements Mapper<DepartmentEntity, Department> {

    private static void addIfValid(List<Department.Speciality> list, String name) {
        if (name != null) list.add(new Department.Speciality(name));
    }


    @Override
    public Department map(DepartmentEntity entry) {

        List<Department.Speciality> specialities = new ArrayList<>();

        addIfValid(specialities, entry.PrioritizedEntitySpeciality1Name());
        addIfValid(specialities, entry.PrioritizedEntitySpeciality2Name());
        addIfValid(specialities, entry.PrioritizedEntitySpeciality3Name());
        addIfValid(specialities, entry.PrioritizedEntitySpeciality4Name());
        addIfValid(specialities, entry.PrioritizedEntitySpeciality5Name());
        addIfValid(specialities, entry.PrioritizedEntitySpeciality6Name());
        addIfValid(specialities, entry.PrioritizedEntitySpeciality7Name());
        addIfValid(specialities, entry.PrioritizedEntitySpeciality8Name());

        return new Department(
                Optional.ofNullable(entry.ShakId()).map(Department.Identifier.SHAK::new),
                Optional.ofNullable(entry.sorId()).map(Department.Identifier.SOR::new),
                new HashSet<>(specialities)
        );
    }
}
