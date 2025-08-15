# **Leverancebeskrivelse**
**Komponent:** Klausuleret Tilskud – Valideringskomponent  
**Version:** 1.0  
**Dato:** 14. august 2025  
**Udarbejdet af:** Jonas Holsting @ KvalitetsIT

---

## **1. Formål**
De forretningsmæssige forudsætninger for validering af klausuleret tilskud baserer sig på en afvejning af
ønsker til validering og hvilke data det er muligt at få adgang til. Idet det er ønsket at valideringen primært
sker centralt, og f.eks. ikke i de enkelte LPS og EPJ-systemer, er det oplagt at FMK er det centrale system.

Validering af tidligere medicinsk behandling ud fra data i FMK. Grundlaget for denne validering er
lægemiddelordinationer, hvor der er tilknyttet mindst én recept og effektuering, dvs. at lægemidlet er
udleveret på apoteket. Eventuelt kan effektueringer være oprettet ved udlevering i forbindelse med f.eks.
ambulant behandling på sygehuset, af læge eller speciallæge mm. Disse kan indgå i valideringen, men udgør
en forholdsvis lille del af effektueringerne

Patientens alder kan simpelt valideres, ud fra CPR-nummer og for personer med erstatnings-cprnummer
(eCPR-nummer) ud fra obligatoriske data i eCPR-registret.

Indikationen, som angives på FMK, er indikationskode fra Medicinpriser, som bl.a. også anvendes ved
receptudstedelse. Disse Indikationer er ikke nødvendigvis det samme som diagnoser (på trods af at dette
fremgår af Sundhedsvæsenets begrebsbase), f.eks. siger indikationen “mod åndenød” ikke noget om
hvorvidt patienten har KOL, astma, hjerteproblemer osv.
Desuden kan indikation angives som en fritekst, når der er behov for alternativer til kode og tekst fra
Medicinpriser, og kan dermed ikke sikkert valideres.

---

## **2. Omfang**
Dokumentet omfatter:
- Beskrivelse af leverancen  

