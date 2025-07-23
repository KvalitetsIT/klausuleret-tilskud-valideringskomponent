package dk.kvalitetsit.klaus.repository;

import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.repository.entity.ClauseEntity;
import dk.kvalitetsit.klaus.repository.entity.DrugEntity;
import dk.kvalitetsit.klaus.repository.entity.PackingEnitity;
import dk.kvalitetsit.klaus.repository.mapper.ClauseRowMapper;
import dk.kvalitetsit.klaus.repository.mapper.DrugRowMapper;
import dk.kvalitetsit.klaus.repository.mapper.PackageRowMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MasterDaoImpl implements MasterDao {

    private final DrugRowMapper drugRowMapper = new DrugRowMapper();
    private final PackageRowMapper packageRowMapper = new PackageRowMapper();
    private final ClauseRowMapper clauseRowMapper = new ClauseRowMapper();

    @Qualifier("masterJdbcTemplate")
    private final NamedParameterJdbcTemplate template;

    public MasterDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
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
    public Optional<PackingEnitity> findPackageById(long id) throws ServiceException {
        try {
            String sql = "SELECT * FROM Pakning WHERE pakningpid = :packagePID";
            return Optional.ofNullable(template.queryForObject(
                    sql,
                    new MapSqlParameterSource("packagePID", id),
                    packageRowMapper
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new ServiceException("Failed to fetch package by id", e);
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




}
