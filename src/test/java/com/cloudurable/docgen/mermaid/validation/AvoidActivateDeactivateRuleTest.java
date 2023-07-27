package com.cloudurable.docgen.mermaid.validation;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;


public class AvoidActivateDeactivateRuleTest {


    @Test
    void testActivateBeforeColon() {
        Rule rule = new AvoidActivateDeactivateRule();
        String line = "activate: This is a test.";
        RuleResult result = rule.check(line, 4);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testDeactivateBeforeColon() {
        Rule rule = new AvoidActivateDeactivateRule();
        String line = "deactivate: This is a test.";
        RuleResult result = rule.check(line, 4);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testActivateAfterColon() {
        Rule rule = new AvoidActivateDeactivateRule();
        String line = "This is a test: activate";
        RuleResult result = rule.check(line, 4);
        assertEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testDeactivateAfterColon() {
        Rule rule = new AvoidActivateDeactivateRule();
        String line = "This is a test: deactivate";
        RuleResult result = rule.check(line, 4);
        assertEquals(RuleResult.SUCCESS, result);
    }


    @Test
    public void testRuleViolated() {
        Rule rule = new AvoidActivateDeactivateRule();
        String line = "activate Foo";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    public void testRuleViolateDeactivate() {
        Rule rule = new AvoidActivateDeactivateRule();
        String line = "deactivate Foo";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    public void testRuleNotViolated() {
        Rule rule = new AvoidActivateDeactivateRule();
        String line = " A ->  B : Hello";
        RuleResult result = rule.check(line, 1);
        assertEquals(RuleResult.SUCCESS, result);
    }
}
