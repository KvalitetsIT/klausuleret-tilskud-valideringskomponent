package dk.kvalitetsit.itukt.validation.repository.mapping;

import dk.kvalitetsit.itukt.validation.repository.DrugClauseView;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DrugClauseViewMapper implements RowMapper<DrugClauseView> {
    @Override
    public DrugClauseView mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DrugClauseView(
                new DrugClauseView.Laegemiddel(rs.getLong("d.DrugId")),
                new DrugClauseView.Klausulering(
                        rs.getString("k.Kode"),
                        rs.getString("k.Tekst")
                )
        );
    }
}
