package com.cloudurable.docgen.mermaid.validation;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataClassesAndPrimitiveRuleTest {
    private Rule rule;
    private int lineNumber;

    @BeforeEach
    void setUp() {
        rule = new DataClassesAndPrimitiveRule();
        lineNumber = 1;
    }

    @Test
    void testCheckReturnsSuccessWhenLineDoesNotContainRestrictedWords() {
        String line = "participant \"Customer\"";
        RuleResult result = rule.check(line, lineNumber);
        assertEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testCheckReturnsFailureWhenLineContainsRestrictedWords() {
        String line = "participant \"Customer\" as int";
        RuleResult result = rule.check(line, lineNumber);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testCheckReturnsFailureWhenLineContainsDataClass() {
        String line = "participant \"Customer\" as List";
        RuleResult result = rule.check(line, lineNumber);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void makeSureListIsOkIfPartOfAnotherWord() {
        String line = "participant \"Customer\" as CustomerList";
        RuleResult result = rule.check(line, lineNumber);
        assertEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testCheckReturnsFailureWhenLineContainsSquareBrackets() {
        String line = "participant \"Customer\" as []";
        RuleResult result = rule.check(line, lineNumber);
        assertNotEquals(RuleResult.SUCCESS, result);
    }

    @Test
    void testCheckReturnsSuccessWhenLineContainsRestrictedWordsCaseInsensitive() {
        String line = "participant \"Customer\" as Byte";
        RuleResult result = rule.check(line, lineNumber);
        assertNotEquals(RuleResult.SUCCESS, result);
    }
}
