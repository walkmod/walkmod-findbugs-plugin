package org.walkmod.findbugs.transformations;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.PackageDeclaration;
import org.walkmod.javalang.ast.expr.AnnotationExpr;
import org.walkmod.javalang.ast.expr.MarkerAnnotationExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

import java.util.List;

public class NotNullTransformation extends VoidVisitorAdapter<VisitorContext> {

  @Override
  public void visit(PackageDeclaration packageDeclaration, VisitorContext ctx) {
    List<AnnotationExpr> annotations = packageDeclaration.getAnnotations();
    if (annotations != null) {
      annotations.add(new MarkerAnnotationExpr(new NameExpr("NotNullByDefault")));
      CompilationUnit cu = (CompilationUnit) packageDeclaration.getParentNode();
      cu.getImports()
    }
  }

  @Override
  public void visit(CompilationUnit compilationUnit, VisitorContext ctx) {
    if (compilationUnit.getFileName().endsWith("package-info.java")) {
      super.visit(compilationUnit, ctx);
    }
  }


}
