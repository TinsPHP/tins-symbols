/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.symbols.PolymorphicFunctionTypeSymbol;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PolymorphicFunctionTypeSymbolTest
{

    @Test
    public void getTypeVariables_Standard_ReturnsOnePassedToConstructor() {
        Map<String, ITypeVariableSymbol> typeVariables = new HashMap<>();

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol("foo", asList("$a"), null, typeVariables);
        Map<String, ITypeVariableSymbol> result = symbol.getTypeVariables();

        assertThat(result, is(typeVariables));
    }

    @Test
    public void getCachedApply_NothingCached_ReturnsNull() {
        //no arrange necessary

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        ITypeSymbol result = symbol.getCachedApply(new ArrayList<IUnionTypeSymbol>());

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getCachedApply_IntResultsInFloat_ReturnsFloat() {
        IUnionTypeSymbol intTypeSymbol = mock(IUnionTypeSymbol.class);
        when(intTypeSymbol.getAbsoluteName()).thenReturn("int");
        IUnionTypeSymbol floatTypeSymbol = mock(IUnionTypeSymbol.class);
        when(floatTypeSymbol.getAbsoluteName()).thenReturn("float");

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        symbol.cacheApply(asList(intTypeSymbol), floatTypeSymbol);
        ITypeSymbol result = symbol.getCachedApply(asList(intTypeSymbol));

        assertThat(result, is((ITypeSymbol) floatTypeSymbol));
    }

    @Test
    public void getCachedApply_IntResultsInFloatAndFloatGiven_ReturnsNull() {
        IUnionTypeSymbol intTypeSymbol = mock(IUnionTypeSymbol.class);
        when(intTypeSymbol.getAbsoluteName()).thenReturn("int");
        IUnionTypeSymbol floatTypeSymbol = mock(IUnionTypeSymbol.class);
        when(floatTypeSymbol.getAbsoluteName()).thenReturn("float");

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        symbol.cacheApply(asList(intTypeSymbol), floatTypeSymbol);
        ITypeSymbol result = symbol.getCachedApply(asList(floatTypeSymbol));

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getCachedApply_IntAndFloatResultsInFloatAndFloatAndIntGiven_ReturnsFloat() {
        IUnionTypeSymbol intTypeSymbol = mock(IUnionTypeSymbol.class);
        when(intTypeSymbol.getAbsoluteName()).thenReturn("int");
        IUnionTypeSymbol floatTypeSymbol = mock(IUnionTypeSymbol.class);
        when(floatTypeSymbol.getAbsoluteName()).thenReturn("float");

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        symbol.cacheApply(asList(intTypeSymbol, floatTypeSymbol), floatTypeSymbol);
        ITypeSymbol result = symbol.getCachedApply(asList(floatTypeSymbol, intTypeSymbol));

        assertThat(result, is(nullValue()));
    }

    private IFunctionTypeSymbol createFunctionTypeSymbol() {
        return createFunctionTypeSymbol(
                "foo", new ArrayList<String>(), mock(ITypeSymbol.class), new HashMap<String, ITypeVariableSymbol>());
    }

    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<String> parameterIds,
            ITypeSymbol parentTypeSymbol,
            Map<String, ITypeVariableSymbol> typeVariables) {
        return new PolymorphicFunctionTypeSymbol(name, parameterIds, parentTypeSymbol, typeVariables);
    }
}
