import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompareToolTest {

    @Test
    void testCompare_SimpleMismatch() {
        // Arrange
        Map<String, Object> ldtObject = Map.of("field1", "value1", "field2", "value2");
        Map<String, Object> csObject = Map.of("field1", "value1", "field2", "differentValue");

        // Act
        List<String> mismatches = CompareTool.compare(ldtObject, csObject, Set.of());

        // Assert
        assertEquals(1, mismatches.size());
        assertEquals("Field 'field2' mismatch - LDT: value2, CS: differentValue", mismatches.get(0));
    }

    @Test
    void testCompare_NestedMapMismatch() {
        // Arrange
        Map<String, Object> ldtObject = Map.of(
                "field1", "value1",
                "nested", Map.of("subfield1", "subvalue1", "subfield2", "subvalue2"));

        Map<String, Object> csObject = Map.of(
                "field1", "value1",
                "nested", Map.of("subfield1", "subvalue1", "subfield2", "differentValue"));

        // Act
        List<String> mismatches = CompareTool.compare(ldtObject, csObject, Set.of());

        // Assert
        assertEquals(1, mismatches.size());
        assertEquals("Field 'nested.subfield2' mismatch - LDT: subvalue2, CS: differentValue", mismatches.get(0));
    }

    @Test
    void testCompare_ListMismatch() {
        // Arrange
        Map<String, Object> ldtObject = Map.of(
                "listField", List.of("item1", "item2", "item3"));

        Map<String, Object> csObject = Map.of(
                "listField", List.of("item1", "differentItem", "item3"));

        // Act
        List<String> mismatches = CompareTool.compare(ldtObject, csObject, Set.of());

        // Assert
        assertEquals(1, mismatches.size());
        assertEquals("Field 'listField[1]' mismatch - LDT: item2, CS: differentItem", mismatches.get(0));
    }

    @Test
    void testCompare_ListSizeMismatch() {
        // Arrange
        Map<String, Object> ldtObject = Map.of(
                "listField", List.of("item1", "item2"));

        Map<String, Object> csObject = Map.of(
                "listField", List.of("item1"));

        // Act
        List<String> mismatches = CompareTool.compare(ldtObject, csObject, Set.of());

        // Assert
        assertEquals(1, mismatches.size());
        assertEquals("Field 'listField' size mismatch - LDT: 2, CS: 1", mismatches.get(0));
    }

    @Test
    void testCompare_IgnoreFields() {
        // Arrange
        Map<String, Object> ldtObject = Map.of(
                "field1", "value1",
                "nested", Map.of("ignoreField", "ignoredValue", "subfield", "subvalue"));

        Map<String, Object> csObject = Map.of(
                "field1", "value1",
                "nested", Map.of("ignoreField", "differentValue", "subfield", "subvalue"));

        Set<String> fieldsToIgnore = Set.of("nested.ignoreField");

        // Act
        List<String> mismatches = CompareTool.compare(ldtObject, csObject, fieldsToIgnore);

        // Assert
        assertEquals(0, mismatches.size());
    }

    @Test
    void testCompare_IgnoreFieldsWithWildcard() {
        // Arrange
        Map<String, Object> ldtObject = Map.of(
                "listField", List.of(
                        Map.of("id", "1", "value", "value1"),
                        Map.of("id", "2", "value", "value2")));

        Map<String, Object> csObject = Map.of(
                "listField", List.of(
                        Map.of("id", "1", "value", "value1"),
                        Map.of("id", "2", "value", "differentValue")));

        Set<String> fieldsToIgnore = Set.of("listField[*].value");

        // Act
        List<String> mismatches = CompareTool.compare(ldtObject, csObject, fieldsToIgnore);

        // Assert
        assertEquals(0, mismatches.size());
    }
}