package org.walkmod.findbugs.transformations;

import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.ImportDeclaration;
import org.walkmod.javalang.ast.Node;
import org.walkmod.javalang.ast.expr.AnnotationExpr;
import org.walkmod.javalang.test.SemanticTest;
import org.walkmod.walkers.VisitorContext;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NotNullTransformationTest extends SemanticTest {

  @Test
  public void shouldAddAnnotationToPackageInfoWithOtherAnnotations() throws Exception {
    final File packageInfoFile = new File(getClass().getClassLoader()
        .getResource("package-info-with-annotations/package-info.java").getFile());
    final CompilationUnit cu = ASTManager.parse(packageInfoFile);

    final NotNullTransformation visitor = new NotNullTransformation();
    visitor.visit(cu, null);

    verifyAnnotationPresent(cu, 2);
  }

  @Test
  public void shouldAddAnnotationToEmptyPackageInfo() throws Exception {
    final File packageInfoFile = new File(getClass().getClassLoader()
        .getResource("package-info-empty/package-info.java").getFile());
    final CompilationUnit cu = ASTManager.parse(packageInfoFile);

    final NotNullTransformation visitor = new NotNullTransformation();
    visitor.visit(cu, null);

    verifyAnnotationPresent(cu, 1);
  }

  @Test
  public void shouldNotAddMoreAnnotationToPackageInfoThatAlreadyProcessed() throws Exception {
    final File packageInfoFile = new File(getClass().getClassLoader()
        .getResource("package-info-processed/package-info.java").getFile());
    final CompilationUnit cu = ASTManager.parse(packageInfoFile);

    final NotNullTransformation visitor = new NotNullTransformation();
    visitor.visit(cu, null);

    verifyAnnotationPresent(cu, 1);
  }

  @Test
  public void shouldCreateAPackageInfoFileWhereNeeded() throws Exception {
    final CompilationUnit cu = compile("package hello.world; class Hello { }");

    final NotNullTransformation visitor = new NotNullTransformation();
    final VisitorContext ctx = new VisitorContext();
    visitor.visit(cu, ctx);

    final Collection<Object> resultNodes = ctx.getResultNodes();
    assertThat(resultNodes)
        .hasSize(1)
        .first()
        .isOfAnyClassIn(CompilationUnit.class);
    verifyAnnotationPresent((CompilationUnit)resultNodes.iterator().next(), 1);
  }

  @Test
  public void shouldNotCreateAPackageInfoFileWhereExists() throws Exception {
    final CompilationUnit cu = compile("package hello.world; class Hello { }",
        "package hello; import java.lang.annotation.*; @Retention(RetentionPolicy.RUNTIME) public @interface NotNullByDefault { }",
        "@NotNullByDefault package hello.world; import hello.NotNullByDefault;");

    final NotNullTransformation visitor = new NotNullTransformation();
    final VisitorContext ctx = new VisitorContext();
    visitor.visit(cu, ctx);

    final Collection<Object> resultNodes = ctx.getResultNodes();
    assertThat(resultNodes).isEmpty();
  }

  private void verifyAnnotationPresent(final CompilationUnit cu, final int totalImportsSize) {
    final List<ImportDeclaration> imports = cu.getImports();
    assertThat(imports)
        .hasSize(totalImportsSize)
        .extracting(ImportDeclaration::getName)
        .extracting(Node::toString)
        .contains("hello.NotNullByDefault");

    final List<AnnotationExpr> annotations = cu.getPackage().getAnnotations();
    assertThat(annotations)
        .hasSize(totalImportsSize)
        .extracting(AnnotationExpr::getName)
        .extracting("name")
        .contains("NotNullByDefault");
  }

}
