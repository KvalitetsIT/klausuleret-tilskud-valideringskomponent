package dk.kvalitetsit.itukt.common.model;

public record Medication() {
    public record Form(String code) {
        public Form(String code) {
            this.code = code.toUpperCase();
        }
    }
}
