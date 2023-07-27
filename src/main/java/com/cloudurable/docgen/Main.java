package com.cloudurable.docgen;


import com.cloudurable.docgen.extract.FileUtils;
import com.cloudurable.jai.OpenAIClient;
import com.cloudurable.jai.model.ClientResponse;
import com.cloudurable.jai.model.FinishReason;
import com.cloudurable.jai.model.text.completion.chat.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

            ExecutorService executorService = Executors.newCachedThreadPool();

            //Java2CSV.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
            //        .run();
//            DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
//                    .genBusinessRules();


           // DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
            //        .genImageIfMissing();

            final CountDownLatch countDownLatch = new CountDownLatch(3);

            executorService.submit(() -> {
                try {
                    DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile)
                            .useExistingMermaidIfFound(true)
                            .inlineMermaid(true).build()
                            //.genDesignDoc();
                            .runMethodGen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });

            executorService.submit(() -> {
                try {
//                    DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
//                            .genImprovements();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });

            executorService.submit(() -> {
                try {
//                    DocGenerator.builder().inputDirectoryPath(directoryPath).outputFile(outputFile).build()
//                            .generateMissingJavaDoc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });



            countDownLatch.await(5, TimeUnit.HOURS);



            executorService.shutdown();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
