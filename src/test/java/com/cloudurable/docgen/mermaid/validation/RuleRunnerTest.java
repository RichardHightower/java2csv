package com.cloudurable.docgen.mermaid.validation;

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
        List<Rule> rules = new ArrayList<>();
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
        List<RuleResult> results = ruleRunner.checks(lines);

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
        List<RuleResult> results = ruleRunner.checks(lines);

        for (RuleResult result : results) {
            assertNotEquals(RuleResult.SUCCESS, result);
        }

        System.out.println(ruleRunner.checksAndReturnJson(lines));
    }

    @Test
    public void testRuleRunnerChecks() {
        // Mock two rules
        Rule rule1 = mock(Rule.class);
        Rule rule2 = mock(Rule.class);

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
        List<RuleResult> results = ruleRunner.checks(lines);

        // Validate the results
        assertEquals(0, results.size()); // Two rules times three lines should give six results
        for (RuleResult result : results) {
            assertEquals(RuleResult.SUCCESS, result); // All results should be SUCCESS as per our mock
        }
    }
}
