package com.returntypes.crawler.visitor.structs;

public class SimplifiedType {
    String name;
    boolean arrayType;

    public SimplifiedType(String name, boolean arrayType) {
        this.name = name;
        this.arrayType = arrayType;
    }

    public SimplifiedType() {}

    public String getName() {
        int index = name.indexOf("<");
        if (index >= 0) {
            return name.substring(0, index);
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isArrayType() {
        return arrayType;
    }

    public void setArrayType(boolean arrayType) {
        this.arrayType = arrayType;
    }
}
