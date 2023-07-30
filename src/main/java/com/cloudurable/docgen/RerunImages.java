package com.cloudurable.docgen;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RerunImages {

    public static void main(String... args) {
        final File directoryPath = new File(args.length > 0 ? args[0] : ".");
        final File outputDir = new File(args.length > 1 ? args[1] : "./out");

        if (!outputDir.exists()) {
            return;
        }
        final File mermaidOutputDir = new File(outputDir, "mermaid");
        final File imagesOutputDir = new File(outputDir, "images");

        Set<String> mermaidFiles = Arrays.stream(mermaidOutputDir.listFiles()).map(f -> f.getName()).map(name -> removeExt(name)).collect(Collectors.toSet());
        Set<String> imageFiles = Arrays.stream(imagesOutputDir.listFiles()).map(f -> f.getName()).map(name -> removeExt(name)).collect(Collectors.toSet());

        if (imageFiles.size() != mermaidFiles.size()) {
            System.out.println("Sizes not equal");
            mermaidFiles.removeAll(imageFiles);
            System.out.println("Missing images");
            mermaidFiles.forEach(f ->
                    System.out.println(new File(imagesOutputDir, f + ".png"))
            );

//            mermaidFiles.forEach(f ->
//                    reRun(new File(mermaidOutputDir, f + ".mmd"), new File(imagesOutputDir, f + ".png"))
//            );
        }


    }

    private static void reRun(File input, File output) {
        MermaidUtils.runMmdc(input, output);
    }

    private static String removeExt(String name) {
        int i = name.indexOf('.');
        if (i != -1) {
            return name.substring(0, i);
        } else {
            return name;
        }
    }
}
