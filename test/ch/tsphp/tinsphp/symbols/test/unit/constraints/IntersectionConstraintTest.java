/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.symbols.constraints.Constraint;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class IntersectionConstraintTest
{
    @Test
    public void getLeftHandSide_Standard_IsOnePassedByConstructor() {
        IVariable leftHandSide = mock(IVariable.class);

        IConstraint intersectionConstraint = createIntersectionConstraint(
                leftHandSide, new ArrayList<IVariable>(), mock(IMinimalMethodSymbol.class));
        IVariable result = intersectionConstraint.getLeftHandSide();

        assertThat(result, is(leftHandSide));
    }

    @Test
    public void getArguments_Standard_IsOnePassedByConstructor() {
        List<IVariable> arguments = new ArrayList<>();

        IConstraint intersectionConstraint = createIntersectionConstraint(
                mock(IVariable.class), arguments, mock(IMinimalMethodSymbol.class));
        List<IVariable> result = intersectionConstraint.getArguments();

        assertThat(result, is(arguments));
    }

    @Test
    public void getMethodSymbol_Standard_IsOnePassedByConstructor() {
        IMinimalMethodSymbol methodSymbol = mock(IMethodSymbol.class);

        IConstraint intersectionConstraint = createIntersectionConstraint(
                mock(IVariable.class), new ArrayList<IVariable>(), methodSymbol);
        IMinimalMethodSymbol result = intersectionConstraint.getMethodSymbol();

        assertThat(result, is(methodSymbol));
    }

    protected IConstraint createIntersectionConstraint(
            IVariable theLeftHandSideVariable,
            List<IVariable> theVariables,
            IMinimalMethodSymbol theMethodSymbol) {
        return new Constraint(theLeftHandSideVariable, theVariables, theMethodSymbol);
    }
}
