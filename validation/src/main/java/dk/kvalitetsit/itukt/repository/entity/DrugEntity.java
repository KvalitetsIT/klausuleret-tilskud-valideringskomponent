package dk.kvalitetsit.itukt.repository.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record DrugEntity(
        Long drugid,
        LocalDateTime validto,
        String administrationsvejkode,
        String alfabetsekvensplads,
        String atckode,
        String atctekst,
        String datoforafregistraflaegemiddel,
        byte dosisdispenserbar,
        String drugname,
        String formkode,
        String formtekst,
        String genordinering,
        String karantaenedato,
        String kodeforyderligereformoplysn,
        String laegemiddelformtekst,
        Long laegemiddelpid,
        String laegemidletssubstitutionsgruppe,
        Timestamp lastreplicated,
        Long mtindehaverkode,
        Long repraesentantdistributoerkode,
        String specnummer,
        String styrkeenhed,
        BigDecimal styrkenumerisk,
        String styrketekst,
        String substitution,
        byte trafikadvarsel,
        LocalDateTime validfrom,
        String varedeltype,
        String varetype
) {}

