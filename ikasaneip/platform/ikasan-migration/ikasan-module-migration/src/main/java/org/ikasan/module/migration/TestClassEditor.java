package org.ikasan.module.migration;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestClassEditor {
    private String projectBasePackage;
    private File testClassFile;
    private File moduleMetaDataOutputFile;

    /**
     * Constructor for TestClassEditor.
     *
     * @param projectBasePackage         The base package of the project.
     * @param testClassFile              The test class file.
     * @param moduleMetaDataOutputFile   The output file for module metadata.
     */
    public TestClassEditor(String projectBasePackage, File testClassFile,
                           File moduleMetaDataOutputFile) {
        this.projectBasePackage = projectBasePackage;
        this.testClassFile = testClassFile;
        this.moduleMetaDataOutputFile = moduleMetaDataOutputFile;
    }

    /**
     * Adds a new metadata generation method to the test class file.
     *
     * @param methodName The name of the new method to be added.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public void addMetaDataGenerationMethod(String methodName) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(testClassFile);
        ClassOrInterfaceDeclaration classOrInterface = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new RuntimeException("No class found in the file."));

        this.addRelevantImportStatementsToTestClassIfTheyDoNotExist(cu);

        for (MethodDeclaration methodDeclaration : classOrInterface.getMethods()) {
            if (methodDeclaration.getNameAsString().equals(methodName)) methodDeclaration.remove();
        }
        MethodDeclaration newMethod = new MethodDeclaration();
        newMethod.setName(methodName);
        newMethod.setPublic(true);
        newMethod.addThrownException(IOException.class);
        newMethod.setType("void");
        newMethod.addAnnotation("Test");

        BlockStmt blockStmt = new BlockStmt();
        blockStmt.addStatement("JsonModuleManifestMetaDataProvider moduleMetaDataProvider " +
                "= new JsonModuleManifestMetaDataProvider(jsonModuleMetaDataProvider, jsonConfigurationMetaDataExtractor);");
        blockStmt.addStatement("moduleMetaDataProvider.setApplicationContext(applicationContext);");
        blockStmt.addStatement("Module module = applicationContext.getBean(Module.class);");
        blockStmt.addStatement("String moduleMetaData = moduleMetaDataProvider.serialiseModuleManifest" +
            "(moduleMetaDataProvider.describeModuleManifest(module, new HashMap()));");

        blockStmt.addStatement("this.writeStringToFile(\""+ this.moduleMetaDataOutputFile.getAbsolutePath() +"\", moduleMetaData);");
        newMethod.setBody(blockStmt);

        classOrInterface.addMember(newMethod);
        this.addStringToFileOutputMethod(classOrInterface);

        Files.write(testClassFile.toPath(), cu.toString().getBytes());
    }

    /**
     * Adds relevant import statements to the given CompilationUnit if they do not already exist.
     *
     * @param cu The CompilationUnit to which import statements will be added if needed.
     */
    private void addRelevantImportStatementsToTestClassIfTheyDoNotExist(CompilationUnit cu) {
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.ikasan.manifest.JsonModuleManifestMetaDataProvider"))) {
            cu.addImport("org.ikasan.manifest.JsonModuleManifestMetaDataProvider");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.ikasan.topology.metadata.JsonModuleMetaDataProvider"))) {
            cu.addImport("org.ikasan.topology.metadata.JsonModuleMetaDataProvider");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.ikasan.spec.module.Module"))) {
            cu.addImport("org.ikasan.spec.module.Module");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.springframework.beans.factory.config.ConfigurableListableBeanFactory"))) {
            cu.addImport("org.springframework.beans.factory.config.ConfigurableListableBeanFactory");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.springframework.beans.factory.config.ConfigurableListableBeanFactory"))) {
            cu.addImport("org.springframework.beans.factory.config.ConfigurableListableBeanFactory");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.springframework.beans.factory.annotation.Autowired"))) {
            cu.addImport("org.springframework.beans.factory.annotation.Autowired");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.springframework.beans.factory.config.BeanDefinition"))) {
            cu.addImport("org.springframework.beans.factory.config.BeanDefinition");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.springframework.beans.factory.support.AbstractBeanDefinition"))) {
            cu.addImport("org.springframework.beans.factory.support.AbstractBeanDefinition");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.springframework.beans.factory.support.GenericBeanDefinition"))) {
            cu.addImport("org.springframework.beans.factory.support.GenericBeanDefinition");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.springframework.context.ConfigurableApplicationContext"))) {
            cu.addImport("org.springframework.context.ConfigurableApplicationContext");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("org.springframework.core.type.MethodMetadata"))) {
            cu.addImport("org.springframework.core.type.MethodMetadata");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("java.util.HashMap"))) {
            cu.addImport("java.util.HashMap");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("java.io.IOException"))) {
            cu.addImport("java.io.IOException");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("java.nio.file.Files"))) {
            cu.addImport("java.nio.file.Files");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("java.nio.file.Path"))) {
            cu.addImport("java.nio.file.Path");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("java.nio.file.Paths"))) {
            cu.addImport("java.nio.file.Paths");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("java.util.Arrays"))) {
            cu.addImport("java.util.Arrays");
        }
        if (cu.getImports().stream().noneMatch(importDeclaration -> importDeclaration.getNameAsString()
            .equals("java.util.List"))) {
            cu.addImport("java.util.List");
        }
    }

    /**
     * Adds a new method named "writeStringToFile" to the provided ClassOrInterfaceDeclaration.
     *
     * @param classOrInterface The ClassOrInterfaceDeclaration to which the method will be added.
     */
    private void addStringToFileOutputMethod(ClassOrInterfaceDeclaration classOrInterface) {
        for (MethodDeclaration methodDeclaration : classOrInterface.getMethods()) {
            if (methodDeclaration.getNameAsString().equals("writeStringToFile")) methodDeclaration.remove();
        }
        MethodDeclaration newMethod = new MethodDeclaration();
        newMethod.setName("writeStringToFile");
        newMethod.setPublic(false);
        newMethod.addThrownException(IOException.class);
        newMethod.setType("void");
        newMethod.addParameter("String", "filePath");
        newMethod.addParameter("String", "contents");

        BlockStmt blockStmt = new BlockStmt();
        blockStmt.addStatement("Path path = Paths.get(filePath);");
        blockStmt.addStatement("Files.write(path, contents.getBytes());");
        newMethod.setBody(blockStmt);

        classOrInterface.addMember(newMethod);
    }

    /**
     * Checks if a field of the specified type and variable name is present in the given test class file.
     *
     * @param testClassFile The test class file to examine for the field.
     * @param type The type of the field to check for.
     * @param variableName The name of the variable representing the field.
     * @return true if the field of the specified type and variable name is present, false otherwise.
     * @throws IOException If an I/O error occurs while parsing the test class file.
     */
    private boolean isFieldPresent(File testClassFile, String type, String variableName) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(testClassFile);
        ClassOrInterfaceDeclaration classOrInterface = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new RuntimeException("No class found in the file."));

        for (FieldDeclaration field : classOrInterface.getFields()) {
            if (field.getElementType().asString().equals(type) &&
                field.getVariables().stream().filter(variableDeclarator
                    -> variableDeclarator.getName().asString().equals(variableName)).findFirst().isPresent()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an autowired field of type ApplicationContext to the provided test class file if not already present.
     *
     * @param testClassFile The test class file to which ApplicationContext field will be added.
     * @throws IOException If an I/O error occurs while updating the test class file.
     */
    public void addAutowiredApplicationContext(File testClassFile) throws IOException {
        if(this.isFieldPresent(testClassFile,"ApplicationContext", "applicationContext")) return;
        CompilationUnit cu = StaticJavaParser.parse(testClassFile);
        ClassOrInterfaceDeclaration classOrInterface = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new RuntimeException("No class found in the file."));

        FieldDeclaration applicationContextField = new FieldDeclaration();
        applicationContextField.addVariable(new VariableDeclarator(
                new ClassOrInterfaceType("ApplicationContext")
                , "applicationContext"));
        applicationContextField.addAnnotation("Autowired");

        classOrInterface.getMembers().add(0, applicationContextField);

        cu.addImport("org.springframework.beans.factory.annotation.Autowired");
        cu.addImport("org.springframework.context.ApplicationContext");

        Files.write(testClassFile.toPath(), cu.toString().getBytes());
    }

    /**
     * Adds a JsonModuleMetaDataProvider field with @Autowired annotation to the provided test class file.
     *
     * @param testClassFile The test class file to which the JsonModuleMetaDataProvider field will be added.
     * @throws IOException If an I/O error occurs while updating the test class file.
     */
    public void addJsonModuleMetaDataProvider(File testClassFile) throws IOException {
        if(this.isFieldPresent(testClassFile,"JsonModuleMetaDataProvider", "jsonModuleMetaDataProvider")) return;
        CompilationUnit cu = StaticJavaParser.parse(testClassFile);
        ClassOrInterfaceDeclaration classOrInterface = cu.findFirst(ClassOrInterfaceDeclaration.class)
            .orElseThrow(() -> new RuntimeException("No class found in the file."));

        FieldDeclaration applicationContextField = new FieldDeclaration();
        applicationContextField.addVariable(new VariableDeclarator(
            new ClassOrInterfaceType("JsonModuleMetaDataProvider")
            , "jsonModuleMetaDataProvider"));
        applicationContextField.addAnnotation("Autowired");

        classOrInterface.getMembers().add(0, applicationContextField);

        cu.addImport("org.springframework.beans.factory.annotation.Autowired");
        cu.addImport("org.ikasan.topology.metadata.JsonModuleMetaDataProvider");

        Files.write(testClassFile.toPath(), cu.toString().getBytes());
    }

    /**
     * Adds a JsonConfigurationMetaDataExtractor field with @Autowired annotation to the provided test class file.
     *
     * @param testClassFile The test class file to which the JsonConfigurationMetaDataExtractor field will be added.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public void addJsonConfigurationMetaDataExtractor(File testClassFile) throws IOException {
        if(this.isFieldPresent(testClassFile,"JsonConfigurationMetaDataExtractor", "jsonConfigurationMetaDataExtractor")) return;
        CompilationUnit cu = StaticJavaParser.parse(testClassFile);
        ClassOrInterfaceDeclaration classOrInterface = cu.findFirst(ClassOrInterfaceDeclaration.class)
            .orElseThrow(() -> new RuntimeException("No class found in the file."));

        FieldDeclaration applicationContextField = new FieldDeclaration();
        applicationContextField.addVariable(new VariableDeclarator(
            new ClassOrInterfaceType("JsonConfigurationMetaDataExtractor")
            , "jsonConfigurationMetaDataExtractor"));
        applicationContextField.addAnnotation("Autowired");

        classOrInterface.getMembers().add(0, applicationContextField);

        cu.addImport("org.springframework.beans.factory.annotation.Autowired");
        cu.addImport("org.ikasan.configurationService.metadata.JsonConfigurationMetaDataExtractor");

        Files.write(testClassFile.toPath(), cu.toString().getBytes());
    }
}
