package dk.kvalitetsit.itukt.validation.service.mapping.error;

import dk.kvalitetsit.itukt.common.model.ExistingDrugMedication;
import dk.kvalitetsit.itukt.common.model.ExistingDrugMedicationConditionExpression;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.common.model.ValidationError;
import dk.kvalitetsit.itukt.common.service.StamdataCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExistingDrugMedicationErrorMessageAssemblerTest {
    @Mock
    private StamdataCacheService<Medication.ATC> atcCache;
    @Mock
    private StamdataCacheService<Medication.Form> formCache;
    @Mock
    private StamdataCacheService<Medication.Route> routeCache;

    private ExistingDrugMedicationErrorMessageAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new ExistingDrugMedicationErrorMessageAssembler(atcCache, formCache, routeCache);
    }

    @Test
    void assemble_WithKnownAtcFormAndRoute_ReturnsErrorStringWithTextAndCode() {
        var atc = new Medication.ATC("ATC", "mapped atc");
        var form = new Medication.Form("FORM", "mapped form");
        var route = new Medication.Route("ROUTE", "mapped route");
        Mockito.when(atcCache.get("atc")).thenReturn(Optional.of(atc));
        Mockito.when(formCache.get("form")).thenReturn(Optional.of(form));
        Mockito.when(routeCache.get("adm")).thenReturn(Optional.of(route));
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication("atc", "form", "adm");
        var conditionError = new ValidationError.ExistingDrugMedicationError(existingDrugMedication);

        String errorString = assembler.assemble(conditionError);

        assertEquals("tidligere medicinsk behandling med følgende påkrævet: ATC = 'mapped atc' (atc), Formkode = 'mapped form' (form), Administrationsvejkode = 'mapped route' (adm)", errorString);
    }

    @Test
    void assemble_WithAtcAndRouteWildcards_ReturnsCorrespondingErrorString() {
        Mockito.when(formCache.get("form")).thenReturn(Optional.empty());
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication(ExistingDrugMedicationConditionExpression.WILDCARD, "form", ExistingDrugMedicationConditionExpression.WILDCARD);
        var conditionError = new ValidationError.ExistingDrugMedicationError(existingDrugMedication);

        String errorString = assembler.assemble(conditionError);

        assertEquals("tidligere medicinsk behandling med følgende påkrævet: Formkode = 'form'", errorString);
    }

    @Test
    void assemble_WithFormWildcard_ReturnsCorrespondingErrorString() {
        Mockito.when(atcCache.get("atc")).thenReturn(Optional.empty());
        Mockito.when(routeCache.get("adm")).thenReturn(Optional.empty());
        ExistingDrugMedication existingDrugMedication = new ExistingDrugMedication("atc", ExistingDrugMedicationConditionExpression.WILDCARD, "adm");
        var conditionError = new ValidationError.ExistingDrugMedicationError(existingDrugMedication);

        String errorString = assembler.assemble(conditionError);

        assertEquals("tidligere medicinsk behandling med følgende påkrævet: ATC = 'atc', Administrationsvejkode = 'adm'", errorString);
    }
}