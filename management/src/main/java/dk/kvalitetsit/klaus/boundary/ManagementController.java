package dk.kvalitetsit.klaus.boundary;


import dk.kvalitetsit.klaus.boundary.mapping.DslMapper;
import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Pagination;
import dk.kvalitetsit.klaus.service.ManagementServiceAdaptor;
import jakarta.validation.constraints.Pattern;
import org.openapitools.api.ManagementApi;
import org.openapitools.model.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ManagementController implements ManagementApi {

    private final ManagementServiceAdaptor service;

    public ManagementController(@Autowired ManagementServiceAdaptor service, DslMapper dslMapper) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<String>> v1ClausesDslPost(List<@Pattern(regexp = "^\\s*(?:(Klausul|og|eller)|(>=|<=|=|i)|([A-Za-z][A-Za-z0-9]*)|([0-9]+)|([:,()])|(\\S))") String> requestBody) {
        return ResponseEntity.ok(this.service.createDSL(requestBody));
    }

    @Override
    public ResponseEntity<List<Expression>> v1ClausesGet(Optional<Integer> offset, Optional<Integer> limit) {
        try {
            return ResponseEntity.ok(service.read_all(new Pagination(offset, limit)));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Expression> v1ClausesIdDelete(UUID id) {
        try {
            return ResponseEntity.ok(service.delete(id));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Expression> v1ClausesIdGet(UUID id) {
        try {
            return ResponseEntity.ok(service.read(id));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ResponseEntity<List<Expression>> v1ClausesPost(List<Expression> expression) {
        try {
            return ResponseEntity.ok(service.create(expression));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

}
