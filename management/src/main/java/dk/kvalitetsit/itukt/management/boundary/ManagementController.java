package dk.kvalitetsit.itukt.management.boundary;


import dk.kvalitetsit.itukt.common.exceptions.NotFoundException;
import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.management.service.ManagementServiceAdaptor;
import org.openapitools.api.ManagementApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RestController
@Transactional
public class ManagementController implements ManagementApi {

    private final ManagementServiceAdaptor service;

    public ManagementController(@Autowired ManagementServiceAdaptor service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<List<DslOutput>> call20250801clausesDslGet(ClauseStatus status) {
        return ResponseEntity.ok(service.readDslByStatus(status));
    }

    @Override
    public ResponseEntity<DslOutput> call20250801clausesDslIdGet(UUID id) {
        return service.readDsl(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Clause was not found"));
    }

    @Override
    public ResponseEntity<List<DslOutput>> call20250801clausesDslNameHistoryGet(String name) {
        return ResponseEntity.ok(service.readHistoryDsl(name));
    }

    @Override
    public ResponseEntity<DslOutput> call20250801clausesDslPost(DslInput dslInput) {
        var created = this.service.createDSL(dslInput);
        UUID uuid = created.getUuid();
        URI location = getLocation(c -> c.call20250801clausesDslIdGet(uuid), uuid);
        return ResponseEntity.created(location).body(created);
    }

    @Override
    public ResponseEntity<List<ClauseOutput>> call20250801clausesGet(ClauseStatus status) {
        return ResponseEntity.ok(service.readByStatus(status));
    }

    @Override
    public ResponseEntity<Void> call20250801clausesIdApprovePatch(UUID id) {
        service.approve(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ClauseOutput> call20250801clausesIdGet(UUID id) {
        return service.read(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Clause was not found"));
    }

    @Override
    public ResponseEntity<ClauseOutput> call20250801clausesPost(ClauseInput clause) {
        var created = service.create(clause);
        UUID uuid = created.getUuid();
        URI location = getLocation(c -> c.call20250801clausesIdGet(uuid), uuid);
        return ResponseEntity.created(location).body(created);
    }

    private URI getLocation(Function<ManagementController, Object> methodRef, UUID uuid) {
        return MvcUriComponentsBuilder
                .fromMethodCall(methodRef.apply(MvcUriComponentsBuilder.on(ManagementController.class)))
                .buildAndExpand(uuid)
                .toUri();
    }

}
