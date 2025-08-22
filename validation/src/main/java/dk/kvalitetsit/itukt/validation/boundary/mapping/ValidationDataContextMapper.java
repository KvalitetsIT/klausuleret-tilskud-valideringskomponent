package dk.kvalitetsit.itukt.validation.boundary.mapping;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import org.openapitools.model.ExistingDrugMedication;
import org.openapitools.model.ValidationRequest;

import java.util.HashMap;
import java.util.List;

public class ValidationDataContextMapper implements Mapper<ValidationRequest, DataContext> {
    @Override
    public DataContext map(ValidationRequest entry) {

        HashMap<String, List<String>> map = new HashMap<>();

        map.put("ALDER", List.of(entry.getAge().toString()));

        entry.getExistingDrugMedications().ifPresent(x -> {
            if (!x.isEmpty()) map.put("ATC", x.stream().map(ExistingDrugMedication::getAtcCode).toList());
        });

        return new DataContext(map);
    }

}
