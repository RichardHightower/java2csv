package com.cloudurable.docgen.mermaid.validation;


import com.cloudurable.docgen.mermaid.validation.sequence.SystemOutRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemOutRuleTest {
    private SystemOutRule rule;
    private int lineNumber = 1;

    @BeforeEach
    void setUp() {
        rule = new SystemOutRule();
    }

    @Test
    void testRuleCheckSuccess() {
        String line = "SomeCode -> SomeCode : Foo";
        RuleResult result = rule.check(line, lineNumber);
        assertEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testRuleCheckFailure() {
        String line = "System.out.println(\"Hello World!\");";
        RuleResult result = rule.check(line, lineNumber);
        assertNotEquals(RuleResult.SUCCESS, result);
        assertEquals(line, result.getViolatedLine());
        assertEquals(lineNumber, result.getLineNumber());
        assertEquals("System Out Rule", result.getRuleName());
        assertEquals("Avoid using System.out in your code.", result.getDescription());
    }
}
