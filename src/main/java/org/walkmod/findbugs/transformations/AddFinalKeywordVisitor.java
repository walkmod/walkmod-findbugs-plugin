package org.walkmod.findbugs.transformations;

import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

public class AddFinalKeywordVisitor extends VoidVisitorAdapter<VisitorContext> {
    @Override
    public void visit(Parameter param, VisitorContext arg) {
        super.visit(param, arg);
        param.setModifiers(param.getModifiers() | ModifierSet.FINAL);
    }
}
