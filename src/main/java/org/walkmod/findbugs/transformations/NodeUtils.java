package org.walkmod.findbugs.transformations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.walkmod.javalang.ast.Node;

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
}
