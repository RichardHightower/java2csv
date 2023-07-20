package com.cloudurable.java2csv;


public class Main {

    /**
     * Entry point of the program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        try {
            final String directoryPath = args.length > 0 ? args[0] : ".";
            final String outputFile = args.length > 1 ? args[1] : "output.csv";
            //Java2CSV.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
            //        .run();
            Java2CSV.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
                                .genImageIfMissing();
            Java2CSV.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
                    .extractClasses();
            Java2CSV.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
                    .generateMissingJavaDoc();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
