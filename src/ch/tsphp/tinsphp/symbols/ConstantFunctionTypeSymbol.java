/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

import java.util.List;
import java.util.Map;

public class ConstantFunctionTypeSymbol extends AFunctionTypeSymbol implements IFunctionTypeSymbol
{
    private final ITypeSymbol returnTypeSymbol;

    public ConstantFunctionTypeSymbol(
            String theName,
            List<String> theParameterIds,
            ITypeSymbol theParentTypeSymbol,
            ITypeSymbol theReturnTypeSymbol) {

        super(theName, theParameterIds, theParentTypeSymbol);
        returnTypeSymbol = theReturnTypeSymbol;

    }

    @Override
    public Map<String, ITypeVariableSymbol> getTypeVariables() {
        throw new UnsupportedOperationException("constant functions do not have type variables");
    }

    @Override
    public ITypeSymbol getCachedApply(List<IUnionTypeSymbol> arguments) {
        return returnTypeSymbol;
    }

    @Override
    public void cacheApply(List<IUnionTypeSymbol> arguments, ITypeSymbol returnType) {
        throw new UnsupportedOperationException("constant functions do not cache a result type");
    }
}
