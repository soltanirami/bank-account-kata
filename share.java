import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class SyncChecker {

    /**
     * Compares two objects field-by-field and identifies differences
     * @param ldtObject The LDT Deal object
     * @param csObject The CS Deal object
     * @return A detailed mismatch report
     */
    public static SyncReport compareObjects(Object ldtObject, Object csObject) {
        Map<String, Object> ldtMap = ObjectConverter.convertObjectToMap(ldtObject);
        Map<String, Object> csMap = ObjectConverter.convertObjectToMap(csObject);

        MapDifference<String, Object> differences = Maps.difference(ldtMap, csMap);

        SyncReport report = new SyncReport();

        // Fields only in LDT but not in CS
        report.addMismatches("Fields only in LDT", differences.entriesOnlyOnLeft());

        // Fields only in CS but not in LDT
        report.addMismatches("Fields only in CS", differences.entriesOnlyOnRight());

        // Fields that are present in both but have different values
        differences.entriesDiffering().forEach((key, valueDifference) -> {
            report.addMismatch(key, valueDifference.leftValue(), valueDifference.rightValue());
        });

        return report;
    }
}