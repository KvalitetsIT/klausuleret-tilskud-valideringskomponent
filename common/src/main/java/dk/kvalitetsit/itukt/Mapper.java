package dk.kvalitetsit.itukt;

public interface Mapper<FROM, TO> {
    TO map(FROM entry);
}
