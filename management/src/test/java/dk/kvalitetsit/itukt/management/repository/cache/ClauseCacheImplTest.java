package dk.kvalitetsit.itukt.management.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.management.repository.entity.Field;
import dk.kvalitetsit.itukt.management.repository.ClauseRepositoryImpl;
import dk.kvalitetsit.itukt.management.repository.entity.ClauseEntity;
import dk.kvalitetsit.itukt.management.repository.entity.ExpressionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClauseCacheImplTest {

    @InjectMocks
    private ClauseCacheImpl cache;

    @Mock
    private ClauseRepositoryImpl concreteRepository;

    @Test
    void givenAnEmptyCache_whenGet_thenReturnEmpty() {
        var expected = Optional.empty();
        assertEquals(expected, cache.get("WhatEver"));
    }

    @Test
    void givenAValidCronFormattedString_whenGetCron_theReturnTheSame() {
        var expected = "0 0 0 * * *";
        var cache = new ClauseCacheImpl(new CacheConfiguration(expected), null);

        assertEquals(expected, cache.getCron());
    }

    @Test
    void givenAClauseFromRepository_whenLoad_thenReturnReturnSameClause() {

        var expected = new ClauseEntity(
                1L,
                UUID.randomUUID(),
                "CLAUSE",
                1,
                "Message",
                new ExpressionEntity.StringConditionEntity(
                        Field.AGE,
                        "value"
                )
        );

        Mockito.when(concreteRepository.readAll()).thenReturn(List.of(expected));
        cache.load();
        Mockito.verify(concreteRepository, Mockito.times(1)).readAll();

        assertEquals(Optional.of(expected), cache.get("CLAUSE"));
    }

    @Test
    void getByErrorCode_WhenNoClauseMatchesErrorCode_ReturnsEmptyOptional() {
        var result = cache.getByErrorCode(999);

        assertFalse(result.isPresent(), "Expected empty Optional when no clause matches the error code");
    }

    @Test
    void getByErrorCode_WhenClauseMatchesErrorCode_ReturnsClause() {
        var existingClause1 = new ClauseEntity(null, null, "test1", 111, "message1",null);
        var existingClause2 = new ClauseEntity(null, null, "test2", 222, "message2", null);
        Mockito.when(concreteRepository.readAll()).thenReturn(List.of(existingClause1, existingClause2));
        cache.load();

        var result = cache.getByErrorCode(existingClause2.errorCode());

        assertTrue(result.isPresent(), "Expected clause to be found for matching error code");
        assertEquals(existingClause2, result.get(), "Expected returned clause to match the one with the given error code");
    }
}