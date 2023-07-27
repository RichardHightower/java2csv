package com.cloudurable.docgen.mermaid.validation;

public interface Rule {
    RuleResult check(String line, int lineNumber);
}
