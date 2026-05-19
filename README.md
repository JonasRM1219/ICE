# StockTrack – Funktionelle Krav

## Formål

StockTrack er en applikation til privatinvestorer, der ønsker overblik over deres aktieportefølje og et estimat af den skat, de skal betale ved salg af aktier. Systemet tager højde for tab på andre aktier, der kan modregnes i gevinsten, samt hvilken kontotype investeringerne er placeret på.

---

## Domænets entiteter

**Portefølje**
En bruger har én portefølje, som indeholder alle brugerens beholdninger (holdings). Porteføljen kan gemmes og hentes igen, så data bevares mellem sessioner.

**Beholdning (Holding)**
En beholdning repræsenterer en investering i én bestemt aktie. Den har en ticker (aktiesymbol), et navn, et antal aktier, en gennemsnitlig købspris, en købsdato og en kontotype. En beholdning kan have tilknyttede salg og udbytter.

**Salg (Sale)**
Et salg registrerer at brugeren har solgt et antal aktier fra en beholdning. Et salg har en salgspris, en købspris, et antal aktier, en dato og en kontotype. Salget danner grundlag for beregning af realiseret gevinst eller tab.

**Udbytte (Dividend)**
Et udbytte er en udbetaling fra en aktie til brugeren. Det har et beløb og en dato, og indgår i den samlede afkastberegning.

**Kontotype (AccountType)**
En beholdning er knyttet til én af tre kontotyper:
- **Frie midler** – almindelig skattepligtig konto
- **Aktiesparekonto** – særlig lav beskatning op til et loft
- **Pension** – beskattes efter særlige regler

Kontotypen har afgørende betydning for hvilken skattesats der anvendes.

---

## Funktionelle krav

1. **Tilføj beholdning** – Brugeren skal kunne registrere en ny aktie i porteføljen med ticker, navn, antal aktier, gennemsnitlig købspris, købsdato og kontotype.

2. **Registrer salg** – Brugeren skal kunne registrere et salg af aktier fra en beholdning, med angivelse af salgspris, antal solgte aktier og dato.

3. **Registrer udbytte** – Brugeren skal kunne registrere et udbyttebeløb tilknyttet en bestemt beholdning.

4. **Skatteberegning** – Systemet skal beregne et estimeret skattebeløb for realiserede gevinster baseret på:
   - Realiseret gevinst (salgspris minus købspris)
   - Modregning af tab fra andre beholdninger eller salg
   - Kontotypen (frie midler, aktiesparekonto eller pension)

5. **Afkastoverblik** – Systemet skal vise det samlede afkast for porteføljen, herunder urealiseret gevinst/tab, realiseret gevinst og samlede udbytter.

6. **Historik** – Brugeren skal kunne se en oversigt over alle tidligere salg på tværs af porteføljen.

7. **Gem og hent data** – Brugerens porteføljedata skal kunne gemmes lokalt og hentes igen ved næste opstart.

8. **Slet data** – Brugeren skal kunne nulstille alle data i porteføljen.

---

## Vigtige regler

- Tab på salg i frie midler kan modregnes i gevinster fra andre salg i frie midler.
- Aktiesparekontoen og pensionskontoen beskattes efter egne regler og kan ikke sammenblandes med frie midler ved modregning.
- Skatteberegningen er et estimat og ikke juridisk rådgivning.
- En beholdning kan have nul eller flere salg og nul eller flere udbytter.
- En portefølje indeholder nul eller flere beholdninger.
