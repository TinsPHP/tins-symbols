/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IObservableTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class UnionTypeSymbol extends AContainerTypeSymbol implements IUnionTypeSymbol
{
    public UnionTypeSymbol(ITypeHelper theTypeHelper) {
        super(theTypeHelper);
    }

    public UnionTypeSymbol(
            ITypeHelper theTypeHelper,
            UnionTypeSymbol unionTypeSymbol,
            Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        super(theTypeHelper, unionTypeSymbol, parametricTypeSymbols);
    }

    @Override
    public String getTypeSeparator() {
        return " | ";
    }

    @Override
    public String getDefaultName() {
        return PrimitiveTypeNames.NOTHING;
    }


    //Warning! start code duplication - almost the same as in IntersectionTypeSymbol
    @Override
    public boolean addTypeSymbol(ITypeSymbol typeSymbol) {
        boolean hasChanged;
        if (typeSymbol instanceof IUnionTypeSymbol) {
            hasChanged = super.merge((IUnionTypeSymbol) typeSymbol);
        } else if (typeSymbol instanceof IContainerTypeSymbol) {
            IContainerTypeSymbol containerTypeSymbol = (IContainerTypeSymbol) typeSymbol;
            Map<String, ITypeSymbol> otherTypeSymbols = containerTypeSymbol.getTypeSymbols();
            if (otherTypeSymbols.size() == 1) {
                //type in container could be a container as well - hence call not the parent method but this one
                hasChanged = addTypeSymbol(otherTypeSymbols.values().iterator().next());
            } else {
                isFixed = isFixed && containerTypeSymbol.isFixed();
                containerTypeSymbol.register(this);
                hasChanged = super.addTypeSymbol(typeSymbol);
            }
        } else {
            if (isFixed && typeSymbol instanceof IPolymorphicTypeSymbol) {
                isFixed = ((IPolymorphicTypeSymbol) typeSymbol).isFixed();
            }
            if (typeSymbol instanceof IObservableTypeSymbol) {
                ((IObservableTypeSymbol) typeSymbol).register(this);
            }
            hasChanged = super.addTypeSymbol(typeSymbol);
        }
        return hasChanged;
    }

    @Override
    public IUnionTypeSymbol copy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        return new UnionTypeSymbol(typeHelper, this, parametricTypeSymbols);
    }
    //Warning! start code duplication - almost the same as in IntersectionTypeSymbol


    @Override
    protected boolean addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol) {
        boolean changedUnion = false;

        //Warning! start code duplication - almost the same as in IntersectionTypeSymbol

        ETypeRelation status = ETypeRelation.NO_RELATION;
        Iterator<Map.Entry<String, ITypeSymbol>> iterator = typeSymbols.entrySet().iterator();
        while (iterator.hasNext()) {
            ITypeSymbol existingTypeInUnion = iterator.next().getValue();
            if (typeHelper.isFirstSameOrParentTypeOfSecond(newTypeSymbol, existingTypeInUnion, false)) {
                //remove subtype, it does no longer add information to the union type
                status = ETypeRelation.PARENT_TYPE;
                iterator.remove();
            } else if (status == ETypeRelation.NO_RELATION
                    && typeHelper.isFirstSameOrSubTypeOfSecond(newTypeSymbol, existingTypeInUnion, false)) {
                //new type is a subtype of an existing and hence it does not add new information to the union
                status = ETypeRelation.SUBTYPE;
                break;
            }
        }

        if (status != ETypeRelation.SUBTYPE) {
            changedUnion = true;
            typeSymbols.put(absoluteName, newTypeSymbol);
        }

        //Warning! start code duplication - almost the same as in IntersectionTypeSymbol

        return changedUnion;
    }
}
