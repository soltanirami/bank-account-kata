
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    // Modified loadMapping to accept a specific LDT Table to check
    public static Map<String, FieldInfo> loadMapping(String excelPath, String specifiedLdtTable) throws IOException {
        Map<String, FieldInfo> fieldMappings = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell tableCell = row.getCell(1); // LDT Table column
                Cell columnCell = row.getCell(2); // LDT Table Column column
                Cell idCell = row.getCell(3);     // ID Collibra column

                if (tableCell != null && columnCell != null && idCell != null) {
                    String tableName = tableCell.getStringCellValue();
                    if (tableName.equalsIgnoreCase(specifiedLdtTable)) { // Filter by specified LDT Table
                        String fieldName = columnCell.getStringCellValue();
                        int collibraId = (int) idCell.getNumericCellValue();
                        String key = tableName + "." + fieldName;
                        fieldMappings.put(key, new FieldInfo(tableName, fieldName, collibraId));
                    }
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
            System.out.println("Usage: java AnnotationInjector <excelPath> <sourceDirectory> <LDT Table>");
            return;
        }

        String excelPath = args[0];
        String sourceDirectory = args[1];
        String specifiedLdtTable = args[2];

        // Step 1: Load mapping from Excel filtered by specified LDT Table
        Map<String, FieldInfo> mappings = loadMapping(excelPath, specifiedLdtTable);

        // Step 2: Inject annotations into source files
        injectAnnotations(sourceDirectory, mappings);

        System.out.println("Annotation injection completed for LDT Table: " + specifiedLdtTable);
    }
}