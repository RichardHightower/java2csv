package com.cloudurable.docgen.mermaid.validation;


import com.cloudurable.docgen.mermaid.validation.sequence.ParticipantAliasRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantAliasRuleTest {
    private ParticipantAliasRule rule;
    private int lineNumber = 1;

    @BeforeEach
    void setUp() {
        rule = new ParticipantAliasRule();
    }

    @Test
    void testRuleCheckSuccess() {
        String line = "participant \"Customer\"";
        RuleResult result = rule.check(line, lineNumber);
        assertEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void someOtherLine() {
        String line = "Foo -> Bar : Crap";
        RuleResult result = rule.check(line, lineNumber);
        assertEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testRuleCheckFailure() {
        String line = "participant \"Customer\" as C";
        RuleResult result = rule.check(line, lineNumber);
        assertNotEquals(RuleResult.SUCCESS, result);
        assertEquals(line, result.getViolatedLine());
        assertEquals(lineNumber, result.getLineNumber());
        assertEquals("Participant Alias Rule", result.getRuleName());
        assertEquals("Avoid participant aliases in diagrams. Use original class/object names from code.", result.getDescription());
    }
}
