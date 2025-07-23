package dk.kvalitetsit.klaus.repository.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record DrugEntity(
        long drugID,
        LocalDateTime validTo,
        String administrationRouteCode,
        String sequencePlace,
        String atcCode,
        String atcText,
        String registrationDate,
        byte doseDispenser,
        String drugName,
        String formCode,
        String formText,
        String genderSpecific,
        String quarantineDate,
        String additionalFormInfoCode,
        String drugFormText,
        long drugPID,
        String substitutionGroup,
        Timestamp lastReplicated,
        long ownerCode,
        long distributorCode,
        String specNumber,
        String strengthUnit,
        BigDecimal strengthNumeric,
        String strengthText,
        String substitution,
        byte trafficWarning,
        LocalDateTime validFrom,
        String itemType,
        String type
) {}

