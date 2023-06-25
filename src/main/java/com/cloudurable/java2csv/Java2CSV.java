package com.cloudurable.java2csv;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
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
     *
     * @param field input field
     */
    private static String fieldName(FieldDeclaration field) {
        final Optional<VariableDeclarator> variableDeclarator = field.getChildNodes().stream()
                .filter(node -> node instanceof VariableDeclarator)
                .map(n -> (VariableDeclarator) n)
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
            String directoryPath = args.length > 0 ? args[0] : ".";

            String output = args.length > 1 ? args[1] : "output.csv";

            File dir = new File(directoryPath).getCanonicalFile();
            if (dir.exists() && dir.isDirectory()) {
                List<Item> items = scanDirectory(dir);
                List<List<String>> lines = items.stream().map(Item::row).collect(Collectors.toList());
                try (CSVWriter writer = new CSVWriter(new FileWriter(output))) {
                    writer.writeNext(Item.headers().toArray(new String[0]));
                    for (List<String> line : lines) {
                        writer.writeNext(line.toArray(new String[0]));
                    }
                }


            } else {
                System.out.printf("Directory does not exist %s or is not a directory", dir);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static List<Item> scanDirectory(File directoryPath) throws IOException {
        List<Item> items = new ArrayList<>(32);
        Files.walk(directoryPath.toPath())
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> parseFile(p.toFile(), items));

        return items;
    }

    private static void parseFile(File file, List<Item> items) {
        System.out.println(file);
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            compilationUnit.accept(new ClassVisitor(compilationUnit, items), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClassVisitor extends VoidVisitorAdapter<Void> {


        private final CompilationUnit compilationUnit;
        private final List<Item> items;


        public ClassVisitor(CompilationUnit compilationUnit, List<Item> items) {
            this.compilationUnit = compilationUnit;
            this.items = items;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration cls, Void arg) {



            if (!cls.isInnerClass()) {
                visitClass(compilationUnit.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(""),
                        compilationUnit.getImports().stream().map(Node::toString).collect(Collectors.joining("\n")),
                        cls);
            }

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
            cls.getImplementedTypes().stream().filter(typ -> typ.isClassOrInterfaceType())
                    .forEach(clsInner -> visitClassType(packageName, importBody, item, clsInner.asClassOrInterfaceType()));
            cls.getMethods().forEach(method -> visitMethod(item, method));
            cls.getFields().forEach(field -> visitField(item, field));

        }

        private void visitClassType(String packageName, String importBody, Item parent, ClassOrInterfaceType cls) {
            Item item = Item.builder().importBody(importBody)
                    .type(JavaItemType.CLASS)
                    .name(parent.getName() + "." + cls.getNameAsString())
                    .simpleName(cls.getNameAsString())
                    .definition(getSmallDefinition(cls.toString()))
                    .javadoc("")
                    .parent(parent)
                    .body(getBodyDefinition(cls, 200, "")).build();
            items.add(item);
            System.out.println(item);
        }


        private void visitField(Item parent, FieldDeclaration field) {

            Item item = Item.builder().type(JavaItemType.FIELD)
                    .name(parent.getName() + "." + fieldName(field))
                    .simpleName(fieldName(field))
                    .definition(field.toString())
                    .javadoc(field.getJavadoc().map(Object::toString).orElse(""))
                    .parent(parent)
                    .build();
            items.add(item);
            System.out.println(item);
        }


        public void visitMethod(Item parent, final MethodDeclaration method) {


            Item item = Item.builder().type(JavaItemType.METHOD)
                    .name(parent.getName() + "." + method.getName())
                    .simpleName(method.getName().toString())
                    .definition(method.toString())
                    .javadoc(method.getJavadoc().map(Object::toString).orElse(""))
                    .parent(parent)
                    .build();
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

