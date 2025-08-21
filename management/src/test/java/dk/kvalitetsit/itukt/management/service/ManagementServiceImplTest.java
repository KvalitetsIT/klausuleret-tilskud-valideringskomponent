package dk.kvalitetsit.itukt.management.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ManagementServiceImplTest {
    @InjectMocks
    private ManagementServiceImpl service;
    @Mock
    private ClauseRepositoryAdaptor dao;

    @Test
    void testCreate() {
        var expression = new Expression.Condition("field", Operator.EQUAL, List.of());
        var input = new Clause("CHOL", null, expression);
        Mockito.when(dao.create(Mockito.any(Clause.class))).thenReturn(Optional.of(input));

        var result = service.create(input);
        assertNotNull(result);
    }

    @Test
    void testRead() {
        var model = MockFactory.clauseModel;
        Mockito.when(dao.read(model.uuid().get())).thenReturn(Optional.of(model));
        var result = service.read(model.uuid().get());
        assertEquals(Optional.of(model), result);
    }

    @Test
    void testReadAll() {
        var model = MockFactory.clauseModel;
        Mockito.when(dao.readAll()).thenReturn(List.of(model));
        var result = service.readAll();
        assertEquals(List.of(model), result);
    }


}
