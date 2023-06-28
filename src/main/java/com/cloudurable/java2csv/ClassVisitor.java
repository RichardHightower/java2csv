package com.cloudurable.java2csv;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Visitor for visiting classes, interfaces, and enums.
 */
class ClassVisitor extends VoidVisitorAdapter<Void> {

    private static final Pattern JAVA_DOC_REGEX = Pattern.compile("^/\\*\\*.*?\\*/\\s*", Pattern.DOTALL);

    private final List<JavaItem> javaItems;
    private CompilationUnit compilationUnit;

    public ClassVisitor() {
        this.javaItems = new ArrayList<>(32);
    }




    /**
     * Returns a small definition of the given text by keeping only the first line and replacing the rest with a placeholder.
     *
     * @param text the input text
     * @return the small definition
     */
    private static String getSmallDefinition(String text) {
        // Split the string into an array of lines
        String[] lines = text.split("\\{");
        return lines[0];
    }

    /**
     * Returns the definition of the given object's body with a maximum number of lines.
     * If the number of lines exceeds the maximum, an empty string is returned.
     *
     * @param n           the object
     * @param numLinesMax the maximum number of lines
     * @return the body definition
     */
    private static String getBodyDefinition(Object n, int numLinesMax) {
        String[] lines = n.toString().split("\n");
        if (lines.length > numLinesMax) {
            int count = 0;
            StringBuilder builder = new StringBuilder(lines.length * 20);
            for (String line : lines) {
                builder.append(line).append('\n');
                count++;
                if (count >= 200) {
                    break;
                }
            }
            return builder.toString();
        } else {
            return n.toString();
        }
    }

