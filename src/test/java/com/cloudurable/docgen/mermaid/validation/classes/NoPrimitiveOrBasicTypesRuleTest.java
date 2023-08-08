package com.cloudurable.docgen.mermaid.validation.classes;

import com.cloudurable.docgen.mermaid.validation.LineRule;
import com.cloudurable.docgen.mermaid.validation.RuleResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoPrimitiveOrBasicTypesRuleTest {

    private LineRule noPrimitiveOrBasicTypesRule = new NoPrimitiveOrBasicTypesRule();

    @Test
    public void testValidRelationships() {
        String line1 = "Class1 -- Class2";
        String line2 = "Class1  --> Class2";
        String line3 = "Class1 <|--   Class2";

        RuleResult result1 = noPrimitiveOrBasicTypesRule.check(line1, 1);
        RuleResult result2 = noPrimitiveOrBasicTypesRule.check(line2, 2);
        RuleResult result3 = noPrimitiveOrBasicTypesRule.check(line3, 3);

        assertEquals(RuleResult.SUCCESS, result1);
        assertEquals(RuleResult.SUCCESS, result2);
        assertEquals(RuleResult.SUCCESS, result3);
    }

    @Test
    public void testInvalidRelationshipsWithPrimitiveTypes() {
        String line1 = "int -- Class1";
        String line2 = "Class2 -- String";
        String line3 = "Class3  --> double";

        RuleResult result1 = noPrimitiveOrBasicTypesRule.check(line1, 1);
        RuleResult result2 = noPrimitiveOrBasicTypesRule.check(line2, 2);
        RuleResult result3 = noPrimitiveOrBasicTypesRule.check(line3, 3);

        assertEquals("NoPrimitivesLeft int", result1.getRuleName());
        assertEquals("NoPrimitivesRight String", result2.getRuleName());
        assertEquals("NoPrimitivesRight double", result3.getRuleName());
        assertEquals("Not basic or primitive types", result1.getDescription());
        assertEquals("Not basic or primitive types", result2.getDescription());
        assertEquals("Not basic or primitive types", result3.getDescription());
    }

    @Test
    public void testObjectVsObjectMapper() {
        String line1 = "Object -- Class1";
        String line2 = "ObjectMapper -- Foo";

        RuleResult result1 = noPrimitiveOrBasicTypesRule.check(line1, 1);
        RuleResult result2 = noPrimitiveOrBasicTypesRule.check(line2, 2);

        assertEquals("NoPrimitivesLeft Object", result1.getRuleName());
        assertEquals("pass", result2.getRuleName());

    }
}
