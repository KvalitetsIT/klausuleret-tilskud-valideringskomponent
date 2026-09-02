package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.exceptions.ApiException;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.management.boundary.mapping.csv.ClauseDslToCsvMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslDtoMapper;
import dk.kvalitetsit.itukt.management.boundary.mapping.dsl.ClauseDslUpdateModelMapper;
import dk.kvalitetsit.itukt.management.exceptions.ManagementException;
import dk.kvalitetsit.itukt.management.service.model.ClauseInput;
import org.openapitools.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ManagementServiceAdaptor {

    private final ManagementService clauseService;
    private final Mapper<Clause, ClauseOutput> clauseDtoMapper;
    private final ClauseDslDtoMapper dslClauseMapper;
    private final Mapper<ClauseOutput, DslOutput> clauseDtoDslMapper;
    private final Mapper<org.openapitools.model.ClauseInput, ClauseInput> clauseInputMapper;
    private final Mapper<ManagementException, ApiException> managementExceptionMapper;
    private final ClauseDslUpdateModelMapper dslUpdateModelMapper;
    private final ClauseDslToCsvMapper clauseDslToCsvMapper;

    public ManagementServiceAdaptor(
            ManagementService clauseService,
            Mapper<Clause, ClauseOutput> modelDtoMapper,
            ClauseDslDtoMapper dslClauseMapper,
            Mapper<ClauseOutput, DslOutput> clauseDtoDslMapper,
            Mapper<org.openapitools.model.ClauseInput, ClauseInput> clauseInputMapper,
            Mapper<ManagementException, ApiException> managementExceptionMapper,
            ClauseDslUpdateModelMapper dslUpdateModelMapper, ClauseDslToCsvMapper clauseDslToCsvMapper
    ) {
        this.clauseService = clauseService;
        this.clauseDtoMapper = modelDtoMapper;
        this.dslClauseMapper = dslClauseMapper;
        this.clauseDtoDslMapper = clauseDtoDslMapper;
        this.clauseInputMapper = clauseInputMapper;
        this.managementExceptionMapper = managementExceptionMapper;
        this.dslUpdateModelMapper = dslUpdateModelMapper;
        this.clauseDslToCsvMapper = clauseDslToCsvMapper;
    }

    public ClauseOutput create(org.openapitools.model.ClauseInput clauseInput, Optional<Boolean> skipValidation) {
        try {
            var clauseForCreation = clauseInputMapper.map(clauseInput);
            return clauseDtoMapper.map(clauseService.create(clauseForCreation, skipValidation.orElse(false)));
        } catch (ManagementException e) {
            throw managementExceptionMapper.map(e);
        }
    }

    public DslOutput createDSL(DslInput dsl, Optional<Boolean> skipValidation) {
        try {
            var clauseInput = this.dslClauseMapper.map(dsl);
            return clauseDtoDslMapper.map(this.create(clauseInput, skipValidation));
        } catch (ManagementException e) {
            throw managementExceptionMapper.map(e);
        }
    }

    public DslOutput update(String name, DslUpdateInput dslUpdateInput, Optional<Boolean> skipValidation) {
        try {
            var updateInput = dslUpdateModelMapper.map(dslUpdateInput);
            var updatedClause = clauseService.updateDraft(new Clause.Name(name), updateInput, skipValidation.orElse(false));
            return clauseDtoDslMapper.map(clauseDtoMapper.map(updatedClause));
        } catch (ManagementException e) {
            throw managementExceptionMapper.map(e);
        }
    }

    public Optional<ClauseOutput> read(UUID id) {
        return clauseService.read(id).map(clauseDtoMapper::map);
    }

    public Optional<DslOutput> readDsl(UUID id) {
        return clauseService
                .read(id)
                .map(clauseDtoMapper::map)
                .map(clauseDtoDslMapper::map);
    }

    public List<DslOutput> readHistoryDsl(UUID uuid) {
        List<Clause> clauses = clauseService.readHistory(uuid);
        return clauseDtoDslMapper.map(clauseDtoMapper.map(clauses));
    }

    public List<ClauseOutput> readByStatus(ClauseStatus status) {
        return clauseDtoMapper.map(clauseService.readByStatus(mapStatus(status)));
    }

    public List<DslOutput> readDslByStatus(ClauseStatus status) {
        List<Clause> clauses = clauseService.readByStatus(mapStatus(status));
        return clauseDtoDslMapper.map(clauseDtoMapper.map(clauses));
    }

    public String readDslCsvByStatus(ClauseStatus status) {
        var dsl = readDslByStatus(status);
        return clauseDslToCsvMapper.map(dsl);
    }

    public DslOutput approveClause(UUID clauseUuid, boolean resetSkippedValidation) {
        try {
            return mapResponse(clauseService.approve(clauseUuid, resetSkippedValidation));
        } catch (ManagementException e) {
            throw managementExceptionMapper.map(e);
        }
    }


    public DslOutput inactivateClause(String clauseName) {
        try {
            var clause = clauseService.inactivate(new Clause.Name(clauseName));
            return mapResponse(clause);
        } catch (ManagementException e) {
            throw managementExceptionMapper.map(e);
        }
    }

    public DslOutput activateClause(String clauseName) {
        try {
            var clause = clauseService.activate(new Clause.Name(clauseName));
            return mapResponse(clause);
        } catch (ManagementException e) {
            throw managementExceptionMapper.map(e);
        }
    }

    public DrugCount getNumberOfDrugsForClause(String clauseName) {
        long drugCount = clauseService.getNumberOfDrugsForClause(new Clause.Name(clauseName));
        return new DrugCount().drugCount(drugCount);
    }

    private Clause.Status mapStatus(ClauseStatus status) {
        return switch (status) {
            case DRAFT -> Clause.Status.DRAFT;
            case ACTIVE -> Clause.Status.ACTIVE;
            case INACTIVE -> Clause.Status.INACTIVE;
        };
    }

    private DslOutput mapResponse(Clause clause) {
        return clauseDtoDslMapper.map(clauseDtoMapper.map(clause));
    }

    public ClauseOutput deleteDraft(UUID id) {
        try {
            return clauseDtoMapper.map(clauseService.deleteDraft(id));
        } catch (ManagementException e) {
            throw managementExceptionMapper.map(e);
        }
    }
}
