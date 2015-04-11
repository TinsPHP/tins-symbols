/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IIntersectionConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;

import java.util.List;

public class IntersectionConstraint implements IIntersectionConstraint
{
    private IVariable assignTypeVariable;
    private List<IVariable> arguments;
    private List<IFunctionType> overloads;

    public IntersectionConstraint(
            IVariable theLeftHandSideVariable,
            List<IVariable> theVariables,
            List<IFunctionType> theOverloads) {
        assignTypeVariable = theLeftHandSideVariable;
        arguments = theVariables;
        overloads = theOverloads;
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
    public List<IFunctionType> getOverloads() {
        return overloads;
    }
}
