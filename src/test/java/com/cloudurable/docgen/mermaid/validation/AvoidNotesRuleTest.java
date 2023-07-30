package com.cloudurable.docgen.mermaid.validation;

import com.cloudurable.docgen.mermaid.validation.sequence.AvoidNotesRule;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AvoidNotesRuleTest {

    @Test
    public void testNoteLine() {
        LineRule rule = new AvoidNotesRule();
        String line = " Note over Json: This is a note.";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    public void testVariant() {
        LineRule rule = new AvoidNotesRule();
        String line = "Note over ObjectMapper,JsonException: Handle error if IOException occurs.";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    public void testRegularLine() {
        LineRule rule = new AvoidNotesRule();
        String line = "Alice->Bob: Hello";
        RuleResult result = rule.check(line, 1);
        assertEquals(RuleResult.SUCCESS, result);
    }
}
