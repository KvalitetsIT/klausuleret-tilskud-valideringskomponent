package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.repository.ClauseRepository;
import dk.kvalitetsit.itukt.validation.service.model.DataContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @InjectMocks
    private ValidationServiceImpl service;
    @Mock
    private ClauseRepository<Clause> dao;

    @Test
    void testCreate() {
        var request = new DataContext(Map.of("ALDER", List.of("20")));
        var result = service.validate(request);
        assertTrue(result);
    }

}
