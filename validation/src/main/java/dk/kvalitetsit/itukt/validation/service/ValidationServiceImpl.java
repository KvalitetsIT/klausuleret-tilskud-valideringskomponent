package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import org.springframework.stereotype.Service;

import java.util.List;

public class ValidationServiceImpl implements ValidationService<DataContext> {

    private final ClauseRepository<Clause> clauseRepository;

    private final Evaluator evaluator = new Evaluator();

    public ValidationServiceImpl(ClauseRepository<Clause> clauseRepository) {
        this.clauseRepository = clauseRepository;
    }

    @Override
    public boolean validate(DataContext ctx) {
        List<Clause> clauses = this.clauseRepository.readAll();
        return clauses.stream().allMatch(clause -> evaluator.eval(clause.expression(), ctx));
    }
}
