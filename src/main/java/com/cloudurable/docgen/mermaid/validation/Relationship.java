package com.cloudurable.docgen.mermaid.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Relationship {

    public static final Relationship NOT_FOUND = new Relationship("", "",
            RelationshipType.NOT_FOUND, "", "", "");
    public static final String INHERITANCE_PATTERN_LEFT = "<\\|";
    public static final String COMPOSITION_PATTERN = "\\*";
    public static final String AGGREGATION_PATTERN = "o";
    public static final String ASSOCIATION_PATTERN_LEFT = "<";

    public static final String ASSOCIATION_PATTERN_RIGHT = ">";
    public static final String INHERITANCE_PATTERN_RIGHT = "\\|>";

    // Link Patterns
    public static final String LINK_SOLID_PATTERN = "--";
    public static final String LINK_DASHED_PATTERN = "\\.\\.";

    public static final String LEFT = "(" + INHERITANCE_PATTERN_LEFT + "|"
            + COMPOSITION_PATTERN + "|" + AGGREGATION_PATTERN + "|" +
            ASSOCIATION_PATTERN_LEFT + "){0,1}";

    public static final String MIDDLE =
            "(" + LINK_SOLID_PATTERN + "|" + LINK_DASHED_PATTERN + "){1}";
    public static final String RIGHT = "(" + COMPOSITION_PATTERN + "|" + AGGREGATION_PATTERN + "|" +
            ASSOCIATION_PATTERN_RIGHT + "|" + INHERITANCE_PATTERN_RIGHT + "){0,1}";

    public static final String RELATIONSHIP_TYPE_PATTERN =  "(" + LEFT + MIDDLE + RIGHT + "){1}";


    private final String leftClass;
    private final String leftCardinality;
    private final RelationshipType relationshipType;
    private final String rightCardinality;
    private final String rightClass;
    private final String relationshipDescription;

    public Relationship(String leftClass, String leftCardinality, RelationshipType relationshipType, String rightCardinality, String rightClass, String relationshipDescription) {
        this.leftClass = leftClass;
        this.leftCardinality = leftCardinality;
        this.relationshipType = relationshipType;
        this.rightCardinality = rightCardinality;
        this.rightClass = rightClass;
        this.relationshipDescription = relationshipDescription;
    }

    public static boolean hasRelationship(String line) {
        Matcher matcher = Pattern.compile(RELATIONSHIP_TYPE_PATTERN).matcher(line);
        return matcher.find();
    }


    public static String[] splitByRelationshipPattern(String line) {

        Matcher matcher = Pattern.compile(RELATIONSHIP_TYPE_PATTERN).matcher(line);

        if (!matcher.find()) {
            return null;
        }

        int splitIndex = matcher.start();
        String left = line.substring(0, splitIndex).trim();
        String relationship = matcher.group().trim();
        String rightAndDescription = line.substring(splitIndex + relationship.length()).trim();

        if (rightAndDescription.isEmpty()) {
            return null;
        }
        // Split right and description if the description exists
        String right = rightAndDescription;
        String description = "";
        int descriptionIndex = rightAndDescription.indexOf(':');
        if (descriptionIndex != -1) {
            right = rightAndDescription.substring(0, descriptionIndex).trim();
            description = rightAndDescription.substring(descriptionIndex + 1).trim();
        }

        if (right.isEmpty()) {
            return null;
        }

        String leftClass;
        String leftCard = "";

        if (left.contains("\"")) {
            String[] split = left.split("\"");
            leftClass = split[0].trim();
            leftCard = split[1].trim();
        } else {
            leftClass = left;
        }

        String rightClass;
        String rightCard = "";

        if (right.contains("\"")) {
            String[] split = right.split("\"");

            rightClass = split[2].trim();
            rightCard = split[1].trim();

        } else {
            rightClass = right;
        }
        if (rightClass.isEmpty()) {
            return null;
        }
        return new String[]{leftClass, leftCard, relationship, rightCard, rightClass, description};
    }


    // Split Class and Cardinality
    public static Relationship parseRelationship(String line) {
        if (hasRelationship(line)) {
            final String[] split = splitByRelationshipPattern(line);
            if (split == null) {
                return NOT_FOUND;
            }
            String classNameL = split[0];
            String cardinalityL = split[1];
            String relationshipS = split[2];
            String cardinalityR = split[3];
            String classNameR = split[4];
            String description = split[5];

            RelationshipType relationshipType = RelationshipType.parseType(relationshipS);
            return new Relationship(classNameL, cardinalityL, relationshipType, cardinalityR, classNameR, description);
        } else {
            return NOT_FOUND;
        }
    }


    // getters, setters, and toString..

    public String getLeftClass() {
        return leftClass;
    }

    public String getLeftCardinality() {
        return leftCardinality;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public String getRightCardinality() {
        return rightCardinality;
    }

    public String getRightClass() {
        return rightClass;
    }

    public String getRelationshipDescription() {
        return relationshipDescription;
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "leftClass='" + leftClass + '\'' +
                ", leftCardinality='" + leftCardinality + '\'' +
                ", relationshipType=" + relationshipType +
                ", rightCardinality='" + rightCardinality + '\'' +
                ", rightClass='" + rightClass + '\'' +
                ", relationshipDescription='" + relationshipDescription + '\'' +
                '}';
    }
}
