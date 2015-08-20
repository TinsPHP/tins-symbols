/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.ERelation;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.TypeHelperDto;

import java.util.Collection;
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
            }
        } else {
            hasChanged = super.addTypeSymbol(typeSymbol);
        }
        return hasChanged;
    }

    @Override
    public IIntersectionTypeSymbol copy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        return new IntersectionTypeSymbol(typeHelper, this, parametricTypeSymbols);
    }
    //Warning! end code duplication - almost the same as in UnionTypeSymbol


    @Override
    protected boolean firstTypeReplacesSecond(ITypeSymbol newTypeSymbol, ITypeSymbol existingTypeSymbol) {
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(newTypeSymbol, existingTypeSymbol, false);
        boolean replacesIt = result.relation == ERelation.HAS_RELATION;
        if (replacesIt
                && existingTypeSymbol instanceof IConvertibleTypeSymbol
                && newTypeSymbol instanceof IConvertibleTypeSymbol) {
            IConvertibleTypeSymbol oldConvertibleType = (IConvertibleTypeSymbol) existingTypeSymbol;
            IConvertibleTypeSymbol newConvertibleType = (IConvertibleTypeSymbol) newTypeSymbol;
            if (!oldConvertibleType.isFixed() && newConvertibleType.isFixed()) {
                replacesIt = !typeHelper.areSame(
                        oldConvertibleType.getUpperTypeBounds(), newConvertibleType.getUpperTypeBounds());
            }
        }
        return replacesIt;
    }

    @Override
    protected boolean secondTypeReplacesFirst(ITypeSymbol newTypeSymbol, ITypeSymbol existingTypeSymbol) {
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(newTypeSymbol, existingTypeSymbol, false);
        return result.relation == ERelation.HAS_RELATION;
    }

    @Override
    public boolean isFinal() {
        switch (typeSymbols.size()) {
            case 0:
                //empty intersection = mixed, mixed has subtypes
                return false;
            case 1:
                return typeSymbols.values().iterator().next().isFinal();
            default:
                //an intersection with multiple types must have subtypes we do not create one if its types are not
                // none-final
                return false;
        }
    }
}
