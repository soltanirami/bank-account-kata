import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ComparisonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Compares two objects and returns a list of flattened mismatches.
     * 
     * @param ldtObject the first object to compare
     * @param csObject the second object to compare
     * @param ignoreFields a set of paths (with wildcards) to ignore
     * @return a list of mismatches with the full path to the differing fields
     */
    public static <T> List<String> compareObjects(T ldtObject, T csObject, Set<String> ignoreFields) {
        Set<Pattern> ignorePatterns = convertToPatterns(ignoreFields);

        Map<String, Object> ldtMap = objectMapper.convertValue(ldtObject, Map.class);
        Map<String, Object> csMap = objectMapper.convertValue(csObject, Map.class);

        List<String> mismatches = new ArrayList<>();
        compareMapsUsingGuava(ldtMap, csMap, "", mismatches, ignorePatterns);

        return mismatches;
    }

    private static void compareMapsUsingGuava(Map<String, Object> ldtMap, Map<String, Object> csMap, 
                                              String path, List<String> mismatches, Set<Pattern> ignorePatterns) {
        MapDifference<String, Object> difference = Maps.difference(ldtMap, csMap);

        difference.entriesDiffering().forEach((key, valueDifference) -> {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            if (isPathIgnored(currentPath, ignorePatterns)) return;

            Object leftValue = valueDifference.leftValue();
            Object rightValue = valueDifference.rightValue();

            if (leftValue instanceof Map && rightValue instanceof Map) {
                compareMapsUsingGuava((Map<String, Object>) leftValue, (Map<String, Object>) rightValue, currentPath, mismatches, ignorePatterns);
            } else if (leftValue instanceof List && rightValue instanceof List) {
                compareLists((List<?>) leftValue, (List<?>) rightValue, currentPath, mismatches, ignorePatterns);
            } else {
                mismatches.add(String.format("Field '%s' mismatch - LDT: %s, CS: %s", currentPath, leftValue, rightValue));
            }
        });

        difference.entriesOnlyOnLeft().forEach((key, value) -> {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            if (!isPathIgnored(currentPath, ignorePatterns)) {
                mismatches.add(String.format("Field '%s' only in LDT: %s", currentPath, value));
            }
        });

        difference.entriesOnlyOnRight().forEach((key, value) -> {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            if (!isPathIgnored(currentPath, ignorePatterns)) {
                mismatches.add(String.format("Field '%s' only in CS: %s", currentPath, value));
            }
        });
    }

    private static void compareLists(List<?> ldtList, List<?> csList, String path, List<String> mismatches, Set<Pattern> ignorePatterns) {
        if (ldtList.size() != csList.size()) {
            if (!isPathIgnored(path, ignorePatterns)) {
                mismatches.add(String.format("Field '%s' size mismatch - LDT: %d, CS: %d", path, ldtList.size(), csList.size()));
            }
            return;
        }

        for (int i = 0; i < ldtList.size(); i++) {
            String currentPath = path + "[*]"; // Use wildcard for list elements
            Object ldtItem = ldtList.get(i);
            Object csItem = csList.get(i);

            if (isPathIgnored(currentPath, ignorePatterns)) continue;

            if (ldtItem != null && csItem != null) {
                Map<String, Object> ldtItemMap = objectMapper.convertValue(ldtItem, Map.class);
                Map<String, Object> csItemMap = objectMapper.convertValue(csItem, Map.class);
                compareMapsUsingGuava(ldtItemMap, csItemMap, path + "[" + i + "]", mismatches, ignorePatterns);
            } else if (!Objects.equals(ldtItem, csItem)) {
                mismatches.add(String.format("Field '%s' mismatch - LDT: %s, CS: %s", path + "[" + i + "]", ldtItem, csItem));
            }
        }
    }

    private static boolean isPathIgnored(String path, Set<Pattern> ignorePatterns) {
        for (Pattern pattern : ignorePatterns) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }

    private static Set<Pattern> convertToPatterns(Set<String> ignoreFields) {
        return ignoreFields.stream()
                .map(path -> path.replace("*", "\\d+").replace(".", "\\.").replace("[*]", "\\[\\d+\\]"))
                .map(Pattern::compile)
                .collect(Collectors.toSet());
    }
private static Set<Pattern> convertToPatterns(Set<String> ignoreFields) {
    return ignoreFields.stream()
            .map(path -> path
                .replace("[*]", "\\[\\d+\\]") // Replace list wildcards (e.g., [*]) with [\d+]
                .replace(".", "\\.") // Escape dots for proper path separation
                .replace("*", "[^\\.\\[\\]]+") // Replace * with "any field name" regex
            )
            .map(Pattern::compile) // Compile the pattern
            .collect(Collectors.toSet());
}
}