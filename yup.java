import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.*;

@UtilityClass
public class ComparisonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> List<String> compareObjects(T ldtObject, T csObject) {
        // Convert objects to maps
        Map<String, Object> ldtMap = objectMapper.convertValue(ldtObject, Map.class);
        Map<String, Object> csMap = objectMapper.convertValue(csObject, Map.class);

        List<String> mismatches = new ArrayList<>();
        compareMapsUsingGuava(ldtMap, csMap, "", mismatches);

        return mismatches;
    }

    private static void compareMapsUsingGuava(Map<String, Object> ldtMap, Map<String, Object> csMap, String path, List<String> mismatches) {
        MapDifference<String, Object> difference = Maps.difference(ldtMap, csMap);

        // Fields with differing values
        difference.entriesDiffering().forEach((key, valueDifference) -> {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            Object leftValue = valueDifference.leftValue();
            Object rightValue = valueDifference.rightValue();

            // If both values are maps, recurse
            if (leftValue instanceof Map && rightValue instanceof Map) {
                compareMapsUsingGuava((Map<String, Object>) leftValue, (Map<String, Object>) rightValue, currentPath, mismatches);
            }
            // If both values are lists, compare lists
            else if (leftValue instanceof List && rightValue instanceof List) {
                compareLists((List<?>) leftValue, (List<?>) rightValue, currentPath, mismatches);
            } else {
                // Add field mismatch
                mismatches.add(String.format("Field '%s' mismatch - LDT: %s, CS: %s", currentPath, leftValue, rightValue));
            }
        });

        // Fields only in LDT
        difference.entriesOnlyOnLeft().forEach((key, value) -> {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            mismatches.add(String.format("Field '%s' only in LDT: %s", currentPath, value));
        });

        // Fields only in CS
        difference.entriesOnlyOnRight().forEach((key, value) -> {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            mismatches.add(String.format("Field '%s' only in CS: %s", currentPath, value));
        });
    }

    private static void compareLists(List<?> ldtList, List<?> csList, String path, List<String> mismatches) {
        if (ldtList.size() != csList.size()) {
            mismatches.add(String.format("Field '%s' size mismatch - LDT: %d, CS: %d", path, ldtList.size(), csList.size()));
            return;
        }

        for (int i = 0; i < ldtList.size(); i++) {
            String currentPath = path + "[" + i + "]";
            Object ldtItem = ldtList.get(i);
            Object csItem = csList.get(i);

            if (ldtItem instanceof Map && csItem instanceof Map) {
                compareMapsUsingGuava((Map<String, Object>) ldtItem, (Map<String, Object>) csItem, currentPath, mismatches);
            } else if (!Objects.equals(ldtItem, csItem)) {
                mismatches.add(String.format("Field '%s' mismatch - LDT: %s, CS: %s", currentPath, ldtItem, csItem));
            }
        }
    }
}