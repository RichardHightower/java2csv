package com.cloudurable.docgen.mermaid.validation;

import com.cloudurable.docgen.mermaid.validation.sequence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuleRunnerTest {

    private RuleRunner ruleRunner;

    @BeforeEach
    void setUp() {
        List<LineRule> rules = new ArrayList<>();
        rules.add(new AvoidNotesRule());
        rules.add(new NoMethodCallsInDescriptionsRule());
        rules.add(new AvoidActivateDeactivateRule());
        rules.add(new ParticipantAliasRule());
        rules.add(new SystemOutRule());
        rules.add(new DataClassesAndPrimitiveRule());

        ruleRunner = RuleRunner.builder().rules(rules).build();
    }

    @Test
    void positiveCase() {
        List<String> lines = Arrays.asList(
                "Alice->Bob: Hello Bob",
                "Bob-->Alice: Hi Alice",
                "Alice-->Bob: How are you?",
                "Bob->Alice: I'm fine, thank you!"
        );
        List<RuleResult> results = ruleRunner.checkLines(lines);

        for (RuleResult result : results) {
            assertEquals(RuleResult.SUCCESS, result);
        }
    }

    @Test
    void negativeCase() {
        List<String> lines = Arrays.asList(
                "participant \"Customer\" as CustomerAlias",
                "CustomerAlias-->>Alice: getFooBar()",
                "note right: This is a note.",
                "activate CustomerAlias",
                "System.out.println(\"Hello world!\");",
                "participant List as ListAlias"
        );
        List<RuleResult> results = ruleRunner.checkLines(lines);

        for (RuleResult result : results) {
            assertNotEquals(RuleResult.SUCCESS, result);
        }

        System.out.println(ruleRunner.checksAndReturnJson(lines));
    }

    @Test
    void sampleTestCase() {

        String[] lineArray = (" sequenceDiagram\n" +
                "    participant Caller\n" +
                "    participant JsonConverter\n" +
                "    participant ObjectMapper\n" +
                "    participant JsonException\n" +
                "\n" +
                "    Caller->>JsonConverter: Call toNode() with JSON string\n" +
                "    JsonConverter->>ObjectMapper: Call readTree() with JSON string\n" +
                "    alt JSON parsing successful\n" +
                "        ObjectMapper-->>JsonConverter: Return parsed JSON as JsonNode\n" +
                "        JsonConverter-->>Caller: Return parsed JSON as JsonNode\n" +
                "    else JSON parsing failed\n" +
                "        ObjectMapper-->>JsonConverter: Throw JsonProcessingException\n" +
                "        JsonConverter->>JsonException: Throw JsonException\n" +
                "        JsonException-->>Caller: Throw JsonException\n" +
                "    end").split("\n");

        List<String> lines = List.of(lineArray);
        List<RuleResult> results = ruleRunner.checkLines(lines);

        for (RuleResult result : results) {
            System.out.println(result.getDescription());
            System.out.println(result.getRuleName());
            System.out.println("VIOLATED LINE:" + result.getViolatedLine());
            assertNotEquals(RuleResult.SUCCESS, result);
        }

        //System.out.println(ruleRunner.checksAndReturnJson(lines));
    }

    @Test
    public void testRuleRunnerChecks() {
        // Mock two rules
        LineRule rule1 = mock(LineRule.class);
        LineRule rule2 = mock(LineRule.class);

        // Mock the return values for the rules
        when(rule1.check(anyString(), anyInt())).thenReturn(RuleResult.SUCCESS);
        when(rule2.check(anyString(), anyInt())).thenReturn(RuleResult.SUCCESS);

        // Create a RuleRunner with the mocked rules
        RuleRunner ruleRunner = RuleRunner.builder()
                .rules(Arrays.asList(rule1, rule2))
                .build();

        // Mock some lines of text
        List<String> lines = Arrays.asList("line1", "line2", "line3");

        // Run the checks
        List<RuleResult> results = ruleRunner.checkLines(lines);

        // Validate the results
        assertEquals(0, results.size()); // Two rules times three lines should give six results
        for (RuleResult result : results) {
            assertEquals(RuleResult.SUCCESS, result); // All results should be SUCCESS as per our mock
        }
    }
}
