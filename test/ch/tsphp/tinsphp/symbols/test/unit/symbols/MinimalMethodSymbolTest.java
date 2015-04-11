/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.symbols.MinimalMethodSymbol;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class MinimalMethodSymbolTest
{

    @Test
    public void getOverloads_NothingDefined_ReturnsEmptyList() {
        //no arrange necessary

        IMinimalMethodSymbol symbol = createMinimalMethodSymbol("foo");
        List<IFunctionType> result = symbol.getOverloads();

        assertThat(result.size(), is(0));
    }

    @Test
    public void addAndGetOverloads_OneDefined_ReturnsListWithIt() {
        IFunctionType functionTypeSymbol = mock(IFunctionType.class);

        IMinimalMethodSymbol symbol = createMinimalMethodSymbol("foo");
        symbol.addOverload(functionTypeSymbol);
        List<IFunctionType> result = symbol.getOverloads();

        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(functionTypeSymbol));
    }

    protected IMinimalMethodSymbol createMinimalMethodSymbol(String name) {
        return new MinimalMethodSymbol(name);
    }
}
