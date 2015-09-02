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

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.mock;

public class MinimalMethodSymbolTest
{

    @Test
    public void getOverloads_NothingDefined_ReturnsEmptyList() {
        //no arrange necessary

        IMinimalMethodSymbol symbol = createMinimalMethodSymbol("foo");
        Collection<IFunctionType> result = symbol.getOverloads();

        assertThat(result.size(), is(0));
    }

    @Test
    public void setAndGetOverloads_OneDefined_ReturnsListWithIt() {
        IFunctionType functionTypeSymbol = mock(IFunctionType.class);

        IMinimalMethodSymbol symbol = createMinimalMethodSymbol("foo");
        symbol.setOverloads(asList(functionTypeSymbol));
        Collection<IFunctionType> result = symbol.getOverloads();

        assertThat(result, contains(functionTypeSymbol));
    }

    protected IMinimalMethodSymbol createMinimalMethodSymbol(String name) {
        return new MinimalMethodSymbol(name);
    }
}
