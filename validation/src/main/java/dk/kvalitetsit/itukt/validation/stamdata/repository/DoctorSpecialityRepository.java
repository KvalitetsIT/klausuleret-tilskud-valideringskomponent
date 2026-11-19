package dk.kvalitetsit.itukt.validation.stamdata.repository;

import dk.kvalitetsit.itukt.common.model.DoctorSpeciality;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class DoctorSpecialityRepository implements Repository<DoctorSpeciality> {

    private final DataClassRowMapper<DoctorSpeciality> rowMapper;

    private final NamedParameterJdbcTemplate template;

    public DoctorSpecialityRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        rowMapper = DataClassRowMapper.newInstance(DoctorSpeciality.class);
    }

    @Override
    public List<DoctorSpeciality> fetchAll() {
        String sql = String.join(" UNION ",
                selectSpecialitiesSql("Speciale1"),
                selectSpecialitiesSql("Speciale2"),
                selectSpecialitiesSql("Speciale3")
        );
        try {
            return template.query(sql, rowMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all doctor specialities", e);
        }
    }

    private String selectSpecialitiesSql(String specialityColumn) {
        return """
                SELECT DISTINCT %s as value
                FROM Autorisation3
                WHERE %s IS NOT NULL AND %s <> ''
                    AND ValidFrom < NOW() AND ValidTo > NOW()
                """
                .formatted(specialityColumn, specialityColumn, specialityColumn);
    }
}
