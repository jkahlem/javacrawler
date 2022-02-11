package com.returntypes.crawler.visitor.structs;

import java.util.LinkedList;
import java.util.List;

public class SimplifiedClass implements ClassContainer, TypeParameterContainer {
    public enum ClassType {
        CLASS, INTERFACE, ENUM
    }

    String className;
    ClassType classType;
    List<String> modifiers;
    List<SimplifiedClass> classes;
    List<SimplifiedMethod> methods;
    List<SimplifiedTypeParameter> typeParameters;
    List<String> extendsImplements;
    SimplifiedClass parent;
    List<SimplifiedClassField> fields;
    
    public SimplifiedClass() {
        classes = new LinkedList<SimplifiedClass>();
        methods = new LinkedList<SimplifiedMethod>();
        typeParameters = new LinkedList<SimplifiedTypeParameter>();
        extendsImplements = new LinkedList<String>();
        modifiers = new LinkedList<String>();
        fields = new LinkedList<>();
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void addModifier(String modifier) {
        this.modifiers.add(modifier);
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void addClass(SimplifiedClass simplifiedClass) {
        classes.add(simplifiedClass);
    }

    public List<SimplifiedClass> getClasses() {
        return classes;
    }

    public void addMethod(SimplifiedMethod simplifiedMethod) {
        methods.add(simplifiedMethod);
    }

    public List<SimplifiedMethod> getMethods() {
        return methods;
    }

    public void addTypeParameter(SimplifiedTypeParameter simplifiedTypeParameter) {
        typeParameters.add(simplifiedTypeParameter);
    }

    public List<SimplifiedTypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public void addExtendedOrImplementedClassName(String className) {
        extendsImplements.add(className);
    }

    public List<String> getExtendedAndImplementedClassNames() {
        return extendsImplements;
    }

    public void setParent(SimplifiedClass parentClass) {
        parent = parentClass;
    }

    public SimplifiedClass getParent() {
        return parent;
    }

    public void addField(SimplifiedClassField field) {
        fields.add(field);
    }

    public List<SimplifiedClassField> getFields() {
        return fields;
    }
}
