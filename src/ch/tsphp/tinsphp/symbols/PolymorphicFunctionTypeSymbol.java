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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PolymorphicFunctionTypeSymbol extends AFunctionTypeSymbol implements IFunctionTypeSymbol
{

    private final Map<String, ITypeVariableSymbol> typeVariables;
    private final Map<String, ITypeSymbol> cachedReturnTypes = new HashMap<>();

    public PolymorphicFunctionTypeSymbol(
            String theName,
            List<String> theParameterIds,
            ITypeSymbol theParentTypeSymbol, Map<String, ITypeVariableSymbol> theTypeVariables) {

        super(theName, theParameterIds, theParentTypeSymbol);
        typeVariables = theTypeVariables;
    }

    @Override
    public Map<String, ITypeVariableSymbol> getTypeVariables() {
        return typeVariables;
    }

    @Override
    public ITypeSymbol getCachedApply(List<IUnionTypeSymbol> arguments) {
        return cachedReturnTypes.get(getKey(arguments));
    }

    private String getKey(List<IUnionTypeSymbol> arguments) {
        StringBuilder sb = new StringBuilder();

        Iterator<IUnionTypeSymbol> iterator = arguments.iterator();
        if (iterator.hasNext()) {
            sb.append(iterator.next());
        }
        while (iterator.hasNext()) {
            sb.append(" x ").append(iterator.next());
        }
        return sb.toString();
    }

    @Override
    public void cacheApply(List<IUnionTypeSymbol> arguments, ITypeSymbol returnTypeSymbol) {
        cachedReturnTypes.put(getKey(arguments), returnTypeSymbol);
    }
}
