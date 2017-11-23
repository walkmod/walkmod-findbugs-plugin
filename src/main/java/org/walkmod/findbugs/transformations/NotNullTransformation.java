package org.walkmod.findbugs.transformations;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.ImportDeclaration;
import org.walkmod.javalang.ast.PackageDeclaration;
import org.walkmod.javalang.ast.expr.AnnotationExpr;
import org.walkmod.javalang.ast.expr.MarkerAnnotationExpr;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;

public class NotNullTransformation extends VoidVisitorAdapter<VisitorContext> {
    private static final String ANNOTATION_NAME = "NotNullByDefault";
    private static final String PACKAGE_NAME = "com.schibsted.engprod.commons.annotations.";

    @Override
    public void visit(PackageDeclaration packageDeclaration, VisitorContext ctx) {
        final List<AnnotationExpr> annotations = packageDeclaration.getAnnotations();
        if (annotations != null) {
            boolean hasAnnotation = annotations.stream()
                    .map(AnnotationExpr::getName)
                    .map(NameExpr::getName)
                    .anyMatch(name -> name.equals(ANNOTATION_NAME));
            if (!hasAnnotation) {
                annotations.add(createAnnotationExpression());
            }
        } else {
            packageDeclaration.setAnnotations(of(
                    createAnnotationExpression()
            ));
        }
    }

    @Override
    public void visit(CompilationUnit compilationUnit, VisitorContext ctx) {
        if (compilationUnit.getURI().getPath().endsWith("package-info.java")) {
            super.visit(compilationUnit, ctx);

            final List<ImportDeclaration> imports = compilationUnit.getImports();
            if (imports != null) {
                boolean hasImport = imports.stream()
                        .map(ImportDeclaration::getName)
                        .map(NameExpr::getName)
                        .anyMatch(name -> name.equals(PACKAGE_NAME + ANNOTATION_NAME));
                if (!hasImport) {
                    imports.add(createImportExpression());
                }
            } else {
                compilationUnit.setImports(of(createImportExpression()));
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
