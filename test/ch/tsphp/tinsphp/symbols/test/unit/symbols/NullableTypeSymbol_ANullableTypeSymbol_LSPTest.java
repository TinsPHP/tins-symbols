/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ANullableTypeSymbol;
import ch.tsphp.tinsphp.symbols.NullTypeSymbol;

import java.util.Set;

public class NullableTypeSymbol_ANullableTypeSymbol_LSPTest extends ANullableTypeSymbolTest
{
    @Override
    protected ANullableTypeSymbol createNullableTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        return new NullTypeSymbol();
    }

    @Override
    protected ANullableTypeSymbol createNullableTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbol) {
        return new NullTypeSymbol();
    }
}
