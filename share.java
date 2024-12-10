import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyncReport {

    private List<String> mismatches = new ArrayList<>();

    public void addMismatch(String field, Object ldtValue, Object csValue) {
        mismatches.add(String.format("Field '%s' mismatch - LDT: %s, CS: %s", field, ldtValue, csValue));
    }

    public void addMismatches(String context, Map<String, Object> differences) {
        differences.forEach((key, value) -> 
            mismatches.add(String.format("%s: Field '%s' only in %s with value %s", context, key, context, value))
        );
    }

    public void printReport() {
        if (mismatches.isEmpty()) {
            System.out.println("✅ LDT and CS are in sync!");
        } else {
            System.out.println("❌ LDT and CS are NOT in sync. Differences:");
            mismatches.forEach(System.out::println);
        }
    }

    public boolean isInSync() {
        return mismatches.isEmpty();
    }
}