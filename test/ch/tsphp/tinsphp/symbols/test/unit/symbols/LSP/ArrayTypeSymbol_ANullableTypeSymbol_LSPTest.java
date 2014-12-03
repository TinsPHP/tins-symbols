/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ANullableTypeSymbol;
import ch.tsphp.tinsphp.symbols.ArrayTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ANullableTypeSymbolTest;

import java.util.Set;

public class ArrayTypeSymbol_ANullableTypeSymbol_LSPTest extends ANullableTypeSymbolTest
{

    @Override
    public void isNullable_NothingDefinedParents_ReturnsTrue() {
        //different behaviour - does not support multiple parents
    }

    @Override
    protected ANullableTypeSymbol createNullableTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        return new ArrayTypeSymbol(name, null, null, parentTypeSymbol);
    }

    @Override
    protected ANullableTypeSymbol createNullableTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbol) {
        throw new UnsupportedOperationException("ArrayTypeSymbol does not support multiple parent type symbols");
    }
}
