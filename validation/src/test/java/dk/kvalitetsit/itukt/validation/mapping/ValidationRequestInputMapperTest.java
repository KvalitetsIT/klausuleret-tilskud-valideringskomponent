package dk.kvalitetsit.itukt.validation.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.ValidationInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ValidationRequestInputMapperTest {

    @InjectMocks
    private ValidationRequestInputMapper subject;

    @Mock
    private Mapper<Actor, ValidationInput.Actor> actorMapper;

    @Test
    void map() {
        String creator1 = "creator1";
        String reporter1 = "reporter1";
        String path1 = "path1";
        Validate validate1 = createValidate(1L, "1111", path1, creator1, reporter1);
        String creator2 = "creator2";
        String reporter2 = "reporter2";
        String path2 = "path2";

        Validate validate2 = createValidate(2L, "2222", path2, creator2, reporter2);
        ExistingDrugMedicationInput existingDrugMedication = new ExistingDrugMedicationInput("atc", "form", "adm", 1L);
        ValidationRequest request = createValidationRequest(List.of(1), 10, List.of(existingDrugMedication), validate1, validate2);

        var expectedExistingDrugMedication = new dk.kvalitetsit.itukt.common.model.ExistingDrugMedication(
                existingDrugMedication.getAtcCode(),
                existingDrugMedication.getFormCode(),
                existingDrugMedication.getRouteOfAdministrationCode()
        );

        ValidationInput.Actor createdBy1 = new ValidationInput.Actor(creator1, Optional.empty(), Optional.empty());
        ValidationInput.Actor reportedBy1 = new ValidationInput.Actor(reporter1, Optional.empty(), Optional.empty());

        ValidationInput.Actor createdBy2 = new ValidationInput.Actor(creator2, Optional.empty(), Optional.empty());
        ValidationInput.Actor reportedBy2 = new ValidationInput.Actor(reporter2, Optional.empty(), Optional.empty());

        Mockito.when(actorMapper.map(new Actor().identifier(creator1))).thenReturn(createdBy1);
        Mockito.when(actorMapper.map(new Actor().identifier(reporter1))).thenReturn(reportedBy1);
        Mockito.when(actorMapper.map(new Actor().identifier(creator2))).thenReturn(createdBy2);
        Mockito.when(actorMapper.map(new Actor().identifier(reporter2))).thenReturn(reportedBy2);

        var actual = subject.map(request);

        var expected = List.of(
                new ValidationInput(
                        request.getPersonIdentifier(),
                        createdBy1,
                        Optional.of(reportedBy1),
                        request.getSkipValidations(),
                        request.getAge(),
                        validate1.getNewDrugMedication().getDrugIdentifier(),
                        validate1.getNewDrugMedication().getIndicationCode(),
                        of(List.of(expectedExistingDrugMedication)),
                        path1
                ),
                new ValidationInput(
                        request.getPersonIdentifier(),
                        createdBy2,
                        Optional.of(reportedBy2),
                        request.getSkipValidations(),
                        request.getAge(),
                        validate2.getNewDrugMedication().getDrugIdentifier(),
                        validate2.getNewDrugMedication().getIndicationCode(),
                        Optional.of(List.of(expectedExistingDrugMedication)),
                        path2
                )
        );

        assertEquals(expected, actual);
    }


    private ValidationRequest createValidationRequest(List<Integer> skippedValidations, int age, List<ExistingDrugMedicationInput> existingDrugMedication, Validate... validates) {
        return new ValidationRequest()
                .personIdentifier("1234")
                .skipValidations(skippedValidations)
                .age(age)
                .existingDrugMedications(existingDrugMedication)
                .validate(Arrays.stream(validates).toList());
    }

    private Validate createValidate(long drugId, String indicationCode, String elementPath, String createdBy, String reportedBy) {
        return new Validate()
                .newDrugMedication(new NewDrugMedication()
                        .createdBy(new Actor().identifier(createdBy))
                        .reportedBy(new Actor().identifier(reportedBy))
                        .drugIdentifier(drugId)
                        .indicationCode(indicationCode))
                .elementPath(elementPath);
    }
}