package com.cloudurable.docgen.mermaid.validation.classes;

import com.cloudurable.docgen.mermaid.validation.LineRule;
import com.cloudurable.docgen.mermaid.validation.Relationship;
import com.cloudurable.docgen.mermaid.validation.RuleResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoPrimitiveOrBasicTypesRule implements LineRule {

    private final Pattern PATTERN = Pattern.compile("\\b(int|float|double|short|long|byte|boolean|String|" +
            "Integer|Float|Double|Short|Long|Byte|Boolean|StringBuffer|File|Object)\\b");


    @Override
    public RuleResult check(String line, int lineNumber) {

        if (Relationship.hasRelationship(line)) {

            Relationship relationship = Relationship.parseRelationship(line);
            if (relationship == Relationship.NOT_FOUND) {
                System.out.println("NOT VALID " + line);
                return RuleResult.builder()
                        .ruleName("ValidRelationshipRule")
                        .lineNumber(lineNumber).violatedLine(line)
                        .description("Not a valid relationship")
                        .build();
            }

            final RuleResult leftCheck = checkCollection("Left", relationship.getLeftClass(),line, lineNumber);
            if (leftCheck == RuleResult.SUCCESS) {
                return checkCollection("Right", relationship.getRightClass(), line, lineNumber);
            } else {
                return leftCheck;
            }

        } else {
            return RuleResult.SUCCESS;
        }
    }

    private RuleResult checkCollection(String side, String input, String line, int lineNumber) {
        Matcher matcher = PATTERN.matcher(input);

        if (matcher.find()) {

            String type = matcher.group(1);

            return RuleResult.builder()
                    .ruleName("NoPrimitives"+side+ " " + type)
                    .lineNumber(lineNumber).violatedLine(line)
                    .description("Not basic or primitive types")
                    .build();
        }
        return RuleResult.SUCCESS;
    }
}
