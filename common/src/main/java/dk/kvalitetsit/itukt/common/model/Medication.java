package dk.kvalitetsit.itukt.common.model;

public record Medication() {
    public record Form(String code) {
        public Form(String code) {
            this.code = code.toUpperCase();
        }
    }

    public record ATC(String code) {
        public ATC(String code) {
            this.code = code.toUpperCase();
        }
    }

    public record Route(String code) {
        public Route(String code) {
            this.code = code.toUpperCase();
        }
    }
}
