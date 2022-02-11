package com.returntypes.crawler.visitor;

import java.util.LinkedList;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.returntypes.crawler.visitor.structs.JavaCodeFile;
import com.returntypes.crawler.visitor.structs.SimplifiedClass;
import com.returntypes.crawler.visitor.structs.SimplifiedClassField;
import com.returntypes.crawler.visitor.structs.SimplifiedImport;
import com.returntypes.crawler.visitor.structs.SimplifiedMethod;
import com.returntypes.crawler.visitor.structs.SimplifiedParameter;
import com.returntypes.crawler.visitor.structs.SimplifiedType;
import com.returntypes.crawler.visitor.structs.SimplifiedTypeParameter;

/**
 * Visits the elements of the code file and extracts the needed data from it (like classes, methods etc.)
 */
public class CodeAbstractionVisitor extends GenericVisitorAdapter<JavaCodeFile, Void> {
    JavaCodeFile javaCodeFile;
    LinkedList<SimplifiedClass> classTree;
    SimplifiedClass currentClass;

    public CodeAbstractionVisitor(String filepath) {
        javaCodeFile = new JavaCodeFile(filepath);
        classTree = new LinkedList<SimplifiedClass>();
    }

    public JavaCodeFile visit(CompilationUnit compilationUnit, Void arg) {
        compilationUnit.getImports().accept(this, arg);
        if (compilationUnit.getModule().isPresent()) {
            compilationUnit.getModule().get().accept(this, arg);
        }
        if (compilationUnit.getPackageDeclaration().isPresent()) {
            compilationUnit.getPackageDeclaration().get().accept(this, arg);
        }
        compilationUnit.getTypes().accept(this, arg);
        return javaCodeFile;
    }

    @Override
    public JavaCodeFile visit(PackageDeclaration packageDeclaration, Void arg) {
        javaCodeFile.setPackage(packageDeclaration.getNameAsString());
        return null;
    }

    @Override
    public JavaCodeFile visit(ImportDeclaration importDeclaration, Void arg) {
        SimplifiedImport simplifiedImport = createSimplifiedImport(importDeclaration); 
        javaCodeFile.addImport(simplifiedImport);
        return null;
    }

    private SimplifiedImport createSimplifiedImport(ImportDeclaration importDeclaration) {
        SimplifiedImport simplifiedImport = new SimplifiedImport();
        simplifiedImport.setImportPath(importDeclaration.getNameAsString());
        simplifiedImport.setStatic(importDeclaration.isStatic());
        simplifiedImport.setWildcard(importDeclaration.isAsterisk());
        return simplifiedImport;
    }

    @Override
    public JavaCodeFile visit(MethodDeclaration methodDeclaration, Void arg) {
        SimplifiedMethod simplifiedMethod = createSimplifiedMethod(methodDeclaration);
        currentClass.addMethod(simplifiedMethod);
        return null;
    }

    private SimplifiedMethod createSimplifiedMethod(MethodDeclaration methodDeclaration) {
        SimplifiedMethod simplifiedMethod = new SimplifiedMethod();

        simplifiedMethod.setMethodName(methodDeclaration.getNameAsString());

        methodDeclaration.getModifiers().forEach(modifier -> {
            simplifiedMethod.addModifier(modifier.toString());
        });

        methodDeclaration.getParameters().forEach(parameter -> {
            simplifiedMethod.addParameter(createSimplifiedParameter(parameter));
        });

        methodDeclaration.getThrownExceptions().forEach(exception -> {
            simplifiedMethod.addException(createSimplifiedType(exception));
        });

        SimplifiedType simplifiedReturnType = createSimplifiedType(methodDeclaration.getType());
        simplifiedMethod.setReturnType(simplifiedReturnType);
        
        methodDeclaration.getAnnotations().forEach(annotationExpr -> {
            simplifiedMethod.addAnnotation(annotationExpr.getNameAsString());
        });

        methodDeclaration.getTypeParameters().forEach(typeParameter -> {
            SimplifiedTypeParameter simplifiedTypeParameter = createSimplifiedTypeParameter(typeParameter);
            simplifiedMethod.addTypeParameter(simplifiedTypeParameter);
        });

        setMethodRanges(methodDeclaration, simplifiedMethod);

        simplifiedMethod.setChainMethod(isChainMethod(methodDeclaration));
        return simplifiedMethod;
    }

    private SimplifiedParameter createSimplifiedParameter(Parameter parameter) {
        SimplifiedParameter simplifiedParameter = new SimplifiedParameter();
        simplifiedParameter.setName(parameter.getNameAsString());
        simplifiedParameter.setType(createSimplifiedType(parameter.getType()));
        return simplifiedParameter;
    }

    private SimplifiedType createSimplifiedType(Type type) {
        SimplifiedType simplifiedType = new SimplifiedType();
        simplifiedType.setName(type.getElementType().asString());
        simplifiedType.setArrayType(type.isArrayType());
        return simplifiedType;
    }

    private boolean isChainMethod(MethodDeclaration methodDeclaration) {
        Optional<BlockStmt> blockStatement = methodDeclaration.getBody();

        if (blockStatement.isPresent()) {
            return isAllReturnValuesThis(blockStatement.get());
        }
        return false;
    }

