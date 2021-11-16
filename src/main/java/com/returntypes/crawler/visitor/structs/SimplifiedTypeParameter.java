package com.returntypes.crawler.visitor.structs;

import java.util.LinkedList;
import java.util.List;

public class SimplifiedTypeParameter {
    String name;
    List<String> typeBounds;

    public SimplifiedTypeParameter() {
        typeBounds = new LinkedList<String>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addTypeBound(String className) {
        typeBounds.add(className);
    }

    public List<String> getTypeBounds() {
        return typeBounds;
    }
}
