package com.cloudurable.docgen.mermaid.validation;

public interface LineRule {
    RuleResult check(String line, int lineNumber);
}
