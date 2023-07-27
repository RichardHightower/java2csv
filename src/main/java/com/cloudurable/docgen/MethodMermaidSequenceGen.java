package com.cloudurable.docgen;

import com.cloudurable.docgen.extract.FileUtils;
import com.cloudurable.docgen.mermaid.validation.*;
import com.cloudurable.jai.OpenAIClient;
import com.cloudurable.jai.model.text.completion.chat.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodMermaidSequenceGen {

    private final OpenAIClient client = OpenAIClient.builder().validateJson(true).setApiKey(System.getenv("OPENAI_API_KEY")).build();

    private final List<Message> context = new ArrayList<>();

    {
        Message systemMessage = Message.builder().role(Role.SYSTEM)
                .content(FileUtils.readFile(new File("templates/method_sequence_system.md")))
                .build();
        context.add(systemMessage);

        Message assistantMessage = Message.builder().role(Role.ASSISTANT)
                .content(FileUtils.readFile(new File("templates/example_sequence_diagrams.md")))
                .build();
        context.add(assistantMessage);
    }



    public String generateSequenceFromMethod(String javaMethodSource) {

        final var  builder = requesatBuilder(context);
        final var template = FileUtils.readFile(new File("templates/generate_method_mermaid_sequence_by_method.md"));
        final var instruction = template.replace("{{{JAVA_METHOD}}", javaMethodSource);
        final var  request = builder.addMessage(Message.builder().role(Role.USER).content(instruction).build()).build();


        for (int i = 0; i < 5; i++) {
            final var chatResponse = client.chat(request);
            if (chatResponse.getException().isPresent()) {
                System.out.printf("%s\n", instruction);
                chatResponse.getException().ifPresent(Throwable::printStackTrace);
            } else if (chatResponse.getStatusMessage().isPresent()) {
                chatResponse.getStatusMessage().ifPresent(status-> {
                    System.out.printf("%s %s %d\n", instruction, status,chatResponse.getStatusCode().orElse(666) );
                });
            } else if (chatResponse.getResponse().isPresent()){
                final var response = chatResponse.getResponse().get();
                final var chatChoice = response.getChoices().get(0);
                final var mermaidDiagram = extractSequenceDiagram(chatChoice.getMessage().getContent());

               return validateMermaid(javaMethodSource,  mermaidDiagram);
            }
        }
        return "";
    }

    private  String validateMermaid(String javaMethodSource,  String mermaidDiagram) {
        return validateMermaid(javaMethodSource, mermaidDiagram, 5);
    }

    private  String validateMermaid(String javaMethodSource,  String mermaidDiagram, int count) {

        if (count <= 0) {
            return mermaidDiagram;
        }

        ChatRequest.Builder builder = requesatBuilder(context);

        final var ruleRunner = buildRuleRunner();

        final var templateMermaid = FileUtils.readFile(new File("templates/generate_method_mermaid_sequence_by_method_mermaid.md"));

        final var checks = ruleRunner.checks(List.of(mermaidDiagram.split("\n")));

        if (!checks.isEmpty()) {
            final var fixInstruction = templateMermaid.replace("{{{JAVA_METHOD}}", javaMethodSource)
                    .replace("{{JSON}}", RuleRunner.serializeRuleResults(checks))
                    .replace("{{MERMAID}}", mermaidDiagram);

            final var  fixRequest = builder.addMessage(Message.builder().role(Role.USER)
                    .content(fixInstruction).build()).build();

            final var fixMermaidResponse = client.chat(fixRequest);

            if (fixMermaidResponse.getException().isPresent()) {
                System.out.printf("%s\n", fixRequest);
                fixMermaidResponse.getException().ifPresent(Throwable::printStackTrace);
            } else if (fixMermaidResponse.getStatusMessage().isPresent()) {
                fixMermaidResponse.getException().ifPresent(status-> {
                    System.out.printf("%s %s %d\n", fixInstruction, status, fixMermaidResponse.getStatusCode().orElse(666) );
                });
            }
            return validateMermaid(javaMethodSource, mermaidDiagram, count-1);

        } else {
            return mermaidDiagram;
        }
    }

    private static ChatRequest.Builder requesatBuilder(List<Message> context) {
        return ChatRequest.builder().messages(new ArrayList<>(context))
                .maxTokens(2000).temperature(0.15f).model("gpt-3.5-turbo-16k");
    }
//
//    public void buildContext(OpenAIClient client, List<Message> context, boolean length) {
//        System.out.println("Build Context");
//        ChatRequest.Builder builder = ChatRequest.builder().messages(new ArrayList<>(context)).completionCount(1)
//                .maxTokens(2000).temperature(0.15f).model("gpt-3.5-turbo");
//
//        if (length) {
//            builder.addMessage(Message.builder().role(Role.USER).content("continue").build());
//        }
//
//        final var buildContext = builder.build();
//        final var chatResponse = client.chat(buildContext);
//
//        chatResponse.getResponse().ifPresent(response -> {
//            System.out.println("Build Context Response");
//
//            ChatChoice chatChoice = response.getChoices().get(0);
//            if (chatChoice.getFinishReason() == FinishReason.STOP) {
//                Message message = chatChoice.getMessage();
//                System.out.println("Build Context Response STOP");
//
//                context.add(message);
//                System.out.println(message);
//            } else if (chatChoice.getFinishReason() == FinishReason.LENGTH) {
//                System.out.println("Build Context Response LENGTH \n" + response);
//
//                buildContext(client, context, true);
//            } else {
//                throw new IllegalStateException();
//            }
//        });
//
//        chatResponse.getStatusMessage().ifPresent(status -> {
//                    System.out.printf("%s %d\n", status, chatResponse.getStatusCode().orElse(666));
//                }
//        );
//
//        chatResponse.getException().ifPresent(err -> err.printStackTrace());
//    }


    private static RuleRunner buildRuleRunner() {
        RuleRunner ruleRunner;
        List<Rule> rules = new ArrayList<>();
        rules.add(new AvoidNotesRule());
        rules.add(new NoMethodCallsInDescriptionsRule());
        rules.add(new AvoidActivateDeactivateRule());
        rules.add(new ParticipantAliasRule());
        rules.add(new SystemOutRule());
        rules.add(new DataClassesAndPrimitiveRule());
        ruleRunner = RuleRunner.builder().rules(rules).build();
        return ruleRunner;
    }

    public static String extractSequenceDiagram(String mermaidCode) {

        final var lines = mermaidCode.split("\n");
        final var extractedCode = new StringBuilder();

        boolean foundStart = false;

        for (String line : lines) {

            if (line.startsWith("```mermaid")) {
                foundStart = true;
            } else if (foundStart ) {
                if (line.startsWith("```")) {
                    break;
                }
                extractedCode.append(line).append("\n");
            }
        }

        return extractedCode.toString();
    }

}
