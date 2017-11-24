package org.walkmod.findbugs.transformations;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@RequiresSemanticAnalysis
public class NotNullTransformation extends VoidVisitorAdapter<VisitorContext> {
    private static final String ANNOTATION_NAME = "NotNullByDefault";
    private static final String PACKAGE_NAME = "com.schibsted.engprod.commons.annotations.";

  static final String ANNOTATION_NAME = "NotNullByDefault";
  static final String PACKAGE_NAME = "hello.";

  private final ExistingPackageInfoVisitor existingPackageInfoVisitor = new ExistingPackageInfoVisitor();

  private final Set<String> processedPackages = new HashSet<>();

  @Override
  public void visit(final CompilationUnit compilationUnit, final VisitorContext ctx) {
    if (compilationUnit.getFileName().endsWith("package-info.java")) {
      existingPackageInfoVisitor.visit(compilationUnit, ctx);
    } else {
      final Class<?> clazz = compilationUnit.getTypes().get(0).getSymbolData().getClazz();

      boolean hasAnnotations = Stream.of(clazz.getPackage().getAnnotations())
          .map(Annotation::annotationType)
          .map(Class::getName)
          .anyMatch(name -> name.equals(PACKAGE_NAME + ANNOTATION_NAME));
      if (!hasAnnotations && !processedPackages.contains(clazz.getPackage().getName())) {
        final CompilationUnit packageInfo = createPackageInfoCompilationUnit(compilationUnit);
        existingPackageInfoVisitor.visit(packageInfo, ctx);
        ctx.addResultNode(packageInfo);
        processedPackages.add(clazz.getPackage().getName());
      }
    }

  private CompilationUnit createPackageInfoCompilationUnit(final CompilationUnit siblingCompilationUnit) {
    final CompilationUnit compilationUnit = new CompilationUnit();
    compilationUnit.setPackage(siblingCompilationUnit.getPackage());
    return compilationUnit;
  }


}
