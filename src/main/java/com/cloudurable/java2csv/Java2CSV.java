package com.cloudurable.java2csv;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Parse Java files and turn them into CSV files.
 */
public class Java2CSV {

    public final static String MY_FIELD = "BOO";


    /**
     * Extract field name from FieldDeclaration.
     * @param field input field
     */
    private static String fieldName(FieldDeclaration field) {
        final Optional<VariableDeclarator> variableDeclarator = field.getChildNodes().stream()
                .filter(node -> node instanceof VariableDeclarator)
                .map(n -> (VariableDeclarator)n)
                .findFirst();

        return variableDeclarator.map(NodeWithSimpleName::getNameAsString).orElse("NO_NAME");

    }

    private static String getSmallDefinition(String text) {
        // Split the string into an array of lines
        String[] lines = text.split("\\{");
        return lines[0] + "{ /* the rest ... */ }";


    }

    private static String getBodyDefinition(Object n, int numLinesMax, String alternativeString) {

        String[] lines = n.toString().split("\n");
        if (lines.length > numLinesMax) {
            return alternativeString;
        } else {
            return n.toString();
        }
    }


    public static void main(String[] args) throws IOException {
        System.out.println(new File(".").getCanonicalFile());
        try {
            String directoryPath =  args.length > 0 ? args[0]  : ".";

            File dir  = new File(directoryPath).getCanonicalFile();
            if (dir.exists() && dir.isDirectory()) {
                scanDirectory(dir);
            } else {
                System.out.printf("Directory does not exist %s or is not a directory", dir);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static void scanDirectory(File directoryPath) throws IOException {
        Files.walk(directoryPath.toPath())
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> parseFile(p.toFile()));
    }

    private static void parseFile(File file) {
        System.out.println(file);
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            compilationUnit.accept(new ClassVisitor(compilationUnit), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClassVisitor extends VoidVisitorAdapter<Void> {


        private final CompilationUnit compilationUnit;

        private final List<Item> items;

        public ClassVisitor(CompilationUnit compilationUnit) {
            this.compilationUnit = compilationUnit;
            this.items = new ArrayList<>();
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration cls, Void arg) {
            visitClass(compilationUnit.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(""),
                    compilationUnit.getImports().stream().map(Node::toString).collect(Collectors.joining("\n")),
                    cls);
            super.visit(cls, arg);
        }

        public void visitClass(String packageName, String importBody, ClassOrInterfaceDeclaration cls) {


            Item item = Item.builder().importBody(importBody)
                    .type(cls.isInterface() ? JavaItemType.INTERFACE : JavaItemType.CLASS)
                    .name(packageName + "." + cls.getNameAsString())
                    .simpleName(cls.getNameAsString())
                    .definition(getSmallDefinition(cls.toString()))
                    .javadoc(cls.getJavadoc().map(Object::toString).orElse(""))
                    .body(getBodyDefinition(cls, 200, "")).build();
            items.add(item);
            System.out.println(item);
            cls.getMethods().forEach(method -> visitMethod(item, method));
            cls.getFields().forEach(field -> visitField(item, field));

        }


        private void visitField(Item parent, FieldDeclaration field) {

            Item item = Item.builder().type(JavaItemType.FIELD)
                    .name(parent.getName() + "." + fieldName(field))
                    .simpleName(fieldName(field))
                    .definition(field.toString())
                    .javadoc(field.getJavadoc().map(Object::toString).orElse(""))
                    .build();
            items.add(item);
            System.out.println(item);
        }


        public void visitMethod(Item parent, final MethodDeclaration method) {

            Item item = Item.builder().type(JavaItemType.METHOD)
                    .name(parent.getName() + "." + method.getName())
                    .simpleName(method.getName().toString())
                    .definition(method.toString())
                    .javadoc(method.getJavadoc().map(Object::toString).orElse("")).build();
            items.add(item);
            System.out.println(item);
        }

        @Override
        public void visit(EnumDeclaration n, Void arg) {
            visitEnum(compilationUnit.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(""),
                    compilationUnit.getImports().stream().map(Node::toString).collect(Collectors.joining("\n")),
                    n);
            super.visit(n, arg);


        }

        private void visitEnum(String packageName, String importBody, EnumDeclaration enumD) {

            Item item = Item.builder().importBody(importBody)
                    .type(JavaItemType.ENUM)
                    .name(packageName + "." + enumD.getNameAsString())
                    .simpleName(enumD.getNameAsString())
                    .definition(getSmallDefinition(enumD.toString()))
                    .javadoc(enumD.getJavadoc().map(Object::toString).orElse(""))
                    .body(getBodyDefinition(enumD, 200, "")).build();
            items.add(item);
            System.out.println(item);
            enumD.getMethods().forEach(method -> visitMethod(item, method));
            enumD.getFields().forEach(field -> visitField(item, field));

        }
    }
}

