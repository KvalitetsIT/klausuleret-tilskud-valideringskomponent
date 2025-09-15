package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.StamdataEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StamDataRepositoryImpl implements StamDataRepository {

    private final NamedParameterJdbcTemplate template;

    public StamDataRepositoryImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }


    @Override
    public List<StamdataEntity> findAll() throws ServiceException {
        try {
            String sql = """
                    SELECT d.DrugId, c.Kode, c.Tekst
                    FROM Laegemiddel d
                    JOIN Pakning p on p.DrugID = d.DrugID
                    JOIN Klausulering c ON c.kode = p.klausuleringskode
                    WHERE p.KlausuleringsKode IS NOT NULL
                    AND d.validfrom < NOW() AND d.validto > NOW()
                    AND p.validfrom < NOW() AND p.validto > NOW()
                    AND c.validfrom < NOW() AND c.validto > NOW()
                    """;

            var result = template.query(sql, (rs, rowNum) -> new StamdataEntity(
                    new StamdataEntity.Drug(rs.getLong("d.DrugId")),
                    List.of(new StamdataEntity.Clause(
                            rs.getString("c.Kode"),
                            rs.getString("c.Tekst")
                    ))
            ));

            return result.stream().collect(Collectors.toMap(
                    x -> x.drug().id(),
                    x -> x,
                    (x, y) -> {
                        var clauses = Stream.concat(x.clause().stream(), y.clause().stream()).collect(Collectors.toList());
                        return new StamdataEntity(x.drug(), clauses);
                    }
            )).values().stream().toList();
        } catch (Exception e) {
            throw new ServiceException("Failed to fetch all StamdataEntities", e);
        }
    }


}
