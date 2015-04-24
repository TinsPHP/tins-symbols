/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.common.ITSPHPAst;
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

public class ConstraintTest
{
    @Test
    public void getOperator_Standard_IsOnePassedByConstructor() {
        ITSPHPAst operator = mock(ITSPHPAst.class);

        IConstraint intersectionConstraint = createIntersectionConstraint(
                operator, mock(IVariable.class), new ArrayList<IVariable>(), mock(IMethodSymbol.class));
        ITSPHPAst result = intersectionConstraint.getOperator();

        assertThat(result, is(operator));
    }

    @Test
    public void getLeftHandSide_Standard_IsOnePassedByConstructor() {
        IVariable leftHandSide = mock(IVariable.class);

        IConstraint intersectionConstraint = createIntersectionConstraint(
                mock(ITSPHPAst.class), leftHandSide, new ArrayList<IVariable>(), mock(IMinimalMethodSymbol.class));
        IVariable result = intersectionConstraint.getLeftHandSide();

        assertThat(result, is(leftHandSide));
    }

    @Test
    public void getArguments_Standard_IsOnePassedByConstructor() {
        List<IVariable> arguments = new ArrayList<>();

        IConstraint intersectionConstraint = createIntersectionConstraint(
                mock(ITSPHPAst.class), mock(IVariable.class), arguments, mock(IMinimalMethodSymbol.class));
        List<IVariable> result = intersectionConstraint.getArguments();

        assertThat(result, is(arguments));
    }

    @Test
    public void getMethodSymbol_Standard_IsOnePassedByConstructor() {
        IMinimalMethodSymbol methodSymbol = mock(IMethodSymbol.class);

        IConstraint intersectionConstraint = createIntersectionConstraint(
                mock(ITSPHPAst.class), mock(IVariable.class), new ArrayList<IVariable>(), methodSymbol);
        IMinimalMethodSymbol result = intersectionConstraint.getMethodSymbol();

        assertThat(result, is(methodSymbol));
    }

    protected IConstraint createIntersectionConstraint(
            ITSPHPAst theOperator,
            IVariable theLeftHandSideVariable,
            List<IVariable> theVariables,
            IMinimalMethodSymbol theMethodSymbol) {
        return new Constraint(theOperator, theLeftHandSideVariable, theVariables, theMethodSymbol);
    }
}
