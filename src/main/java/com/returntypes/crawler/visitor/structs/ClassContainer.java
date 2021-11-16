package com.returntypes.crawler.visitor.structs;

import java.util.List;

public interface ClassContainer {
    public List<SimplifiedClass> getClasses();
    public void addClass(SimplifiedClass simplifiedClass);
}
