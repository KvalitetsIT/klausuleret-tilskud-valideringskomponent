package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Pagination;
import org.openapitools.model.Expression;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ManagementServiceAdaptor {

    private final ManagementService<dk.kvalitetsit.klaus.model.Expression> clauseService;
    private final Mapper<Expression, dk.kvalitetsit.klaus.model.Expression> dtoMapper;
    private final Mapper<dk.kvalitetsit.klaus.model.Expression, Expression> modelMapper;
    private final Mapper<String, Expression> dslToExpressionMapper;
    private final Mapper<dk.kvalitetsit.klaus.model.Expression, String> expressionToDSLMapper;

    public ManagementServiceAdaptor(
            ManagementServiceImpl clauseService,
            Mapper<Expression, dk.kvalitetsit.klaus.model.Expression> dtoMapper,
            Mapper<dk.kvalitetsit.klaus.model.Expression, Expression> modelMapper,
            Mapper<String, Expression> dslToExpressionMapper,
            Mapper<dk.kvalitetsit.klaus.model.Expression, String> expressionToDSLMapper
    ) {
        this.clauseService = clauseService;
        this.dtoMapper = dtoMapper;
        this.modelMapper = modelMapper;
        this.dslToExpressionMapper = dslToExpressionMapper;
        this.expressionToDSLMapper = expressionToDSLMapper;
    }

    public  List<String> createDSL(List<String> dsl) throws ServiceException {
        return dsl.stream().map(this::createDSL).toList();
    }

    public List<Expression> create(List<Expression> expressions) throws ServiceException {
        return expressions.stream().map(this::create).toList();
    }

    public Expression create(Expression entry) throws ServiceException {
        var result = clauseService.create(dtoMapper.map(entry));
        return modelMapper.map(result);
    }

    public String createDSL(String dsl) throws ServiceException {
        var clause = this.dslToExpressionMapper.map(dsl);
        var model = this.dtoMapper.map(clause);
        return expressionToDSLMapper.map(clauseService.create(model));
    }


    public Expression delete(UUID entry) throws ServiceException {
        return modelMapper.map(clauseService.delete(entry));
    }

    public Expression read(UUID id) throws ServiceException {
        return modelMapper.map(clauseService.read(id));
    }

    public List<Expression> read_all(Pagination pagination) throws ServiceException {
        return clauseService.read_all(pagination).stream().map(modelMapper::map).toList();
    }

    public List<Expression> read_all() throws ServiceException {
        return clauseService.read_all().stream().map(modelMapper::map).toList();
    }

    public Expression update(UUID id, Expression entry) throws ServiceException {
        return modelMapper.map(clauseService.update(id, dtoMapper.map(entry)));
    }
}
