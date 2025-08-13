package dk.kvalitetsit.itukt.validation.boundary.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import org.openapitools.model.ExistingDrugMedication;
import org.openapitools.model.ValidationRequest;

import java.util.List;
import java.util.Map;

public class ValidationDataContextMapper implements Mapper<ValidationRequest, DataContext> {
    @Override
    public DataContext map(ValidationRequest entry) {
        return new DataContext(Map.of(
                "ALDER", List.of(entry.getAge().toString()),
                "ATC", entry.getExistingDrugMedications().orElse(List.of()).stream().map(ExistingDrugMedication::getAtcCode).toList()
        ));
    }

}
