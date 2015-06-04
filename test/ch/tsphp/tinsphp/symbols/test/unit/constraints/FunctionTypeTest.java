/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.symbols.constraints.FunctionType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;

public class FunctionTypeTest
{

    @Test
    public void getName_Standard_ReturnsTheOnePassedByConstructor() {
        String name = "foo";
        IOverloadBindings bindings = mock(IOverloadBindings.class);
        ArrayList<IVariable> parameters = new ArrayList<>();

        IFunctionType function = createFunction(name, bindings, parameters);
        String result = function.getName();

        assertThat(result, is(name));
    }

    @Test
    public void getOverloadBindings_Standard_ReturnsTheOnePassedByConstructor() {
        String name = "foo";
        IOverloadBindings bindings = mock(IOverloadBindings.class);
        ArrayList<IVariable> parameters = new ArrayList<>();

        IFunctionType function = createFunction(name, bindings, parameters);
        IOverloadBindings result = function.getOverloadBindings();

        assertThat(result, is(bindings));
    }

    @Test
    public void getParameters_Standard_ReturnsTheOnePassedByConstructor() {
        String name = "foo";
        IOverloadBindings bindings = mock(IOverloadBindings.class);
        List<IVariable> parameters = new ArrayList<>();

        IFunctionType function = createFunction(name, bindings, parameters);
        List<IVariable> result = function.getParameters();

        assertThat(result, is(parameters));
    }

    @Test
    public void getNumbersOfNonOptionalParameters_NoParams_Returns0() {
        String name = "foo";
        IOverloadBindings bindings = mock(IOverloadBindings.class);
        List<IVariable> parameters = new ArrayList<>();

        IFunctionType function = createFunction(name, bindings, parameters);
        int result = function.getNumberOfNonOptionalParameters();

        assertThat(result, is(0));
    }

    @Test
    public void getNumbersOfNonOptionalParameters_TwoParams_Returns2() {
        String name = "foo";
        IOverloadBindings bindings = mock(IOverloadBindings.class);
        List<IVariable> parameters = asList(mock(IVariable.class), mock(IVariable.class));

        IFunctionType function = createFunction(name, bindings, parameters);
        int result = function.getNumberOfNonOptionalParameters();

        assertThat(result, is(2));
    }

    @Test
    public void getSuffix_NoDefined_ReturnsNull() {
        //no arrange necessary

        IFunctionType function = createFunction();
        String result = function.getSuffix("translatorId");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getSuffix_OneDefined_ReturnsNull() {
        //pre-act necessary for arrange
        IFunctionType function = createFunction();

        //arrange
        function.addSuffix("translatorId", "_");

        //act
        String result = function.getSuffix("translatorId");

        assertThat(result, is("_"));
    }

    @Test
    public void getSuffix_UnknownTranslatorId_ReturnsNull() {
        //pre-act necessary for arrange
        IFunctionType function = createFunction();

        //arrange
        function.addSuffix("translatorId", "_");

        //act
        String result = function.getSuffix("unknownTranslatorId");

        assertThat(result, is(nullValue()));
    }

    @Test(expected = IllegalStateException.class)
    public void getSignature_NotYetFixed_ThrowsIllegalStateException() {
        //no arrange necessary

        IFunctionType function = createFunction();
        function.getSignature();

        //assert in annotation
    }

    private IFunctionType createFunction() {
        return createFunction("foo", mock(IOverloadBindings.class), new ArrayList<IVariable>());
    }

    protected IFunctionType createFunction(
            String theName, IOverloadBindings theOverloadBindings, List<IVariable> theParameterVariables) {
        return new FunctionType(theName, theOverloadBindings, theParameterVariables);
    }
}
