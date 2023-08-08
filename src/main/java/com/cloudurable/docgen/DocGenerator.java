package com.cloudurable.docgen;

import com.cloudurable.docgen.extract.FileUtils;
import com.cloudurable.docgen.generators.MethodMermaidSequenceGen;
import com.cloudurable.docgen.generators.PackageMermaidClassDiagramGen;
import com.cloudurable.docgen.mermaid.validation.*;
import com.cloudurable.docgen.mermaid.validation.sequence.*;
import com.cloudurable.jai.OpenAIClient;
import com.cloudurable.jai.model.ClientResponse;
import com.cloudurable.jai.model.text.completion.chat.ChatRequest;
import com.cloudurable.jai.model.text.completion.chat.ChatResponse;
import com.cloudurable.jai.model.text.completion.chat.Message;
import com.cloudurable.jai.model.text.completion.chat.Role;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class responsible for processing Java files from a directory and converting them into a CSV format.
 * This is done by using the Builder pattern to set the input directory path and output file name.
 */
public class DocGenerator {

    private static String PRODUCT_MANAGER ="software product manager:\n" +
            "* Serves as the link between business, technology, and user experience.\n" +
            "* Understands market demands and sets the product vision.\n" +
            "* Defines product strategy based on the vision.\n" +
            "* Creates and manages the product roadmap.\n" +
            "* Prioritizes features based on customer needs, business priorities, and technical constraints.\n" +
            "* Coordinates with development teams to ensure quality and adherence to requirements.\n" +
            "* Plans and executes product launches in collaboration with marketing, sales, and customer support teams.\n" +
            "* Monitors product performance and uses data for informed decision-making.\n" +
            "* Manages communication with various stakeholders about the product strategy, roadmap, and progress.\n" +
            "Importance:\n" +
            "* Ensures optimal use of company resources by directing them towards the right products and features.\n" +
            "* Delivers a product that provides customer value, leading to satisfaction, revenue growth, and market expansion.\n" +
            "* Without a PM, a company might lack a clear product strategy, resulting in wasted resources, missed market opportunities, and a product that fails to meet user needs. ";


    /**
     * Path of the directory that contains the Java files to be converted.
     */
    private final String inputDirectoryPath;
    /**
     * Name of the file where the output of the conversion will be stored.
     */
    private final String outputFile;
    private final boolean inlineMermaid;
    private final boolean useExistingMermaidIfFound;

    /**
     * Constructs a Java2CSV object with the specified directory path and output file.
     *
     * @param directoryPath             the path of the directory that contains the Java files to be converted.
     * @param outputFile                the name of the file where the conversion output will be stored.
     * @param inlineMermaid
     * @param useExistingMermaidIfFound
     */
    public DocGenerator(String directoryPath, String outputFile, boolean inlineMermaid,
                        boolean useExistingMermaidIfFound) {
        this.inputDirectoryPath = directoryPath;
        this.outputFile = outputFile;
        this.inlineMermaid = inlineMermaid;
        this.useExistingMermaidIfFound = useExistingMermaidIfFound;
    }

