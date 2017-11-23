package org.walkmod.findbugs.transformations;

import org.walkmod.javalang.ast.Comment;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.LineComment;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.Parameter;
import org.walkmod.javalang.ast.expr.AssignExpr;
import org.walkmod.javalang.ast.expr.Expression;
import org.walkmod.javalang.ast.expr.NameExpr;
import org.walkmod.javalang.ast.expr.UnaryExpr;
import org.walkmod.javalang.ast.stmt.Statement;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class AddFinalKeywordVisitor extends VoidVisitorAdapter<VisitorContext> {
    final private ArrayList<Parameter> parameters = new ArrayList<>();
    final private HashSet<String> parameterNames = new HashSet<>();
    final private HashSet<String> modifiedParameters = new HashSet<>();
    final private HashSet<Statement> doNotModifyStatements = new LinkedHashSet<>();

    @Override
    public void visit(final ClassOrInterfaceDeclaration n, final VisitorContext arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(final MethodDeclaration n, final VisitorContext arg) {
        parameterNames.clear();
        parameters.clear();
        modifiedParameters.clear();
        doNotModifyStatements.clear();

        super.visit(n, arg);

        for (Parameter param : parameters) {
            if (!modifiedParameters.contains(param.getSymbolName())) {
                param.setModifiers(param.getModifiers() | ModifierSet.FINAL);
            }
        }

        for (Statement entry : this.doNotModifyStatements) {
            CompilationUnit cu = NodeUtils.getAncestorOfType(entry, CompilationUnit.class);
            List<Comment> comments = cu.getComments();
            int beginLine = entry.getBeginLine();
            comments.add(new LineComment(beginLine, 0, beginLine, 0, " @TODO: Do not modify parameters!"));
            cu.setComments(comments);
        }
    }

    @Override
    public void visit(final Parameter param, final VisitorContext arg) {
        super.visit(param, arg);
        parameters.add(param);
        parameterNames.add(param.getSymbolName());
    }

    @Override
    public void visit(final AssignExpr n, final VisitorContext arg) {
        super.visit(n, arg);
        final Expression target = n.getTarget();
        if (target instanceof NameExpr) {
            checkNameExpr((NameExpr) target);
        }
    }

    @Override
    public void visit(final UnaryExpr n, final VisitorContext arg) {
        NodeUtils.getAncestorOfType(n, Statement.class);
        super.visit(n, arg);
        switch (n.getOperator()) {
            case preIncrement:
            case preDecrement:
            case posIncrement:
            case posDecrement:
                final Expression expr = n.getExpr();
                if (expr instanceof NameExpr) {
                    checkNameExpr((NameExpr) expr);
                }
                break;
        }
    }

    private void checkNameExpr(NameExpr expr) {
        String name = expr.getName();
        if (parameterNames.contains(name)) {
            doNotModifyStatements.add(NodeUtils.getAncestorOfType(expr, Statement.class));
            modifiedParameters.add(name);
        }
    }
}
