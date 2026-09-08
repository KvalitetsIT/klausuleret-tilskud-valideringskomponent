package dk.kvalitetsit.itukt.management.boundary.mapping.dsl.expression2dsl;

import dk.kvalitetsit.itukt.common.Mapper;

public class StringToDslValueMapper implements Mapper<String, String> {
    private static final String WILDCARD = "*";

    @Override
    public String map(String value) {
        return value.equals(WILDCARD) ? WILDCARD : "\"" + value + "\"";
    }
}
