package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.validation.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.validation.repository.entity.DrugEntity;
import dk.kvalitetsit.itukt.validation.repository.entity.PackingEnitity;

import java.util.List;
import java.util.Optional;

public interface MasterDao {

    Optional<DrugEntity> findDrugById(long drugId) throws ServiceException;

    Optional<PackingEnitity> findPackageById(long id) throws ServiceException;

    Optional<ClauseEntity> findClausesByID(long id) throws ServiceException;

    List<DrugEntity> findAllDrugs() throws ServiceException;

    List<PackingEnitity> findAllPackages() throws ServiceException;

    List<ClauseEntity> findAllClauses() throws ServiceException;
}
