/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IObservableTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class IntersectionTypeSymbol extends AContainerTypeSymbol implements IIntersectionTypeSymbol
{

    public IntersectionTypeSymbol(ITypeHelper theTypeHelper) {
        super(theTypeHelper);
    }

    public IntersectionTypeSymbol(
            ITypeHelper typeHelper,
            IntersectionTypeSymbol intersectionTypeSymbol,
            Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        super(typeHelper, intersectionTypeSymbol, parametricTypeSymbols);
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
        } else if (typeSymbol instanceof IContainerTypeSymbol) {
            IContainerTypeSymbol containerTypeSymbol = (IContainerTypeSymbol) typeSymbol;
            Map<String, ITypeSymbol> otherTypeSymbols = containerTypeSymbol.getTypeSymbols();
            if (otherTypeSymbols.size() == 1) {
                //type in container could be a container as well - hence call not the parent method but this one
                hasChanged = addTypeSymbol(otherTypeSymbols.values().iterator().next());
            } else {
                hasChanged = super.addTypeSymbol(typeSymbol);
                if (hasChanged) {
                    if (!containerTypeSymbol.isFixed()) {
                        ++nonFixedTypesCount;
                    }
                    containerTypeSymbol.registerObservableListener(this);
                }
            }
        } else {
            hasChanged = super.addTypeSymbol(typeSymbol);
            if (hasChanged) {
                if (typeSymbol instanceof IPolymorphicTypeSymbol && !((IPolymorphicTypeSymbol) typeSymbol).isFixed()) {
                    ++nonFixedTypesCount;
                }
                if (typeSymbol instanceof IObservableTypeSymbol) {
                    ((IObservableTypeSymbol) typeSymbol).registerObservableListener(this);
                }
            }
        }
        return hasChanged;
    }

    @Override
    public IIntersectionTypeSymbol copy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        return new IntersectionTypeSymbol(typeHelper, this, parametricTypeSymbols);
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
            if (typeHelper.isFirstSameOrSubTypeOfSecond(newTypeSymbol, existingTypeInUnion, false)) {
                //remove parent type, it does no longer add information to the intersection type
                status = ETypeRelation.SUBTYPE;
                unregisterAndDecreaseNonFixedCounter(existingTypeInUnion);
                iterator.remove();
            } else if (status == ETypeRelation.NO_RELATION
                    && typeHelper.isFirstSameOrParentTypeOfSecond(newTypeSymbol, existingTypeInUnion, false)) {
                //new type is a parent type of an existing and hence it does not add new information to the union
                status = ETypeRelation.PARENT_TYPE;
                break;
            }
        }

        if (status != ETypeRelation.PARENT_TYPE) {
            changedUnion = true;
            typeSymbols.put(absoluteName, newTypeSymbol);
        }

        //Warning! end code duplication - almost the same as in UnionTypeSymbol

        return changedUnion;
    }
}
