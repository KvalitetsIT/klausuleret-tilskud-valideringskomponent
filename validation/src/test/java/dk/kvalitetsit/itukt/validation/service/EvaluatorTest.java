package dk.kvalitetsit.itukt.validation.service;


import dk.kvalitetsit.itukt.validation.MockFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EvaluatorTest {
    @InjectMocks
    private Evaluator evaluator;

    @Test
    void testEval() {
        var result = evaluator.eval(MockFactory.expressionModel, MockFactory.ctx);
        Assertions.assertTrue(result);
    }
}