    private boolean isAllReturnValuesThis(BlockStmt blockStatement) {
        boolean hasReturnStatement = false;
        for (Statement statement : blockStatement.getStatements()) {
            if (statement.isReturnStmt()) {
                hasReturnStatement = true;

                ReturnStmt returnStatement = (ReturnStmt) statement;
                if (!isReturningThis(returnStatement)) {
                    return false;
                }
            }
        }

        if (!hasReturnStatement) {
            return false;
        }

        return true;
    }

    private boolean isReturningThis(ReturnStmt returnStatement) {
        Optional<Expression> optionalExpression = returnStatement.getExpression();
        if (optionalExpression.isPresent()) {
            Expression expression = optionalExpression.get();
            return expression.isThisExpr();
        }
        return false;
    }

    private void setMethodRanges(MethodDeclaration methodDeclaration, SimplifiedMethod simplifiedMethod) {
        Optional<Range> methodNameRange = methodDeclaration.getName().getRange();
        if (methodNameRange.isPresent()) {
            simplifiedMethod.setMethodNameRange(methodNameRange.get());
        }

        Optional<Range> returnTypeRange = methodDeclaration.getType().getRange();
        if (returnTypeRange.isPresent()) {
            simplifiedMethod.setReturnTypeRange(returnTypeRange.get());
        }
    }

    @Override
    public JavaCodeFile visit(ClassOrInterfaceDeclaration classDeclaration, Void arg) {
        switchToNewClass();

        currentClass.setClassName(classDeclaration.getName().asString());

        addModifiers(classDeclaration);
        addExtendedAndImplementedTypesToCurrentClass(classDeclaration);
        addClassFieldsToCurrentClass(classDeclaration);
        setClassTypeOfCurrentClass(classDeclaration);
        addTypeParametersToCurrentClass(classDeclaration);

        // also visit members
        classDeclaration.getMembers().forEach(p -> p.accept(this, arg));

        switchToParentClass();
        return null;
    }

    public void addModifiers(ClassOrInterfaceDeclaration classDeclaration) {
        classDeclaration.getModifiers().forEach(p -> {
            currentClass.addModifier(p.toString().trim());
        });
    }

    public void addExtendedAndImplementedTypesToCurrentClass(ClassOrInterfaceDeclaration classDeclaration) {
        classDeclaration.getExtendedTypes().forEach(p -> {
            currentClass.addExtendedOrImplementedClassName(p.getNameWithScope());
        });
        classDeclaration.getImplementedTypes().forEach(p -> {
            currentClass.addExtendedOrImplementedClassName(p.getNameWithScope());
        });
    }

    public void addClassFieldsToCurrentClass(ClassOrInterfaceDeclaration classDeclaration) {
        classDeclaration.getFields().forEach(field -> {
            field.getVariables().forEach(variable -> {
                // Field declarations can define multiple fields (-> variables), e.g.  class { private int a, b; } declares different fields
                // (Additionally, declarations like int a, b[]; may have different types, as b will be an array.)
                currentClass.addField(createSimplifiedClassField(variable));
            });
        });
    }

    public SimplifiedClassField createSimplifiedClassField(VariableDeclarator field) {
        final SimplifiedClassField simplifiedClassField = new SimplifiedClassField();
        simplifiedClassField.setName(field.getNameAsString());
        simplifiedClassField.setType(createSimplifiedType(field.getType()));
        return simplifiedClassField;
    }

    public void setClassTypeOfCurrentClass(ClassOrInterfaceDeclaration classDeclaration) {
        if (classDeclaration.isInterface()) {
            currentClass.setClassType(SimplifiedClass.ClassType.INTERFACE);
        } else {
            currentClass.setClassType(SimplifiedClass.ClassType.CLASS);
        }
    }

    public void addTypeParametersToCurrentClass(ClassOrInterfaceDeclaration classDeclaration) {
        classDeclaration.getTypeParameters().forEach(typeParameter -> {
            currentClass.addTypeParameter(createSimplifiedTypeParameter(typeParameter));
        });
    }

    public SimplifiedTypeParameter createSimplifiedTypeParameter(TypeParameter typeParameter) {
        SimplifiedTypeParameter simplifiedTypeParameter = new SimplifiedTypeParameter();
        simplifiedTypeParameter.setName(typeParameter.getNameAsString());
        typeParameter.getTypeBound().forEach(p -> {
            simplifiedTypeParameter.addTypeBound(p.getNameAsString());
        });
        return simplifiedTypeParameter;
    }
    
    @Override
    public JavaCodeFile visit(EnumDeclaration enumDeclaration, Void arg) {
        switchToNewClass();
        currentClass.setClassName(enumDeclaration.getNameAsString());
        currentClass.setClassType(SimplifiedClass.ClassType.ENUM);
        switchToParentClass();
        return null;
    }

    private void switchToNewClass() {
        SimplifiedClass previousClass = currentClass;
        classTree.add(currentClass);
        currentClass = new SimplifiedClass();

        if (previousClass != null) {
            previousClass.addClass(currentClass);
        } else {
            javaCodeFile.addClass(currentClass);
        }
    }

    private void switchToParentClass() {
        if (!classTree.isEmpty()) {
            currentClass = classTree.removeLast();
        } else {
            currentClass = null;
        }
    }
}
