package com.cloudurable.docgen;

import com.cloudurable.jai.OpenAIClient;
import com.cloudurable.jai.model.text.completion.chat.ChatRequest;
import com.cloudurable.jai.model.text.completion.chat.Message;
import com.cloudurable.jai.model.text.completion.chat.Role;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class responsible for processing Java files from a directory and converting them into a CSV format.
 * This is done by using the Builder pattern to set the input directory path and output file name.
 */
public class DocGenerator {


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
    public DocGenerator(String directoryPath, String outputFile) {
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
    public static CompletableFuture<String> chat(String input, String system) {


//        System.out.println("INPUT\n" + input);
//        System.out.println("SYSTEM\n" + system);

        final var client = OpenAIClient.builder().setApiKey(System.getenv("OPENAI_API_KEY")).build();

        final var chatRequest = ChatRequest.builder().addMessage(Message.builder().role(Role.SYSTEM).content(
                        system.replace("\n", "\\n").replace("\t", "\\t"))
                .build()).addMessage(Message.builder().role(Role.USER).content(
                input.replace("\n", "\\n").replace("\t", "\\t")).build()).build();

        return client.chatAsync(chatRequest).thenApply(chat -> {
            if (chat.getResponse().isPresent()) {
                String output = chat.getResponse().get().getChoices().get(0).getMessage().getContent();
                //System.out.println("OUTPUT\n" + output);
                return output;
            } else {
                System.out.println(chat.getStatusCode().orElse(666) + " " + chat.getStatusMessage().orElse(""));
                throw new IllegalStateException(chat.getStatusCode().orElse(666) + " " + chat.getStatusMessage().orElse(""));
            }
        });
    }

    public static Result runMmdc(File input, File output) {
        String command = "/opt/homebrew/bin/mmdc -i " + input.toString() + " -o " + output + " -s 2 -b white";
        ExecutorService executorService = Executors.newCachedThreadPool();

        final AtomicReference<StringBuilder> outputRef = new AtomicReference<>(new StringBuilder());
        final AtomicReference<StringBuilder> errorRef = new AtomicReference<>(new StringBuilder());
        final CountDownLatch latch = new CountDownLatch(2);

        try {
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(command);


            executorService.submit(() -> {
                StringBuilder outputBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while (true) {
                    try {
                        if (!((line = reader.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }
                    outputBuilder.append(line).append("\n");
                    System.out.println(line);
                }
                outputRef.set(outputBuilder);
                latch.countDown();
            });

            executorService.submit(() -> {
                StringBuilder outputBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while (true) {
                    try {
                        if (!((line = reader.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }
                    outputBuilder.append(line).append("\n");
                    System.err.println(line);
                }
                errorRef.set(outputBuilder);
                latch.countDown();
            });

            int exitCode = process.waitFor();

            if (latch.await(30, TimeUnit.SECONDS)) {
                executorService.shutdown();
                return new Result(exitCode, outputRef.get().toString(), errorRef.get().toString(), null, true);
            } else {
                executorService.shutdown();
                return new Result(exitCode, outputRef.get().toString(), errorRef.get().toString(), null, false);
            }

        } catch (IOException | InterruptedException e) {
            return new Result(-1, outputRef.get().toString(), errorRef.get().toString(), e, false);
        }
    }

    private static void getGenerateUMLClassDiagramForPackage(File mermaid, File images,
                                                             String packageName, List<String> classDefs, StringBuilder markdownBuilder) throws Exception {

        final String mermaidInstructions = "Only put mermaid output in the output. Do not put any explanation. Just the mermaid output. The output is only mermaid markup. Do not add any non mermaid output. Do not add extends to the class defintion, but only the association. " +
                "\nYou are a software engineer writing up documentation for a project. " +
                "For a given package a class definitions create a class diagram in mermaid using the following example given below. " +
                "All output should be in mermaid format. Here is an example input\n" +
                "SAMPLE INPUT:\n " +
                "for package com.cloud.text.completion.chat create a class diagram in mermaid, add the package name as the title\n" +
                "public class Message \n" +
                "\tprivate final Role role;\n" +
                "\tprivate final String content;\n" +
                "\tprivate final String name;\n" +
                "\tprivate final FunctionalCall functionCall;\n" +
                "public class ChatRequest extends CommonCompletionRequest \n" +
                "\tprivate final List<Message> messages;\n" +
                "\tprivate final List<FunctionDef> functions;\n" +
                "\tprivate final FunctionalCall functionalCall;\n" +
                "\n" +
                "SAMPLE OUTPUT:\n" +
                "---\n" +
                "title: Package Chat (com.cloud.text.completion.chat)\n" +
                "---\n" +
                "classDiagram\n" +
                "    class Message{\n" +
                "        -Role role\n" +
                "        -String content\n" +
                "        -String name\n" +
                "        -FunctionalCall functionCall\n" +
                "    }\n" +
                "\n" +
                "    class ChatRequest{\n" +
                "        -List<Message> messages\n" +
                "        -List<FunctionDef> functions\n" +
                "        -FunctionalCall functionalCall\n" +
                "    }\n" +
                "\n" +
                "    CommonCompletionRequest <|-- ChatRequest: extends\n" +
                "    ChatRequest \"1\" o-- \"*\" Message: has-many\n" +
                "    ChatRequest \"1\" o-- \"*\" FunctionDef: has-many\n" +
                "    ChatRequest \"1\" o-- \"1\" FunctionalCall: has-a\n" +
                "To create UML class diagrams using Mermaid syntax, follow these concise guidelines:\n" +
                "Start by defining the class diagram using the classDiagram keyword.\n" +
                "Define a class using the class keyword followed by the class name.\n" +
                "Specify attributes and methods inside the class using - for attributes and + for methods.\n" +
                "Use the appropriate data types for attributes and specify the return type for methods.\n" +
                "Separate attributes and methods with line breaks.\n" +
                "Add optional annotation text to describe the class.\n" +
                "Use different symbols to represent relationships:\n" +
                "Inheritance: Use --|> between the derived class and the base class.\n" +
                "Composition: Use *-- between the container class and the contained class.\n" +
                "Aggregation: Use o-- between the container class and the contained class.\n" +
                "Dependency: Use ..> between the dependent class and the dependency.\n" +
                "Association: Use -- between two classes.\n" +
                "Use the appropriate syntax for each relationship.<<Interface>> To represent an Interface class\n" +
                "Use <<Abstract>> To represent an abstract class\n" +
                "Use <<Service>> To represent a service class\n" +
                "Use <<Enumeration>> To represent an enum\n" +
                "Use <<interface>>  to represent an interface" +
                "Here's an example of concise Mermaid code for a class diagram with different relationships:" +
                "\n```mermaid" +
                "classDiagram\n" +
                "class Animal {\n" +
                "  name String\n" +
                "  +eat() void\n" +
                "}\n" +
                "\n" +
                "class Shape {\n" +
                "    <<interface>>\n" +
                "}\n" +
                "\n" +
                "class Mammal{\n" +
                "  - furColor: String\n" +
                "}\n" +
                "\n" +
                "class Zoo {\n" +
                "  name String\n" +
                "}\n" +
                "\n" +
                "class Car {\n" +
                "    make String\n" +
                "    model String\n" +
                "}\n" +
                "\n" +
                "class Student {\n" +
                "    -idCard : IdCard\n" +
                "}\n" +
                "\n" +
                "class IdCard{\n" +
                "    -id : int\n" +
                "    -name : string\n" +
                "}\n" +
                "\n" +
                "class Bike{\n" +
                "    -id : int\n" +
                "    -name : string\n" +
                "}\n" +
                "\n" +
                "Square --|> Shape : implements\n" +
                "Student \"1\" --o \"1\" IdCard : carries\n" +
                "Student \"1\" --o \"1\" Bike : rides\n" +
                "Mammal --|> Animal\n" +
                "Zoo \"1\" --* \"*\" Animal\n" +
                "Zoo \"1\" -- \"1\" ZooKeeper\n" +
                "Car \"1\" --* \"1\" Engine\n" +
                "Car ..> Driver" +
                "```\n" +
                "In this example:\n" +
                "* Mammal inherits from Animal using --|>.\n" +
                "* Zoo has a composition relationship with Animal using *--.\n" +
                "* Zoo has an association relationship with ZooKeeper using --.\n" +
                "* Car has an aggregation relationship with Engine using *--.\n" +
                "* Car has a dependency relationship with Driver using ..>.\n" +
                "* Student has a composition relationship with IdCard using --o.\n" +
                "* Student has a composition relationship with Bike using --o.\n"
                ;


        final String imageForPackage = packageName.replace(".", "_") + ".png";



        File mermaidFile = new File(mermaid, packageName.replace(".", "_") + ".mmd");
        File pngFile = new File(images, imageForPackage);


        String error = "";
        String mermaidContent = "";
        String classDefsStr = String.join("\n", classDefs.toArray(new String[0]));
        for (int i =0; i < 3; i++) {
            String message = String.format("Create a class UML class diagram for package %s. Here are the classes for this UML diagram:\n%s\n%s",
                    packageName,
                    classDefsStr,
                    error);


            try {
                mermaidContent = chat(message, mermaidInstructions).get();
                mermaidContent = mermaidContent.replace("interface ", "class ");
                mermaidContent = mermaidContent.replace("abstract class ", "class ");
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
            Files.write(mermaidFile.toPath(), mermaidContent.getBytes(StandardCharsets.UTF_8));
            System.out.println(mermaidContent);
            Result result = runMmdc(mermaidFile, pngFile);
            if (result.result == 0) {
                break;
            } else {
                System.err.println("ERROR $$$$$$$$$$$$ \n\n" + mermaidContent);
                error = "\n Error generating UML class diagram with the mermaid you generated, can you try again? errors\n "
                        + result.errors + "\noutput\n " + result.errors;
            }
        }

        if (pngFile.exists()) {
            markdownBuilder.append("![class diagram](./images/" + imageForPackage + ")\n");
        }

    }

    public void extractClasses() throws IOException {

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
                                    markdownBuilder.append("## Class: " + javaClass.getSimpleName()).append("\n");

                                    markdownBuilder.append("\n**" + javaClass.getName()).append("**\n");

                                    try {
                                        String output = chat(String.format("As an software engineer writing docs create a short description of this class based on its DEFINITION and JAVADOC, " +
                                                        "just combine them and create valid markdown which is a short description of this class. Do not mention JavaDoc. Just use the JavaDoc to describe the class" +
                                                        "\nDEFINITION:\n %s \n" +
                                                        "\nJAVADOC:\n %s \n", javaClass.getDefinition(), javaClass.getJavadoc()),
                                                "output should be in markdown format").get();

                                        markdownBuilder.append("\n").append(output).append("\n");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    createMethodFilter(javaItems, javaClass)
                                            .forEach(javaMethod -> {
                                                methodCodeListingJava(markdownBuilder, javaMethod);

                                                try {
                                                    String output = chat(String.format("As an software engineer writing docs create a brief description " +
                                                                    "of what this method does, method = %s (%s) which is defined in class %s is doing based on its BODY" +
                                                                    "\nBODY:\n %s \n", javaMethod.getSimpleName(), javaMethod.getName(), javaClass.getName(), javaMethod.getBody()),
                                                            "output should be in markdown format").get();

                                                    if (output != null && !output.isBlank()) {
                                                        markdownBuilder.append("\n").append("### ").append(javaMethod.getSimpleName()).append(" Overview \n");
                                                        markdownBuilder.append("\n").append(output).append("\n");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }



                                                final String imageForMethod = javaMethod.getName().replace(".", "_") + ".png";
                                                final String mermaidSeqForMethod = javaMethod.getName().replace(".", "_") + ".mmd";

                                                File mermaidMethodFile = new File(mermaid, mermaidSeqForMethod);
                                                File pngMethodFile = new File(images, imageForMethod);

                                                String methodInstruction = String.format("You will create a sequence mermaid diagram for a given method. \n" +
                                                        "Here's a concise guide for creating Mermaid sequence diagrams:\n" +
                                                        "Start by defining the sequence diagram using the sequenceDiagram keyword." +
                                                        "Don't use fully qualified class names instead put the package name in a title \n" +
                                                        "Define participants using the participant keyword followed by the participant name.\n" +
                                                        "Use arrows to represent messages between participants.\n" +
                                                        "Use -> for synchronous messages.\n" +
                                                        "Use --> for asynchronous messages.\n" +
                                                        "Use ->> for response messages.\n" +
                                                        "Use -->> for asynchronous response messages.\n" +
                                                        "Use the appropriate syntax for each message.\n" +
                                                        "Use the Note keyword to add notes to the diagram.\n" +
                                                        "Use Note right of to add a note to the right of a participant.\n" +
                                                        "Use Note left of to add a note to the left of a participant.\n" +
                                                        "Use Note over to add a note over two or more participants.\n" +
                                                        "Use the loop keyword to create loops in the diagram.\n" +
                                                        "Use the alt and opt keywords to create alternative paths in the diagram.\n" +
                                                        "Use the title keyword to add a title to the diagram.\n" +
                                                        "Here's an example of Mermaid code for a sequence diagram:\n" +
                                                        "sequenceDiagram\n" +
                                                                "    participant A\n" +
                                                                "    participant B\n" +
                                                                "    Note right of A: A note\n" +
                                                                "    A->>B: Synchronous message\n" +
                                                                "    B-->A: Asynchronous message\n" +
                                                                "    B->>A: Response message\n" +
                                                                "    A-->>B: Asynchronous response message\n" +
                                                                "    Note left of B: Another note\n" +
                                                                "    loop Loop example\n" +
                                                                "        alt Alternative example\n" +
                                                                "            A->>B: Option 1\n" +
                                                                "        else Option 2\n" +
                                                                "            break when the booking process fails\n" +
                                                                "                API-->Consumer: show failure\n" +
                                                                "            end\n" +
                                                                "        end\n" +
                                                                "        A->>B: Loop message\n" +
                                                                "    end\n" +
                                                                "    title Sequence diagram example\n" +
                                                                "    B->A: A very long message that needs to be broken\n" +
                                                        "\nAs an software engineer create a UML sequence diagram for this method %s for class %s \nBODY:\n %s \n", javaMethod.getSimpleName(), javaClass.getName(), javaMethod.getBody());

                                                try {

                                                    String errors = "";

                                                    for (int i = 0; i < 5; i++) {

                                                        String output = chat(methodInstruction,
                                                                    "output should be in mermaid format and only mermaid format no markdown DO NO put in the text ```mermaid " + errors).get();


                                                        if (output == null || output.isBlank()) {
                                                                continue;
                                                        }



                                                        output = extractSequenceDiagram(output);
                                                        Files.write(mermaidMethodFile.toPath(), output.getBytes(StandardCharsets.UTF_8));

                                                        Result result = runMmdc(mermaidMethodFile, pngMethodFile);
                                                        if (result.result != 0 || result.exception != null) {
                                                            errors = String.format("Please just valid mermaid as output. That mermaid text you sent failed, error output from mermaid %s output from mermaid %s ", result.errors, result.output);
                                                        } else {
                                                            break;
                                                        }
                                                    }

                                                    if (pngMethodFile.exists()) {
                                                        markdownBuilder.append("\n![sequence diagram](./images/" + imageForMethod + ")\n\n");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                                try {
                                                    String output = chat(String.format("As an software engineer writing docs create a step by step but concise description " +
                                                                    "of this method %s which is defined in class %s is doing based on its BODY" +
                                                                    "\nBODY:\n %s \n", javaMethod.getSimpleName(), javaClass.getName(), javaMethod.getBody()),
                                                            "output should be in markdown format").get();

                                                    if (output != null && !output.isBlank()) {
                                                        markdownBuilder.append("\n").append("### ").append(javaMethod.getSimpleName()).append(" Step by Step  \n");
                                                        markdownBuilder.append("\n").append(output).append("\n");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
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
                        "output should be in JavaDoc format").get();

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
                    "output should be in JavaDoc format").get();

            markdownBuilder.append("\n\n```java\n").append(classJavadocTmp).append("\n```\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classJavadocTmp;
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

        var files = mermaidDir.listFiles((dir, name) -> name.endsWith(".mmd"));

        if (files != null) {
            List<File> imageFiles = Arrays.stream(files)
                    .map(file -> new File(imagesDir, file.getName().replace(".mmd", ".png")))
                    .filter(imageFile -> !imageFile.exists())
                    .collect(Collectors.toList());
            System.out.println("These images were missing! " + imageFiles.size());
            imageFiles.forEach(imageFile -> {
                System.out.println(imageFile);
                File mermaidFile = new File(mermaidDir, imageFile.getName().replace(".png", ".mmd"));
                runMmdc(mermaidFile, imageFile);
            });
        }
    }

    public void provideImprovements() throws Exception{

        File outputDir = new File(new File(outputFile).getParentFile(), "improve");
        outputDir.mkdirs();

        File dir = new File(inputDirectoryPath).getCanonicalFile();
        if (dir.exists() && dir.isDirectory()) {

            generateAll(outputDir);

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
                                                    final var output = chat("Summarize these into a single list with one header level 2 called improvements: " + methodMarkdownBuilder, "output should be markdown").get();
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
                    output = chat(String.format("%s " +
                                            "for this method %s which is defined in class %s is doing based on its BODY" +
                                            "\nBODY:\n %s \n JAVADOC FOR CLASS: \n %s \n", direction, javaMethod.getSimpleName(),
                                    javaClass.getName(), javaMethod.getBody(), javaClass.getJavadoc()),
                            String.format("output should be in %s format", outputFormat)).get();

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

    public static class Result {
        private final int result;
        private final String output;
        private final String errors;

        private final Exception exception;

        private final boolean complete;

        public Result(int result, String output, String errors, Exception exception, boolean complete) {
            this.result = result;
            this.output = output;
            this.errors = errors;
            this.exception = exception;
            this.complete = complete;
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
        public DocGenerator build() {
            return new DocGenerator(this.inputDirectoryPath, this.outputFile);
        }
    }

}