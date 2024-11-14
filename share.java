import java.io.*;
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
                int collibraId = Integer.parseInt(columns[3].trim().replaceAll("^\"|\"$", ""));

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

                // Insert the import after the package declaration
                if (!content.contains("import com.example.BusinessTerm;")) {
                    content = content.replaceFirst(
                        "(?m)^package\\s+.*?;", // Match the package declaration
                        "$0\nimport com.example.BusinessTerm;" // Add import after package
                    );
                }

                // Regex pattern to match the field with modifiers and flexible naming conventions
                String fieldPattern = "(\\s+)(public|private|protected|static|final|volatile|\\s)*\\s+[\\w<>]+\\s+" + createFieldRegex(fieldInfo.fieldName) + "\\s*;";
                String annotation = String.format("@BusinessTerm(%d)", fieldInfo.collibraId);

                // Inject annotation above the matching field and before any modifiers
                String modifiedContent = content.replaceAll(fieldPattern, "$1" + annotation + "\n$1$2 $3");

                // Write back to file
                Files.write(classFilePath, modifiedContent.getBytes());
                System.out.println("Annotated " + fieldInfo.className + "." + fieldInfo.fieldName + " with @BusinessTerm(" + fieldInfo.collibraId + ")");
            } else {
                System.out.println("Class file not found: " + classFilePath);
            }
        }
    }

    // Utility to create a flexible regex for matching fields with underscores and camelCase variations
    private static String createFieldRegex(String fieldName) {
        StringBuilder regexBuilder = new StringBuilder();
        for (char c : fieldName.toCharArray()) {
            if (Character.isUpperCase(c)) {
                regexBuilder.append("[_]*").append(Character.toLowerCase(c));
            } else {
                regexBuilder.append(c);
            }
        }
        return regexBuilder.toString();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java AnnotationInjector <csvPath> <sourceDirectory> <LDT Table>");
            return;
        }

        String csvPath = args[0];
        String sourceDirectory = args[1];
        String specifiedLdtTable = args[2];

        Map<String, FieldInfo> mappings = loadMapping(csvPath, specifiedLdtTable);

        injectAnnotations(sourceDirectory, mappings);

        System.out.println("Annotation injection completed for LDT Table: " + specifiedLdtTable);
    }
}

import java.io.*;
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
                // Clean the collibraId field to handle extra quotes
                int collibraId = Integer.parseInt(columns[3].trim().replaceAll("^\"|\"$", ""));

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

                // Check and add import if not present
                if (!content.contains("import com.example.BusinessTerm;")) {
                    content = "import com.example.BusinessTerm;\n" + content;
                }

                // Regex pattern to match the field with flexible naming conventions
                String fieldPattern = "(\\s+)([\\w<>]+\\s+" + createFieldRegex(fieldInfo.fieldName) + "\\s*;)";
                String annotation = String.format("@BusinessTerm(%d)", fieldInfo.collibraId);

                // Inject annotation above the matching field
                String modifiedContent = content.replaceAll(fieldPattern, "$1" + annotation + "\n$1$2");

                // Write back to file
                Files.write(classFilePath, modifiedContent.getBytes());
                System.out.println("Annotated " + fieldInfo.className + "." + fieldInfo.fieldName + " with @BusinessTerm(" + fieldInfo.collibraId + ")");
            } else {
                System.out.println("Class file not found: " + classFilePath);
            }
        }
    }

    // Utility to create a flexible regex for matching fields with underscores and camelCase variations
    private static String createFieldRegex(String fieldName) {
        StringBuilder regexBuilder = new StringBuilder();
        for (char c : fieldName.toCharArray()) {
            if (Character.isUpperCase(c)) {
                regexBuilder.append("[_]*").append(Character.toLowerCase(c));
            } else {
                regexBuilder.append(c);
            }
        }
        return regexBuilder.toString();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java AnnotationInjector <csvPath> <sourceDirectory> <LDT Table>");
            return;
        }

        String csvPath = args[0];
        String sourceDirectory = args[1];
        String specifiedLdtTable = args[2];

        // Step 1: Load mapping from CSV filtered by specified LDT Table
        Map<String, FieldInfo> mappings = loadMapping(csvPath, specifiedLdtTable);

        // Step 2: Inject annotations into source files
        injectAnnotations(sourceDirectory, mappings);

        System.out.println("Annotation injection completed for LDT Table: " + specifiedLdtTable);
    }
}

}