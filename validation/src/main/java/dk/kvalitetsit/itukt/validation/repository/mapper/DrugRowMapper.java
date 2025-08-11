package dk.kvalitetsit.itukt.validation.repository.mapper;

import dk.kvalitetsit.itukt.validation.repository.entity.DrugEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DrugRowMapper implements RowMapper<DrugEntity> {

    @Override
    public DrugEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DrugEntity(
                rs.getLong("drugid"),
                rs.getTimestamp("validto").toLocalDateTime(),
                rs.getString("administrationsvejkode"),
                rs.getString("alfabetsekvensplads"),
                rs.getString("atckode"),
                rs.getString("atctekst"),
                rs.getString("datoforafregistraflaegemiddel"),
                rs.getByte("dosisdispenserbar"),
                rs.getString("drugname"),
                rs.getString("formkode"),
                rs.getString("formtekst"),
                rs.getString("genordinering"),
                rs.getString("karantaenedato"),
                rs.getString("kodeforyderligereformoplysn"),
                rs.getString("laegemiddelformtekst"),
                rs.getLong("laegemiddelpid"),
                rs.getString("laegemidletssubstitutionsgruppe"),
                rs.getTimestamp("lastreplicated"),
                rs.getLong("mtindehaverkode"),
                rs.getLong("repraesentantdistributoerkode"),
                rs.getString("specnummer"),
                rs.getString("styrkeenhed"),
                rs.getBigDecimal("styrkenumerisk"),
                rs.getString("styrketekst"),
                rs.getString("substitution"),
                rs.getByte("trafikadvarsel"),
                rs.getTimestamp("validfrom").toLocalDateTime(),
                rs.getString("varedeltype"),
                rs.getString("varetype")
        );
    }
}