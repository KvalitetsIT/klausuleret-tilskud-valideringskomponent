package dk.kvalitetsit.klaus.service;


import dk.kvalitetsit.klaus.model.Expression;
import dk.kvalitetsit.klaus.repository.ClauseDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ManagementServiceImplTest {
    @InjectMocks
    private ManagementServiceImpl helloService;
    @Mock
    private ClauseDao helloDao;

    @Test
    void testCreate() {
        var input = new Expression.Condition("field", "operator", List.of());
        Mockito.when(helloDao.create(Mockito.any(Expression.class))).thenReturn(input);

        var result = helloService.create(input);
        assertNotNull(result);
    }

}
