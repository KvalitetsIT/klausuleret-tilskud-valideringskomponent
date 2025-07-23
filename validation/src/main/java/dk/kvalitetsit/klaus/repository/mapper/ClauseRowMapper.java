package dk.kvalitetsit.klaus.repository.mapper;

import dk.kvalitetsit.klaus.repository.entity.ClauseEntity;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClauseRowMapper implements RowMapper<ClauseEntity> {

    @Override
    public ClauseEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ClauseEntity(
                rs.getString("Kode"),
                rs.getTimestamp("ValidTo").toLocalDateTime(),
                rs.getLong("KlausuleringPID"),
                rs.getString("KortTekst"),
                rs.getTimestamp("LastReplicated"),
                rs.getString("Tekst"),
                rs.getTimestamp("ValidFrom").toLocalDateTime()
        );
    }
}

