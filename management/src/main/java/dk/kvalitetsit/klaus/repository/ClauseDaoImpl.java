package dk.kvalitetsit.klaus.repository;



import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.model.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Repository
public class ClauseDaoImpl implements ClauseDao {

    private static final Logger logger = LoggerFactory.getLogger(ClauseDaoImpl.class);
    private final NamedParameterJdbcTemplate template;

    public ClauseDaoImpl(@Qualifier("validationDataSource") DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Expression create(Expression entry) throws ServiceException {
        return null;
    }

    @Override
    public List<Expression> create(List<Expression> entry) throws ServiceException {
        return List.of();
    }

    @Override
    public Expression delete(UUID entry) throws ServiceException {
        return null;
    }

    @Override
    public Expression read(UUID id) throws ServiceException {
        return null;
    }

    @Override
    public List<Expression> read_all(Pagination pagination) throws ServiceException {
        return List.of();
    }

    @Override
    public List<Expression> read_all() throws ServiceException {
        return List.of();
    }

    @Override
    public Expression update(UUID id, Expression entry) throws ServiceException {
        return null;
    }
}
