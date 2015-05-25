/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;

import java.util.Collection;
import java.util.Set;

public class TypeHelper implements ITypeHelper
{
    private ITypeSymbol mixedTypeSymbol;

    @Override
    public void setMixedTypeSymbol(ITypeSymbol typeSymbol) {
        mixedTypeSymbol = typeSymbol;
    }

    @Override
    public boolean areSame(ITypeSymbol firstType, ITypeSymbol secondType) {
        return firstType == secondType || firstType.getAbsoluteName().equals(secondType.getAbsoluteName());
    }

    @Override
    public boolean isFirstSameOrSubTypeOfSecond(ITypeSymbol potentialSubType, ITypeSymbol typeSymbol) {
        return areSame(potentialSubType, typeSymbol) || hasUpRelationFromTo(potentialSubType, typeSymbol);
    }

    @Override
    public boolean isFirstSameOrParentTypeOfSecond(ITypeSymbol potentialParentType, ITypeSymbol typeSymbol) {
        return areSame(potentialParentType, typeSymbol) || hasUpRelationFromTo(typeSymbol, potentialParentType);
    }

    private boolean hasUpRelationFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        if (actualParameterType instanceof IUnionTypeSymbol) {
            return hasUpRelationActualIsUnion((IUnionTypeSymbol) actualParameterType, formalParameterType);
        } else if (actualParameterType instanceof IIntersectionTypeSymbol) {
            return hasUpRelationActualIsIntersection(
                    (IIntersectionTypeSymbol) actualParameterType, formalParameterType);
        }
        return hasUpRelationActualIsNotUnionNorIntersection(actualParameterType, formalParameterType);
    }

    private boolean hasUpRelationActualIsNotUnionNorIntersection(
            ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        if (formalParameterType instanceof IUnionTypeSymbol) {
            return hasUpRelationFormalIsUnion(actualParameterType, (IUnionTypeSymbol) formalParameterType);
        } else if (formalParameterType instanceof IIntersectionTypeSymbol) {
            return hasUpRelationFormalIsIntersection(
                    actualParameterType, (IIntersectionTypeSymbol) formalParameterType);
        }
        return hasNominalUpRelation(actualParameterType, formalParameterType);
    }

    private boolean hasUpRelationActualIsUnion(IUnionTypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        Collection<ITypeSymbol> typeSymbols = actualParameterType.getTypeSymbols().values();

        if (!typeSymbols.isEmpty()) {
            return allAreSameOrSubtypes(typeSymbols, formalParameterType);
        }
        // an empty union is the bottom type of all types and hence is at least the same type as formalParameterType
        // (could also be a subtype)
        return true;
    }

    private boolean hasUpRelationActualIsIntersection(
            IIntersectionTypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        Collection<ITypeSymbol> typeSymbols = actualParameterType.getTypeSymbols().values();

        boolean hasUpRelation;

        if (!typeSymbols.isEmpty()) {
            if (formalParameterType instanceof IIntersectionTypeSymbol) {
                // in this case, each type in the intersection type of the formal parameter must be a parent type of
                // the actual parameter type. Following an example: int & float & bool <: int & bool because:
                //   int & float & bool <: int
                //   int & float & bool <: bool
                // Another example which does not meet these criteria int & float < int & string is wrong since:
                //   int & float </: string (is not a subtype)
                //
                Collection<ITypeSymbol> formalParameterTypes
                        = ((IIntersectionTypeSymbol) formalParameterType).getTypeSymbols().values();

                hasUpRelation = allAreSameOrParentTypes(formalParameterTypes, actualParameterType);
            } else {
                hasUpRelation = isAtLeastOneIsSameOrSubtype(typeSymbols, formalParameterType);
            }
        } else {
            hasUpRelation = hasUpRelationActualIsNotUnionNorIntersection(mixedTypeSymbol, formalParameterType);
        }

        return hasUpRelation;
    }

    private boolean hasUpRelationFormalIsUnion(ITypeSymbol actualParameterType, IUnionTypeSymbol formalParameterType) {
        //if union is empty, then it cannot be a subtype or the same (if actual was an empty union type then we would
        // already have stopped in hasUpRelationActualIsUnion and return true)

        Collection<ITypeSymbol> typeSymbols = formalParameterType.getTypeSymbols().values();
        return isAtLeastOneSameOrParentType(typeSymbols, actualParameterType);
    }

    private boolean hasUpRelationFormalIsIntersection(
            ITypeSymbol actualParameterType, IIntersectionTypeSymbol formalParameterType) {

        Collection<ITypeSymbol> typeSymbols = formalParameterType.getTypeSymbols().values();

        boolean hasUpRelation;

        if (!typeSymbols.isEmpty()) {
            hasUpRelation = allAreSameOrParentTypes(typeSymbols, actualParameterType);
        } else {
            // an empty intersection is the top type of all types and hence is a parent type of all types,
            // it is represented by mixed
            hasUpRelation = hasNominalUpRelation(actualParameterType, mixedTypeSymbol);
        }

        return hasUpRelation;
    }

    private boolean allAreSameOrParentTypes(
            Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompareWith) {

        boolean areAllSameOrParentTypes = true;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = hasUpRelationFromTo(typeSymbolToCompareWith, typeSymbol);
            if (!hasRelation) {
                areAllSameOrParentTypes = false;
                break;
            }
        }
        return areAllSameOrParentTypes;
    }

    private boolean allAreSameOrSubtypes(
            Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompareWith) {

        boolean areAllSameOrSubtypes = true;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = hasUpRelationFromTo(typeSymbol, typeSymbolToCompareWith);
            if (!hasRelation) {
                areAllSameOrSubtypes = false;
                break;
            }
        }
        return areAllSameOrSubtypes;
    }

    private boolean isAtLeastOneIsSameOrSubtype(
            Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompareWith) {

        boolean oneIsSameOrSubtype = false;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = hasUpRelationFromTo(typeSymbol, typeSymbolToCompareWith);
            if (hasRelation) {
                oneIsSameOrSubtype = true;
                break;
            }
        }
        return oneIsSameOrSubtype;
    }

    private boolean isAtLeastOneSameOrParentType(
            Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompareWith) {

        boolean oneIsParentType = false;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = hasUpRelationFromTo(typeSymbolToCompareWith, typeSymbol);
            if (hasRelation) {
                oneIsParentType = true;
                break;
            }
        }
        return oneIsParentType;
    }

    private boolean hasNominalUpRelation(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        boolean hasUpRelation = areSame(actualParameterType, formalParameterType);
        if (!hasUpRelation) {
            Set<ITypeSymbol> parentTypes = actualParameterType.getParentTypeSymbols();
            for (ITypeSymbol parentType : parentTypes) {
                boolean hasRelation = hasNominalUpRelation(parentType, formalParameterType);
                if (hasRelation) {
                    hasUpRelation = true;
                    break;
                }
            }
        }
        return hasUpRelation;
    }
}
