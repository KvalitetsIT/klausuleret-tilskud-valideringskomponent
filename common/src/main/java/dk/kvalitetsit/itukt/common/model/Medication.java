package dk.kvalitetsit.itukt.common.model;

public record Medication() {
    public record Form(String code) {
        public Form(String code) {
            this.code = code.toUpperCase();
        }
    }

    public record ATC(String code, String text) {
        public ATC(String code, String text) {
            this.code = code.toUpperCase();
            this.text = text;
        }
    }

    public record Route(String code) {
        public Route(String code) {
            this.code = code.toUpperCase();
        }
    }
}
