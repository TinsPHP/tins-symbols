/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.symbols.ConstantFunctionTypeSymbol;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;

public class ConstantFunctionTypeSymbolTest
{

    @Test
    public void getParametersConstraints_NothingDefined_ReturnsEmptyList() {
        //no arrange necessary

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        List<List<IConstraint>> result = symbol.getParametersConstraints();

        assertThat(result.size(), is(0));
    }

    @Test
    public void addAndGetParametersConstraints_OneDefined_ReturnsListWithIt() {
        IConstraint constraint = mock(IConstraint.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol("$x");
        symbol.addParameterConstraint("$x", constraint);
        List<List<IConstraint>> result = symbol.getParametersConstraints();

        assertThat(result.size(), is(1));
        assertThat(result.get(0).size(), is(1));
        assertThat(result.get(0).get(0), is(constraint));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addParametersConstraints_NonExistingParameter_ThrowsIllegalArgumentException() {
        IConstraint constraint = mock(IConstraint.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        symbol.addParameterConstraint("$nonExistingParameter", constraint);
        symbol.getParametersConstraints();

        //assert in annotation
    }

    @Test
    public void getFunctionConstraints_Standard_ReturnsNull() {
        //no arrange necessary

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        Map<String, List<IConstraint>> result = symbol.getFunctionConstraints();

        assertThat(result, is(nullValue()));
    }

    @Test
    public void apply_Standard_ReturnsOnePassedByConstructor() {
        ITypeSymbol returnType = mock(ITypeSymbol.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol("$x", null, returnType, null);
        ITypeSymbol result = symbol.apply(null);

        assertThat(result, is(returnType));
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
                "foo", new ArrayList<String>(), mock(ITypeSymbol.class), mock(ITypeSymbol.class));
    }

    private IFunctionTypeSymbol createFunctionTypeSymbol(String... parameterIds) {
        return createFunctionTypeSymbol(
                "foo", Arrays.asList(parameterIds), mock(ITypeSymbol.class), mock(ITypeSymbol.class));
    }

    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<String> parameterIds,
            ITypeSymbol returnTypeSymbol,
            ITypeSymbol parentTypeSymbol) {
        return new ConstantFunctionTypeSymbol(name, parameterIds, returnTypeSymbol, parentTypeSymbol);
    }
}
