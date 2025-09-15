package dk.kvalitetsit.itukt.validation.repository;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;
import dk.kvalitetsit.itukt.common.model.StamdataEntity;

import java.util.List;

public interface StamDataRepository  {

    List<StamdataEntity> findAll() throws ServiceException;
}
