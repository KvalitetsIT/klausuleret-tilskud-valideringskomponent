package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DepartmentEntity;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class DepartmentRepository implements Repository<DepartmentEntity> {

    private final DataClassRowMapper<DepartmentEntity> rowMapper;

    private final NamedParameterJdbcTemplate template;

    public DepartmentRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        rowMapper = DataClassRowMapper.newInstance(DepartmentEntity.class);
    }

    @Override
    public List<DepartmentEntity> fetchAll() throws ServiceException {
        try {
            String sql = """
                    SELECT
                    	se.sorId,
                    	se.ShakId,
                    	se.PrioritizedEntitySpeciality1Name,
                    	se.PrioritizedEntitySpeciality2Name,
                    	se.PrioritizedEntitySpeciality3Name,
                    	se.PrioritizedEntitySpeciality4Name,
                    	se.PrioritizedEntitySpeciality5Name,
                    	se.PrioritizedEntitySpeciality6Name,
                    	se.PrioritizedEntitySpeciality7Name,
                    	se.PrioritizedEntitySpeciality8Name
                    FROM (
                      SELECT t.*,
                             ROW_NUMBER() OVER (
                               PARTITION BY t.SorId
                               ORDER BY t.FromDate DESC, t.ValidFrom DESC
                             ) AS rn
                      FROM SorEntity t
                      WHERE t.ToDate IS NULL OR t.ToDate > NOW() AND t.ValidTo > NOW()
                    ) se
                    WHERE se.rn = 1 AND (
                            se.PrioritizedEntitySpeciality1Name IS NOT NULL OR
                            se.PrioritizedEntitySpeciality2Name IS NOT NULL OR
                            se.PrioritizedEntitySpeciality3Name IS NOT NULL OR
                            se.PrioritizedEntitySpeciality4Name IS NOT NULL OR
                            se.PrioritizedEntitySpeciality5Name IS NOT NULL OR
                            se.PrioritizedEntitySpeciality6Name IS NOT NULL OR
                            se.PrioritizedEntitySpeciality7Name IS NOT NULL OR
                            se.PrioritizedEntitySpeciality8Name IS NOT NULL
                    );
    """;



            return template.query(sql, rowMapper);

        } catch (Exception e) {
            throw new ServiceException("Failed to fetch all DepartmentEntities", e);
        }
    }
}
