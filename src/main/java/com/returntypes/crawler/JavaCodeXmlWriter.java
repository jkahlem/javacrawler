package com.returntypes.crawler;

import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.github.javaparser.Range;
import com.returntypes.crawler.messages.CrawlerOptions;
import com.returntypes.crawler.visitor.structs.AnnotationsContainer;
import com.returntypes.crawler.visitor.structs.ClassContainer;
import com.returntypes.crawler.visitor.structs.JavaCodeFile;
import com.returntypes.crawler.visitor.structs.SimplifiedClass;
import com.returntypes.crawler.visitor.structs.SimplifiedClassField;
import com.returntypes.crawler.visitor.structs.SimplifiedImport;
import com.returntypes.crawler.visitor.structs.SimplifiedMethod;
import com.returntypes.crawler.visitor.structs.SimplifiedParameter;
import com.returntypes.crawler.visitor.structs.SimplifiedType;
import com.returntypes.crawler.visitor.structs.SimplifiedTypeParameter;
import com.returntypes.crawler.visitor.structs.TypeParameterContainer;

/**
 * Writes the content of a (simplified) JavaCodeFile to xml
 */
public class JavaCodeXmlWriter {
    XMLStreamWriter outputStreamWriter;
    boolean fileListOpened;
    CrawlerOptions crawlerOptions;

    public JavaCodeXmlWriter(OutputStream outputStream, CrawlerOptions options) throws XMLStreamException {
        this.fileListOpened = false;
        this.crawlerOptions = options;
        createXMLStreamWriter(outputStream);
        writeDocumentStart();
    }

