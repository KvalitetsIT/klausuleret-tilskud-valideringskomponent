package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClauseDtoModelMapperTest {
    @InjectMocks
    private ClauseDtoModelMapper mapper;

    @Mock
    private Mapper<org.openapitools.model.Expression, Expression> mock;

    @BeforeEach
    void setup() {
        this.mapper = new ClauseDtoModelMapper(mock);
        Mockito.when(mock.map(MockFactory.CLAUSE_1_OUTPUT.getExpression())).thenReturn(MockFactory.CLAUSE_1_MODEL.expression());
    }

    @Test
    void map() {
        assertThat(this.mapper.map(MockFactory.CLAUSE_1_INPUT))
                .usingRecursiveComparison()
                .ignoringFields("uuid")
                .isEqualTo(MockFactory.CLAUSE_1_MODEL);
    }
}