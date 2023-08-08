package com.cloudurable.docgen.mermaid.validation.classes;

import com.cloudurable.docgen.mermaid.validation.LineRule;
import com.cloudurable.docgen.mermaid.validation.Relationship;
import com.cloudurable.docgen.mermaid.validation.RuleResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoCollectionRule implements LineRule {

    private final Pattern PATTERN = Pattern.compile("\\b(List|Set|ArrayList|Collection|HashSet|Queue)\\b<([A-Za-z0-9,]+)>");


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

            String collectionType = matcher.group(1);
            String genericType = matcher.group(2);

            String description =  side.equals("Right") ? "Not a valid actor " + input + " try `" +
                    line.replace(input, "\"many\" " + genericType) +"` instead" :
                    "Not a valid actor " + input + " try `" +
                            line.replace(input, genericType + " \"many\"") +"` instead";

            return RuleResult.builder()
                    .ruleName("No"+side+collectionType)
                    .lineNumber(lineNumber).violatedLine(line)
                    .description(description)
                    .build();
        }
        return RuleResult.SUCCESS;
    }
}
