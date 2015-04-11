/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IIntersectionConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.symbols.constraints.IntersectionConstraint;
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

        IIntersectionConstraint intersectionConstraint = createIntersectionConstraint(
                leftHandSide, new ArrayList<IVariable>(), new ArrayList<IFunctionType>());
        IVariable result = intersectionConstraint.getLeftHandSide();

        assertThat(result, is(leftHandSide));
    }

    @Test
    public void getArguments_Standard_IsOnePassedByConstructor() {
        List<IVariable> arguments = new ArrayList<>();

        IIntersectionConstraint intersectionConstraint = createIntersectionConstraint(
                mock(IVariable.class), arguments, new ArrayList<IFunctionType>());
        List<IVariable> result = intersectionConstraint.getArguments();

        assertThat(result, is(arguments));
    }

    @Test
    public void getOverloads_Standard_IsOnePassedByConstructor() {
        List<IFunctionType> overloads = new ArrayList<>();

        IIntersectionConstraint intersectionConstraint = createIntersectionConstraint(
                mock(IVariable.class), new ArrayList<IVariable>(), overloads);
        List<IFunctionType> result = intersectionConstraint.getOverloads();

        assertThat(result, is(overloads));
    }

    protected IIntersectionConstraint createIntersectionConstraint(
            IVariable theLeftHandSideVariable,
            List<IVariable> theVariables,
            List<IFunctionType> theOverloads) {
        return new IntersectionConstraint(theLeftHandSideVariable, theVariables, theOverloads);
    }
}
