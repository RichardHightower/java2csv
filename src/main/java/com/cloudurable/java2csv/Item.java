package com.cloudurable.java2csv;

public class Item {
    private final String body;
    private final String javadoc;
    private final String name;
    private final String definition;
    private final Item parent;

    public Item(String body, String javadoc, String name, String definition, Item parent) {
        this.body = body;
        this.javadoc = javadoc;
        this.name = name;
        this.definition = definition;
        this.parent = parent;
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder () {
        }
        private String body;
        private String javadoc;
        private String name;
        private String definition;
        private Item parent;

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder javadoc(String javadoc) {
            this.javadoc = javadoc;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder definition(String definition) {
            this.definition = definition;
            return this;
        }

        public Builder parent(Item parent) {
            this.parent = parent;
            return this;
        }

        public Item build() {
            return new Item(body, javadoc, name, definition, parent);
        }
    }

}
