package com.cloudurable.docgen.mermaid.validation.classes;

import static org.junit.jupiter.api.Assertions.*;

import com.cloudurable.docgen.mermaid.validation.RuleResult;
import org.junit.jupiter.api.Test;


public class NoMapRuleTest {

    @Test
    public void testValidRelationship() {
        NoMapRule rule = new NoMapRule();

        // Valid relationship, should return SUCCESS
        String line = "Class1 --> Class2: Association";
        RuleResult result = rule.check(line, 1);
        assertEquals(RuleResult.SUCCESS, result);
    }

    @Test
    public void testInvalidMapOnLeftSide() {
        NoMapRule rule = new NoMapRule();

        // Invalid map on left side, should return a violation
        String line = "Map<String, Integer> --> Class2: Association";
        RuleResult result = rule.check(line, 1);
        assertEquals("NoMapLeft", result.getRuleName());
        assertEquals("No Maps allowed, remove relationship from diagram or use \"*\" and describe key association in description of relationship", result.getDescription());
    }

    @Test
    public void testInvalidMapOnRightSide() {
        NoMapRule rule = new NoMapRule();

        // Invalid map on right side, should return a violation
        String line = "Class1 --> Map<String, Integer>: Association";
        RuleResult result = rule.check(line, 1);
        assertEquals("NoMapRight", result.getRuleName());
        assertEquals("No Maps allowed, remove relationship from diagram or use \"*\" and describe key association in description of relationship", result.getDescription());

    }

    @Test
    public void testInvalidMapOnBothSides() {
        NoMapRule rule = new NoMapRule();

        // Invalid map on both sides, should return a violation for the left side
        String line = "Map<String, Integer> --> Map<String, String>: Association";
        RuleResult result = rule.check(line, 1);
        assertEquals("NoMapLeft", result.getRuleName());
        assertEquals("No Maps allowed, remove relationship from diagram or use \"*\" and describe key association in description of relationship", result.getDescription());

    }
}
