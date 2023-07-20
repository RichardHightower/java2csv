package com.cloudurable.docgen.extract;

public class FileUtils {

    public static String readFile(java.io.File file) {
        try {
            return java.nio.file.Files.readString(file.toPath());
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFile(java.io.File file, String content) {
        try {
            java.nio.file.Files.writeString(file.toPath(), content);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
