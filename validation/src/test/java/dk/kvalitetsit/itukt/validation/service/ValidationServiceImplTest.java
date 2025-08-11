package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.common.model.Operator;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryAdaptor;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @InjectMocks
    private ValidationServiceImpl service;
    @Mock
    private ClauseRepositoryAdaptor dao;

    @Test
    void testCreate() {

        var expression = new Expression.Condition("age", Operator.EQUAL, List.of("20"));

        var clause = new Clause("CHOL", null, expression);

        Mockito.when(dao.readAll()).thenReturn(List.of(clause));

        var request = new DataContext(Map.of("ALDER", List.of("20")));

        var result = service.validate(request);
        assertNotNull(result);
    }

}
