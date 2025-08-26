package dk.kvalitetsit.itukt.management.boundary.mapping.dsl;

import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ClauseDslModelMapperTest {

    @InjectMocks
    private ClauseDslModelMapper mapper;

    @Test
    void map() {
        assertThat(mapper.map(MockFactory.CLAUSE_1_DSL))
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .isEqualTo(MockFactory.CLAUSE_1_DTO);
    }
}