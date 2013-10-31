package com.medvision360.medrecord.itest;

import org.openehr.am.archetype.assertion.Assertion;
import org.openehr.am.archetype.assertion.ExpressionBinaryOperator;
import org.openehr.am.archetype.assertion.ExpressionItem;
import org.openehr.am.archetype.assertion.ExpressionLeaf;
import org.openehr.am.archetype.assertion.OperatorKind;
import org.openehr.am.archetype.constraintmodel.primitive.CString;
import org.openehr.rm.support.identification.ArchetypeID;

/**
 * Utilities for working with openehr-aom {@link Assertion} rules.
 */
public class AssertionSupport
{
    protected boolean testArchetypeAssertion(ArchetypeID archetypeID, Assertion include) throws GenerateException
    {
        boolean match = false;
        String leftRule;
        OperatorKind op;
        String rightRule;
        String testValue;

        ExpressionItem expression = include.getExpression();
        if (!(expression instanceof ExpressionBinaryOperator))
        {
            throw new GenerateException(String.format(
                    "Expected binary expression (x matches y) for archetype slot expression \"%s\", " +
                            "but was %s",
                    expression.toString(),
                    expression.getClass().getSimpleName()));
        }
        ExpressionBinaryOperator binaryExpression = (ExpressionBinaryOperator) expression;

        ExpressionItem left = binaryExpression.getLeftOperand();
        if (!(left instanceof ExpressionLeaf))
        {
            throw new GenerateException(String.format(
                    "Expected binary expression (x matches y) for archetype slot expression \"%s\", " +
                            "but left hand side is of class %s, not ExpressionLeaf",
                    expression.toString(),
                    left.getClass().getSimpleName()));
        }

        op = binaryExpression.getOperator();
        if (!op.equals(OperatorKind.OP_EQ) && !op.equals(OperatorKind.OP_MATCHES))
        {
            throw new GenerateException(String.format(
                    "Expected string expression (x op y) for archetype slot expression \"%s\", " +
                            "but operator is of type %s, not = or matches",
                    expression.toString(),
                    op.toString()));
        }

        ExpressionItem right = binaryExpression.getRightOperand();
        if (!(right instanceof ExpressionLeaf))
        {
            throw new GenerateException(String.format(
                    "Expected binary expression (x matches y) for archetype slot expression \"%s\", " +
                            "but right hand side is of class %s, not ExpressionLeaf",
                    expression.toString(),
                    right.getClass().getSimpleName()));
        }

        ExpressionLeaf leftLeaf = (ExpressionLeaf) left;
        String leftType = leftLeaf.getType();
        if (ExpressionItem.STRING.equalsIgnoreCase(leftType) || "C_STRING".equalsIgnoreCase(leftType))
        {
            Object value = leftLeaf.getItem();
            if (value instanceof CString)
            {
                leftRule = ((CString) value).getPattern();
            }
            else
            {
                leftRule = String.valueOf(leftLeaf.getItem());
            }
        }
        else
        {
            throw new GenerateException(String.format(
                    "Expected attribute expression (x matches y) for archetype slot expression \"%s\", " +
                            "but left hand side is of type %s, not string",
                    expression.toString(),
                    leftType));
        }

        ExpressionLeaf rightLeaf = (ExpressionLeaf) right;
        String rightType = rightLeaf.getType();
        if (ExpressionItem.STRING.equalsIgnoreCase(rightType) || "C_STRING".equalsIgnoreCase(rightType))
        {
            Object value = rightLeaf.getItem();
            if (value instanceof CString)
            {
                rightRule = ((CString) value).getPattern();
            }
            else
            {
                rightRule = String.valueOf(rightLeaf.getItem());
            }
        }
        else
        {
            throw new GenerateException(String.format(
                    "Expected attribute expression (x matches y) for archetype slot expression \"%s\", " +
                            "but right hand side is of type %s, not string",
                    expression.toString(),
                    rightType));
        }

        if (leftRule.startsWith("/"))
        {
            leftRule = leftRule.substring(1);
        }
        if (leftRule.startsWith("archetype_id"))
        {
            leftRule = leftRule.substring("archetype_id".length());
        }
        if (leftRule.startsWith("/"))
        {
            leftRule = leftRule.substring(1);
        }

        if ("rm_originator".equals(leftRule))
        {
            testValue = archetypeID.rmOriginator();
        }
        else if ("rm_name".equals(leftRule))
        {
            testValue = archetypeID.rmName();
        }
        else if ("rm_entity".equals(leftRule) || "rm_type".equals(leftRule))
        {
            testValue = archetypeID.rmEntity();
        }
        else if ("concept_name".equals(leftRule))
        {
            testValue = archetypeID.conceptName();
        }
        else if ("domain_concept".equals(leftRule))
        {
            testValue = archetypeID.domainConcept();
        }
        else if ("value".equals(leftRule) || "".equals(leftRule))
        {
            testValue = archetypeID.getValue();
        }
        else
        {
            throw new GenerateException(String.format(
                    "Expected attribute expression (x matches y) for archetype slot expression \"%s\", " +
                            "but do not recognize left hand side \"%s\"",
                    expression.toString(),
                    leftLeaf.getItem()));
        }

        if (op.equals(OperatorKind.OP_EQ))
        {
            if (rightRule.equals(testValue))
            {
                match = true;
            }
        }
        else if (op.equals(OperatorKind.OP_MATCHES))
        {
            if (testValue.matches(rightRule))
            {
                match = true;
            }
        }

        return match;
    }
}
