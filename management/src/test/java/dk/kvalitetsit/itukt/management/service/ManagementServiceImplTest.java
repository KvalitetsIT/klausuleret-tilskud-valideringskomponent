package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ManagementServiceImplTest {
    @InjectMocks
    private ManagementServiceImpl service;
    @Mock
    private ClauseRepositoryAdaptor dao;

    @Test
    void testCreate() {
        var model = MockFactory.CLAUSE_1_MODEL;
        Mockito.when(dao.create(model.name(), model.expression())).thenReturn(model);
        var result = service.create(model.name(), model.expression());
        assertEquals(model, result);
    }

    @Test
    void testRead() {
        var model = MockFactory.CLAUSE_1_MODEL;
        Mockito.when(dao.read(model.uuid())).thenReturn(Optional.of(model));
        var result = service.read(model.uuid());
        assertEquals(Optional.of(model), result);
    }

    @Test
    void testReadAll() {
        var model = MockFactory.CLAUSE_1_MODEL;
        Mockito.when(dao.readAll()).thenReturn(List.of(model));
        var result = service.readAll();
        assertEquals(List.of(model), result);
    }


}
