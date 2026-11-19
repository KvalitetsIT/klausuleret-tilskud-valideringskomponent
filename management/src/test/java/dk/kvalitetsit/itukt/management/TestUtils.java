package dk.kvalitetsit.itukt.management;

import dk.kvalitetsit.itukt.management.repository.entity.ClauseQuery;
import org.mockito.Mockito;

public class TestUtils {
    public static ClauseQuery queryMatcher(ClauseQuery expected) {
        return Mockito.argThat(actual -> actual != null
                && actual.getName().equals(expected.getName())
                && actual.getStatuses().equals(expected.getStatuses())
                && actual.isWithoutChildren() == expected.isWithoutChildren()
                && actual.isWithoutPrimaryChildren() == expected.isWithoutPrimaryChildren()
        );
    }
}
