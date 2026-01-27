package dk.kvalitetsit.itukt.management.repository.cache;

import dk.kvalitetsit.itukt.common.configuration.CacheConfiguration;
import dk.kvalitetsit.itukt.common.model.Clause;
import dk.kvalitetsit.itukt.common.model.Field;
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
class ActiveClauseCacheImplTest {

    @InjectMocks
    private ActiveClauseCacheImpl cache;

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
        var cache = new ActiveClauseCacheImpl(new CacheConfiguration(expected), null);

        assertEquals(expected, cache.getCron());
    }

    @Test
    void givenAnActiveAndInactiveClauseFromRepository_whenLoad_thenReturnActiveClause() {
        var activeClause = new ClauseEntity(
                1L,
                UUID.randomUUID(),
                "CLAUSE1",
                Clause.Status.ACTIVE,
                1,
                "Message",
                new ExpressionEntity.StringConditionEntity(
                        Field.AGE,
                        "value"
                ),
                Optional.empty()
        );
        var inactiveClause = new ClauseEntity(
                2L,
                UUID.randomUUID(),
                "CLAUSE2",
                Clause.Status.INACTIVE,
                2,
                "Message",
                new ExpressionEntity.StringConditionEntity(
                        Field.AGE,
                        "value"
                ),
                Optional.empty()
        );
        Mockito.when(concreteRepository.readLatestVersions()).thenReturn(List.of(activeClause, inactiveClause));

        cache.load();

        Mockito.verify(concreteRepository, Mockito.times(1)).readLatestVersions();
        assertEquals(Optional.of(activeClause), cache.get(activeClause.name()));
        assertEquals(Optional.empty(), cache.get(inactiveClause.name()));
    }

    @Test
    void getByErrorCode_WhenNoClauseMatchesErrorCode_ReturnsEmptyOptional() {
        var result = cache.getByErrorCode(999);

        assertFalse(result.isPresent(), "Expected empty Optional when no clause matches the error code");
    }

    @Test
    void getByErrorCode_WhenClauseMatchesErrorCode_ReturnsClause() {
        var existingClause1 = new ClauseEntity(null, null, "test1", Clause.Status.ACTIVE, 111, "message1", null, Optional.empty());
        var existingClause2 = new ClauseEntity(null, null, "test2", Clause.Status.ACTIVE, 222, "message2", null, Optional.empty());
        Mockito.when(concreteRepository.readLatestVersions()).thenReturn(List.of(existingClause1, existingClause2));
        cache.load();

        var result = cache.getByErrorCode(existingClause2.errorCode());

        assertTrue(result.isPresent(), "Expected clause to be found for matching error code");
        assertEquals(existingClause2, result.get(), "Expected returned clause to match the one with the given error code");
    }
}