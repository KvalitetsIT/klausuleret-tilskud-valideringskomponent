package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.Mapper;
import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.repository.ClauseDaoAdaptor;
import dk.kvalitetsit.klaus.repository.ValidationDao;
import dk.kvalitetsit.klaus.repository.ValidationDaoImpl;
import dk.kvalitetsit.klaus.service.model.DataContext;
import org.openapitools.model.ValidationRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValidationServiceImpl implements ValidationService<DataContext> {

    private final ClauseDaoAdaptor clauseRepository;
    private final ValidationDao validationDao;

    private final Evaluator evaluator = new Evaluator();

    public ValidationServiceImpl(ClauseDaoAdaptor clauseRepository, ValidationDaoImpl validationDao) {
        this.clauseRepository = clauseRepository;
        this.validationDao = validationDao;
    }

    @Override
    public boolean validate(DataContext ctx) {
        List<Clause> clauses = this.clauseRepository.read_all();
        return clauses.stream().allMatch(clause -> evaluator.eval(clause.expression(), ctx));
    }




}
