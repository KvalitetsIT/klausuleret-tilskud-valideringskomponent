package dk.kvalitetsit.itukt.repository;

import dk.kvalitetsit.itukt.exceptions.ServiceException;
import dk.kvalitetsit.itukt.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.repository.entity.DrugEntity;
import dk.kvalitetsit.itukt.repository.entity.PackingEnitity;

import java.util.List;
import java.util.Optional;

public interface StamDataDao {

    Optional<DrugEntity> findDrugById(long drugId) throws ServiceException;

    Optional<PackingEnitity> findPackageById(long id) throws ServiceException;

    Optional<ClauseEntity> findClausesByID(long id) throws ServiceException;

    List<DrugEntity> findAllDrugs() throws ServiceException;

    List<PackingEnitity> findAllPackages() throws ServiceException;

    List<ClauseEntity> findAllClauses() throws ServiceException;
}
