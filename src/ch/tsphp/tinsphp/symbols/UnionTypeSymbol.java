/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.ERelation;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.TypeHelperDto;

import java.util.Collection;
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
                hasChanged = super.addTypeSymbol(typeSymbol);
            }
        } else {
            hasChanged = super.addTypeSymbol(typeSymbol);
        }
        return hasChanged;
    }

    @Override
    public IUnionTypeSymbol copy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        return new UnionTypeSymbol(typeHelper, this, parametricTypeSymbols);
    }
    //Warning! end code duplication - almost the same as in IntersectionTypeSymbol

    @Override
    protected boolean firstTypeReplacesSecond(ITypeSymbol newTypeSymbol, ITypeSymbol existingTypeSymbol) {
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(newTypeSymbol, existingTypeSymbol, false);
        return result.relation == ERelation.HAS_RELATION;
    }

    @Override
    protected boolean secondTypeReplacesFirst(ITypeSymbol newTypeSymbol, ITypeSymbol existingTypeSymbol) {
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(newTypeSymbol, existingTypeSymbol, false);
        return result.relation == ERelation.HAS_RELATION;
    }

    @Override
    public boolean isFinal() {
        switch (typeSymbols.size()) {
            case 0:
                //empty union = nothing => is always fixed
                return true;
            case 1:
                return typeSymbols.values().iterator().next().isFinal();
            default:
                //there is always a subtype of a union with multiple types
                return false;
        }
    }
}
