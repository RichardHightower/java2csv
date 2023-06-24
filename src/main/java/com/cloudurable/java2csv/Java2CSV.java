package com.cloudurable.java2csv;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;


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

        public ClassVisitor(CompilationUnit compilationUnit) {
            this.compilationUnit = compilationUnit;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration cls, Void arg) {

            visitClass(compilationUnit.getPackageDeclaration().get().getNameAsString(), cls);
            super.visit(cls, arg);
        }

        public void visitClass(String packageName, ClassOrInterfaceDeclaration cls) {


            String type = cls.isInterface() ? "interface" : "class";
            System.out.printf("%s: %s \n %s \n %s \n",
                    type,
                    packageName + "." + cls.getNameAsString(),
                    getSmallDefinition(cls.toString()),
                    cls.getJavadoc(),
                    getBodyDefinition(cls, 200, "")
                    );
            cls.getMethods().forEach(method -> visitMethod(cls, method));
            cls.getFields().forEach(field -> visitField(cls, field));

        }


        private void visitField(ClassOrInterfaceDeclaration cls, FieldDeclaration field) {
            System.out.printf("Field: %s \n %s \n %s \n",
                    cls.getNameAsString() + "." + fieldName(field),
                    field,
                    field.getJavadoc()
            );
        }


        public void visitMethod(final ClassOrInterfaceDeclaration cls, final MethodDeclaration method) {
            System.out.printf("Method: %s \n %s \n %s \n %s \n",
                    cls.getNameAsString() + "." + method.getName(),
                    getSmallDefinition(method.toString()),
                    method.getJavadoc(),
                    getBodyDefinition(method, 500, "")
            );
        }

        @Override
        public void visit(EnumDeclaration n, Void arg) {
            System.out.println("Enum: " + n.getName());
            n.getMethods().forEach(method -> System.out.println("    Method: " + method.getName()));
            super.visit(n, arg);
        }
    }
}

