package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.model.Operator;
import dk.kvalitetsit.klaus.repository.ClauseRepositoryAdaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

}
