no import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class AnnotationInjector {

    public static class FieldInfo {
        String className;
        String fieldName;
        int collibraId;

        public FieldInfo(String className, String fieldName, int collibraId) {
            this.className = className;
            this.fieldName = fieldName;
            this.collibraId = collibraId;
        }
    }

    // Load mappings from a CSV file with specified LDT Table
    public static Map<String, FieldInfo> loadMapping(String csvPath, String specifiedLdtTable) throws IOException {
        Map<String, FieldInfo> fieldMappings = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(";");
                if (columns.length < 4) continue;

                String tableName = columns[1].trim();
                String fieldName = columns[2].trim();
                int collibraId = Integer.parseInt(columns[3].trim());

                // Filter by specified LDT Table
                if (tableName.equalsIgnoreCase(specifiedLdtTable)) {
                    String key = tableName + "." + fieldName;
                    fieldMappings.put(key, new FieldInfo(tableName, fieldName, collibraId));
                }
            }
        }
        return fieldMappings;
    }

    public static void injectAnnotations(String sourceDirectory, Map<String, FieldInfo> mappings) throws IOException {
        for (FieldInfo fieldInfo : mappings.values()) {
            Path classFilePath = Paths.get(sourceDirectory, fieldInfo.className + ".java");

            if (Files.exists(classFilePath)) {
                String content = new String(Files.readAllBytes(classFilePath));

                // Regex pattern to find the field and inject annotation
                String fieldPattern = "(\\s+)([\\w<>]+\\s+" + fieldInfo.fieldName + "\\s*;)";
                String annotation = String.format("@BusinessTerm(%d)", fieldInfo.collibraId);

                // Modify the content
                String modifiedContent = content.replaceAll(fieldPattern, "$1" + annotation + "\n$1$2");

                // Write back to file
                Files.write(classFilePath, modifiedContent.getBytes());
                System.out.println("Annotated " + fieldInfo.className + "." + fieldInfo.fieldName + " with @BusinessTerm(" + fieldInfo.collibraId + ")");
            } else {
                System.out.println("Class file not found: " + classFilePath);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java AnnotationInjector <csvPath> <sourceDirectory> <LDT Table>");
            return;
        }

        String csvPath = args[0];
        String sourceDirectory = args[1];
        String specifiedLdtTable = args[2];
// Remove any extra quotes and trim whitespace
int collibraId = Integer.parseInt(columns[3].trim().replaceAll("^\"|\"$", ""));
        // Step 1: Load mapping from CSV filtered by specified LDT Table
        Map<String, FieldInfo> mappings = loadMapping(csvPath, specifiedLdtTable);

        // Step 2: Inject annotations into source files
        injectAnnotations(sourceDirectory, mappings);

        System.out.println("Annotation injection completed for LDT Table: " + specifiedLdtTable);
    }


}