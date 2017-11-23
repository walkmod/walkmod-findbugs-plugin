package org.walkmod.findbugs.transformations;

import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.ImportDeclaration;
import org.walkmod.javalang.ast.expr.AnnotationExpr;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NotNullTransformationTest {

  @Test
  public void shouldAddAnnotationToPackageInfoWithOtherAnnotations() throws Exception {
    final File packageInfoFile = new File(getClass().getClassLoader()
        .getResource("package-info-with-annotations/package-info.java").getFile());
    final CompilationUnit cu = ASTManager.parse(packageInfoFile);

    final NotNullTransformation visitor = new NotNullTransformation();
    visitor.visit(cu, null);

    System.out.println(cu);

    final List<ImportDeclaration> imports = cu.getImports();
    assertThat(imports)
        .hasSize(2)
        .extracting(ImportDeclaration::getName)
        .extracting("name")
        .contains("com.schibsted.engprod.commons.annotations.NotNullByDefault");

    final List<AnnotationExpr> annotations = cu.getPackage().getAnnotations();
    assertThat(annotations)
        .hasSize(2)
        .extracting(AnnotationExpr::getName)
        .extracting("name")
        .contains("NotNullByDefault");
  }

  @Test
  public void shouldAddAnnotationToEmptyPackageInfo() throws Exception {
    final File packageInfoFile = new File(getClass().getClassLoader()
        .getResource("package-info-empty/package-info.java").getFile());
    final CompilationUnit cu = ASTManager.parse(packageInfoFile);

    final NotNullTransformation visitor = new NotNullTransformation();
    visitor.visit(cu, null);

    System.out.println(cu);

    final List<ImportDeclaration> imports = cu.getImports();
    assertThat(imports)
        .hasSize(1)
        .extracting(ImportDeclaration::getName)
        .extracting("name")
        .contains("com.schibsted.engprod.commons.annotations.NotNullByDefault");

    final List<AnnotationExpr> annotations = cu.getPackage().getAnnotations();
    assertThat(annotations)
        .hasSize(1)
        .extracting(AnnotationExpr::getName)
        .extracting("name")
        .contains("NotNullByDefault");
  }

  @Test
  public void shouldNotAddMoreAnnotationToPackageInfoThatAlreadyProcessed() throws Exception {
    final File packageInfoFile = new File(getClass().getClassLoader()
        .getResource("package-info-processed/package-info.java").getFile());
    final CompilationUnit cu = ASTManager.parse(packageInfoFile);

    final NotNullTransformation visitor = new NotNullTransformation();
    visitor.visit(cu, null);

    System.out.println(cu);

    final List<ImportDeclaration> imports = cu.getImports();
    assertThat(imports)
        .hasSize(1)
        .extracting(ImportDeclaration::getName)
        .extracting("name")
        .contains("com.schibsted.engprod.commons.annotations.NotNullByDefault");

    final List<AnnotationExpr> annotations = cu.getPackage().getAnnotations();
    assertThat(annotations)
        .hasSize(1)
        .extracting(AnnotationExpr::getName)
        .extracting("name")
        .contains("NotNullByDefault");
  }

}
