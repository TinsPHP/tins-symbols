/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

import java.util.List;

public class AssignFunctionTypeSymbol extends AFunctionTypeSymbol
{
    private final ITypeSymbol returnTypeSymbol;

    public AssignFunctionTypeSymbol(
            String theName,
            List<String> parameterId,
            ITypeSymbol theParentTypeSymbol,
            ITypeSymbol theReturnTypeSymbol) {
        super(theName, parameterId, theParentTypeSymbol);
        returnTypeSymbol = theReturnTypeSymbol;
    }

    @Override
    public ITypeSymbol apply(List<ITypeVariableSymbol> arguments) {
        arguments.get(0).setType(returnTypeSymbol);
        return returnTypeSymbol;
    }
}
