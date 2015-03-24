/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.symbols.AFunctionTypeSymbol;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class AFunctionTypeSymbolTest
{

    private class DummyFunctionTypeSymbol extends AFunctionTypeSymbol
    {

        public DummyFunctionTypeSymbol(String theName, List<String> theParameterIds, ITypeSymbol theParentTypeSymbol) {
            super(theName, theParameterIds, theParentTypeSymbol);
        }

        @Override
        public ITypeSymbol apply(List<ITypeVariableSymbol> arguments) {
            return null;
        }
    }

    @Test
    public void getParametersConstraints_NothingDefined_ReturnsEmptyList() {
        //no arrange necessary

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        List<List<IConstraint>> result = symbol.getInputConstraints();

        assertThat(result.size(), is(0));
    }

    @Test
    public void addAndGetParametersConstraints_OneDefined_ReturnsListWithIt() {
        IConstraint constraint = mock(IConstraint.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol("$x");
        symbol.addInputConstraint("$x", constraint);
        List<List<IConstraint>> result = symbol.getInputConstraints();

        assertThat(result.size(), is(1));
        assertThat(result.get(0).size(), is(1));
        assertThat(result.get(0).get(0), is(constraint));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addParametersConstraints_NonExistingParameter_ThrowsIllegalArgumentException() {
        IConstraint constraint = mock(IConstraint.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        symbol.addInputConstraint("$nonExistingParameter", constraint);
        symbol.getInputConstraints();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefaultValue_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        symbol.getDefaultValue();

        //assert in annotation
    }

    private IFunctionTypeSymbol createFunctionTypeSymbol() {
        return createFunctionTypeSymbol(
                "foo", new ArrayList<String>(), mock(ITypeSymbol.class));
    }

    private IFunctionTypeSymbol createFunctionTypeSymbol(String... parameterIds) {
        return createFunctionTypeSymbol(
                "foo", Arrays.asList(parameterIds), mock(ITypeSymbol.class));
    }

    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<String> parameterIds,
            ITypeSymbol parentTypeSymbol) {
        return new DummyFunctionTypeSymbol(name, parameterIds, parentTypeSymbol);
    }
}