    /**
     * Extracts the Javadoc comment and method code from the given code.
     *
     * @param code the input code
     * @return an array containing the Javadoc comment and method code
     */
    public String[] extractJavaDoc(String code) {
        // Define the regular expression pattern to match Javadoc comments
        Matcher matcher = JAVA_DOC_REGEX.matcher(code);

        if (matcher.find()) {
            // Extract the Javadoc comment
            String javadoc = matcher.group();

            // Extract the method code
            String methodCode = code.substring(matcher.end());

            return new String[]{javadoc, methodCode};
        } else {
            return new String[]{"", code}; // No Javadoc found
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration cls, Void arg) {
        if (!cls.isInnerClass() && !cls.isStatic()) {
            visitClass(
                    compilationUnit.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(""),
                    compilationUnit.getImports().stream().map(Node::toString).collect(Collectors.joining("\n")),
                    cls
            );
        }

        super.visit(cls, arg);
    }

    /**
     * Extracts the field name from a FieldDeclaration.
     *
     * @param field the input field
     * @return the field name
     */
    private String fieldName(FieldDeclaration field) {
        final Optional<VariableDeclarator> variableDeclarator = field.getChildNodes().stream()
                .filter(node -> node instanceof VariableDeclarator)
                .map(n -> (VariableDeclarator) n)
                .findFirst();

        return variableDeclarator.map(NodeWithSimpleName::getNameAsString).orElse("NO_NAME");
    }

    /**
     * Visits a class or interface.
     *
     * @param packageName the package name
     * @param importBody  the import statements
     * @param cls         the class or interface
     */
    public void visitClass(String packageName, String importBody, ClassOrInterfaceDeclaration cls) {
        final String[] parts = extractJavaDoc(getBodyDefinition(cls, 200));
        final String javaDoc = parts[0];
        final String code = parts[1];


        final JavaItem javaItem = JavaItem.builder()
                .importBody(importBody)
                .type(cls.isInterface() ? JavaItemType.INTERFACE : JavaItemType.CLASS)
                .name(packageName + "." + cls.getNameAsString())
                .simpleName(cls.getNameAsString())
                .definition(getSmallDefinition(code))
                .javadoc(javaDoc)
                .body(code)
                .build();
        javaItems.add(javaItem);
        cls.getChildNodes().stream().filter(n -> n instanceof ClassOrInterfaceDeclaration)
                .forEach(clsInner -> visitClassType(packageName, importBody, javaItem, (ClassOrInterfaceDeclaration) clsInner));
        cls.getMethods().forEach(method -> visitMethod(javaItem, method));
        cls.getFields().forEach(field -> visitField(javaItem, field));
    }

    /**
     * Visits a class or interface type.
     *
     * @param packageName the package name
     * @param importBody  the import statements
     * @param parent      the parent item
     * @param cls         the class or interface type
     */
    private void visitClassType(String packageName, String importBody, JavaItem parent, ClassOrInterfaceDeclaration cls) {
        final String[] parts = extractJavaDoc(getBodyDefinition(cls, 200));
        final String javaDoc = parts[0];
        final String code = parts[1];

        JavaItem javaItem = JavaItem.builder()
                .importBody(importBody)
                .type(JavaItemType.CLASS)
                .name(parent.getName() + "." + cls.getNameAsString())
                .simpleName(cls.getNameAsString())
                .definition(getSmallDefinition(code))
                .javadoc(javaDoc)
                .parent(parent)
                .body(code)
                .build();
        javaItems.add(javaItem);
    }

    /**
     * Visits a field.
     *
     * @param parent the parent item
     * @param field  the field
     */
    private void visitField(JavaItem parent, FieldDeclaration field) {

        final String[] parts = extractJavaDoc(field.toString());
        final String javaDoc = parts[0];
        final String code = parts[1];


        JavaItem javaItem = JavaItem.builder()
                .type(JavaItemType.FIELD)
                .name(parent.getName() + "." + fieldName(field))
                .simpleName(fieldName(field))
                .definition(code)
                .javadoc(javaDoc)
                .parent(parent)
                .build();
        javaItems.add(javaItem);
    }

    /**
     * Visits a method.
     *
     * @param parent the parent item
     * @param method the method
     */
    public void visitMethod(JavaItem parent, final MethodDeclaration method) {
        String[] parts = extractJavaDoc(getBodyDefinition(method, 500));
        final String javaDoc = parts[0];
        final String code = parts[1];

        JavaItem javaItem = JavaItem.builder()
                .type(JavaItemType.METHOD)
                .name(parent.getName() + "." + method.getName())
                .simpleName(method.getName().toString())
                .definition(getSmallDefinition(code))
                .javadoc(javaDoc)
                .parent(parent)
                .body(code)
                .build();
        javaItems.add(javaItem);
        //System.out.println(item);
    }

    @Override
    public void visit(EnumDeclaration n, Void arg) {
        visitEnum(
                compilationUnit.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(""),
                compilationUnit.getImports().stream().map(Node::toString).collect(Collectors.joining("\n")),
                n
        );
        super.visit(n, arg);
    }

    /**
     * Visits an enum.
     *
     * @param packageName the package name
     * @param importBody  the import statements
     * @param enumD       the enum
     */
    private void visitEnum(String packageName, String importBody, EnumDeclaration enumD) {
        String[] parts = extractJavaDoc(getBodyDefinition(enumD, 200));
        final String javaDoc = parts[0];
        final String code = parts[1];

        JavaItem javaItem = JavaItem.builder()
                .importBody(importBody)
                .type(JavaItemType.ENUM)
                .name(packageName + "." + enumD.getNameAsString())
                .simpleName(enumD.getNameAsString())
                .definition(getSmallDefinition(code))
                .javadoc(javaDoc)
                .body(code)
                .build();
        javaItems.add(javaItem);
        enumD.getMethods().forEach(method -> visitMethod(javaItem, method));
        enumD.getFields().forEach(field -> visitField(javaItem, field));
    }

    public List<JavaItem> run(File file) throws FileNotFoundException {
        compilationUnit = StaticJavaParser.parse(file);
        compilationUnit.accept(this, null);
        return this.javaItems;
    }
}
