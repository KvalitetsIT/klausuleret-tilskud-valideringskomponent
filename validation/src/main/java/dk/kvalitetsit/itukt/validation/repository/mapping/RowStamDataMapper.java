package dk.kvalitetsit.itukt.validation.repository.mapping;

import dk.kvalitetsit.itukt.validation.repository.StamDataEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowStamDataMapper implements RowMapper<StamDataEntity> {
    @Override
    public StamDataEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StamDataEntity(
                new StamDataEntity.Laegemiddel(rs.getLong("d.DrugId")),
                new StamDataEntity.Klausulering(
                        rs.getString("k.Kode"),
                        rs.getString("k.Tekst")
                )
        );
    }
}
