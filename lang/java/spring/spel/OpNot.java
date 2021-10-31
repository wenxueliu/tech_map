package com.wexueliu.bizhidao.auth;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.Operator;
import org.springframework.expression.spel.ast.SpelNodeImpl;

/**
* @author liuwenxue
* @date 2021-10-31
*/
public class OpNot extends Operator {

    public OpNot(int startPos, int endPos, SpelNodeImpl... operands) {
        super("!=", startPos, endPos, operands);
        this.exitTypeDescriptor = "Z";
    }

    @Override
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        return null;
    }
}
