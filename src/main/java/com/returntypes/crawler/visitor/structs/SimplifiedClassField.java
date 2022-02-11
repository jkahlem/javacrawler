package com.returntypes.crawler.visitor.structs;

public class SimplifiedClassField {
    private String name;
    private SimplifiedType type;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(SimplifiedType type) {
        this.type = type;
    }

    public SimplifiedType getType() {
        return type;
    }
}
