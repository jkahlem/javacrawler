package com.returntypes.crawler.visitor.structs;

import java.util.List;

public interface TypeParameterContainer {
    public List<SimplifiedTypeParameter> getTypeParameters();
    public void addTypeParameter(SimplifiedTypeParameter typeParameter);
}
