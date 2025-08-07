package dk.kvalitetsit.itukt.service;


import dk.kvalitetsit.itukt.repository.ClauseRepository;
import dk.kvalitetsit.itukt.model.Clause;
import dk.kvalitetsit.itukt.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.service.model.DataContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService<DataContext> {

    private final ClauseRepository<Clause> clauseRepository;

    private final Evaluator evaluator = new Evaluator();

    public ValidationServiceImpl(ClauseRepositoryAdaptor clauseRepository) {
        this.clauseRepository = clauseRepository;
    }

    @Override
    public boolean validate(DataContext ctx) {
        List<Clause> clauses = this.clauseRepository.readAll();
        return clauses.stream().allMatch(clause -> evaluator.eval(clause.expression(), ctx));
    }
}
