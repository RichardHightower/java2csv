package com.cloudurable.docgen.mermaid.validation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RelationshipTest {


    @Test
    public void testSplitRelationshipLine_LeftSideValidation() {
        String[] result;

        // Valid left sides
        result = Relationship.splitByRelationshipPattern("   Class1 \"one\" *-- \"many\" Class2: fieldName   ");
        assertNotNull(result);
        assertEquals("Class1", result[0]);
        assertEquals("\"one\"", result[1]);
        assertEquals("*--", result[2]);
        assertEquals("\"many\"", result[3]);
        assertEquals("Class2", result[4]);
        assertEquals("fieldName", result[5]);

        result = Relationship.splitByRelationshipPattern("  Class1    \"one\"    *--    \"many\"    Class2  : fieldName ");
        assertEquals("Class1", result[0]);
        assertEquals("\"one\"", result[1]);
        assertEquals("*--", result[2]);
        assertEquals("\"many\"", result[3]);
        assertEquals("Class2", result[4]);
        assertEquals("fieldName", result[5]);

        result = Relationship.splitByRelationshipPattern("Class1 *-- Class2: fieldName");
        assertNotNull(result);
        assertEquals("Class1", result[0]);
        assertEquals("", result[1]);
        assertEquals("*--", result[2]);
        assertEquals("", result[3]);
        assertEquals("Class2", result[4]);
        assertEquals("fieldName", result[5]);

        result = Relationship.splitByRelationshipPattern("Class1 *--   Class2  ");
        assertNotNull(result);
        assertEquals("Class1", result[0]);
        assertEquals("", result[1]);
        assertEquals("*--", result[2]);
        assertEquals("", result[3]);
        assertEquals("Class2", result[4]);
        assertEquals("", result[5]);

        result = Relationship.splitByRelationshipPattern("Class1 .. Class2: fieldName");
        assertEquals("Class1", result[0]);
        assertEquals("", result[1]);
        assertEquals("..", result[2]);
        assertEquals("", result[3]);
        assertEquals("Class2", result[4]);
        assertEquals("fieldName", result[5]);

        // Invalid left sides
        result = Relationship.splitByRelationshipPattern("Class1 Class2: fieldName");
        assertNull(result);


        result = Relationship.splitByRelationshipPattern("Class1 \"one\" Class2: fieldName");
        assertNull(result);

        result = Relationship.splitByRelationshipPattern("Class1 \"one\"");
        assertNull(result);

        result = Relationship.splitByRelationshipPattern("Class1");
        assertNull(result);

        result = Relationship.splitByRelationshipPattern("Class1 *--");
        assertNull(result);


    }

    @Test
    public void testHasRelationship() {
        assertTrue(Relationship.hasRelationship("Class1 <|-- Class2"));
        assertTrue(Relationship.hasRelationship("Class1 <|.. Class2"));
        assertTrue(Relationship.hasRelationship("Class1 *-- Class2"));
        assertTrue(Relationship.hasRelationship("Class1 o-- Class2"));
        assertTrue(Relationship.hasRelationship("Class1 --> Class2"));
        assertTrue(Relationship.hasRelationship("Class1 -- Class2"));
        assertTrue(Relationship.hasRelationship("Class1 ..> Class2"));
        assertTrue(Relationship.hasRelationship("Class1 .... Class2"));
        assertTrue(Relationship.hasRelationship("Class1 >.. Class2"));
        assertTrue(Relationship.hasRelationship("Class1 .. Class2"));
        assertFalse(Relationship.hasRelationship("Class1 Class2"));
        assertTrue(Relationship.hasRelationship("Class1  \"one\" *-- \"many\" Class2: fieldName"));
        assertTrue(Relationship.hasRelationship("  Class1    \"one\"    *--    \"many\"    Class2  : fieldName "));
        assertTrue(Relationship.hasRelationship("Class1 *-- Class2: fieldName"));
        assertTrue(Relationship.hasRelationship("Class1 *-- Class2"));
        assertTrue(Relationship.hasRelationship("Class1  \"one\" *-- \"many\" Class2"));
        assertTrue(Relationship.hasRelationship("Class1 *-- \"many\" Class2: fieldName"));
        assertTrue(Relationship.hasRelationship("Class1  \"one\" *-- Class2: fieldName"));
        assertTrue(Relationship.hasRelationship("Class1  \"one\" o-- \"many\" Class2: fieldName"));
        assertTrue(Relationship.hasRelationship("Class1 *--"));
        assertFalse(Relationship.hasRelationship("Class1 Class2: fieldName"));
        assertFalse(Relationship.hasRelationship("Class1 - Class2: fieldName"));
        assertFalse(Relationship.hasRelationship("Class1 *- Class2: fieldName"));
        assertFalse(Relationship.hasRelationship("Class1 fieldName"));
        assertFalse(Relationship.hasRelationship("Class1 \"one\" Class2: fieldName"));
        assertFalse(Relationship.hasRelationship("Class1 \"one\" fieldName"));
        assertFalse(Relationship.hasRelationship("Class1 \"one\""));
        assertFalse(Relationship.hasRelationship("Class1"));

    }


    @Test
    public void testParseRelationship() {
        String line = "Class1  \"one\" *-- \"many\" Class2: fieldName";
        Relationship relationship = Relationship.parseRelationship(line);
        assertNotEquals(Relationship.NOT_FOUND, relationship);
        assertEquals("Class1", relationship.getLeftClass());
        assertEquals("one", relationship.getLeftCardinality());
        assertEquals("Class2", relationship.getRightClass());
        assertEquals("many", relationship.getRightCardinality());
        assertEquals(RelationshipType.COMPOSITION, relationship.getRelationshipType());
        assertEquals("fieldName", relationship.getRelationshipDescription());
    }

    @Test
    public void testParseRelationshipWithExtraSpaces() {
        String line = "  Class1    \"one\"    *--    \"many\"    Class2  : fieldName ";
        Relationship relationship = Relationship.parseRelationship(line);
        assertNotEquals(Relationship.NOT_FOUND, relationship);
        assertEquals("Class1", relationship.getLeftClass());
        assertEquals("one", relationship.getLeftCardinality());
        assertEquals("Class2", relationship.getRightClass());
        assertEquals("many", relationship.getRightCardinality());
        assertEquals(RelationshipType.COMPOSITION, relationship.getRelationshipType());
        assertEquals("fieldName", relationship.getRelationshipDescription());
    }

    @Test
    public void testParseRelationshipNotFound() {
        String line = "Not a relationship line";
        Relationship relationship = Relationship.parseRelationship(line);
        assertEquals(Relationship.NOT_FOUND, relationship);
    }

    @Test
    public void testAFewParseRelationship() {
        String[] lines = {
                "ParentClass \"one\" <|-- \"many\" ChildClass: extends",
                "Interface \"one\" <|.. \"many\" ImplementingClass: implements",
                "Class1 \"one\" *-- \"many\" Class2: fieldName",
                "Class1 \"one\" o-- \"many\" Class2: fieldName",
                "Class1 \"one\" --> \"many\" Class2: fieldName",
                "Class1 \"one\" -- \"many\" Class2: fieldName",
                "Class1 \"one\" ..> \"many\" Class2: fieldName",
                "Class1 \"one\" ..|> \"many\" Class2: fieldName",
                "Class1 \"one\" .. \"many\" Class2: fieldName",
                "Class1 \"few\" .. \"bunch\" Class2: fieldName",
                "Class1 \"solo\" .. \"duo\" Class2: fieldName",
                "ParentClass <-- ChildClass: extends",
                "ParentClass <|-- \"1\" ChildClass: extends",
                "ParentClass <|-- \"0..1\" ChildClass: extends",
                "ParentClass <|-- \"1..\" ChildClass: extends",
                "ParentClass <|-- \"0..n\" ChildClass: extends",
                "ParentClass <|-- \"*\" ChildClass: extends",
                "Interface <|.. ImplementingClass: implements",
                "Interface <|.. \"1\" ImplementingClass: implements",
                "Interface <|.. \"0..1\" ImplementingClass: implements",
                "Interface <|.. \"1..\" ImplementingClass: implements",
                "Interface <|.. \"0..n\" ImplementingClass: implements",
                "Interface <|.. \"*\" ImplementingClass: implements",
                "Class1 *-- Class2: fieldName",
                "Class1 *-- \"1\" Class2: fieldName",
                "Class1 *-- \"0..1\" Class2: fieldName",
                "Class1 *-- \"1..\" Class2: fieldName",
                "Class1 *-- \"0..n\" Class2: fieldName",
                "Class1 *-- \"*\" Class2: fieldName",
                "Class1 o-- Class2: fieldName",
                "Class1 o-- \"1\" Class2: fieldName",
                "Class1 o-- \"0..1\" Class2: fieldName",
                "Class1 o-- \"1..\" Class2: fieldName",
                "Class1 o-- \"0..n\" Class2: fieldName",
                "Class1 o-- \"*\" Class2: fieldName",
                "Class1 --> Class2: fieldName",
                "Class1 --> \"1\" Class2: fieldName",
                "Class1 --> \"0..1\" Class2: fieldName",
                "Class1 --> \"1..\" Class2: fieldName",
                "Class1 --> \"0..n\" Class2: fieldName",
                "Class1 --> \"*\" Class2: fieldName",
                "Class1 -- Class2: fieldName",
                "Class1 -- \"1\" Class2: fieldName",
                "Class1 -- \"0..1\" Class2: fieldName",
                "Class1 -- \"1..\" Class2: fieldName",
                "Class1 -- \"0..n\" Class2: fieldName",
                "Class1 -- \"*\" Class2: fieldName",
                "Class1 ..> Class2: fieldName",
                "Class1 ..> \"1\" Class2: fieldName",
                "Class1 ..> \"0..1\" Class2: fieldName",
                "Class1 ..> \"1..\" Class2: fieldName",
                "Class1 ..> \"0..n\" Class2: fieldName",
                "Class1 ..> \"*\" Class2: fieldName",
                "Class1 ..|> Class2: fieldName",
                "Class1 ..|> \"1\" Class2: fieldName",
                "Class1 ..|> \"0..1\" Class2: fieldName",
                "Class1 ..|> \"1..\" Class2: fieldName",
                "Class1 ..|> \"0..n\" Class2: fieldName",
                "Class1 ..|> \"*\" Class2: fieldName",
                "Class1 .. Class2: fieldName",
                "Class1 .. \"1\" Class2: fieldName",
                "Class1 .. \"0..1\" Class2: fieldName",
                "Class1 .. \"1..\" Class2: fieldName",
                "Class1 .. \"0..n\" Class2: fieldName",
                "Class1 .. \"*\" Class2: fieldName",
                "Student \"1\" --o \"1\" IdCard : carries",
                "Student \"1\" --o \"1\" Bike : rides",
                "Customer \"1\" --> \"*\" Ticket",
                " Student \"1\" --> \"1..*\" Course",
                "    Galaxy --> \"many\" Star : Contains"
        };

        for (String line : lines) {
            Relationship relationship = Relationship.parseRelationship(line);
            assertNotEquals(Relationship.NOT_FOUND, relationship, "Failed on line: " + line);
        }
    }

}
