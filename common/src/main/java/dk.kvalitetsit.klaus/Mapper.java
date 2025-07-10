package dk.kvalitetsit.klaus;

public interface Mapper<FROM, TO> {
    TO map(FROM entry);
}
