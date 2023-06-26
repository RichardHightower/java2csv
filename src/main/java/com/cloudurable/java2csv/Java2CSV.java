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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parse Java files and turn them into CSV files.
 */
public class Java2CSV {

    /**
     * Extracts the field name from a FieldDeclaration.
     *
     * @param field the input field
     * @return the field name
     */
    private static String fieldName(FieldDeclaration field) {
        final Optional<VariableDeclarator> variableDeclarator = field.getChildNodes().stream()
                .filter(node -> node instanceof VariableDeclarator)
                .map(n -> (VariableDeclarator) n)
                .findFirst();

        return variableDeclarator.map(NodeWithSimpleName::getNameAsString).orElse("NO_NAME");
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
        return lines[0] + "{ /* the rest ... */ }";
    }

    /**
     * Returns the definition of the given object's body with a maximum number of lines.
     * If the number of lines exceeds the maximum, an empty string is returned.
     *
     * @param n            the object
     * @param numLinesMax  the maximum number of lines
     * @return the body definition
     */
    private static String getBodyDefinition(Object n, int numLinesMax) {
        String[] lines = n.toString().split("\n");
        if (lines.length > numLinesMax) {
            return "";
        } else {
            return n.toString();
        }
    }

    /**
     * Entry point of the program.
     *
     * @param args the command line arguments
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        try {
            final String directoryPath = args.length > 0 ? args[0] : ".";
            final String outputFile = args.length > 1 ? args[1] : "output.csv";

            File dir = new File(directoryPath).getCanonicalFile();
            if (dir.exists() && dir.isDirectory()) {
                List<Item> items = scanDirectory(dir);
                List<List<String>> lines = items.stream().map(Item::row).collect(Collectors.toList());
                try (CSVWriter writer = new CSVWriter(new FileWriter(outputFile))) {
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

    /**
     * Scans the given directory and returns a list of items representing the Java code.
     *
     * @param directoryPath the directory path
     * @return the list of items
     * @throws IOException if an I/O error occurs
     */
    private static List<Item> scanDirectory(File directoryPath) throws IOException {
        List<Item> items = new ArrayList<>(32);

        try (Stream<Path> walk = Files.walk(directoryPath.toPath())) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> parseFile(p.toFile(), items));

        } catch (IOException e) {
            // Handle the IOException if an error occurs while scanning the directory
            System.err.println("An error occurred while scanning the directory: " + e.getMessage());
            throw e;
        }

        return items;
    }

    /**
     * Parses the given file and adds the parsed items to the list.
     *
     * @param file  the file to parse
     * @param items the list of items to add to
     */
    private static void parseFile(File file, List<Item> items) {
        System.out.println(file);
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            compilationUnit.accept(new ClassVisitor(compilationUnit, items), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Visitor for visiting classes, interfaces, and enums.
     */
    private static class ClassVisitor extends VoidVisitorAdapter<Void> {
        static Pattern JAVA_DOC_REGEX = Pattern.compile("^/\\*\\*.*?\\*/\\s*", Pattern.DOTALL);
        private final CompilationUnit compilationUnit;
        private final List<Item> items;

        public ClassVisitor(CompilationUnit compilationUnit, List<Item> items) {
            this.compilationUnit = compilationUnit;
            this.items = items;
        }

        /**
         * Extracts the Javadoc comment and method code from the given code.
         *
         * @param code the input code
         * @return an array containing the Javadoc comment and method code
         */
        public static String[] extractJavaDoc(String code) {
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
            if (!cls.isInnerClass()) {
                visitClass(
                        compilationUnit.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(""),
                        compilationUnit.getImports().stream().map(Node::toString).collect(Collectors.joining("\n")),
                        cls
                );
            }

            super.visit(cls, arg);
        }

        /**
         * Visits a class or interface.
         *
         * @param packageName the package name
         * @param importBody  the import statements
         * @param cls         the class or interface
         */
        public void visitClass(String packageName, String importBody, ClassOrInterfaceDeclaration cls) {
            String[] parts = extractJavaDoc(getBodyDefinition(cls, 200));
            final String javaDoc = parts[0];
            final String code = parts[1];

            Item item = Item.builder()
                    .importBody(importBody)
                    .type(cls.isInterface() ? JavaItemType.INTERFACE : JavaItemType.CLASS)
                    .name(packageName + "." + cls.getNameAsString())
                    .simpleName(cls.getNameAsString())
                    .definition(getSmallDefinition(code))
                    .javadoc(javaDoc)
                    .body(code)
                    .build();
            items.add(item);
            System.out.println(item);
            cls.getImplementedTypes().stream()
                    .filter(ClassOrInterfaceType::isClassOrInterfaceType)
                    .forEach(clsInner -> visitClassType(packageName, importBody, item, clsInner.asClassOrInterfaceType()));
            cls.getMethods().forEach(method -> visitMethod(item, method));
            cls.getFields().forEach(field -> visitField(item, field));
        }

        /**
         * Visits a class or interface type.
         *
         * @param packageName the package name
         * @param importBody  the import statements
         * @param parent      the parent item
         * @param cls         the class or interface type
         */
        private void visitClassType(String packageName, String importBody, Item parent, ClassOrInterfaceType cls) {
            final String[] parts = extractJavaDoc(getBodyDefinition(cls, 200));
            final String javaDoc = parts[0];
            final String code = parts[1];

            Item item = Item.builder()
                    .importBody(importBody)
                    .type(JavaItemType.CLASS)
                    .name(parent.getName() + "." + cls.getNameAsString())
                    .simpleName(cls.getNameAsString())
                    .definition(getSmallDefinition(code))
                    .javadoc(javaDoc)
                    .parent(parent)
                    .body(code)
                    .build();
            items.add(item);
            System.out.println(item);
        }

        /**
         * Visits a field.
         *
         * @param parent the parent item
         * @param field  the field
         */
        private void visitField(Item parent, FieldDeclaration field) {
            Item item = Item.builder()
                    .type(JavaItemType.FIELD)
                    .name(parent.getName() + "." + fieldName(field))
                    .simpleName(fieldName(field))
                    .definition(field.toString())
                    .javadoc(field.getJavadoc().map(Object::toString).orElse(""))
                    .parent(parent)
                    .build();
            items.add(item);
            System.out.println(item);
        }

        /**
         * Visits a method.
         *
         * @param parent the parent item
         * @param method the method
         */
        public void visitMethod(Item parent, final MethodDeclaration method) {
            String[] parts = extractJavaDoc(getBodyDefinition(method, 500));
            final String javaDoc = parts[0];
            final String code = parts[1];

            Item item = Item.builder()
                    .type(JavaItemType.METHOD)
                    .name(parent.getName() + "." + method.getName())
                    .simpleName(method.getName().toString())
                    .definition(getSmallDefinition(code))
                    .javadoc(javaDoc)
                    .parent(parent)
                    .body(code)
                    .build();
            items.add(item);
            System.out.println(item);
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

            Item item = Item.builder()
                    .importBody(importBody)
                    .type(JavaItemType.ENUM)
                    .name(packageName + "." + enumD.getNameAsString())
                    .simpleName(enumD.getNameAsString())
                    .definition(getSmallDefinition(code))
                    .javadoc(javaDoc)
                    .body(code)
                    .build();
            items.add(item);
            System.out.println(item);
            enumD.getMethods().forEach(method -> visitMethod(item, method));
            enumD.getFields().forEach(field -> visitField(item, field));
        }
    }
}
