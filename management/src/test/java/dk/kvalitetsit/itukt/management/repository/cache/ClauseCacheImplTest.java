package dk.kvalitetsit.itukt.management.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Expression;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClauseCacheImplTest {

    @InjectMocks
    private ClauseCacheImpl cache;

    @Mock
    private ClauseRepositoryImpl concreteRepository;

    @Test
    void givenAnEmptyCache_whenGet_thenReturnEmpty() {
        var expected = Optional.empty();
        Assertions.assertEquals(expected, cache.get("WhatEver"));
    }

    @Test
    void givenAValidCronFormattedString_whenGetCron_theReturnTheSame() {
        var expected = "0 0 0 * * *";
        var cache = new ClauseCacheImpl(new CacheConfiguration(expected), null);

        Assertions.assertEquals(expected, cache.getCron());
    }

    @Test
    void givenAClauseFromRepository_whenLoad_thenReturnReturnSameClause() {

        var expected = new ClauseEntity(
                1L,
                UUID.randomUUID(),
                "CLAUSE",
                1,
                new ExpressionEntity.StringConditionEntity(
                        Expression.Condition.Field.AGE,
                        "value"
                )
        );

        Mockito.when(concreteRepository.readAll()).thenReturn(List.of(expected));
        cache.load();
        Mockito.verify(concreteRepository, Mockito.times(1)).readAll();

        Assertions.assertEquals(Optional.of(expected), cache.get("CLAUSE"));
    }
}