package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.validation.stamdata.repository.entity.DepartmentEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;
import java.util.UUID;

public class SorEntityRepository {
    private final NamedParameterJdbcTemplate template;

    public SorEntityRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(DepartmentEntity sorEntity, Date fromDate, Date toDate, Date validFrom, Date validTo) {
        String sql = """
                INSERT INTO SorEntity (UniqueCurrentKey, SorId, ShakId, PrioritizedEntitySpeciality1Name, PrioritizedEntitySpeciality2Name, PrioritizedEntitySpeciality3Name, PrioritizedEntitySpeciality4Name, PrioritizedEntitySpeciality5Name, PrioritizedEntitySpeciality6Name, PrioritizedEntitySpeciality7Name, PrioritizedEntitySpeciality8Name, FromDate, ToDate, ValidFrom, ValidTo)
                VALUES (:uniqueCurrentKey, :sorId, :shakId, :prioritizedEntitySpeciality1Name, :prioritizedEntitySpeciality2Name, :prioritizedEntitySpeciality3Name, :prioritizedEntitySpeciality4Name, :prioritizedEntitySpeciality5Name, :prioritizedEntitySpeciality6Name, :prioritizedEntitySpeciality7Name, :prioritizedEntitySpeciality8Name, :fromDate, :toDate, :validFrom, :validTo)
                """;

        var params = new java.util.HashMap<String, Object>();
        params.put("uniqueCurrentKey", UUID.randomUUID().toString());
        params.put("sorId", sorEntity.sorId());
        params.put("shakId", sorEntity.ShakId());
        params.put("prioritizedEntitySpeciality1Name", sorEntity.PrioritizedEntitySpeciality1Name());
        params.put("prioritizedEntitySpeciality2Name", sorEntity.PrioritizedEntitySpeciality2Name());
        params.put("prioritizedEntitySpeciality3Name", sorEntity.PrioritizedEntitySpeciality3Name());
        params.put("prioritizedEntitySpeciality4Name", sorEntity.PrioritizedEntitySpeciality4Name());
        params.put("prioritizedEntitySpeciality5Name", sorEntity.PrioritizedEntitySpeciality5Name());
        params.put("prioritizedEntitySpeciality6Name", sorEntity.PrioritizedEntitySpeciality6Name());
        params.put("prioritizedEntitySpeciality7Name", sorEntity.PrioritizedEntitySpeciality7Name());
        params.put("prioritizedEntitySpeciality8Name", sorEntity.PrioritizedEntitySpeciality8Name());
        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        params.put("validFrom", validFrom);
        params.put("validTo", validTo);

        template.update(sql, params);
    }
}
