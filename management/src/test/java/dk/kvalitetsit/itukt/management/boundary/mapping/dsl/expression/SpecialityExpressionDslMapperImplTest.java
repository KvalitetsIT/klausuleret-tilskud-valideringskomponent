package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression;

import dk.kvalitetsit.itukt.management.boundary.ExpressionType;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.DoctorSpecialityCondition;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SpecialityExpressionDslMapperImplTest {

    @InjectMocks
    private SpecialityExpressionDslMapperImpl mapper;

    @Test
    void merge_givenDslWithTwoSpecialityConditions_whenMap_thenMergeCorrectly() {
        var conditions = List.of(
                new DoctorSpecialityCondition().type(ExpressionType.DOCTOR_SPECIALITY).value("speciality1"),
                new DoctorSpecialityCondition().type(ExpressionType.DOCTOR_SPECIALITY).value("speciality2")
        );

        String expected = Identifier.DOCTOR_SPECIALITY + " i [speciality1, speciality2]";
        String actual = mapper.merge(conditions);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + conditions);
    }

    @Test
    void map_givenAValidSpecialityExpressionCondition_whenMap_thenReturnExpectedDsl() {
        var condition = new DoctorSpecialityCondition().type(ExpressionType.DOCTOR_SPECIALITY).value("blaah");
        var actual = mapper.map(condition);
        var expected = new Dsl(Identifier.DOCTOR_SPECIALITY + " = blaah", Dsl.Type.CONDITION);
        Assertions.assertEquals(expected, actual, "Unexpected mapping of: " + condition);
    }
}