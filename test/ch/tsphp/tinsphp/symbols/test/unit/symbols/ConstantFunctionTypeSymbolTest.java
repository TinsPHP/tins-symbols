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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ConstantFunctionTypeSymbolTest
{

    @Test
    public void apply_Standard_ReturnsOnePassedByConstructor() {
        ITypeSymbol returnType = mock(ITypeSymbol.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol("$x", null, null, returnType);
        ITypeSymbol result = symbol.apply(null);

        assertThat(result, is(returnType));
    }

    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<String> parameterIds,
            ITypeSymbol parentTypeSymbol,
            ITypeSymbol returnTypeSymbol) {
        return new ConstantFunctionTypeSymbol(name, parameterIds, parentTypeSymbol, returnTypeSymbol);
    }
}