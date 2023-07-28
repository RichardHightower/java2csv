package com.cloudurable.docgen.mermaid.validation;


public enum RelationshipType {
    EXTENDS("<|--"),
    IMPLEMENTS("<|.."),
    COMPOSITION("*--"),
    AGGREGATION("o--"),
    ASSOCIATION("-->"),
    SOLID_LINK("--"),
    DEPENDENCY("..>"),
    REALIZATION("..|>"),
    DASHED_LINK(".."),
    NOT_FOUND("NOT FOUND");

    private String symbol;

    RelationshipType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public static RelationshipType parseType(String str) {
        for (RelationshipType type : RelationshipType.values()) {
            if (type.getSymbol().equals(str)) {
                return type;
            }
        }
        return NOT_FOUND;
    }

}
