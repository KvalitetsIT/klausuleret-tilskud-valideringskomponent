package dk.kvalitetsit.itukt.common;

import java.util.List;

public interface Mapper<FROM, TO> {
    TO map(FROM entry);

    default List<TO> map(List<FROM> entries) {
        return entries.stream().map(this::map).toList();
    }
}
