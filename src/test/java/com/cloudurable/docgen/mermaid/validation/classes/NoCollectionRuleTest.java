package com.cloudurable.docgen.mermaid.validation.classes;


import com.cloudurable.docgen.mermaid.validation.RuleResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoCollectionRuleTest {

    private final NoCollectionRule noCollectionRule = new NoCollectionRule();

    @Test
    public void testNoCollectionInValidRelationship() {
        String line = "Class1 <-- List<Class2>: fieldName";
        int lineNumber = 1;
        RuleResult result = noCollectionRule.check(line, lineNumber);
        assertEquals("NoRightList", result.getRuleName());
        assertEquals(lineNumber, result.getLineNumber());
        assertEquals(line, result.getViolatedLine());
        assertEquals("Not a valid actor List<Class2> try `Class1 <-- \"many\" Class2: fieldName` instead", result.getDescription());
    }

    @Test
    public void testNoCollectionInValidLeftClass() {
        String line = "List<Class2> *-- Class1: fieldName";
        int lineNumber = 1;
        RuleResult result = noCollectionRule.check(line, lineNumber);
        assertEquals("NoLeftList", result.getRuleName());
        assertEquals(lineNumber, result.getLineNumber());
        assertEquals(line, result.getViolatedLine());
        assertEquals("Not a valid actor List<Class2> try `Class2 \"many\" *-- Class1: fieldName` instead", result.getDescription());
    }

    @Test
    public void testNoCollectionInValidRightClass() {
        String line = "Class1 *-- Set<Dogs>: fieldName";
        int lineNumber = 1;
        RuleResult result = noCollectionRule.check(line, lineNumber);
        assertEquals("NoRightSet", result.getRuleName());
        assertEquals(lineNumber, result.getLineNumber());
        assertEquals(line, result.getViolatedLine());
        assertEquals("Not a valid actor Set<Dogs> try `Class1 *-- \"many\" Dogs: fieldName` instead", result.getDescription());
    }

    @Test
    public void testValidRelationship() {
        String line = "Class1 *-- Class2: fieldName";
        int lineNumber = 1;
        RuleResult result = noCollectionRule.check(line, lineNumber);
        assertEquals(RuleResult.SUCCESS, result);
    }

    // Add more test cases to cover different scenarios
}
