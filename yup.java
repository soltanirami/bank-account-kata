import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CompareTool {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    public static List<String> compare(Object ldtObject, Object csObject, Set<String> fieldsToIgnore) {
        Set<Pattern> ignorePatterns = convertToPatterns(fieldsToIgnore);
        Map<String, Object> ldtMap = objectMapper.convertValue(ldtObject, Map.class);
        Map<String, Object> csMap = objectMapper.convertValue(csObject, Map.class);
        List<String> mismatches = new ArrayList<>();
        compareMaps(ldtMap, csMap, "", mismatches, ignorePatterns);
        return mismatches;
    }

    private static void compareMaps(Map<String, Object> ldtMap, Map<String, Object> csMap,
                                    String path, List<String> mismatches, Set<Pattern> ignorePatterns) {
        Maps.difference(ldtMap, csMap).entriesDiffering().forEach((key, valueDifference) -> {
            String currentPath = path.isEmpty() ? key : path + "." + key;

            if (isPathIgnored(currentPath, ignorePatterns)) {
                return;
            }

            Object ldtValue = valueDifference.leftValue();
            Object csValue = valueDifference.rightValue();

            if (ldtValue instanceof Map && csValue instanceof Map) {
                compareMaps((Map<String, Object>) ldtValue, (Map<String, Object>) csValue, currentPath, mismatches, ignorePatterns);
            } else if (ldtValue instanceof List && csValue instanceof List) {
                compareLists((List<?>) ldtValue, (List<?>) csValue, currentPath, mismatches, ignorePatterns);
            } else {
                if (!Objects.equals(ldtValue, csValue)) {
                    mismatches.add(String.format("Field '%s' mismatch - LDT: %s, CS: %s", currentPath, ldtValue, csValue));
                }
            }
        });

        // Handle missing fields
        Maps.difference(ldtMap, csMap).entriesOnlyOnRight().forEach((key, value) ->
            mismatches.add(String.format("Field '%s' present in CS but missing in LDT", path + "." + key))
        );

        Maps.difference(ldtMap, csMap).entriesOnlyOnLeft().forEach((key, value) ->
            mismatches.add(String.format("Field '%s' present in LDT but missing in CS", path + "." + key))
        );
    }

    private static void compareLists(List<?> ldtList, List<?> csList, String path,
                                     List<String> mismatches, Set<Pattern> ignorePatterns) {
        if (ldtList.size() != csList.size()) {
            mismatches.add(String.format("Field '%s' size mismatch - LDT: %d, CS: %d", path, ldtList.size(), csList.size()));
            return;
        }

        for (int i = 0; i < ldtList.size(); i++) {
            String currentPath = path + "[" + i + "]";
            Object ldtItem = ldtList.get(i);
            Object csItem = csList.get(i);

            if (isPathIgnored(currentPath, ignorePatterns)) {
                continue;
            }

            if (ldtItem == null && csItem == null) {
                continue;
            }

            if (ldtItem == null || csItem == null) {
                mismatches.add(String.format("Field '%s' mismatch - LDT: %s, CS: %s", currentPath, ldtItem, csItem));
                continue;
            }

            // Compare simple types
            if (isSimpleType(ldtItem) || isSimpleType(csItem)) {
                if (!Objects.equals(ldtItem, csItem)) {
                    mismatches.add(String.format("Field '%s' mismatch - LDT: %s, CS: %s", currentPath, ldtItem, csItem));
                }
            } else {
                // Compare complex objects
                Map<String, Object> ldtItemMap = objectMapper.convertValue(ldtItem, Map.class);
                Map<String, Object> csItemMap = objectMapper.convertValue(csItem, Map.class);
                compareMaps(ldtItemMap, csItemMap, currentPath, mismatches, ignorePatterns);
            }
        }
    }

    private static boolean isPathIgnored(String path, Set<Pattern> ignorePatterns) {
        return ignorePatterns.stream().anyMatch(pattern -> pattern.matcher(path).matches());
    }

    private static Set<Pattern> convertToPatterns(Set<String> fieldsToIgnore) {
        return fieldsToIgnore.stream()
                .map(path -> path.replace("[*]", "\\[\\d+\\]")  // Replace list wildcards
                                 .replace(".", "\\.")         // Escape dots
                                 .replace("*", "[^.\\[]+"))   // Replace * with any field name
                .map(Pattern::compile)
                .collect(Collectors.toSet());
    }

    private static boolean isSimpleType(Object obj) {
        return obj == null || ClassUtils.isPrimitiveOrWrapper(obj.getClass())
                || obj instanceof String || obj instanceof Enum;
    }
}