    private void createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        outputStreamWriter = outputFactory.createXMLStreamWriter(outputStream, "utf-8");
    }

    private void writeDocumentStart() throws XMLStreamException {
        outputStreamWriter.writeStartDocument("utf-8", "1.0");
        outputStreamWriter.writeStartElement("root");
    }

    public void closeOutputFile() throws XMLStreamException {
        if (fileListOpened) {
            writeFileListEnd();
        }
        writeDocumentEnd();
        outputStreamWriter.close();
    }

    private void writeFileListStart() throws XMLStreamException {
        outputStreamWriter.writeStartElement("files");
        fileListOpened = true;
    }

    private void writeFileListEnd() throws XMLStreamException {
        outputStreamWriter.writeEndElement();
    }

    private void writeDocumentEnd() throws XMLStreamException {
        outputStreamWriter.writeEndElement(); // </root>
        outputStreamWriter.writeEndDocument();
    }

    public void writeJavaCodeFile(JavaCodeFile javaCodeFile) throws XMLStreamException {
        if (!fileListOpened) {
            writeFileListStart();
        }
        outputStreamWriter.writeStartElement("file");
        outputStreamWriter.writeAttribute("path", javaCodeFile.getFilePath());

        writePackage(javaCodeFile);
        writeImports(javaCodeFile);
        writeClasses(javaCodeFile);
        
        outputStreamWriter.writeEndElement();
    }

    private void writePackage(JavaCodeFile javaCodeFile) throws XMLStreamException {
        outputStreamWriter.writeStartElement("package");
        outputStreamWriter.writeCharacters(javaCodeFile.getPackage());
        outputStreamWriter.writeEndElement();
    }

    private void writeImports(JavaCodeFile javaCodeFile) throws XMLStreamException {
        outputStreamWriter.writeStartElement("imports");

        for (SimplifiedImport simplifiedImport : javaCodeFile.getImports()) {
            writeImport(simplifiedImport);
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeImport(SimplifiedImport simplifiedImport) throws XMLStreamException {
        outputStreamWriter.writeStartElement("import");

        if (simplifiedImport.isWildcard()) {
            outputStreamWriter.writeAttribute("isWildcard", "true");
        }
        if (simplifiedImport.isStatic()) {
            outputStreamWriter.writeAttribute("isStatic", "true");
        }
        outputStreamWriter.writeCharacters(simplifiedImport.getImportPath());

        outputStreamWriter.writeEndElement();
    }

    private void writeClasses(ClassContainer classContainer) throws XMLStreamException {
        if (classContainer.getClasses().isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("classes");

        for (SimplifiedClass simplifiedClass : classContainer.getClasses()) {
            writeClass(simplifiedClass);
        }    

        outputStreamWriter.writeEndElement();
    }

    private void writeClass(SimplifiedClass simplifiedClass) throws XMLStreamException {
        outputStreamWriter.writeStartElement("class");

        outputStreamWriter.writeAttribute("type", simplifiedClass.getClassType().toString());
        outputStreamWriter.writeAttribute("name", simplifiedClass.getClassName());
        writeModifiers(simplifiedClass);
        writeTypeParameters(simplifiedClass);
        writeExtendedAndImplementedClassNames(simplifiedClass);
        writeClassFields(simplifiedClass);
        writeClasses(simplifiedClass);
        writeMethods(simplifiedClass);

        outputStreamWriter.writeEndElement();
    }

    private void writeModifiers(SimplifiedClass simplifiedClass) throws XMLStreamException {
        if (simplifiedClass.getModifiers().isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("modifiers");

        for (String modifier : simplifiedClass.getModifiers()) {
            outputStreamWriter.writeStartElement("modifier");
            outputStreamWriter.writeCharacters(modifier);
            outputStreamWriter.writeEndElement();
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeExtendedAndImplementedClassNames(SimplifiedClass simplifiedClass) throws XMLStreamException {
        if (simplifiedClass.getExtendedAndImplementedClassNames().isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("extends");

        for (String className : simplifiedClass.getExtendedAndImplementedClassNames()) {
            writeType(new SimplifiedType(className, false));
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeClassFields(SimplifiedClass simplifiedClass) throws XMLStreamException {
        if (simplifiedClass.getFields().isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("fields");

        for (SimplifiedClassField field : simplifiedClass.getFields()) {
            writeClassField(field);            
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeClassField(SimplifiedClassField simplifiedClassField) throws XMLStreamException {
        outputStreamWriter.writeStartElement("field");

        outputStreamWriter.writeStartElement("name");
        outputStreamWriter.writeCharacters(simplifiedClassField.getName());
        outputStreamWriter.writeEndElement();

        writeType(simplifiedClassField.getType());

        outputStreamWriter.writeEndElement();
    }

    private void writeTypeParameters(TypeParameterContainer typeParameterContainer) throws XMLStreamException {
        if (typeParameterContainer.getTypeParameters().isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("typeParameters");

        for (SimplifiedTypeParameter simplifiedTypeParameter : typeParameterContainer.getTypeParameters()) {
            writeTypeParameter(simplifiedTypeParameter);
        }        

        outputStreamWriter.writeEndElement();
    }

    private void writeTypeParameter(SimplifiedTypeParameter simplifiedTypeParameter) throws XMLStreamException {
        outputStreamWriter.writeStartElement("typeParameter");

        outputStreamWriter.writeAttribute("name", simplifiedTypeParameter.getName());
        if (!simplifiedTypeParameter.getTypeBounds().isEmpty()) {
            for (String typeBound : simplifiedTypeParameter.getTypeBounds()) {
                writeTypeBound(typeBound);
            }
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeTypeBound(String typeBound) throws XMLStreamException {
        outputStreamWriter.writeStartElement("type");
        outputStreamWriter.writeCharacters(typeBound);
        outputStreamWriter.writeEndElement();
    }

    private void writeType(SimplifiedType type) throws XMLStreamException {
        outputStreamWriter.writeStartElement("type");
        
        if (type.isArrayType()) {
            outputStreamWriter.writeAttribute("isArrayType", "true");
        }

        outputStreamWriter.writeCharacters(type.getMainTypeName());
        outputStreamWriter.writeEndElement();
    }
    
    private void writeMethods(SimplifiedClass simplifiedClass) throws XMLStreamException {
        if (simplifiedClass.getMethods().isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("methods");

        for (SimplifiedMethod simplifiedMethod : simplifiedClass.getMethods()) {
            writeMethod(simplifiedMethod);
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeMethod(SimplifiedMethod simplifiedMethod) throws XMLStreamException {
        outputStreamWriter.writeStartElement("method");
        
        outputStreamWriter.writeAttribute("name", simplifiedMethod.getMethodName());
        if (simplifiedMethod.isChainMethod()) {
            outputStreamWriter.writeAttribute("isChainMethod", "true");
        }
        if (simplifiedMethod.isSingleAssignment()) {
            outputStreamWriter.writeAttribute("isSingleAssignment", "true");
        }
        if (simplifiedMethod.isSingleReturn()) {
            outputStreamWriter.writeAttribute("isSingleReturn", "true");
        }
        if (simplifiedMethod.isThrowsErrors()) {
            outputStreamWriter.writeAttribute("throwsErrors", "true");
        }
        writeMethodRanges(simplifiedMethod);
        writeAnnotations(simplifiedMethod);
        writeTypeParameters(simplifiedMethod);
        writeType(simplifiedMethod.getReturnType());
        writeParameters(simplifiedMethod.getParameters());
        writeModifiers(simplifiedMethod.getModifiers());

        outputStreamWriter.writeEndElement();
    }

    private void writeMethodRanges(SimplifiedMethod simplifiedMethod) throws XMLStreamException {
        if (!this.crawlerOptions.isUseRanges()) {
            return;
        }

        outputStreamWriter.writeStartElement("methodNameRange");
        writeRange(simplifiedMethod.getMethodNameRange());
        outputStreamWriter.writeEndElement();
        
        outputStreamWriter.writeStartElement("returnTypeRange");
        writeRange(simplifiedMethod.getReturnTypeRange());
        outputStreamWriter.writeEndElement();
    }

    private void writeRange(Range range) throws XMLStreamException {
        if (!this.crawlerOptions.isUseRanges()) {
            return;
        }

        outputStreamWriter.writeStartElement("range");

        outputStreamWriter.writeStartElement("begin");
        outputStreamWriter.writeAttribute("line", "" + range.begin.line);
        outputStreamWriter.writeAttribute("col", "" + range.begin.column);
        outputStreamWriter.writeEndElement();
        
        outputStreamWriter.writeStartElement("end");
        outputStreamWriter.writeAttribute("line", "" + range.end.line);
        outputStreamWriter.writeAttribute("col", "" + range.end.column);
        outputStreamWriter.writeEndElement();

        outputStreamWriter.writeEndElement();
    }

    private void writeAnnotations(AnnotationsContainer annotationsContainer) throws XMLStreamException {
        if (annotationsContainer.getAnnotations().isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("annotations");

        for (String annotation : annotationsContainer.getAnnotations()) {
            writeAnnotation(annotation);
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeAnnotation(String annotation) throws XMLStreamException {
        outputStreamWriter.writeStartElement("annotation");
        outputStreamWriter.writeCharacters(annotation);
        outputStreamWriter.writeEndElement();
    }

    private void writeParameters(List<SimplifiedParameter> parameters) throws XMLStreamException {
        if (parameters.isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("parameters");

        for (SimplifiedParameter parameter : parameters) {
            writeParameter(parameter);
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeParameter(SimplifiedParameter parameter) throws XMLStreamException {
        outputStreamWriter.writeStartElement("parameter");
        
        outputStreamWriter.writeAttribute("name", parameter.getName());

        writeType(parameter.getType());

        outputStreamWriter.writeEndElement();
    }

    private void writeModifiers(List<String> modifiers) throws XMLStreamException {
        if (modifiers.isEmpty()) {
            return;
        }

        outputStreamWriter.writeStartElement("modifiers");

        for (String modifier : modifiers) {
            writeModifier(modifier);
        }

        outputStreamWriter.writeEndElement();
    }

    private void writeModifier(String modifier) throws XMLStreamException {
        outputStreamWriter.writeStartElement("modifier");

        outputStreamWriter.writeCharacters(modifier);

        outputStreamWriter.writeEndElement();
    }
}
