package dk.kvalitetsit.itukt.validation.stamdata.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Medication;
import dk.kvalitetsit.itukt.validation.stamdata.repository.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MedicationFormCacheTest {
    @Mock
    private CacheConfiguration configuration;

    @Mock
    private Repository<Medication.Form> repository;

    @InjectMocks
    private MedicationFormCache formCache;

    @Test
    void getForms_BeforeLoad_ReturnsEmptySet() {
        assertTrue(formCache.getForms().isEmpty());
    }

    @Test
    void getForms_AfterLoad_ReturnsDistinctForms() {
        var form1 = new Medication.Form("formA");
        var form2 = new Medication.Form("formB");
        var form3 = new Medication.Form("FoRmB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(form1, form2, form3));

        formCache.load();
        var forms = formCache.getForms();

        var expected = Set.of(form1, form2);
        assertEquals(expected, forms);
    }

    @Test
    void getForm_NotMatchingFormFromLoad_ReturnsEmpty() {
        var form1 = new Medication.Form("formA");
        var form2 = new Medication.Form("formB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(form1, form2));

        formCache.load();
        var result = formCache.getForm("nonExistingFormCode");

        assertTrue(result.isEmpty());
    }

    @Test
    void getForm_MatchingFormFromLoad_ReturnsForm() {
        var form1 = new Medication.Form("formA");
        var form2 = new Medication.Form("formB");

        Mockito.when(repository.fetchAll()).thenReturn(List.of(form1, form2));

        formCache.load();
        var result = formCache.getForm(form1.code());

        assertTrue(result.isPresent());
        assertEquals(form1, result.get());
    }

    @Test
    void getForm_BeforeLoad_ReturnsEmpty() {
        assertTrue(formCache.getForm("someFormCode").isEmpty());
    }
}