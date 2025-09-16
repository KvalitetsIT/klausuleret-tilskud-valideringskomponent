package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.validation.repository.entity.DrugEntity;
import dk.kvalitetsit.itukt.validation.repository.entity.PackingEnitity;
import dk.kvalitetsit.itukt.validation.repository.entity.StamdataEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StamDataRepositoryImpl implements StamDataRepository {

    private final DataClassRowMapper<DrugEntity> drugRowMapper;
    private final DataClassRowMapper<PackingEnitity> packageRowMapper;
    private final DataClassRowMapper<ClauseEntity> clauseRowMapper;

    private final NamedParameterJdbcTemplate template;

    public StamDataRepositoryImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        drugRowMapper = DataClassRowMapper.newInstance(DrugEntity.class);
        clauseRowMapper = DataClassRowMapper.newInstance(ClauseEntity.class);
        packageRowMapper = DataClassRowMapper.newInstance(PackingEnitity.class);
    }

    @Override
    public Optional<DrugEntity> findDrugById(long drugId) throws ServiceException {
        try {
            String sql = "SELECT * FROM Laegemiddel WHERE laegemiddelpid = :drugPID";
            return Optional.ofNullable(template.queryForObject(
                    sql,
                    new MapSqlParameterSource("drugPID", drugId),
                    drugRowMapper
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new ServiceException("Failed to fetch drug by ID", e);
        }
    }

    @Override
    public List<PackingEnitity> findPackagesByDrugId(long drugId) throws ServiceException {
        try {
            String sql = "SELECT * FROM Pakning WHERE drugid = :drugId";
            return template.query(
                    sql,
                    new MapSqlParameterSource("drugId", drugId),
                    packageRowMapper
            );
        } catch (Exception e) {
            throw new ServiceException("Failed to fetch package by drugId", e);
        }
    }

    @Override
    public Optional<ClauseEntity> findClausesByID(long id) throws ServiceException {
        try {
            String sql = "SELECT * FROM Klausulering WHERE KlausuleringPID = :id";
            return Optional.ofNullable(template.queryForObject(
                    sql,
                    new MapSqlParameterSource("id", id),
                    clauseRowMapper
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new ServiceException("Failed to fetch clause by id", e);
        }
    }

    @Override
    public List<DrugEntity> findAllDrugs() throws ServiceException {
        try {
            String sql = "SELECT * FROM Laegemiddel";
            return template.query(sql, drugRowMapper);
        } catch (Exception e) {
            throw new ServiceException("Failed to fetch all drugs", e);
        }
    }

    @Override
    public List<PackingEnitity> findAllPackages() throws ServiceException {
        try {
            String sql = "SELECT * FROM Pakning";
            return template.query(sql, packageRowMapper);
        } catch (Exception e) {
            throw new ServiceException("Failed to fetch all packages", e);
        }
    }

    @Override
    public List<ClauseEntity> findAllClauses() throws ServiceException {
        try {
            String sql = "SELECT * FROM Klausulering";
            return template.query(sql, clauseRowMapper);
        } catch (Exception e) {
            throw new ServiceException("Failed to fetch all clauses", e);
        }
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
