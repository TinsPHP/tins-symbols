/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.symbols.ArrayTypeSymbol;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ArrayTypeSymbolTest
{

    @Test
    public void getKeyTypeSymbol_Standard_TypeSymbolIsPassedByConstructor() {
        ITypeSymbol keyTypeSymbol = mock(ITypeSymbol.class);

        IArrayTypeSymbol symbol = createArrayTypeSymbolOnlyKeyType(keyTypeSymbol);
        ITypeSymbol result = symbol.getKeyTypeSymbol();

        assertThat(result, is(keyTypeSymbol));
    }

    @Test
    public void getValueTypeSymbol_Standard_TypeSymbolIsPassedByConstructor() {
        ITypeSymbol valueTypeSymbol = mock(ITypeSymbol.class);

        IArrayTypeSymbol symbol = createArrayTypeSymbolOnlyValueType(valueTypeSymbol);
        ITypeSymbol result = symbol.getValueTypeSymbol();

        assertThat(result, is(valueTypeSymbol));
    }

    private IArrayTypeSymbol createArrayTypeSymbolOnlyKeyType(ITypeSymbol keyTypeSymbol) {
        return createArrayTypeSymbol(
                "foo",
                keyTypeSymbol,
                mock(ITypeSymbol.class),
                mock(ITypeSymbol.class));
    }

    private IArrayTypeSymbol createArrayTypeSymbolOnlyValueType(ITypeSymbol valueTypeSymbol) {
        return createArrayTypeSymbol(
                "foo",
                mock(ITypeSymbol.class),
                valueTypeSymbol,
                mock(ITypeSymbol.class));
    }

    protected IArrayTypeSymbol createArrayTypeSymbol(
            String name,
            ITypeSymbol keyTypeSymbol,
            ITypeSymbol valueTypeSymbol,
            ITypeSymbol parentTypeSymbol) {
        return new ArrayTypeSymbol(name, keyTypeSymbol, valueTypeSymbol, parentTypeSymbol);
    }
}
