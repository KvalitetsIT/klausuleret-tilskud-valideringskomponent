package dk.kvalitetsit.itukt.management.repository.mapping.model;

import dk.kvalitetsit.itukt.common.Mapper;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.MockFactory;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class ClauseModelEntityMapperTest {

    @InjectMocks
    private ClauseModelEntityMapper mapper;

    @Mock
    private Mapper<Expression, ExpressionEntity> mock;

    @BeforeEach
    void setup(){
        this.mapper = new ClauseModelEntityMapper(mock);
        Mockito.when(mock.map(MockFactory.EXPRESSION_1_MODEL)).thenReturn(MockFactory.EXPRESSION_1_ENTITY);
    }

    @Test
    void map() {
        Assertions.assertEquals(MockFactory.CLAUSE_1_ENTITY, mapper.map(MockFactory.CLAUSE_1_MODEL));
    }
}