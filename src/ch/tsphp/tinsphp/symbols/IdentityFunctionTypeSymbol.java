/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

import java.util.Arrays;
import java.util.List;

public class IdentityFunctionTypeSymbol extends AFunctionTypeSymbol
{
    private final ISymbolFactory symbolFactory;

    public IdentityFunctionTypeSymbol(
            String theName,
            String parameterId,
            ITypeSymbol theParentTypeSymbol,
            ISymbolFactory theSymbolFactory) {
        super(theName, Arrays.asList(parameterId), theParentTypeSymbol);
        symbolFactory = theSymbolFactory;
    }

    @Override
    public ITypeSymbol apply(List<ITypeVariableSymbol> arguments) {
        IUnionTypeSymbol unionTypeSymbol = symbolFactory.createUnionTypeSymbol();
        unionTypeSymbol.merge(arguments.get(0).getType());
        unionTypeSymbol.seal();
        return unionTypeSymbol;
    }
}
