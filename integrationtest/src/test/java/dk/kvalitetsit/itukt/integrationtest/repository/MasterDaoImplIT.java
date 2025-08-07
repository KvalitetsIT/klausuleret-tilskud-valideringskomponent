package dk.kvalitetsit.itukt.integrationtest.repository;

import dk.kvalitetsit.itukt.integrationtest.BaseTest;
import dk.kvalitetsit.itukt.repository.MasterDaoImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MasterDaoImplIT extends BaseTest {

    @Autowired
    private MasterDaoImpl dao;

    @Test
    void testGetAllDrugs(){
        var drugs = this.dao.findAllDrugs();
        Assertions.assertFalse(drugs.isEmpty());
    }

    @Test
    void testGetAllPackages(){
        var packages = this.dao.findAllPackages();
        Assertions.assertFalse(packages.isEmpty());
    }

    @Test
    void testGetAllClauses(){
        var clauses = this.dao.findAllClauses();
        Assertions.assertFalse(clauses.isEmpty());
    }

    @Test
    void testFindDrugById(){
        var drugs = this.dao.findDrugById(184497L);
        Assertions.assertFalse(drugs.isEmpty());
    }

    @Test
    void testFindClausesByCode(){
        var clause = this.dao.findClausesByID(546L);
        Assertions.assertFalse(clause.isEmpty());
    }

    @Test
    void testFindPackageByItemNumber(){
        var packing = this.dao.findPackageById(7438234L);
        Assertions.assertFalse(packing.isEmpty());
    }


}
