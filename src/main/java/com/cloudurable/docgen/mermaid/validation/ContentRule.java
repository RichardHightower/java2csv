package com.cloudurable.docgen.mermaid.validation;

import com.cloudurable.docgen.mermaid.validation.RuleResult;

public interface ContentRule {
    RuleResult check(String content);
}
