package com.returntypes.crawler.visitor.structs;

public class SimplifiedType {
    String name;
    boolean arrayType;

    public SimplifiedType(String name, boolean arrayType) {
        this.name = name;
        this.arrayType = arrayType;
    }

    public SimplifiedType() {}

    /**
     * Gets the main part of the type name, e.g. if the full type name is "List<String>", this method will return only "List". 
     * @return the main part of the type name.
     * @see getName
     */
    public String getMainTypeName() {
        int index = name.indexOf("<");
        if (index >= 0) {
            return name.substring(0, index);
        }
        return name;
    }

    /**
     * Returns the full (parameterized) type name, e.g. if the type name is "List<String>", this method will return "List<String>"
     * @return the type name
     * @see getMainTypeName
     */
    public String getName() {
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
