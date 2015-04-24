/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;

import java.util.List;

public class Constraint implements IConstraint
{
    private ITSPHPAst operator;
    private IVariable assignTypeVariable;
    private List<IVariable> arguments;
    private IMinimalMethodSymbol methodSymbol;

    public Constraint(
            ITSPHPAst theOperator,
            IVariable theLeftHandSideVariable,
            List<IVariable> theVariables,
            IMinimalMethodSymbol theMethodSymbol) {
        operator = theOperator;
        assignTypeVariable = theLeftHandSideVariable;
        arguments = theVariables;
        methodSymbol = theMethodSymbol;
    }

    @Override
    public ITSPHPAst getOperator() {
        return operator;
    }

    @Override
    public IVariable getLeftHandSide() {
        return assignTypeVariable;
    }

    @Override
    public List<IVariable> getArguments() {
        return arguments;
    }

    @Override
    public IMinimalMethodSymbol getMethodSymbol() {
        return methodSymbol;
    }
}
