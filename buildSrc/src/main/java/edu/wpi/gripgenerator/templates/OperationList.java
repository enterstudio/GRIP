package edu.wpi.gripgenerator.templates;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The template for the Operation List.
 * This list contains all of the operations that have been generated by the generator.
 */
public class OperationList {
    private final PackageDeclaration packageDec = new PackageDeclaration(new NameExpr("edu.wpi.grip.generated"));
    private final String className = "CVOperations";
    private final List<ImportDeclaration> imports;
    private final List<Operation> operations;

    /**
     * Constructs the operation list
     *
     * @param imports Any additional imports required for this operation to compile.
     */
    public OperationList(ImportDeclaration... imports) {
        this.imports = new ArrayList(Arrays.asList(imports));
        this.imports.addAll(Arrays.asList(
                new ImportDeclaration(new NameExpr("java.util.Arrays"), false, false),
                new ImportDeclaration(new NameExpr("java.util.List"), false, false),
                new ImportDeclaration(new NameExpr("edu.wpi.grip.core.Operation"), false, false)
        ));
        this.operations = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    /**
     * Adds an operation to the list of Operations to be used in the pipeline.
     *
     * @param operation An operation to add.
     */
    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    private FieldDeclaration getOperationList() {
        ClassOrInterfaceType listType = new ClassOrInterfaceType("List");
        listType.setTypeArgs(Arrays.asList(new ClassOrInterfaceType("Operation")));
        return new FieldDeclaration(
                ModifierSet.addModifier(
                        ModifierSet.addModifier(ModifierSet.FINAL, ModifierSet.PUBLIC),
                        ModifierSet.STATIC
                ),
                listType,
                new VariableDeclarator(
                        new VariableDeclaratorId("OPERATIONS"),
                        new MethodCallExpr(
                                new NameExpr("Arrays"),
                                "asList",
                                operations.stream().map(o -> new ObjectCreationExpr(null, new ClassOrInterfaceType(o.getOperationClassName()), null)).collect(Collectors.toList())
                        )
                )
        );
    }

    private ClassOrInterfaceDeclaration getClassDeclaration() {
        ClassOrInterfaceDeclaration operationList = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC, false, "CVOperations");
        operationList.setMembers(Arrays.asList(getOperationList()));
        return operationList;
    }

    /**
     * Generates the CompilationUnit to print to a file.
     *
     * @return The CompilationUnit for this class.
     */
    public CompilationUnit getDeclaration() {
        return new CompilationUnit(
                packageDec,
                this.imports,
                Arrays.asList(getClassDeclaration())
        );
    }

}
