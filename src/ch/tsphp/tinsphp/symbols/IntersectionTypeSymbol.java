/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;

import java.util.Iterator;
import java.util.Map;

public class IntersectionTypeSymbol extends AContainerTypeSymbol<IIntersectionTypeSymbol>
        implements IIntersectionTypeSymbol
{

    public IntersectionTypeSymbol(IOverloadResolver theOverloadResolver) {
        super(theOverloadResolver);
    }

    @Override
    public String getTypeSeparator() {
        return " & ";
    }

    @Override
    public String getDefaultName() {
        return PrimitiveTypeNames.MIXED;
    }

    //Warning! start code duplication - almost the same as in UnionTypeSymbol
    @Override
    public boolean addTypeSymbol(ITypeSymbol typeSymbol) {
        boolean hasChanged;
        if (typeSymbol instanceof IIntersectionTypeSymbol) {
            hasChanged = merge((IIntersectionTypeSymbol) typeSymbol);
        } else {
            hasChanged = super.addTypeSymbol(typeSymbol);
        }
        return hasChanged;
    }

    @Override
    public IIntersectionTypeSymbol copy() {
        IntersectionTypeSymbol copy = new IntersectionTypeSymbol(overloadResolver);
        copy.typeSymbols.putAll(typeSymbols);
        return copy;
    }
    //Warning! end code duplication - almost the same as in UnionTypeSymbol

    @Override
    protected boolean addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol) {
        boolean changedUnion = false;

        //Warning! start code duplication - almost the same as in UnionTypeSymbol

        ETypeRelation status = ETypeRelation.NO_RELATION;
        Iterator<Map.Entry<String, ITypeSymbol>> iterator = typeSymbols.entrySet().iterator();
        while (iterator.hasNext()) {
            ITypeSymbol existingTypeInUnion = iterator.next().getValue();
            if ((status == ETypeRelation.NO_RELATION || status == ETypeRelation.SUB_TYPE)
                    && overloadResolver.isFirstSameOrParentTypeOfSecond(existingTypeInUnion, newTypeSymbol)) {
                //remove parent type, it does no longer add information to the intersection type
                status = ETypeRelation.SUB_TYPE;
                iterator.remove();
            } else if (status == ETypeRelation.NO_RELATION
                    && overloadResolver.isFirstSameOrSubTypeOfSecond(existingTypeInUnion, newTypeSymbol)) {
                //new type is a parent type of an existing and hence it does not add new information to the union
                status = ETypeRelation.PARENT_TYPE;
                break;
            }
        }

        if (status == ETypeRelation.NO_RELATION || status == ETypeRelation.SUB_TYPE) {
            changedUnion = true;
            typeSymbols.put(absoluteName, newTypeSymbol);
        }

        //Warning! end code duplication - almost the same as in UnionTypeSymbol

        return changedUnion;
    }
}
