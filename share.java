import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LdtFeatureTogglingFlagsTest {

    @Test
    void testEmptySuffix() {
        // Given
        List<FeatureDetails> featureDetails = Arrays.asList(new FeatureDetails("feature1"), new FeatureDetails("feature2"));

        // When
        Collection<FeatureDetails> result = LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "");

        // Then
        assertEquals(featureDetails, result);  // Result should be the same as input
    }

    @Test
    void testNullFeatureDetailsCollection() {
        // Given
        Collection<FeatureDetails> featureDetails = null;

        // When
        Collection<FeatureDetails> result = LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "suffix");

        // Then
        assertTrue(result.isEmpty());  // Should return an empty collection
    }

    @Test
    void testValidSuffixWithMatchingFeatureDetails() {
        // Given
        FeatureDetails fd1 = new FeatureDetails("feature1_suffix");
        FeatureDetails fd2 = new FeatureDetails("feature2_suffix");
        FeatureDetails fd3 = new FeatureDetails("feature3");

        List<FeatureDetails> featureDetails = Arrays.asList(fd1, fd2, fd3);

        // When
        Collection<FeatureDetails> result = LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "_suffix");

        // Then
        List<FeatureDetails> expected = Arrays.asList(new FeatureDetails("feature1"), new FeatureDetails("feature2"));
        assertEquals(expected, result);  // Only matching FeatureDetails should be returned with the suffix removed
    }

    @Test
    void testValidSuffixWithNoMatchingFeatureDetails() {
        // Given
        FeatureDetails fd1 = new FeatureDetails("feature1");
        FeatureDetails fd2 = new FeatureDetails("feature2");

        List<FeatureDetails> featureDetails = Arrays.asList(fd1, fd2);

        // When
        Collection<FeatureDetails> result = LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "_suffix");

        // Then
        assertTrue(result.isEmpty());  // No FeatureDetails match the suffix
    }

    @Test
    void testCaseSensitivityOfSuffix() {
        // Given
        FeatureDetails fd1 = new FeatureDetails("feature1_Suffix");
        FeatureDetails fd2 = new FeatureDetails("feature2_suffix");

        List<FeatureDetails> featureDetails = Arrays.asList(fd1, fd2);

        // When
        Collection<FeatureDetails> result = LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "_suffix");

        // Then
        assertEquals(1, result.size());  // Only the case-sensitive match should return
        assertTrue(result.contains(new FeatureDetails("feature2")));  // Only feature2 should be returned
    }

    @Test
    void testCorrectModificationOfUid() {
        // Given
        FeatureDetails fd1 = new FeatureDetails("feature1_suffix");

        List<FeatureDetails> featureDetails = Arrays.asList(fd1);

        // When
        Collection<FeatureDetails> result = LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "_suffix");

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(new FeatureDetails("feature1")));  // Suffix should be removed from the uid
    }

    @Test
    void testEnableMethodCalled() {
        // Given
        FeatureDetails fd1 = Mockito.spy(new FeatureDetails("feature1_suffix"));

        List<FeatureDetails> featureDetails = Arrays.asList(fd1);

        // When
        LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "_suffix");

        // Then
        Mockito.verify(fd1).enable();  // Ensure enable() was called
    }

    @Test
    void testDescriptionMethodCalled() {
        // Given
        FeatureDetails fd1 = Mockito.spy(new FeatureDetails("feature1_suffix"));

        List<FeatureDetails> featureDetails = Arrays.asList(fd1);

        // When
        LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "_suffix");

        // Then
        Mockito.verify(fd1).description();  // Ensure description() was called
    }

    @Test
    void testPerformanceWithLargeDataset() {
        // Given
        List<FeatureDetails> largeFeatureDetails = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            largeFeatureDetails.add(new FeatureDetails("feature" + i + "_suffix"));
        }

        // When
        long startTime = System.currentTimeMillis();
        Collection<FeatureDetails> result = LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(largeFeatureDetails, "_suffix");
        long endTime = System.currentTimeMillis();

        // Then
        assertEquals(100000, result.size());  // All features should be returned with suffix removed
        assertTrue(endTime - startTime < 1000);  // Ensure execution time is reasonable (<1s)
    }

    @Test
    void testEmptyFeatureDetailsCollection() {
        // Given
        List<FeatureDetails> featureDetails = new ArrayList<>();

        // When
        Collection<FeatureDetails> result = LdtFeatureTogglingFlags.filterFeatureToggleBySuffix(featureDetails, "_suffix");

        // Then
        assertTrue(result.isEmpty());  // Empty input should return empty output
    }
}