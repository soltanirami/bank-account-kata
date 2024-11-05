import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

class DealClientE2ETest {

    @Test
    void shouldExecuteQueryWithoutException_whenFindDealById() {
        // Assuming savedDeal is fetched from the database or another source after saving.
        DealOriginationInput savedDeal = fetchSavedDeal(); // replace with actual fetch method

        // Assertions for each field to ensure values are saved correctly
        assertEquals(LocalDate.of(2024, 10, 1), savedDeal.getBookingDate());
        assertEquals("LDT", savedDeal.getOriginationApplication());
        assertEquals("Corporate loan for expansion", savedDeal.getShortDescription());
        assertEquals("CLUB_DEAL", savedDeal.getSyndicationType());
        assertEquals("United States", savedDeal.getOriginatingCountry());
        assertEquals("US", savedDeal.getOriginatingCountryId());
        assertEquals("100000000", savedDeal.getGlobalAmountAtStructuration()); // 100 million
        assertEquals("USD", savedDeal.getMainCurrency());
        assertEquals("NEW", savedDeal.getTypeOfFinancing());
        assertEquals("EXPANSION", savedDeal.getUseOfFunds());
        assertTrue(savedDeal.getIsSustainabilityLinked());
        assertFalse(savedDeal.getIsGreen());
        assertTrue(savedDeal.getIsEligibleToLeagueTables());
        assertTrue(savedDeal.getKeyDeal());
        assertEquals("Yes", savedDeal.getNewMoney());
        assertEquals(60, savedDeal.getMaxTenor()); // in months
        assertEquals(TimeUnit.MONTH, savedDeal.getMaxTenorDurationUnit());
        assertEquals(LocalDate.of(2024, 12, 31), savedDeal.getClosingMemoDate());

        // Assertions for nested objects, lists, and enums
        assertNotNull(savedDeal.getOriginatorGlossary());
        assertEquals("CREDIT_APPROVED", savedDeal.getOriginatorGlossary().getOriginationStatus());
        assertEquals("FINANCING", savedDeal.getOriginatorGlossary().getTypeOfDeal());
        assertEquals(90.0, savedDeal.getOriginatorGlossary().getProbabilityOfSuccess());
        assertEquals("NON MATERIAL", savedDeal.getOriginatorGlossary().getEsgComment());

        assertEquals("90000000", savedDeal.getGlobalAmountAtSigning()); // 90 million
        assertEquals("80000000", savedDeal.getFinalAllocatedAmount()); // 80 million
        assertEquals(LocalDate.of(2024, 11, 1), savedDeal.getFirstArchivedDate());
        assertEquals(50000, savedDeal.getNetNBI());
        assertEquals(300000, savedDeal.getRwa());
        assertEquals("NA", savedDeal.getRegionCode()); // North America
        assertEquals(CMGRequired.NOT_APPLICABLE, savedDeal.getCmgRequired());
        assertEquals(CMGDecision.NOT_APPLICABLE, savedDeal.getCmgDecision());

        // Additional assertions for list values or other complex fields
        assertNotNull(savedDeal.getBookingEntityAliasTypeCodes());
        assertTrue(savedDeal.getBookingEntityAliasTypeCodes().contains("BEAT001"));
        assertTrue(savedDeal.getBookingEntityAliasTypeCodes().contains("BEAT002"));
        
        // Print or log to verify the savedDeal, if needed
        System.out.println(savedDeal);
    }

    // Mock or actual method to fetch saved deal
    private DealOriginationInput fetchSavedDeal() {
        // Replace with actual logic to fetch the deal from storage or service
        return null;
    }
}