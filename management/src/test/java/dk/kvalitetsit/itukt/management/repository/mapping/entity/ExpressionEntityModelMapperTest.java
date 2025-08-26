package dk.kvalitetsit.itukt.management.repository.mapping.entity;

import dk.kvalitetsit.itukt.management.MockFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ExpressionEntityModelMapperTest {

    @InjectMocks
    private ExpressionEntityModelMapper mapper;


    @BeforeEach
    void setup() {
        this.mapper = new ExpressionEntityModelMapper();
    }

    @Test
    public void testMapping(){
        assertEquals(MockFactory.EXPRESSION_1_MODEL, mapper.map(MockFactory.EXPRESSION_1_ENTITY));
    }

}