    /**
     * Returns a new instance of the Builder class to configure a Java2CSV object.
     *
     * @return a new Builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static String extractSequenceDiagram(String mermaidCode) {

        mermaidCode = mermaidCode.replace("```mermaid\n", "").replace("```", "");
        String[] lines = mermaidCode.split("\n");
        StringBuilder extractedCode = new StringBuilder();

        boolean foundStart = false;

        for (String line : lines) {

            if (line.startsWith("sequenceDiagram")) {
                foundStart = true;
                extractedCode.append(line).append("\n");
            } else if (foundStart ) {
                if (!line.isEmpty() && line.charAt(0) == ' ') {
                    extractedCode.append(line).append("\n");
                }else {
                    extractedCode.append("\n");
                }
            }
        }

        return extractedCode.toString().trim();
    }
    public static String chat(String input, String system) {


        System.out.println(input);

        final var client = OpenAIClient.builder().validateJson(true).setApiKey(System.getenv("OPENAI_API_KEY")).build();

        final var chatRequest = ChatRequest.builder().addMessage(Message.builder().role(Role.SYSTEM).content(
                        system)
                .build()).addMessage(Message.builder().role(Role.USER).content(
                input).build()).build();

        ClientResponse<ChatRequest, ChatResponse> chat = client.chat(chatRequest);
        if (chat.getStatusCode().orElse(666) == 503) {
            for (int i = 0; i < 10; i++ ) {
                try {
                    Thread.sleep(1000);
                    System.out.println("System was busy sleeping " + System.currentTimeMillis());
                } catch (InterruptedException e) {

                }
            }
            return chat(input, system);
        } else if (chat.getStatusCode().orElse(666) >= 200 && chat.getStatusCode().orElse(666)  <= 299) {
            return chat.getResponse().get().getChoices().get(0).getMessage().getContent();
        } else {
                String errorMessage = String.format("Error handling request input: %s\n system: %s\n status code %d \n status message %s ",
                        input, system, chat.getStatusCode().orElse(666), chat.getStatusMessage().orElse(""));
                System.err.println(errorMessage);
                return "";
        }

    }



    private  void getGenerateUMLClassDiagramForPackage(File mermaid, File images,
                                                             String packageName, List<String> classDefs, StringBuilder markdownBuilder) throws Exception {


        final String imageForPackage = packageName.replace(".", "_") + ".png";
        File mermaidFile = new File(mermaid, packageName.replace(".", "_") + ".mmd");
        File pngFile = new File(images, imageForPackage);

        String mContent =  "";



        if (useExistingMermaidIfFound && mermaidFile.exists()) {
            mContent = FileUtils.readFile(mermaidFile);
            if(mContent!= null && mContent.startsWith("SKIP"))  {
                return;
            }
            if (pngFile.lastModified() < mermaidFile.lastModified()) {

                final var result = MermaidUtils.runMmdc(mermaidFile, pngFile);
                if (!this.inlineMermaid && pngFile.exists() && result.getResult() == 0){
                    markdownBuilder.append("\n![class diagram](./images/" + imageForPackage + ")\n\n");
                } else if (result.getResult() == 0) {
                    markdownBuilder.append("\n```mermaid\n").append(mContent).append("\n```\n");
                }
                if (result.getResult() == 0) {
                    return;
                }
            }
        }

        final var javaCode = String.join("\n\n", classDefs.toArray(new String[0]));

        final var gen = new PackageMermaidClassDiagramGen();
        mContent = gen.generateClassDiagramFromPackage(packageName, javaCode);
        FileUtils.writeFile(mermaidFile, mContent);
        final var result = MermaidUtils.runMmdc(mermaidFile, pngFile);

        if (result.getResult() == 0) {

            if (!this.inlineMermaid && pngFile.exists()){
                markdownBuilder.append("\n![class diagram](./images/" + imageForPackage + ")\n\n");
            } else {
                markdownBuilder.append("\n```mermaid\n").append(mContent).append("\n```\n");
            }
        }

    }


    public void genJustPackage() throws IOException {

        File outputDir = new File(outputFile).getParentFile();
        outputDir.mkdirs();

        File dir = new File(inputDirectoryPath).getCanonicalFile();
        if (dir.exists() && dir.isDirectory()) {


            generateAll(outputDir);

            List<JavaItem> javaItems = scanDirectory(dir);

            Map<String, List<String>> classNameByPackage = mapPackageToClassDefs(javaItems);

            classNameByPackage.entrySet().stream().forEach(entry -> {

                final var gen = new PackageMermaidClassDiagramGen();
                final String packageName = entry.getKey();

                List<String> classDefs = entry.getValue();
                final var javaCode = String.join("\n\n", classDefs.toArray(new String[0]));

                System.out.println(javaCode);

                System.out.println(gen.generateClassDiagramFromPackage(packageName, javaCode));

            });


        } else {
            throw new IllegalStateException(String.format(
                    "Directory does not exist %s or is not a directory", dir));
        }
    }

    public void genDesignDoc() throws IOException {

        File outputDir = new File(outputFile).getParentFile();
        outputDir.mkdirs();

        File dir = new File(inputDirectoryPath).getCanonicalFile();
        if (dir.exists() && dir.isDirectory()) {


            generateAll(outputDir);

            List<JavaItem> javaItems = scanDirectory(dir);

            Map<String, List<String>> classNameByPackage = mapPackageToClassDefs(javaItems);

            classNameByPackage.entrySet().stream().forEach(entry -> {
                final String packageName = entry.getKey();
                final String markdownForPackage = packageName.replace(".", "_") + ".md";
                File markdownFileForPackage = new File(outputDir, markdownForPackage);
                if (markdownFileForPackage.exists()) {
                    return;
                }

                List<String> classDefs = entry.getValue();
                final StringBuilder markdownBuilder = new StringBuilder();

                File mermaid = new File(outputDir, "mermaid");
                mermaid.mkdirs();
                File images = new File(outputDir, "images");
                images.mkdirs();


                markdownBuilder.append("# " + packageName + "\n");

                try {
                    getGenerateUMLClassDiagramForPackage(mermaid, images, packageName, classDefs, markdownBuilder);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                createClassStream(javaItems, packageName)
                        .forEach(javaClass -> {
                            generateClassDocs(javaItems, markdownBuilder, javaClass);

                            createMethodFilter(javaItems, javaClass)
                                            .forEach(javaMethod -> {
                                                generateMethodDocs(markdownBuilder, mermaid, images, javaClass, javaMethod, packageName);


                                            });
                                }
                        );

                try {
                    Files.write(markdownFileForPackage.toPath(), markdownBuilder.toString().getBytes(StandardCharsets.UTF_8));

                    generateAll(outputDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });


        } else {
            throw new IllegalStateException(String.format(
                    "Directory does not exist %s or is not a directory", dir));
        }
    }

    private void generateMethodDocs(StringBuilder markdownBuilder, File mermaid, File images, JavaItem javaClass, JavaItem javaMethod, String packageName) {
        methodCodeListingJava(markdownBuilder, javaMethod);

        String briefMethodDescription = briefDescriptionOfMethod(javaClass, javaMethod);
        String stepByStep = stepByStepMethodDescription(javaClass, javaMethod);

        String sequenceDiagram = sequenceDiagramGen(mermaid, images, javaClass, javaMethod, packageName);

        markdownBuilder.append(briefMethodDescription).append("\n")
                .append(stepByStep).append("\n")
                .append(sequenceDiagram).append("\n");
    }

    private void generateClassDocs(List<JavaItem> javaItems, StringBuilder markdownBuilder, JavaItem javaClass) {
        markdownBuilder.append("## Class: " + javaClass.getSimpleName()).append("\n");

        markdownBuilder.append("\n**" + javaClass.getName()).append("**\n");

        markdownBuilder.append("\n```java\n" + javaClass.getDefinition()).append("\n").append("```\n");

        markdownBuilder.append(generateShortDescriptionForClass(javaItems, javaClass)).append("\n");
    }


    public void runMethodGen() throws IOException {



        final var mermaidSequenceGen = new MethodMermaidSequenceGen();

        File outputDir = new File(outputFile).getParentFile();
        outputDir.mkdirs();
        File mermaid = new File(outputDir, "mermaid");
        mermaid.mkdirs();
        File images = new File(outputDir, "images");
        images.mkdirs();


        File dir = new File(inputDirectoryPath).getCanonicalFile();
        if (dir.exists() && dir.isDirectory()) {
            List<JavaItem> javaItems = scanDirectory(dir);
            Map<String, List<String>> classNameByPackage = mapPackageToClassDefs(javaItems);

            classNameByPackage.entrySet().stream().forEach(entry -> {
                final String packageName = entry.getKey();
                createClassStream(javaItems, packageName)
                        .forEach(javaClass -> {
                                    createMethodFilter(javaItems, javaClass)
                                            .forEach(javaMethod -> {
                                                String sequenceDiagram = mermaidSequenceGen.generateSequenceFromMethod(javaMethod.getBody(), javaMethod.getSimpleName(), javaClass.getSimpleName(), javaClass.getName());
                                                System.out.println(sequenceDiagram);
                                            });
                                }
                        );

            });


        } else {
            throw new IllegalStateException(String.format(
                    "Directory does not exist %s or is not a directory", dir));
        }
    }

    private  String sequenceDiagramGen(File mermaid, File images, JavaItem javaClass, JavaItem javaMethod, String packageName) {
        final String imageForMethod = javaMethod.getName().replace(".", "_") + ".png";
        final String mermaidSeqForMethod = javaMethod.getName().replace(".", "_") + ".mmd";
        StringBuilder markdownBuilder = new StringBuilder();
        final var mermaidSequenceGen = new  MethodMermaidSequenceGen();
        File mermaidMethodFile = new File(mermaid, mermaidSeqForMethod);
        File pngMethodFile = new File(images, imageForMethod);
        String mContent = "";

        if (useExistingMermaidIfFound) {
            if (mermaidMethodFile.exists()) {
                mContent =  FileUtils.readFile(mermaidMethodFile);
                if(mContent!= null && mContent.startsWith("SKIP"))  {
                    return "\n";
                }
                if (mermaidMethodFile.lastModified() > pngMethodFile.lastModified()) {
                    final var results = MermaidUtils.runMmdc(mermaidMethodFile, pngMethodFile);
                    if (results.getResult() == 0 && pngMethodFile.exists()) {
                        if (!this.inlineMermaid) {
                            markdownBuilder.append("\n![sequence diagram](./images/" + imageForMethod + ")\n\n");
                        } else {
                            markdownBuilder.append("\n```mermaid\n").append(mContent).append("\n```\n");
                        }
                        return markdownBuilder.toString();
                    }
                }
            }
        }

        mContent = mermaidSequenceGen.generateSequenceFromMethod(javaMethod.getBody(), javaMethod.getSimpleName(),
                javaClass.getSimpleName(), packageName);
        if (!isBlank(mContent)) {
            FileUtils.writeFile(mermaidMethodFile, mContent);
            final var results = MermaidUtils.runMmdc(mermaidMethodFile, pngMethodFile);
            if (results.getResult() == 0 && pngMethodFile.exists()) {
                if (!this.inlineMermaid) {
                    markdownBuilder.append("\n![sequence diagram](./images/" + imageForMethod + ")\n\n");
                } else {
                    markdownBuilder.append("\n```mermaid\n").append(mContent).append("\n```\n");
                }
                return markdownBuilder.toString();
            }
        }
        return markdownBuilder.toString();
    }



    private static String briefDescriptionOfMethod(JavaItem javaClass, JavaItem javaMethod) {
        StringBuilder markdownBuilder = new StringBuilder();
        try {
            String output = chat(String.format("As an software engineer writing docs create a brief description " +
                            "of what this method does, method = %s (%s) which is defined in class %s is doing based on its BODY" +
                            "\nBODY:\n %s \n", javaMethod.getSimpleName(), javaMethod.getName(), javaClass.getName(), javaMethod.getBody()),
                    "output should be in markdown format");

            if (output != null && !output.isBlank()) {
                markdownBuilder.append("\n").append("### ").append(javaMethod.getSimpleName()).append(" Overview \n");
                markdownBuilder.append("\n").append(output).append("\n");
            }
            return markdownBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String stepByStepMethodDescription(JavaItem javaClass, JavaItem javaMethod) {
        StringBuilder markdownBuilder = new StringBuilder();
        try {
            String output = chat(String.format("You are an software engineer writing docs. " +
                            " Make your docs as readable as possible to a non-tech business person." +
                            " Use business domain verbiage if possible. Your tone should be direct and confident. " +
                            " Create a step by step but concise description " +
                            "of this method %s which is defined in class %s is doing based on its BODY" +
                            "\nBODY:\n %s \n", javaMethod.getSimpleName(), javaClass.getName(), javaMethod.getBody()),
                    "output should be in markdown format");

            if (output != null && !output.isBlank()) {
                markdownBuilder.append("\n").append("### ").append(javaMethod.getSimpleName()).append(" Step by Step  \n");
                markdownBuilder.append("\n").append(output).append("\n");
                return markdownBuilder.toString();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void methodCodeListingJava(StringBuilder markdownBuilder, JavaItem javaMethod) {
        markdownBuilder.append("### Method: "). append(javaMethod.getSimpleName()).append("\n");
        markdownBuilder.append("```java\n").append(javaMethod.getBody()).append("\n```\n");
    }

    private static Map<String, List<String>> mapPackageToClassDefs(List<JavaItem> javaItems) {
        Map<String, List<String>> classNameByPackage = new HashMap<>();
        javaItems.stream()
                .filter(javaItem -> javaItem.getType() == JavaItemType.CLASS || javaItem.getType() == JavaItemType.INTERFACE)
                .filter(javaItem -> javaItem.getParent() == null)
                .filter(javaItem -> !javaItem.getSimpleName().endsWith("Test"))
                .filter(javaItem -> !javaItem.getSimpleName().endsWith("Tests"))
                .filter(javaItem -> !javaItem.getSimpleName().endsWith("TestBase"))
                .forEach(

                        javaItem -> {
                            String fullyQualifiedName = javaItem.getName();
                            System.out.println(javaItem.getSimpleName());
                            int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
                            String packageName = fullyQualifiedName.substring(0, lastDotIndex);
                            List<String> classDefs = classNameByPackage.getOrDefault(packageName, new ArrayList<>());

                            classDefs.add(javaItem.getDefinition() + "\n\t"

                                    + String.join("\n\t", javaItems.stream().filter(field -> field.getType() == JavaItemType.FIELD)
                                    .filter(field -> field.getParent() == javaItem)

                                    .map(field -> {
                                        String definition = field.getDefinition();
                                        int index = definition.indexOf('=');
                                        if (index == -1) {
                                            return definition;
                                        } else {
                                            return definition.substring(0, index);
                                        }
                                    }).collect(Collectors.toList()).toArray(new String[0])));

                            classNameByPackage.put(packageName, classDefs);
                        }
                );
        return classNameByPackage;
    }

    private static void generateAll(File outputDir) throws IOException {
        String all = Arrays.stream(outputDir.listFiles((dir1, name) -> name.endsWith(".md"))).map(file -> {
            try {
                return Files.readString(file.toPath()) + "\n";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining());

        Files.write(new File(outputDir, "all.md").toPath(), all.getBytes(StandardCharsets.UTF_8));
    }

    private static void generateAllImprovements(File outputDir) throws IOException {
        String all = Arrays.stream(outputDir.listFiles((dir1, name) -> name.endsWith(".md"))).map(file -> {
            try {
                return Files.readString(file.toPath()) + "\n";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining());

        Files.write(new File(outputDir, "all-improve.md").toPath(), all.getBytes(StandardCharsets.UTF_8));
    }

    private static void generateJavaDocAll(File outputDir) throws IOException {
        String all = Arrays.stream(outputDir.listFiles((dir1, name) -> name.endsWith(".md"))).map(file -> {
            try {
                return Files.readString(file.toPath()) + "\n";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining());

        Files.write(new File(outputDir, "all-javadoc.md").toPath(), all.getBytes(StandardCharsets.UTF_8));
    }

    public void generateMissingJavaDoc() throws IOException {

        File outputDir = new File(new File(outputFile).getParentFile(), "javadoc");
        outputDir.mkdirs();

        File dir = new File(inputDirectoryPath).getCanonicalFile();
        if (dir.exists() && dir.isDirectory()) {

            generateJavaDocAll(outputDir);

            List<JavaItem> javaItems = scanDirectory(dir);

            Map<String, List<String>> classNameByPackage = mapPackageToClassDefs(javaItems);

            classNameByPackage.entrySet().stream().forEach(entry -> {
                final String packageName = entry.getKey();
                final String markdownForPackage = packageName.replace(".", "_") + "-javadoc.md";
                File markdownFileForPackage = new File(outputDir, markdownForPackage);
                if (markdownFileForPackage.exists()) {
                    return;
                }

                final StringBuilder markdownBuilder = new StringBuilder();
                markdownBuilder.append("# ").append(packageName).append("\n");

                createClassStream(javaItems, packageName)
                        .forEach(javaClass -> {
                                    var classJavadocTmp = javaClass.getJavadoc();
                                    if (classJavadocTmp == null || classJavadocTmp.isBlank()) {
                                        classJavadocTmp = generateJavaDocForClass(javaItems, markdownBuilder, javaClass, classJavadocTmp);
                                    }
                                    final var classJavaDoc = classJavadocTmp;
                                    createMethodFilter(javaItems, javaClass)
                                            .forEach(javaMethod -> {
                                                generateMethodJavaDoc(markdownBuilder, javaClass, classJavaDoc, javaMethod);
                                            });
                                }
                        );

                try {
                    Files.write(markdownFileForPackage.toPath(), markdownBuilder.toString().getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });


        } else {
            throw new IllegalStateException(String.format(
                    "Directory does not exist %s or is not a directory", dir));
        }
    }

    private static void generateMethodJavaDoc(StringBuilder markdownBuilder, JavaItem javaClass, String classJavaDoc, JavaItem javaMethod) {
        if (javaMethod.getJavadoc() == null || javaMethod.getJavadoc().isBlank()) {
            markdownBuilder.append("## METHOD JAVADOC " + javaMethod.getDefinition()).append("\n");

            try {
                String output = chat(String.format("As an software engineer add a Java Docs for this method  " +
                                " method = %s (%s) which is defined in class %s is doing based on its BODY" +
                                "\nBODY:\n %s \n CLASS JAVADOC: %s \n", javaMethod.getSimpleName(), javaMethod.getName(),
                                javaClass.getName(), javaMethod.getBody(), classJavaDoc),
                        "output should be in JavaDoc format");

                if (output != null && !output.isBlank()) {
                    markdownBuilder.append("\n").append("### ").append(javaMethod.getSimpleName()).append("\n\n");
                    markdownBuilder.append("\n```java\n").append(output).append("\n```\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Stream<JavaItem> createClassStream(List<JavaItem> javaItems, String packageName) {
        return javaItems.stream()
                .filter(javaItem -> javaItem.getType() == JavaItemType.CLASS)
                .filter(javaItem -> javaItem.getParent() == null)
                .filter(javaItem -> !javaItem.getSimpleName().startsWith("Test"))
                .filter(javaItem -> !javaItem.getSimpleName().endsWith("Test"))
                .filter(javaItem -> !javaItem.getSimpleName().endsWith("Mock"))
                .filter(javaItem -> !javaItem.getSimpleName().endsWith("Tests"))
                .filter(javaItem -> !javaItem.getSimpleName().endsWith("TestBase"))
                .filter(javaItem -> (packageName + "." + javaItem.getSimpleName()).equals(javaItem.getName()));
    }

    private static Stream<JavaItem> createMethodFilter(List<JavaItem> javaItems, JavaItem javaClass) {
        return javaItems.stream()
                .filter(javaItem -> javaItem.getType() == JavaItemType.METHOD)
                .filter(javaMethod -> javaMethod.getParent().equals(javaClass))
                .filter(javaMethod -> {
                    if (javaMethod.getDefinition().contains("public static")) {
                        return true;
                    }
                    return !javaMethod.getSimpleName().startsWith("get");
                })
                .filter(javaMethod -> !javaMethod.getSimpleName().startsWith("set"))
                .filter(javaMethod -> !javaMethod.getSimpleName().equals("toString"))
                .filter(javaMethod -> !javaMethod.getSimpleName().equals("hashCode"))
                .filter(javaMethod -> !javaMethod.getSimpleName().equals("equals"))
                .filter(javaMethod -> !javaMethod.getSimpleName().equals("builder"))
                .filter(javaMethod -> javaMethod.getBody().lines().count() > 5);
    }

    private String generateJavaDocForClass(List<JavaItem> javaItems, StringBuilder markdownBuilder, JavaItem javaClass, String classJavadocTmp) {
        final var methods = getJavaMethodsForClass(javaClass, javaItems);
        final var fields = getFieldsForClass(javaClass, javaItems);
        markdownBuilder.append("## CLASS JAVADOCS " + javaClass.getSimpleName()).append("\n\n");

        markdownBuilder.append("\n**" + javaClass.getName()).append("**\n");
        try {

            final var directive = String.format("As an software engineer write the missing Java Docs for this class" +
                    "\nDEFINITION:\n %s \n" +
                    "\nMETHODS:\n%s\nFIELDS:\n%s", javaClass.getDefinition(), javaClass.getJavadoc(), methods, fields);

            classJavadocTmp = chat(directive,
                    "output should be in JavaDoc format");

            markdownBuilder.append("\n\n```java\n").append(classJavadocTmp).append("\n```\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classJavadocTmp;
    }

    private String generateShortDescriptionForClass(List<JavaItem> javaItems, JavaItem javaClass) {
        final var methods = getJavaMethodsForClass(javaClass, javaItems);
        final var fields = getFieldsForClass(javaClass, javaItems);


        final var directive = String.format("As an software engineer write a short description for this class. Just create a description for the class." +
                        "The methods and fields are just here for context. Only describe the class." +
                "\nCLASS DEFINITION:\n %s \n" +
                "\nMETHODS:\n%s\nFIELDS:\n%s\nJAVADOC FOR CLASS\n%s",
                javaClass.getDefinition(), javaClass.getJavadoc(), methods, fields, javaClass.getJavadoc());

        try {
            String output = chat(directive,
                    "output should be in markdown format");

            return output == null ? "" : output;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private String getJavaMethodsForClass(JavaItem javaClass, List<JavaItem> javaItems) {
        final var builder = new StringBuilder();
        createMethodFilter(javaItems, javaClass)
                .forEach(javaMethod -> {
                    builder.append("\n").append(javaMethod.getDefinition()).append("\n");
                });

        return builder.toString();
    }

    private String getFieldsForClass(final JavaItem javaClass, final List<JavaItem> javaItems) {
        final var builder = new StringBuilder();
        javaItems.stream()
                .filter(javaItem -> javaItem.getType() == JavaItemType.FIELD)
                .filter(javaMethod -> javaMethod.getParent().equals(javaClass))
                .forEach(javaMethod -> {
                    builder.append("\n").append(javaMethod.getDefinition()).append("\n");
                });

        return builder.toString();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void genImageIfMissing() {
        File outputDir = new File(this.outputFile).getParentFile();
        File mermaidDir = new File(outputDir, "mermaid");
        File imagesDir = new File(outputDir, "images");



        var mermaidFiles = mermaidDir.listFiles((dir, name) -> name.endsWith(".mmd"));
        var imageFiles = mermaidDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (mermaidFiles != null && imageFiles != null) {
            System.out.println("mermaidFiles" + mermaidFiles.length + "imageFiles " + imageFiles.length);

        }



        if (mermaidFiles != null) {
            List<File> imageFileList = Arrays.stream(mermaidFiles)
                    .map(file -> new File(imagesDir, file.getName().replace(".mmd", ".png")))
                    .collect(Collectors.toList());
            imageFileList.forEach(imageFile -> {


                File mermaidFile = new File(mermaidDir, imageFile.getName().replace(".png", ".mmd"));
                String mermaidContent = FileUtils.readFile(mermaidFile);

                if (mermaidFile.lastModified() > imageFile.lastModified()) {
                    System.out.println("Regenerating " + imageFile);




                    String error = "";
                    for (int i = 0; i < 3; i++) {
                        if (!error.isBlank()) {

                            try {
                                mermaidContent =  chat(error, "output format is a mermaid sequence diagram. Do not put any other content in the response beside the fixed mermaid file");

                                mermaidContent = extractSequenceDiagram(mermaidContent);
                                System.out.println(mermaidContent);
                                FileUtils.writeFile(mermaidFile, mermaidContent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Result result = MermaidUtils.runMmdc(mermaidFile, imageFile);



                        if (result.getResult() == 0) {
                            System.out.println("SUCCESS! ###################################################");
                            break;
                        } else {
                            error = "This mermaid that you generated has errors \n```mermaid\n" + mermaidContent + "\n```\n Can you fix the " +
                                    "mermaid syntax and try again and improve descriptions? " +
                                    "please label each line as in  System-->>searchNewsFunc should be System-->>searchNewsFunc: return." +
                                    "Also activate and deactivate each participant correctly \nerrors:\n "
                                    + result.getErrors() + "\noutput:\n " + result.getOutput();

                        }
                    }

                }
            });
        }
    }

    public void genImprovements() throws Exception{

        File outputDir = new File(new File(outputFile).getParentFile(), "improve");
        outputDir.mkdirs();

        File dir = new File(inputDirectoryPath).getCanonicalFile();
        if (dir.exists() && dir.isDirectory()) {

            generateAllImprovements(outputDir);

            List<JavaItem> javaItems = scanDirectory(dir);

            Map<String, List<String>> classNameByPackage = mapPackageToClassDefs(javaItems);

            classNameByPackage.entrySet().stream().forEach(entry -> {
                final String packageName = entry.getKey();
                final String markdownForPackage = packageName.replace(".", "_") + "-improve.md";
                File markdownFileForPackage = new File(outputDir, markdownForPackage);
                if (markdownFileForPackage.exists()) {
                    return;
                }

                final StringBuilder markdownBuilder = new StringBuilder();
                markdownBuilder.append("# Package ").append(packageName).append("\n");

                createClassStream(javaItems, packageName)
                        .forEach(javaClass -> {
                                    markdownBuilder.append("## Class ").append(javaClass.getSimpleName()).append("\n");
                                    createMethodFilter(javaItems, javaClass)
                                            .forEach(javaMethod -> {
                                                methodCodeListingJava(markdownBuilder, javaMethod);
                                                final StringBuilder methodMarkdownBuilder = new StringBuilder();
                                                generateMarkdownContentForMethod("Improvements","As an software engineer writing docs list areas of improvements",  methodMarkdownBuilder, javaClass, javaMethod);
                                                generateMarkdownContentForMethod("Bugs","As an software engineer writing docs list any bugs or issues you see",  methodMarkdownBuilder, javaClass, javaMethod);
                                                generateMarkdownContentForMethod("Coding Style","As an software engineer who is an expert of programming style and best " +
                                                        "practices list any issues you see you see",  methodMarkdownBuilder, javaClass, javaMethod);

                                                try {
                                                    final var output = chat("Summarize these into a single list with " +
                                                            "one header level 2 called improvements: " + methodMarkdownBuilder,
                                                            "output should be markdown");
                                                    if (output == null || output.isBlank()) {
                                                        markdownBuilder.append(methodMarkdownBuilder);
                                                    } else {
                                                        System.out.println(output);
                                                        markdownBuilder.append(output);
                                                    }
                                                } catch (Exception e) {
                                                    markdownBuilder.append(methodMarkdownBuilder);
                                                }


                                            });
                                }
                        );

                try {
                    Files.write(markdownFileForPackage.toPath(), markdownBuilder.toString().getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });


        } else {
            throw new IllegalStateException(String.format(
                    "Directory does not exist %s or is not a directory", dir));
        }
    }


    public void genBusinessRules() throws Exception{

        File outputDir = new File(new File(outputFile).getParentFile(), "biz");
        outputDir.mkdirs();

        File dir = new File(inputDirectoryPath).getCanonicalFile();
        if (dir.exists() && dir.isDirectory()) {

            generateAll(outputDir);

            List<JavaItem> javaItems = scanDirectory(dir);

            Map<String, List<String>> classNameByPackage = mapPackageToClassDefs(javaItems);

            classNameByPackage.entrySet().stream().forEach(entry -> {
                final String packageName = entry.getKey();
                final String markdownForPackage = packageName.replace(".", "_") + "-biz.md";
                File markdownFileForPackage = new File(outputDir, markdownForPackage);
                if (markdownFileForPackage.exists()) {
                    return;
                }

                final StringBuilder markdownBuilder = new StringBuilder();
                markdownBuilder.append("# Package ").append(packageName).append("\n");

                createClassStream(javaItems, packageName)
                        .forEach(javaClass -> {
                                    markdownBuilder.append("## Class ").append(javaClass.getSimpleName()).append("\n");
                                    createMethodFilter(javaItems, javaClass)
                                            .forEach(javaMethod -> {
                                                methodCodeListingJava(markdownBuilder, javaMethod);
                                                //generateMarkdownContentForMethod("Business Rules",PRODUCT_MANAGER + "As an product manager writing docs listing any business rules if found in a table " +
                                                //        "with headers business rule and another header description",  markdownBuilder, javaClass, javaMethod);
                                                generateMarkdownContentForMethod("Concepts",PRODUCT_MANAGER + "As an product manager writing docs list any business concepts or domain objects found in this code",  markdownBuilder, javaClass, javaMethod);
                                            });
                                }
                        );

                try {
                    Files.write(markdownFileForPackage.toPath(), markdownBuilder.toString().getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });


        } else {
            throw new IllegalStateException(String.format(
                    "Directory does not exist %s or is not a directory", dir));
        }
    }

    private static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
    private static void generateMarkdownContentForMethod(String title, String direction, StringBuilder markdownBuilder, JavaItem javaClass, JavaItem javaMethod) {
        generateContentForMethod(title, direction, markdownBuilder, javaClass, javaMethod, "markdown");
    }
    private static void generateContentForMethod(String title, String direction, StringBuilder markdownBuilder, JavaItem javaClass, JavaItem javaMethod,  String outputFormat) {
        try {
            String output = "";
            for (int i = 0; i < 3; i++) {
                try {

                    final var user = String.format("%s " +
                                    "for this method %s which is defined in class %s is doing based on its BODY" +
                                    "\nBODY:\n %s \n JAVADOC FOR CLASS: \n %s \n", direction, javaMethod.getSimpleName(),
                            javaClass.getName(), javaMethod.getBody(), javaClass.getJavadoc());

                    final var  system = String.format("output should be in %s format", outputFormat);
                    output = chat(user, system);

                    if (!isBlank(output)) {
                        break;
                    }
                } catch (Exception ex) {
                    //continue;
                }

            }
            if (!isBlank(output)) {
                markdownBuilder.append("\n").append("### ").append(javaMethod.getSimpleName()).append(" ").append(title).append("\n");
                markdownBuilder.append("\n").append(output).append("\n");
            }

        } catch (Exception e) {
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

        private boolean inlineMermaid;
        private boolean useExistingMermaidIfFound;

        public Builder inlineMermaid(boolean inlineMermaid) {
            this.inlineMermaid = inlineMermaid;
            return this;
        }

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
        public DocGenerator build() {
            return new DocGenerator(this.inputDirectoryPath, this.outputFile, this.inlineMermaid, this.useExistingMermaidIfFound);
        }

        public Builder useExistingMermaidIfFound(boolean b) {
            this.useExistingMermaidIfFound = b;
            return this;
        }
    }

}
