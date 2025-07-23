package dk.kvalitetsit.klaus.repository;

import dk.kvalitetsit.klaus.exceptions.ServiceException;
import dk.kvalitetsit.klaus.repository.entity.ClauseEntity;
import dk.kvalitetsit.klaus.repository.entity.DrugEntity;
import dk.kvalitetsit.klaus.repository.entity.PackingEnitity;

import java.util.List;
import java.util.Optional;

public interface MasterDao {

    Optional<DrugEntity> findDrugById(long drugId) throws ServiceException;

    Optional<PackingEnitity> findPackageById(long id) throws ServiceException;

    Optional<ClauseEntity> findClassificationByID(long id) throws ServiceException;

    List<DrugEntity> findAllDrugs() throws ServiceException;

    List<PackingEnitity> findAllPackages() throws ServiceException;

    List<ClauseEntity> findAllClauses() throws ServiceException;
}
