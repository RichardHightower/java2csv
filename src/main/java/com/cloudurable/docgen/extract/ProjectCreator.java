package com.cloudurable.docgen.extract;
import java.io.File;
import java.util.concurrent.LinkedTransferQueue;


import java.util.stream.Collectors;

import static com.cloudurable.docgen.extract.FileUtils.readFile;
import static com.cloudurable.docgen.extract.FileUtils.writeFile;

public class ProjectCreator {

    private static final String DIR_MARKER = "## DIR ";
    private static final String FILE_MARKER = "## FILE ";

    public static void main(String[] args) {
        try {

            final var inputFile = args.length > 0 ? new File(args[0]) : new File("./target/output.md");
            final var outputDir = args.length > 1 ? new File(args[1]) : new File("target/project_out");
            outputDir.mkdirs();
            final var contents = readFile(inputFile);
            final var lines = contents.lines().collect(Collectors.toCollection(LinkedTransferQueue::new));

            var line = lines.poll();
            while (line != null) {

                if (line.startsWith(DIR_MARKER)) {
                    handleDir(outputDir, line.substring(DIR_MARKER.length() + 1));
                } else if (line.startsWith(FILE_MARKER)) {
                    handleFile(outputDir, line.substring(FILE_MARKER.length() + 1), lines);
                }

                line = lines.poll();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void handleFile(File outputDir, String fileName, LinkedTransferQueue<String> lines) {
        var line = lines.poll();
        while (line != null) {
            if (line.startsWith("```")) {
                line = lines.poll();
                break;
            }
            line = lines.poll();
        }

        var builder = new StringBuilder();
        while (line != null) {
            if (line.startsWith("```")) {
                break;
            }
            builder.append(line).append("\n");
            line = lines.poll();
        }

        File outputFile = new File(outputDir, fileName);
        writeFile(outputFile, builder.toString());
    }

    private static void handleDir(File outputDir, String dirStr) {
        final var dir = new File(outputDir, dirStr);
        dir.mkdirs();
    }

}
