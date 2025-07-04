package dk.kvalitetsit.klaus.boundary.mapping;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static dk.kvalitetsit.klaus.MockFactory.clause;
import static dk.kvalitetsit.klaus.MockFactory.dsl;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DslMapperTest {

    private final DslMapper mapper = new DslMapper();

    @Test
    void testDSLToModel() {
        assertEquals(clause, mapper.map(dsl));
    }
}
