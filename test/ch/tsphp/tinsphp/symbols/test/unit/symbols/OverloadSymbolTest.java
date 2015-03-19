/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IOverloadSymbol;
import ch.tsphp.tinsphp.symbols.OverloadSymbol;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class OverloadSymbolTest
{

    @Test
    public void getOverloads_NothingDefined_ReturnsEmptyList() {
        //no arrange necessary

        IOverloadSymbol symbol = createOverloadSymbol("foo");
        List<IFunctionTypeSymbol> result = symbol.getOverloads();

        assertThat(result.size(), is(0));
    }

    @Test
    public void addAndGetOverloads_OneDefined_ReturnsListWithIt() {
        IFunctionTypeSymbol functionTypeSymbol = mock(IFunctionTypeSymbol.class);

        IOverloadSymbol symbol = createOverloadSymbol("foo");
        symbol.addOverload(functionTypeSymbol);
        List<IFunctionTypeSymbol> result = symbol.getOverloads();

        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(functionTypeSymbol));
    }

    protected IOverloadSymbol createOverloadSymbol(String name) {
        return new OverloadSymbol(name);
    }
}
