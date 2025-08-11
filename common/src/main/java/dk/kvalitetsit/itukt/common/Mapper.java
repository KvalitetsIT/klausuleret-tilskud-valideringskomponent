package dk.kvalitetsit.itukt.common;

public interface Mapper<FROM, TO> {
    TO map(FROM entry);
}
