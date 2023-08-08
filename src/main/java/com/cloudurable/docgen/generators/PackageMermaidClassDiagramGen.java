package com.cloudurable.docgen.generators;

import com.cloudurable.docgen.extract.FileUtils;
import com.cloudurable.docgen.mermaid.validation.*;
import com.cloudurable.docgen.mermaid.validation.classes.NoArrayRule;
import com.cloudurable.docgen.mermaid.validation.classes.NoCollectionRule;
import com.cloudurable.docgen.mermaid.validation.classes.NoPrimitiveOrBasicTypesRule;
import com.cloudurable.jai.OpenAIClient;
import com.cloudurable.jai.model.text.completion.chat.ChatRequest;
import com.cloudurable.jai.model.text.completion.chat.Message;
import com.cloudurable.jai.model.text.completion.chat.Role;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackageMermaidClassDiagramGen {

    private final OpenAIClient client;
    private final List<Message> context = new ArrayList<>();

    public  PackageMermaidClassDiagramGen(){

        client = OpenAIClient.builder().validateJson(true).setApiKey(System.getenv("OPENAI_API_KEY")).build();

        final var systemMessage = Message.builder().role(Role.SYSTEM)
                .content(FileUtils.readFile(new File("templates/classes/system.md")))
                .build();
        context.add(systemMessage);

        final var assistantMessage = Message.builder().role(Role.ASSISTANT)
                .content(FileUtils.readFile(new File("templates/classes/examples.md")))
                .build();
        context.add(assistantMessage);

    }


    private static ChatRequest.Builder requesatBuilder(List<Message> context) {
        return ChatRequest.builder().messages(new ArrayList<>(context))
                .maxTokens(2000).temperature(0.15f).model("gpt-3.5-turbo-16k-0613");
    }

    private static RuleRunner buildRuleRunner() {
        RuleRunner ruleRunner;
        ruleRunner = RuleRunner.builder()//.contentRules(List.of(MermaidUtils.createRule()))
                .rules(List.of(new NoCollectionRule(), new NoPrimitiveOrBasicTypesRule(), new NoArrayRule()))
                .build();
        return ruleRunner;
    }

    public static String extractMermaidDiagram(String mermaidCode) {

        final var lines = mermaidCode.split("\n");
        final var extractedCode = new StringBuilder();

        boolean foundStart = false;

        for (String line : lines) {

            if (line.startsWith("```mermaid")) {
                foundStart = true;
            } else if (foundStart) {
                if (line.startsWith("```")) {
                    break;
                }
                extractedCode.append(line).append("\n");
            }
        }

        return extractedCode.toString();
    }

    public String generateClassDiagramFromPackage(String packageName, String source) {

        final var title = "Package " + packageName;
        final var builder = requesatBuilder(context);
        final var template = FileUtils.readFile(new File("templates/classes/instruct.md"));
        final var instruction = template.replace("{{JAVA_CODE}}", source)
                .replace("{{TITLE}}", title);
        final var request = builder.addMessage(Message.builder().role(Role.USER).content(instruction).build()).build();


        return runMermaidValidationFeedbackLoop(source, title, instruction, request);
    }

    private String runMermaidValidationFeedbackLoop(String source, String title, String instruction, ChatRequest request) {
        for (int i = 0; i < 5; i++) {
            final var chatResponse = client.chat(request);
            if (chatResponse.getException().isPresent()) {
                System.out.printf("%s\n", instruction);
                chatResponse.getException().ifPresent(Throwable::printStackTrace);
            } else if (chatResponse.getStatusMessage().isPresent()) {
                chatResponse.getStatusMessage().ifPresent(status -> {
                    System.out.printf("%s %s %d\n", instruction, status, chatResponse.getStatusCode().orElse(666));
                });
            } else if (chatResponse.getResponse().isPresent()) {
                final var response = chatResponse.getResponse().get();
                final var chatChoice = response.getChoices().get(0);
                final var original = chatChoice.getMessage().getContent();
                final var mermaidDiagram = extractMermaidDiagram(original);
                System.out.println(mermaidDiagram);
                return validateMermaid(source, mermaidDiagram, title);
            }
        }
        return "";
    }


    private String validateMermaid(String source, String mermaidDiagram, String title) {
        return validateMermaid(source, mermaidDiagram, title, 5);
    }

    private String validateMermaid(String source, String mermaidDiagram, String title, int count) {

        if (count <= 0) {
            return mermaidDiagram;
        }

        ChatRequest.Builder builder = requesatBuilder(context);

        final var ruleRunner = buildRuleRunner();

        final var templateMermaid = FileUtils.readFile(new File("templates/classes/fix.md"));

        final var checks = ruleRunner.checkContent(mermaidDiagram);

        if (!checks.isEmpty()) {
            final var fixInstruction = templateMermaid
                    .replace("{{JAVA_CODE}}", source)
                    .replace("{{JSON}}", RuleRunner.serializeRuleResults(checks))
                    .replace("{{MERMAID}}", mermaidDiagram)
                    .replace("{{TITLE}}", title);

            System.out.println("FIX \n\n\n-----------" + fixInstruction + "\n------------\n------------");

            final var fixRequest = builder.addMessage(Message.builder().role(Role.USER)
                    .content(fixInstruction).build()).build();

            final var fixMermaidResponse = client.chat(fixRequest);

            if (fixMermaidResponse.getException().isPresent()) {
                System.out.printf("%s\n", fixRequest);
                fixMermaidResponse.getException().ifPresent(Throwable::printStackTrace);
            } else if (fixMermaidResponse.getStatusMessage().isPresent()) {
                fixMermaidResponse.getException().ifPresent(status -> {
                    System.out.printf("%s %s %d\n", fixInstruction, status, fixMermaidResponse.getStatusCode().orElse(666));
                });
            }
            return validateMermaid(source, mermaidDiagram, title, count - 1);

        } else {
            return mermaidDiagram;
        }
    }

}
