package org.walkmod.findbugs.transformations;

import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.expr.AssignExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

public class AddFinalKeywordVisitor extends VoidVisitorAdapter<VisitorContext> {
    static private Logger logger = Logger.getLogger(AddFinalKeywordVisitor.class.getName());

    private String processingClass = "";
    final private ArrayList<Parameter> parameters = new ArrayList<>();
    final private HashSet<String> modifiedLocals = new HashSet<>();

    @Override
    public void visit(ClassOrInterfaceDeclaration n, VisitorContext arg) {
        processingClass = n.getSymbolName();
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, VisitorContext arg) {
        parameters.clear();
        modifiedLocals.clear();

        super.visit(n, arg);

        for (Parameter param : parameters) {
            if (!modifiedLocals.contains(param.getSymbolName())) {
                param.setModifiers(param.getModifiers() | ModifierSet.FINAL);
            } else {
                logger.warning("" +
                        "Parameter '" + param.getSymbolName() + "'"
                        + " in method '" + n.getSymbolName() + "'"
                        + " and class '" + processingClass + "'" +
                        " is being modified inside the function!"
                );
            }
        }
    }

    @Override
    public void visit(Parameter param, VisitorContext arg) {
        super.visit(param, arg);
        parameters.add(param);
    }

    @Override
    public void visit(AssignExpr n, VisitorContext arg) {
        super.visit(n, arg);
        final Expression target = n.getTarget();
        if (target instanceof NameExpr) {
            modifiedLocals.add(((NameExpr) target).getName());
        }
    }
}
