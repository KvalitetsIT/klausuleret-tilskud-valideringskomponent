package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.model.Clause;
import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.repository.ClauseDaoAdaptor;
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
    private ManagementServiceImpl helloService;
    @Mock
    private ClauseDaoAdaptor helloDao;

    @Test
    void testCreate() {

        var expression = new Expression.Condition("field", "operator", List.of());
        var input = new Clause("CHOL", null, Optional.of(1), expression);
        Mockito.when(helloDao.create(Mockito.any(Clause.class))).thenReturn(Optional.of(input));

        var result = helloService.create(input);
        assertNotNull(result);
    }

}
