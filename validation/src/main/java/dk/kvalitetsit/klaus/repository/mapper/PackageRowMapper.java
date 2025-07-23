package dk.kvalitetsit.klaus.repository.mapper;

import dk.kvalitetsit.klaus.repository.entity.PackingEnitity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRowMapper implements RowMapper<PackingEnitity> {
    @Override
    public PackingEnitity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PackingEnitity(
                rs.getTimestamp("validto").toLocalDateTime(),
                rs.getLong("varenummer"),
                rs.getString("alfabetsekvensnr"),
                rs.getBigDecimal("antaldddprpakning"),
                rs.getLong("antaldelpakninger"),
                rs.getString("beregningskodeairegpris"),
                rs.getString("datoforsenesteprisaendring"),
                rs.getByte("dosisdispenserbar"),
                rs.getLong("drugid"),
                rs.getString("emballagetypekode"),
                rs.getByte("faerdigfremstillingsgebyr"),
                rs.getString("klausuleringskode"),
                rs.getTimestamp("LastReplicated"),
                rs.getString("medicintilskudskode"),
                rs.getString("opbevaringsbetingelser"),
                rs.getLong("opbevaringstid"),
                rs.getLong("opbevaringstidnumerisk"),
                rs.getString("oprettelsesdato"),
                rs.getByte("pakningoptagetitilskudsgruppe"),
                rs.getLong("pakningpid"),
                rs.getLong("pakningsdistributoer"),
                rs.getBigDecimal("pakningsstoerrelsenumerisk"),
                rs.getString("pakningsstoerrelsesenhed"),
                rs.getString("pakningsstoerrelsetekst"),
                rs.getString("udgaaetdato"),
                rs.getString("udleveringsbestemmelse"),
                rs.getString("udleveringspeciale"),
                rs.getTimestamp("validfrom").toLocalDateTime(),
                rs.getLong("varenummerdelpakning")
        );
    }
}