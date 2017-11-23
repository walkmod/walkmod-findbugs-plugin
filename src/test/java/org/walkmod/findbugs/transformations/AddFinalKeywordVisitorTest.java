package org.walkmod.findbugs.transformations;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Test;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AddFinalKeywordVisitorTest {
    @Test
    public void shouldAddFinalKeywordToParameters() throws Exception {
        testCase("final-parameters/without-final");
    }

    @Test
    public void shouldNotAddFinalKeywordToParametersIfModifiedInside() throws Exception {
        testCase("final-parameters/modified");
    }

    static private void testCase(String folder) throws Exception {
        final String input = folder + "/input";
        final String expected = folder + "/expected";
        final CompilationUnit cu = ASTManager.parse(getResourceFile(input));
        final AddFinalKeywordVisitor visitor = new AddFinalKeywordVisitor();
        visitor.visit(cu, null);
        assertThat(cu.toString()).isEqualTo(getResourceString(expected));
    }

    static private File getResourceFile(String fileReference) {
        return new File(AddFinalKeywordVisitorTest.class.getClassLoader()
                .getResource(fileReference).getFile());
    }

    static private String getResourceString(String fileReference) throws IOException {
        return Files.asCharSource(getResourceFile(fileReference), Charsets.UTF_8).read();
    }
}