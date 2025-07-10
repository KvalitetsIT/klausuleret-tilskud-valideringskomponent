package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.repository.ClauseDaoImpl;
import dk.kvalitetsit.klaus.repository.ValidationDaoImpl;
import dk.kvalitetsit.klaus.service.model.DataContext;
import dk.kvalitetsit.klaus.service.model.Prescription;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService<Prescription> {

    private final ClauseDaoImpl clauseRepository;
    private final ValidationDaoImpl validationDao;

    private final Evaluator evaluator = new Evaluator();

    public ValidationServiceImpl(ClauseDaoImpl clauseRepository, ValidationDaoImpl validationDao) {
        this.clauseRepository = clauseRepository;
        this.validationDao = validationDao;
    }

    @Override
    public boolean validate(Prescription prescription) {
        List<Expression> clauses = this.clauseRepository.read_all();

        // Adapting the prescripting is missing
        DataContext ctx = null; // DataContext.from(prescription);
        var result =  clauses.stream().allMatch((clause) -> evaluator.eval(clause, ctx));

        return false;

    }
}
