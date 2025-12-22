package dk.kvalitetsit.itukt.common;

import java.util.List;

/**
 * Generic interface for mapping one type of object (FROM) to another type (TO).
 * This provides a standard contract for conversion logic throughout the application.
 *
 * @param <FROM> The source type of the object to be converted.
 * @param <TO> The target type of the object after conversion.
 */
public interface Mapper<FROM, TO> {

    /**
     * Converts a single source object of type {@code FROM} into a target object of type {@code TO}.
     *
     * @param entry The single object to be mapped. Must not be null.
     * @return The mapped target object of type {@code TO}.
     */
    TO map(FROM entry);

    /**
     * Converts a list of source objects of type {@code FROM} into a corresponding list
     * of target objects of type {@code TO}.
     *
     * <p>This is a default implementation that uses the stream API to apply the
     * single-object {@link #map(Object)} method to every entry in the list.</p>
     *
     * @param entries The list of objects to be mapped. If the list is empty,
     * an empty list will be returned.
     * @return A new list containing the mapped target objects of type {@code TO}.
     */
    default List<TO> map(List<FROM> entries) {
        return entries.stream().map(this::map).toList();
    }
}
