/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UnionTypeSymbol extends ALazyTypeSymbol implements IUnionTypeSymbol
{

    private final IOverloadResolver overloadResolver;
    private Map<String, ITypeSymbol> typeSymbols;

    public UnionTypeSymbol(IOverloadResolver theOverloadResolver) {
        this(theOverloadResolver, new HashMap<String, ITypeSymbol>());
    }

    public UnionTypeSymbol(IOverloadResolver theOverloadResolver, Map<String, ITypeSymbol> theTypeSymbols) {
        overloadResolver = theOverloadResolver;
        typeSymbols = theTypeSymbols;
    }

    @Override
    public Map<String, ITypeSymbol> getTypeSymbols() {
        return typeSymbols;
    }

    @Override
    public void addTypeSymbol(ITypeSymbol typeSymbol) {
        if (isReadyForEval()) {
            throw new RuntimeException("Cannot add a type symbol to a closed union");
        }
        if (typeSymbol instanceof IUnionTypeSymbol) {
            merge((IUnionTypeSymbol) typeSymbol);
        } else {
            addAndSimplify(typeSymbol.getAbsoluteName(), typeSymbol);
        }
    }

    private void addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol) {
        //no need to add it if it already exist in the union; ergo simplification = do not insert
        if (!typeSymbols.containsKey(absoluteName)) {
            boolean isNotSubTypeOfExisting = true;
            Iterator<Map.Entry<String, ITypeSymbol>> iterator = typeSymbols.entrySet().iterator();
            while (iterator.hasNext()) {
                ITypeSymbol existingTypeInUnion = iterator.next().getValue();
                if (overloadResolver.isFirstSameOrSubTypeOfSecond(existingTypeInUnion, newTypeSymbol)) {
                    //remove all sub-types, they do no longer add information to the union type
                    iterator.remove();
                } else if (isNotSubTypeOfExisting && overloadResolver.isFirstSameOrParentTypeOfSecond
                        (existingTypeInUnion, newTypeSymbol)) {
                    //new type
                    isNotSubTypeOfExisting = false;
                }
            }

            if (isNotSubTypeOfExisting) {
                typeSymbols.put(absoluteName, newTypeSymbol);
            }
        }
    }

    @Override
    public void merge(IUnionTypeSymbol unionTypeSymbol) {
        if (isReadyForEval()) {
            throw new RuntimeException("Cannot add a type symbol to a closed union");
        }
        for (Map.Entry<String, ITypeSymbol> entry : unionTypeSymbol.getTypeSymbols().entrySet()) {
            addAndSimplify(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void seal() {
        notifyForEvalReadyListeners();
    }

    @Override
    public ITypeSymbol evalSelf() {
        return isReadyForEval() ? this : null;
    }

    @Override
    public boolean isFalseable() {
        return typeSymbols.containsKey(PrimitiveTypeNames.FALSE);
    }

    @Override
    public boolean isNullable() {
        return typeSymbols.containsKey(PrimitiveTypeNames.NULL);
    }

}
