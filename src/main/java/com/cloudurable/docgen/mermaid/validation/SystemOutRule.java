package com.cloudurable.docgen.mermaid.validation;


import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SystemOutRule implements Rule {
    private final Pattern PATTERN = Pattern.compile("System\\.out");
    private final String RULE_NAME = "System Out Rule";
    private final String RULE_DESCRIPTION = "Avoid using System.out in your code.";

    @Override
    public RuleResult check(String line, int lineNumber) {
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
