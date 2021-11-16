package com.returntypes.crawler.visitor.structs;

import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.Range;

public class SimplifiedMethod implements TypeParameterContainer, AnnotationsContainer {
    String methodName;
    List<String> annotations;
    List<SimplifiedTypeParameter> typeParameters;
    SimplifiedType returnType;
    boolean chainMethodState;
    Range methodNameRange;
    Range returnTypeRange;

    public SimplifiedMethod() {
        annotations = new LinkedList<String>();
        typeParameters = new LinkedList<SimplifiedTypeParameter>();
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void addAnnotation(String annotation) {
        annotations.add(annotation);
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void addTypeParameter(SimplifiedTypeParameter simplifiedTypeParameter) {
        typeParameters.add(simplifiedTypeParameter);
    }

    public List<SimplifiedTypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public void setReturnType(SimplifiedType returnType) {
        this.returnType = returnType;
    }

    public SimplifiedType getReturnType() {
        return returnType;
    }

    public void setChainMethod(boolean chainMethodState) {
        this.chainMethodState = chainMethodState;
    }

    public boolean isChainMethod() {
        return chainMethodState;
    }

    public void setMethodNameRange(Range range) {
        this.methodNameRange = range;
    }

    public Range getMethodNameRange() {
        return methodNameRange;
    }

    public void setReturnTypeRange(Range range) {
        this.returnTypeRange = range;
    }

    public Range getReturnTypeRange() {
        return returnTypeRange;
    }
}
