package dk.kvalitetsit.klaus.boundary;


import dk.kvalitetsit.klaus.service.ManagementServiceAdaptor;
import org.openapitools.api.ManagementApi;
import org.openapitools.model.Clause;
import org.openapitools.model.DslInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ManagementController implements ManagementApi {

    private final ManagementServiceAdaptor service;

    public ManagementController(@Autowired ManagementServiceAdaptor service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<Void> call20250801clausesDslPost(DslInput dslInput) {
        this.service.createDSL(dslInput);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Clause>> call20250801clausesGet() {
        return ResponseEntity.ok(service.read_all());
    }

    @Override
    public ResponseEntity<Clause> call20250801clausesIdGet(UUID id) {
        return service.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Clause was not found"));
    }

    @Override
    public ResponseEntity<Void> call20250801clausesPost(Clause clause) {
        service.create(clause);
        return ResponseEntity.ok().build();
    }

}
