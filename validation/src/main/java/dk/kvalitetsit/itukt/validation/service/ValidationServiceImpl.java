package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.repository.ClauseCache;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;

public class ValidationServiceImpl implements ValidationService<DataContext> {

    private final ClauseCache clauseCache;

    private final Evaluator evaluator = new Evaluator();

    public ValidationServiceImpl(ClauseCache clauseCache) {
        this.clauseCache = clauseCache;
    }

    @Override
    public boolean validate(DataContext ctx) {
//        List<Clause> clauses = this.clauseRepository.readAll();
//        clauses.stream().anyMatch(clause -> evaluator.eval(clause.expression(), ctx));
        return true;
    }
}
