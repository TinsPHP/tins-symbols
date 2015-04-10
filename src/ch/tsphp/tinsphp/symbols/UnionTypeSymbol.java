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
    private static enum ETypeRelation
    {
        NO_RELATION,
        PARENT_TYPE,
        SUB_TYPE
    }

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
    public boolean addTypeSymbol(ITypeSymbol typeSymbol) {
        if (isReadyForEval()) {
            throw new IllegalStateException("Cannot add a type symbol to a closed union.\n"
                    + "Tried to add: " + typeSymbol.toString());
        }
        if (typeSymbol instanceof IUnionTypeSymbol) {
            return merge((IUnionTypeSymbol) typeSymbol);
        }
        return addAndSimplify(typeSymbol.getAbsoluteName(), typeSymbol);
    }

    private boolean addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol) {
        boolean changedUnion = false;

        //no need to add it if it already exist in the union; ergo simplification = do not insert
        if (!typeSymbols.containsKey(absoluteName)) {

            ETypeRelation status = ETypeRelation.NO_RELATION;
            Iterator<Map.Entry<String, ITypeSymbol>> iterator = typeSymbols.entrySet().iterator();
            while (iterator.hasNext()) {
                ITypeSymbol existingTypeInUnion = iterator.next().getValue();
                if ((status == ETypeRelation.NO_RELATION || status == ETypeRelation.PARENT_TYPE)
                        && overloadResolver.isFirstSameOrSubTypeOfSecond(existingTypeInUnion, newTypeSymbol)) {
                    //remove sub-type, it does no longer add information to the union type
                    status = ETypeRelation.PARENT_TYPE;
                    iterator.remove();
                } else if (status == ETypeRelation.NO_RELATION
                        && overloadResolver.isFirstSameOrParentTypeOfSecond(existingTypeInUnion, newTypeSymbol)) {
                    //new type is a sub type of an existing and hence it does not add new information to the union
                    status = ETypeRelation.SUB_TYPE;
                    break;
                }
            }

            if (status == ETypeRelation.NO_RELATION || status == ETypeRelation.PARENT_TYPE) {
                changedUnion = true;
                typeSymbols.put(absoluteName, newTypeSymbol);
            }
        }

        return changedUnion;
    }

    @Override
    public boolean merge(IUnionTypeSymbol unionTypeSymbol) {
        boolean changedUnion = false;

        if (isReadyForEval()) {
            throw new IllegalStateException("Cannot add a type symbol to a closed union.\n"
                    + "Tried to add: " + unionTypeSymbol.toString());
        }
        for (Map.Entry<String, ITypeSymbol> entry : unionTypeSymbol.getTypeSymbols().entrySet()) {
            boolean hasUnionChanged = addAndSimplify(entry.getKey(), entry.getValue());
            changedUnion = changedUnion || hasUnionChanged;
        }

        return changedUnion;
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
        return isReadyForEval() && typeSymbols.containsKey(PrimitiveTypeNames.FALSE);
    }

    @Override
    public boolean isNullable() {
        return isReadyForEval() && typeSymbols.containsKey(PrimitiveTypeNames.NULL);
    }

    @Override
    public String getName() {
        return getAbsoluteName();
    }

    @Override
    public String getAbsoluteName() {
        if (isReadyForEval()) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> iterator = typeSymbols.keySet().iterator();
            if (iterator.hasNext()) {
                sb.append(iterator.next());
            }
            while (iterator.hasNext()) {
                sb.append(" | ").append(iterator.next());
            }
            if (typeSymbols.size() != 1) {
                sb.insert(0, "{");
                sb.append("}");
            }
            return sb.toString();
        }
        return super.getAbsoluteName();
    }
}
