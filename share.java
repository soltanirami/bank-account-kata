import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        DealOriginationInput dealOriginationInput = DealOriginationInput.builder()
                .originationApplication(new OriginationApplication(/* initialize as needed */))
                .stiName("CIB Loan Deal")
                .shortDescription("Corporate loan for expansion")
                .syndicationType(new dealStructure_SyndicationType(/* initialize as needed */))
                .syndicationTypeCode("STC001")
                .confidentialCode("CONF12345")
                .dealOriginatorEntityInput(new DealOriginatorEntityInput(/* initialize as needed */))
                .originatingCountry("United States")
                .originatingCountryId("US")
                .globalAmountAtStructuration("100000000") // 100 million
                .mainCurrency("USD")
                .typeOfFinancing(new TypeOfFinancing(/* initialize as needed */))
                .typeOfFinancingCode("TF123")
                .useOfFunds("Business Expansion")
                .isSustainabilityLinked(true)
                .isGreen(false)
                .isEligibleToLeagueTables(true)
                .keyDeal(true)
                .newMoney("Yes")
                .maxTenor(60) // in months
                .maxTenorDurationUnit(TimeUnit.MONTHS)
                .closingMemoDate(LocalDate.of(2024, 12, 31))
                .pitch(Arrays.asList(new PitchInput(/* initialize as needed */)))
                .originatorGlossary(new OriginatorGlossaryInput(/* initialize as needed */))
                .bookingEntityAliasTypeCodes(Arrays.asList("BEAT001", "BEAT002"))
                .globalAmountAtSigning("90000000") // 90 million
                .finalAllocatedAmount("80000000") // 80 million
                .bookingDate(LocalDate.of(2024, 1, 15))
                .firstArchivedDate(LocalDate.of(2024, 6, 1))
                .subSectorCode("SSC100")
                .distributionSegmentCode("DSC200")
                .netNBI(500000)
                .rwa(300000)
                .regionCode("NA") // North America
                .cmgRequired(new CMGRequired(/* initialize as needed */))
                .cmgDecision(new CMGDecision(/* initialize as needed */))
                .build();

        // Print or use dealOriginationInput as needed
        System.out.println(dealOriginationInput);
    }
}