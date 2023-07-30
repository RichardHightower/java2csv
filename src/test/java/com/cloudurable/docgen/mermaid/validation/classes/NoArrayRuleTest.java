package com.cloudurable.docgen.mermaid.validation.classes;

import com.cloudurable.docgen.mermaid.validation.RuleResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NoArrayRuleTest {

    @Test
    public void testNoArrayRuleLeft() {
        NoArrayRule rule = new NoArrayRule();

        String line = "Employee[]-- Employee";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);

        assertEquals("NoArrayRuleLeft", result.getRuleName());
        assertEquals("Arrays like Employee[]are not allowed in the relationship Employee[] try `Employee \"*\"-- Employee` instead", result.getDescription());
    }

    @Test
    public void testNoArrayRuleRight() {
        NoArrayRule rule = new NoArrayRule();

        String line = "Employee -- Employee[]";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);

        assertEquals("NoArrayRuleRight", result.getRuleName());
        assertEquals("Arrays like Employee[]are not allowed in the relationship Employee[] try `Employee -- \"*\" Employee` instead", result.getDescription());
    }
}
