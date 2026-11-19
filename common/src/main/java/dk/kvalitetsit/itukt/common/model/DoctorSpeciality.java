package dk.kvalitetsit.itukt.common.model;

public record DoctorSpeciality(String value) {
    public DoctorSpeciality(String value) {
        this.value = value.toUpperCase();
    }
}