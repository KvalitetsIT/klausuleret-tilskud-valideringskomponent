package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.validation.repository.StamDataRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StamDataRepositoryImplIT extends BaseTest {

    private final StamDataRepositoryImpl dao;

    StamDataRepositoryImplIT(@Autowired StamDataRepositoryImpl dao) {
        this.dao = dao;
    }

    @Test
    void testFindAll(){
        var entries = this.dao.findAll();
        Assertions.assertFalse(entries.isEmpty());
    }
}
