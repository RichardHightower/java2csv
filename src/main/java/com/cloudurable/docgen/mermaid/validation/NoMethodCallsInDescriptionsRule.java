package com.cloudurable.docgen.mermaid.validation;


import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NoMethodCallsInDescriptionsRule implements LineRule {
    private final Pattern PATTERN = Pattern.compile("(?<=:).*\\b\\w+\\s*\\(.*\\)"); // matches "methodName(...)" anywhere after a colon
    private final String RULE_NAME = "No Method Calls In Descriptions Rule";
    private final String RULE_DESCRIPTION = "No method calls in descriptions. Instead of 'Foo -> Bar : getFooBar()', use 'Foo -> Bar : Getting some foo from bar'.";

    @Override
    public RuleResult check(String line, int lineNumber) {
        if (line.startsWith("title")) {
            return RuleResult.SUCCESS;
        }
        Matcher matcher = PATTERN.matcher(line);
        if(matcher.find()) {
            return RuleResult.builder()
                    .lineNumber(lineNumber)
                    .violatedLine(line)
                    .ruleName(RULE_NAME)
                    .description(RULE_DESCRIPTION)
                    .build();
        } else {
            return RuleResult.SUCCESS;
        }
    }
}
