package dk.kvalitetsit.itukt.common.service;

import dk.kvalitetsit.itukt.common.exceptions.ServiceException;

import java.util.List;

public interface CommonClauseService {
    List<Long> getClauseIdsByErrorCodes(List<Integer> errorCodes) throws ServiceException;
}
