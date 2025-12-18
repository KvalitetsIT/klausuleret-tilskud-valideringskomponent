package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.common.model.Department;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DepartmentEntity;
import dk.kvalitetsit.itukt.validation.stamdata.repository.mapping.DepartmentEntityModelMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DepartmentEntityModelMapperTest {

    @InjectMocks
    DepartmentEntityModelMapper mapper = new DepartmentEntityModelMapper();

    @Test
    void map_whenValidEntity_thenCorrectlyMapIntoModel() {

        var actual = mapper.map(new DepartmentEntity(
                "641701000016008",
                "3800Z21",
                "intern medicin",
                "kardiologi",
                "neurologi",
                "neurologi",
                null,
                "radiologi",
                null,
                "blaahh"
        ));

        Department expected = new Department(
                new Department.Identifier.SHAK("3800Z21"),
                new Department.Identifier.SOR("641701000016008"),
                Set.of(
                        new Department.Speciality("intern medicin"),
                        new Department.Speciality("kardiologi"),
                        new Department.Speciality("neurologi"),
                        new Department.Speciality("radiologi"),
                        new Department.Speciality("blaahh")
                )
        );

        assertEquals(expected, actual);

    }
}