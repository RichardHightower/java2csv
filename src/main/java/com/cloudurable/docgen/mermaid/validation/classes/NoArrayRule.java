package com.cloudurable.docgen.mermaid.validation.classes;

import com.cloudurable.docgen.mermaid.validation.LineRule;
import com.cloudurable.docgen.mermaid.validation.Relationship;
import com.cloudurable.docgen.mermaid.validation.RuleResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoArrayRule implements LineRule {

    private final Pattern PATTERN = Pattern.compile("([A-Za-z0-9_]+)\\s*\\[\\s*\\]");


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

            final RuleResult leftCheck = checkForArray("Left", relationship.getLeftClass(), line, lineNumber);
            if (leftCheck == RuleResult.SUCCESS) {
                return checkForArray("Right", relationship.getRightClass(), line, lineNumber);
            } else {
                return leftCheck;
            }
        } else {
            return RuleResult.SUCCESS;
        }
    }

    private RuleResult checkForArray(String side, String input, String line, int lineNumber) {
        Matcher matcher = PATTERN.matcher(input);

        if (matcher.find()) {
            String arrayDeclaration = matcher.group(0);
            String className = matcher.group(1);
            String description = side.equals("Right") ? "Arrays like " + arrayDeclaration + "are not allowed in the relationship " + input + " try `" +
                    line.replace(input, "\"*\" " + className) +"` instead" :
                    "Arrays like " + arrayDeclaration + "are not allowed in the relationship " + input + " try `" +
                            line.replace(input, className + " \"*\"") +"` instead"
                    ;

            return RuleResult.builder()
                    .ruleName("NoArrayRule" + side)
                    .lineNumber(lineNumber).violatedLine(line)
                    .description(description)
                    .build();
        }

        return RuleResult.SUCCESS;
    }
}
