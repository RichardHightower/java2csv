package com.cloudurable.docgen.mermaid.validation;

public interface ContentRule {
    RuleResult check(String content);
}
