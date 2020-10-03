package com.zeoflow.stylar.codestyle;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbsVisitor implements CodeStyle.Visitor
{

    @Override
    public void visit(@NotNull List<? extends CodeStyle.Node> nodes) {
        for (CodeStyle.Node node : nodes) {
            if (node.isSyntax()) {
                visitSyntax((CodeStyle.Syntax) node);
            } else {
                visitText((CodeStyle.Text) node);
            }
        }
    }

    protected abstract void visitText(@NotNull CodeStyle.Text text);

    // do not forget to call visit(syntax.children()) inside
    protected abstract void visitSyntax(@NotNull CodeStyle.Syntax syntax);
}
