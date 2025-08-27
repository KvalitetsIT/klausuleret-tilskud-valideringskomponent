package dk.kvalitetsit.itukt.management.boundary.mapping.dto;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExpressionDtoModelMapperTest {

    @InjectMocks
    private ExpressionDtoModelMapper mapper;

    @BeforeEach
    void setup() {
        this.mapper = new ExpressionDtoModelMapper();
    }
    @Test
    void map() {
        assertEquals(MockFactory.EXPRESSION_1_MODEL, this.mapper.map(MockFactory.EXPRESSION_1_DTO));
    }
}