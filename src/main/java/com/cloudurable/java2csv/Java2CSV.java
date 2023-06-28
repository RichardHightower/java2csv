package com.cloudurable.java2csv;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class responsible for processing Java files from a directory and converting them into a CSV format.
 * This is done by using the Builder pattern to set the input directory path and output file name.
 */
public class Java2CSV {


    /**
     * Path of the directory that contains the Java files to be converted.
     */
    private final String inputDirectoryPath;
    /**
     * Name of the file where the output of the conversion will be stored.
     */
    private final String outputFile;

    /**
     * Constructs a Java2CSV object with the specified directory path and output file.
     *
     * @param directoryPath the path of the directory that contains the Java files to be converted.
     * @param outputFile    the name of the file where the conversion output will be stored.
     */
    public Java2CSV(String directoryPath, String outputFile) {
        this.inputDirectoryPath = directoryPath;
        this.outputFile = outputFile;
    }

    /**
     * Returns a new instance of the Builder class to configure a Java2CSV object.
     *
     * @return a new Builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    public void run() throws IOException {
        File dir = new File(inputDirectoryPath).getCanonicalFile();
        if (dir.exists() && dir.isDirectory()) {
            List<JavaItem> javaItems = scanDirectory(dir);
            List<List<String>> lines = javaItems.stream().map(JavaItem::row).collect(Collectors.toList());
            try (CSVWriter writer = new CSVWriter(new FileWriter(outputFile))) {
                writer.writeNext(JavaItem.headers().toArray(new String[0]));
                for (List<String> line : lines) {
                    writer.writeNext(line.toArray(new String[0]));
                }
            }
        } else {
            throw new IllegalStateException(String.format(
                    "Directory does not exist %s or is not a directory", dir));
        }
    }

    /**
     * Scans the given directory and returns a list of items representing the Java code.
     *
     * @param directoryPath the directory path
     * @return the list of items
     * @throws IOException if an I/O error occurs
     */
    public List<JavaItem> scanDirectory(File directoryPath) throws IOException {
        final List<JavaItem> javaItems = new ArrayList<>(32);
        try (Stream<Path> walk = Files.walk(directoryPath.toPath())) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> parseFile(p.toFile(), javaItems));
        }
        return javaItems;
    }

    /**
     * Parses the given file and adds the parsed items to the list.
     *
     * @param file      the file to parse
     * @param javaItems the list of items to add to
     */
    private void parseFile(File file, List<JavaItem> javaItems) {
        try {
            final ClassVisitor cv = new ClassVisitor();
            javaItems.addAll(cv.run(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builder class for the Java2CSV class.
     */
    public static class Builder {
        /**
         * Path of the directory that contains the Java files to be converted.
         */
        private String inputDirectoryPath;
        /**
         * Name of the file where the output of the conversion will be stored.
         */
        private String outputFile;

        private Builder() {
        }

        /**
         * Sets the path of the directory that contains the Java files to be converted.
         *
         * @param inputDirectoryPath the path of the directory.
         * @return the Builder instance with the updated input directory path.
         */
        public Builder inputDirectoryPath(String inputDirectoryPath) {
            this.inputDirectoryPath = inputDirectoryPath;
            return this;
        }

        /**
         * Sets the name of the output file where the conversion result will be stored.
         *
         * @param outputFile the name of the output file.
         * @return the Builder instance with the updated output file name.
         */
        public Builder outputFile(String outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        /**
         * Returns a Java2CSV object built with the parameters set in this Builder instance.
         *
         * @return a new Java2CSV instance.
         */
        public Java2CSV build() {
            return new Java2CSV(this.inputDirectoryPath, this.outputFile);
        }
    }

}
