package com.cloudurable.docgen;


public class Main {

    /**
     * Entry point of the program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            final String directoryPath = args.length > 0 ? args[0] : ".";
            final String outputFile = args.length > 1 ? args[1] : "output.csv";
            //Java2CSV.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
            //        .run();
//            DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
//                    .genBusinessRules();
            DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
                    .genImageIfMissing();
//            DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
//                    .getDesignDoc();
//            DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
//                    .genImprovements();
//            DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
//                    .generateMissingJavaDoc();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
