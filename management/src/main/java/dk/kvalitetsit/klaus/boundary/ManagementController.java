package dk.kvalitetsit.klaus.boundary;


import dk.kvalitetsit.klaus.boundary.mapping.dsl.DslClauseMapper;
import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Pagination;
import dk.kvalitetsit.klaus.service.ManagementServiceAdaptor;
import org.openapitools.api.ManagementApi;
import org.openapitools.model.Clause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ManagementController implements ManagementApi {

    private final ManagementServiceAdaptor service;

    public ManagementController(@Autowired ManagementServiceAdaptor service, DslClauseMapper dslClauseMapper) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<String>> call20250801clausesDslPost(List<String> requestBody) {
        var response = this.service.createDSL(requestBody);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<Clause>> call20250801clausesGet(Optional<Integer> offset, Optional<Integer> limit) {
        try {
            if (offset.isEmpty() && limit.isEmpty()) return ResponseEntity.ok(service.read_all());
            return ResponseEntity.ok(service.read_all(new Pagination(offset, limit)));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Clause> call20250801clausesIdDelete(UUID id) {
        return service.delete(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Could not delete clause"));
    }

    @Override
    public ResponseEntity<Clause> call20250801clausesIdGet(UUID id) {
        return service.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Clause was not found"));
    }


    @Override
    public ResponseEntity<List<Clause>> call20250801clausesPost(List<Clause> expression) {
        try {
            return ResponseEntity.ok(service.create(expression));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

}
