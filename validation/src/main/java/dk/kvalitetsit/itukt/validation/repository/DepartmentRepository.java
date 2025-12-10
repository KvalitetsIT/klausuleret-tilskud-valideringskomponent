package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.repository.entity.DepartmentEntity;
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
    public List<DepartmentEntity> findAll() throws ServiceException {
        try {
            String sql = """
                    SELECT DISTINCT se.sorId, se.ShakId, se.PrioritizedEntitySpeciality1Id, se.PrioritizedEntitySpeciality1Name, se.PrioritizedEntitySpeciality2Id, se.PrioritizedEntitySpeciality2Name, se.PrioritizedEntitySpeciality3Id, se.PrioritizedEntitySpeciality3Name, se.PrioritizedEntitySpeciality4Id, se.PrioritizedEntitySpeciality4Name, se.PrioritizedEntitySpeciality5Id, se.PrioritizedEntitySpeciality5Name, se.PrioritizedEntitySpeciality6Id, se.PrioritizedEntitySpeciality6Name, se.PrioritizedEntitySpeciality7Id, se.PrioritizedEntitySpeciality7Name, se.PrioritizedEntitySpeciality8Id, se.PrioritizedEntitySpeciality8Name FROM SorEntity se
                    WHERE se.PrioritizedEntitySpeciality1Id IS NOT NULL OR se.PrioritizedEntitySpeciality2Id IS NOT NULL OR se.PrioritizedEntitySpeciality3Id IS NOT NULL OR se.PrioritizedEntitySpeciality4Id IS NOT NULL OR se.PrioritizedEntitySpeciality5Id IS NOT NULL OR se.PrioritizedEntitySpeciality6Id IS NOT NULL OR se.PrioritizedEntitySpeciality7Id IS NOT NULL  OR se.PrioritizedEntitySpeciality8Id IS NOT NULL
                    """;
            // Consider doing pagination if this is getting too heavy
            // OFFSET 0 ROWS
            // FETCH NEXT 100000 ROWS ONLY;


            return template.query(sql, rowMapper);

        } catch (Exception e) {
            throw new ServiceException("Failed to fetch all StamdataEntities", e);
        }
    }
}
