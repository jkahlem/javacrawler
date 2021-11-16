package com.returntypes.crawler.visitor.structs;

import java.util.List;

public interface AnnotationsContainer {
    public void addAnnotation(String annotation);
    public List<String> getAnnotations();
}
