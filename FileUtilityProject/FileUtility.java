import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FileUtility - simple menu-driven Java program to read/write/modify text files.
 * Usage: compile with javac and run with java.
 *
 * Type multi-line input when asked; finish with a single line containing: __END__
 */
public class FileUtility {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createOrOverwriteFile();
                case "2" -> appendToFile();
                case "3" -> readFile();
                case "4" -> replaceInFile();
                case "5" -> deleteLinesContaining();
                case "6" -> copyFile();
                case "7" -> moveFile();
                case "8" -> deleteFile();
                case "9" -> showFileInfo();
                case "0" -> { System.out.println("Exiting. Goodbye!"); scanner.close(); return; }
                default -> System.out.println("Invalid choice. Enter a number from the menu.");
            }
            System.out.println("\n--- operation finished ---\n");
        }
    }

    private static void showMenu() {
        System.out.println("=== FileUtility Menu ===");
        System.out.println("1) Create / Overwrite file");
        System.out.println("2) Append to file");
        System.out.println("3) Read & display file");
        System.out.println("4) Find & replace text");
        System.out.println("5) Delete lines containing substring");
        System.out.println("6) Copy file");
        System.out.println("7) Move / Rename file");
        System.out.println("8) Delete file");
        System.out.println("9) Show file info (exists, size, lines, modified)");
        System.out.println("0) Exit");
        System.out.print("Enter choice: ");
    }

    // 1) Create or overwrite a file (multiline input, end with __END__)
    private static void createOrOverwriteFile() {
        System.out.print("Enter path (e.g. notes.txt or folder/notes.txt): ");
        String pathStr = scanner.nextLine().trim();
        Path path = Paths.get(pathStr);
        System.out.println("Enter file content. Type a single line with __END__ to finish.");
        String content = readMultilineInput();
        try {
            // Create parent dirs if needed
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            Files.write(path, content.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("File written (overwrite) -> " + path.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // 2) Append to a file
    private static void appendToFile() {
        System.out.print("Enter path to append to: ");
        String pathStr = scanner.nextLine().trim();
        Path path = Paths.get(pathStr);
        System.out.println("Enter text to append. Type a single line with __END__ to finish.");
        String content = readMultilineInput();
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            Files.write(path, content.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Content appended to -> " + path.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error appending: " + e.getMessage());
        }
    }

    // 3) Read & display
    private static void readFile() {
        System.out.print("Enter path to read: ");
        String pathStr = scanner.nextLine().trim();
        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) { System.out.println("File does not exist: " + path); return; }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            System.out.println("----- File Start -----");
            for (int i = 0; i < lines.size(); i++) {
                System.out.printf("%4d: %s%n", i + 1, lines.get(i));
            }
            System.out.println("----- File End -----");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // 4) Find and replace (simple literal replace across entire file)
    private static void replaceInFile() {
        System.out.print("Enter path to modify: ");
        String pathStr = scanner.nextLine().trim();
        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) { System.out.println("File does not exist: " + path); return; }
        System.out.print("Enter the text to find (exact match): ");
        String target = scanner.nextLine();
        System.out.print("Enter the replacement text: ");
        String replacement = scanner.nextLine();
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> newLines = lines.stream()
                    .map(l -> l.replace(target, replacement))
                    .collect(Collectors.toList());
            Files.write(path, newLines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Replaced all occurrences of [" + target + "] in file.");
        } catch (IOException e) {
            System.out.println("Error replacing text: " + e.getMessage());
        }
    }

    // 5) Delete lines containing a substring
    private static void deleteLinesContaining() {
        System.out.print("Enter path to modify: ");
        String pathStr = scanner.nextLine().trim();
        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) { System.out.println("File does not exist: " + path); return; }
        System.out.print("Enter substring; any line containing it will be removed: ");
        String sub = scanner.nextLine();
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> filtered = lines.stream()
                    .filter(l -> !l.contains(sub))
                    .collect(Collectors.toList());
            Files.write(path, filtered, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Removed lines containing: " + sub);
        } catch (IOException e) {
            System.out.println("Error editing file: " + e.getMessage());
        }
    }

    // 6) Copy file
    private static void copyFile() {
        System.out.print("Enter source path: ");
        Path src = Paths.get(scanner.nextLine().trim());
        System.out.print("Enter destination path: ");
        Path dst = Paths.get(scanner.nextLine().trim());
        try {
            if (dst.getParent() != null) Files.createDirectories(dst.getParent());
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied to: " + dst.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Copy failed: " + e.getMessage());
        }
    }

    // 7) Move / Rename file
    private static void moveFile() {
        System.out.print("Enter source path: ");
        Path src = Paths.get(scanner.nextLine().trim());
        System.out.print("Enter new path (destination): ");
        Path dst = Paths.get(scanner.nextLine().trim());
        try {
            if (dst.getParent() != null) Files.createDirectories(dst.getParent());
            Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved/Renamed to: " + dst.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Move failed: " + e.getMessage());
        }
    }

    // 8) Delete file
    private static void deleteFile() {
        System.out.print("Enter path to delete: ");
        Path path = Paths.get(scanner.nextLine().trim());
        try {
            boolean removed = Files.deleteIfExists(path);
            System.out.println(removed ? "Deleted: " + path.toAbsolutePath() : "File did not exist.");
        } catch (IOException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    // 9) Show file info
    private static void showFileInfo() {
        System.out.print("Enter path to inspect: ");
        Path path = Paths.get(scanner.nextLine().trim());
        try {
            System.out.println("Exists: " + Files.exists(path));
            if (Files.exists(path)) {
                System.out.println("Absolute path: " + path.toAbsolutePath());
                System.out.println("Is directory: " + Files.isDirectory(path));
                System.out.println("Is regular file: " + Files.isRegularFile(path));
                System.out.println("Size (bytes): " + Files.size(path));
                try (Stream<String> s = Files.lines(path, StandardCharsets.UTF_8)) {
                    long lines = s.count();
                    System.out.println("Line count: " + lines);
                } catch (IOException ignore) { /* if can't count lines, skip */ }
                FileTime mt = Files.getLastModifiedTime(path);
                System.out.println("Last modified: " + mt);
            }
        } catch (IOException e) {
            System.out.println("Error inspecting file: " + e.getMessage());
        }
    }

    // Helper - read multiline input until __END__ line
    private static String readMultilineInput() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if ("__END__".equals(line)) break;
            sb.append(line).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
