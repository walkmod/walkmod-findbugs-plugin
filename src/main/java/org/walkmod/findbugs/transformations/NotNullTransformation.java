package org.walkmod.findbugs.transformations;

import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.body.TypeDeclaration;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@RequiresSemanticAnalysis
public class NotNullTransformation extends VoidVisitorAdapter<VisitorContext> {
  static final String ANNOTATION_NAME = "NotNullByDefault";
  static final String PACKAGE_NAME = "hello.";

  private final ExistingPackageInfoVisitor existingPackageInfoVisitor = new ExistingPackageInfoVisitor();

  private final Set<String> processedPackages = new HashSet<>();

  @Override
  public void visit(final CompilationUnit compilationUnit, final VisitorContext ctx) {
    System.out.println("Processing: " + compilationUnit.getFileName());
    if (compilationUnit.getFileName().endsWith("package-info.java")) {
      existingPackageInfoVisitor.visit(compilationUnit, ctx);
    } else {
      final List<TypeDeclaration> types = compilationUnit.getTypes();
      if (types != null) {
        final SymbolData symbolData = types.get(0).getSymbolData();
        if (symbolData != null) {
          final Class<?> clazz = symbolData.getClazz();

          boolean hasAnnotations = Stream.of(clazz.getPackage().getAnnotations())
              .map(Annotation::annotationType)
              .map(Class::getName)
              .anyMatch(name -> name.equals(PACKAGE_NAME + ANNOTATION_NAME));
          if (!hasAnnotations && !processedPackages.contains(clazz.getPackage().getName())) {
            final CompilationUnit packageInfo = createPackageInfoCompilationUnit(compilationUnit);
            existingPackageInfoVisitor.visit(packageInfo, ctx);
            ctx.addResultNode(packageInfo);
            processedPackages.add(clazz.getPackage().getName());
            System.out.println(">>>> Should create " + packageInfo.getFileName());
          }
        }
      }
    }
  }

  private CompilationUnit createPackageInfoCompilationUnit(final CompilationUnit siblingCompilationUnit) {
    final CompilationUnit compilationUnit = new CompilationUnit();
    compilationUnit.setPackage(siblingCompilationUnit.getPackage());
    return compilationUnit;
  }


}
