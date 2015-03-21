/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.symbols.ConstantFunctionTypeSymbol;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ConstantFunctionTypeSymbolTest
{

    @Test(expected = UnsupportedOperationException.class)
    public void getTypeVariables_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        symbol.getTypeVariables();

        //assert in annotation
    }

    @Test
    public void getCachedApply_Standard_ReturnsOnePassedByConstructor() {
        ITypeSymbol returnType = mock(ITypeSymbol.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol("$x", null, null, returnType);
        ITypeSymbol result = symbol.getCachedApply(null);

        assertThat(result, is(returnType));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cacheApply_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol();
        symbol.cacheApply(null, null);

        //assert in annotation
    }

    private IFunctionTypeSymbol createFunctionTypeSymbol() {
        return createFunctionTypeSymbol(
                "foo", new ArrayList<String>(), mock(ITypeSymbol.class), mock(ITypeSymbol.class));
    }

    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<String> parameterIds,
            ITypeSymbol parentTypeSymbol,
            ITypeSymbol returnTypeSymbol) {
        return new ConstantFunctionTypeSymbol(name, parameterIds, parentTypeSymbol, returnTypeSymbol);
    }
}