package org.walkmod.findbugs.transformations;

import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;

import java.io.File;

public class NotNullTransformationTest {

  @Test
  public void shouldAddAnnotationToPackageInfoWithOtherAnnotations() throws Exception {
    final File packageInfoFile = new File(getClass().getClassLoader()
        .getResource("package-info-with-annotations/package-info.java").getFile());
    final CompilationUnit cu = ASTManager.parse(packageInfoFile);

    final NotNullTransformation visitor = new NotNullTransformation();
    visitor.visit(cu, null);

    System.out.println(cu);

//    MethodDeclaration md = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(0);
//    ReferenceType type = (ReferenceType) md.getParameters().get(0).getType();
//    Assert.assertEquals(2, type.getArrayCount());
  }

}
