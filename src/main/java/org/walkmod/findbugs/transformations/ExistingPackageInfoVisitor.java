package org.walkmod.findbugs.transformations;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.ImportDeclaration;
import org.walkmod.javalang.ast.PackageDeclaration;
import org.walkmod.javalang.ast.expr.AnnotationExpr;
import org.walkmod.javalang.ast.expr.MarkerAnnotationExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

import java.util.Arrays;
import java.util.List;

import static org.walkmod.findbugs.transformations.NotNullTransformation.ANNOTATION_NAME;
import static org.walkmod.findbugs.transformations.NotNullTransformation.PACKAGE_NAME;

public class ExistingPackageInfoVisitor extends VoidVisitorAdapter<VisitorContext> {

  @Override
  public void visit(final PackageDeclaration packageDeclaration, final VisitorContext ctx) {
    final List<AnnotationExpr> annotations = packageDeclaration.getAnnotations();
    if (annotations != null) {
      boolean hasAnnotation = annotations.stream()
          .map(AnnotationExpr::getName)
          .map(NameExpr::toString)
          .anyMatch(name -> name.equals(ANNOTATION_NAME));
      if (!hasAnnotation) {
        annotations.add(createAnnotationExpression());
      }
    } else {
      packageDeclaration.setAnnotations(Arrays.asList(
          createAnnotationExpression()
      ));
    }
  }


  @Override
  public void visit(final CompilationUnit compilationUnit, final VisitorContext ctx) {
    if (compilationUnit.getFileName().endsWith("package-info.java")) {
      super.visit(compilationUnit, ctx);

      final List<ImportDeclaration> imports = compilationUnit.getImports();
      if (imports != null) {
        boolean hasImport = imports.stream()
            .map(ImportDeclaration::getName)
            .map(NameExpr::toString)
            .anyMatch(name -> name.equals(PACKAGE_NAME + ANNOTATION_NAME));
        if (! hasImport) {
          System.out.printf("------ Added imports to " + compilationUnit.getFileName());
          imports.add(createImportExpression());
        }
      } else {
        compilationUnit.setImports(Arrays.asList(createImportExpression()));
      }
    }
  }

  private MarkerAnnotationExpr createAnnotationExpression() {
    return new MarkerAnnotationExpr(new NameExpr(ANNOTATION_NAME));
  }

  private ImportDeclaration createImportExpression() {
    return new ImportDeclaration(
        new NameExpr(PACKAGE_NAME + ANNOTATION_NAME), false, false);
  }


}
