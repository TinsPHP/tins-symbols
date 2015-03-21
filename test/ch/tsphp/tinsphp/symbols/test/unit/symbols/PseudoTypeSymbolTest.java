/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.symbols.PseudoTypeSymbol;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class PseudoTypeSymbolTest
{


    @Test(expected = UnsupportedOperationException.class)
    public void getDefaultValue_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IPseudoTypeSymbol symbol = createPseudoTypeSymbol("foo", mock(ITypeSymbol.class));
        symbol.getDefaultValue();

        //assert in annotation
    }

    protected IPseudoTypeSymbol createPseudoTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        return new PseudoTypeSymbol(name, parentTypeSymbol);
    }
}
