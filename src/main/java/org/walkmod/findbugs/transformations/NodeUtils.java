package org.walkmod.findbugs.transformations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.walkmod.javalang.ast.Comment;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.LineComment;
import org.walkmod.javalang.ast.Node;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class NodeUtils {
    @Nullable
    static public <T> T getAncestorOfType(@Nullable final Node node, @NotNull final Class<T> clazz) {
        for (Node currentNode = node; currentNode != null; currentNode = currentNode.getParentNode()) {
            Class<? extends Node> currentNodeClass = currentNode.getClass();
            if (clazz.isAssignableFrom(currentNodeClass)) {
                //noinspection unchecked
                return (T) currentNode;
            }
        }
        return null;
    }

    static public void addComment(@NotNull final CompilationUnit cu, @NotNull final String comment, final int line) {
        List<Comment> comments = cu.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
            cu.setComments(comments);
        }
        comments.add(new LineComment(line, 0, line, 0, comment));
    }
}
