package dk.kvalitetsit.itukt.integrationtest.repository.stamdata;

import dk.kvalitetsit.itukt.common.model.DoctorSpeciality;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Optional;

public class Autorisation3Repository {
    private final NamedParameterJdbcTemplate template;

    public Autorisation3Repository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(Optional<DoctorSpeciality> speciality1, Optional<DoctorSpeciality> speciality2, Optional<DoctorSpeciality> speciality3, Date validFrom, Date validTo) {
        String authNumber = RandomStringUtils.insecure().nextAlphanumeric(10);

        String sql = """
                INSERT INTO Autorisation3 (Autorisationsnummer, CPR, Fornavn, Efternavn, UddannelsesKode, Speciale1, Speciale2, Speciale3, ValidFrom, ValidTo)
                VALUES (:autorisationsnummer, :cpr, :fornavn, :efternavn, :uddannelseskode, :speciale1, :speciale2, :speciale3, :validFrom, :validTo)
                """;
        var params = new MapSqlParameterSource()
                .addValue("autorisationsnummer", authNumber)
                .addValue("cpr", "0000000000")
                .addValue("fornavn", "test")
                .addValue("efternavn", "test")
                .addValue("uddannelseskode", "0000")
                .addValue("speciale1", speciality1.map(DoctorSpeciality::value).orElse(null))
                .addValue("speciale2", speciality2.map(DoctorSpeciality::value).orElse(null))
                .addValue("speciale3", speciality3.map(DoctorSpeciality::value).orElse(null))
                .addValue("validFrom", validFrom)
                .addValue("validTo", validTo);

        template.update(sql, params);
    }
}
