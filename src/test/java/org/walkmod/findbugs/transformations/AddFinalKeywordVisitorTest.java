package org.walkmod.findbugs.transformations;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;

public class AddFinalKeywordVisitorTest {
    @Test
    public void shouldAddFinalKeywordToParameters() throws Exception {
        final String base = "final-parameters/without-final";
        final String input = base + "/input";
        final String expected = base + "/expected";
        final CompilationUnit cu = ASTManager.parse(getResourceFile(input));
        final AddFinalKeywordVisitor visitor = new AddFinalKeywordVisitor();
        visitor.visit(cu, null);
        Assert.assertEquals(getResourceString(expected), cu.toString());
    }

    static private File getResourceFile(String fileReference) {
        return new File(AddFinalKeywordVisitorTest.class.getClassLoader()
                .getResource(fileReference).getFile());
    }

    static private String getResourceString(String fileReference) throws IOException {
        return Files.asCharSource(getResourceFile(fileReference), Charsets.UTF_8).read();
    }
}