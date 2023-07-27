package com.cloudurable.docgen.mermaid.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class NoMethodCallsInDescriptionsRuleTest {

    @Test
    public void testRuleViolated() {
        Rule rule = new NoMethodCallsInDescriptionsRule();
        String line = "Foo -> Bar : getFooBar()";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    public void testMethodWithArgs() {
        Rule rule = new NoMethodCallsInDescriptionsRule();
        String line = "JsonConverter->>ObjectMapper: Call readTree(json)";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    public void testMethodWith2Args() {
        Rule rule = new NoMethodCallsInDescriptionsRule();
        String line = "    Caller->>ObjectMapper: toObject(content, valueType)";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    public void testMethodWith3Args() {
        Rule rule = new NoMethodCallsInDescriptionsRule();
        String line = "    Caller->>ObjectMapper: toObject(content, valueType, foo)";
        RuleResult result = rule.check(line, 1);
        assertNotEquals(RuleResult.SUCCESS, result);
    }


    @Test
    public void testRuleNotViolated() {
        Rule rule = new NoMethodCallsInDescriptionsRule();
        String line = "Foo -> Bar : Getting some foo from bar";
        RuleResult result = rule.check(line, 1);
        assertEquals(RuleResult.SUCCESS, result);
    }
}
