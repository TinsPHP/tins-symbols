/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.ICore;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class TypeHelper implements ITypeHelper
{

    private ITypeSymbol mixedTypeSymbol;
    private ICore core;

    @Override
    public void setCore(ICore theCore) {
        core = theCore;
    }

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

    private boolean hasUpRelationFromTo(ITypeSymbol fromType, ITypeSymbol toType) {
        if (fromType instanceof IUnionTypeSymbol) {
            return hasUpRelationFromUnionTo((IUnionTypeSymbol) fromType, toType);
        } else if (fromType instanceof IIntersectionTypeSymbol) {
            return hasUpRelationFromIntersectionTo((IIntersectionTypeSymbol) fromType, toType);
        } else if (fromType instanceof IConvertibleTypeSymbol) {
            return hasUpRelationFromConvertibleTo((IConvertibleTypeSymbol) fromType, toType);
        }
        return hasUpRelationFromNominalTo(fromType, toType);
    }

    private boolean hasUpRelationFromNominalTo(ITypeSymbol fromType, ITypeSymbol toType) {
        if (toType instanceof IUnionTypeSymbol) {
            return hasUpRelationFromNominalToUnion(fromType, (IUnionTypeSymbol) toType);
        } else if (toType instanceof IIntersectionTypeSymbol) {
            return hasUpRelationFromNominalToIntersection(fromType, (IIntersectionTypeSymbol) toType);
        } else if (toType instanceof IConvertibleTypeSymbol) {
            return hasUpRelationFromNominalToConvertible(fromType, (IConvertibleTypeSymbol) toType);
        }
        return hasUpRelationFromNominalToNominal(fromType, toType);
    }

    private boolean hasUpRelationFromUnionTo(IUnionTypeSymbol fromType, ITypeSymbol toType) {
        Collection<ITypeSymbol> typeSymbols = fromType.getTypeSymbols().values();

        if (!typeSymbols.isEmpty()) {
            return allAreSameOrSubtypes(typeSymbols, toType);
        }
        // an empty union is the bottom type of all types and hence is at least the same type as toType
        // (could also be a subtype)
        return true;
    }

    private boolean hasUpRelationFromIntersectionTo(
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
                hasUpRelation = isAtLeastOneSameOrSubtype(typeSymbols, formalParameterType);
            }
        } else {
            hasUpRelation = hasUpRelationFromNominalTo(mixedTypeSymbol, formalParameterType);
        }

        return hasUpRelation;
    }

    private boolean hasUpRelationFromConvertibleTo(
            IConvertibleTypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        if (formalParameterType instanceof IConvertibleTypeSymbol) {
            IConvertibleTypeSymbol formalConvertibleType = (IConvertibleTypeSymbol) formalParameterType;
            return hasUpRelationFromTo(
                    actualParameterType.getUpperTypeBounds(), formalConvertibleType.getUpperTypeBounds());
        }
        //A convertible type cannot be a subtype of another type if this type is not a convertible type.
        return false;
    }

    private boolean hasUpRelationFromNominalToUnion(ITypeSymbol actualParameterType, IUnionTypeSymbol
            formalParameterType) {
        //if union is empty, then it cannot be a subtype or the same (if actual was an empty union type then we would
        // already have stopped in hasUpRelationFromNominalToUnion and return true)

        Collection<ITypeSymbol> typeSymbols = formalParameterType.getTypeSymbols().values();
        return isAtLeastOneSameOrParentType(typeSymbols, actualParameterType);
    }

    private boolean hasUpRelationFromNominalToIntersection(
            ITypeSymbol actualParameterType, IIntersectionTypeSymbol formalParameterType) {

        Collection<ITypeSymbol> typeSymbols = formalParameterType.getTypeSymbols().values();

        boolean hasUpRelation;

        if (!typeSymbols.isEmpty()) {
            hasUpRelation = allAreSameOrParentTypes(typeSymbols, actualParameterType);
        } else {
            // an empty intersection is the top type of all types and hence is a parent type of all types,
            // it is represented by mixed
            hasUpRelation = hasUpRelationFromNominalToNominal(actualParameterType, mixedTypeSymbol);
        }

        return hasUpRelation;
    }

    private boolean hasUpRelationFromNominalToConvertible(ITypeSymbol fromType, IConvertibleTypeSymbol toType) {
        String fromAbsoluteName = fromType.getAbsoluteName();
        ITypeSymbol toTargetType = toType.getUpperTypeBounds();
        String toTargetAbsoluteName = toTargetType.getAbsoluteName();

        boolean canBeConverted = isFirstSameOrSubTypeOfSecond(fromType, toTargetType);

        if (!canBeConverted) {
            Map<String, Pair<ITypeSymbol, IConversionMethod>> explicitConversions
                    = core.getExplicitConversions().get(fromAbsoluteName);
            Map<String, Pair<ITypeSymbol, IConversionMethod>> implicitConversions
                    = core.getImplicitConversions().get(fromAbsoluteName);
            canBeConverted = explicitConversions != null && explicitConversions.containsKey(toTargetAbsoluteName)

                    || implicitConversions != null && implicitConversions.containsKey(toTargetAbsoluteName);

            if (!canBeConverted) {
                if (explicitConversions != null) {
                    canBeConverted = isAtLeastOneSameOrSubtype(explicitConversions, toTargetType);
                }
                if (!canBeConverted && implicitConversions != null) {
                    canBeConverted = isAtLeastOneSameOrSubtype(implicitConversions, toTargetType);
                }
            }
        }

        return canBeConverted;
    }

    private boolean isAtLeastOneSameOrSubtype(
            Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions, ITypeSymbol targetType) {

        boolean oneIsSameOrSubtype = false;
        for (Map.Entry<String, Pair<ITypeSymbol, IConversionMethod>> entry : conversions.entrySet()) {
            ITypeSymbol typeSymbol = entry.getValue().first;
            boolean hasRelation = isFirstSameOrSubTypeOfSecond(typeSymbol, targetType);
            if (hasRelation) {
                oneIsSameOrSubtype = true;
                break;
            }
        }
        return oneIsSameOrSubtype;
    }

    private boolean allAreSameOrParentTypes(
            Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompareWith) {

        boolean areAllSameOrParentTypes = true;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = isFirstSameOrParentTypeOfSecond(typeSymbol, typeSymbolToCompareWith);
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
            boolean hasRelation = isFirstSameOrSubTypeOfSecond(typeSymbol, typeSymbolToCompareWith);
            if (!hasRelation) {
                areAllSameOrSubtypes = false;
                break;
            }
        }
        return areAllSameOrSubtypes;
    }

    private boolean isAtLeastOneSameOrSubtype(
            Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompareWith) {

        boolean oneIsSameOrSubtype = false;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = isFirstSameOrSubTypeOfSecond(typeSymbol, typeSymbolToCompareWith);
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
            boolean hasRelation = isFirstSameOrParentTypeOfSecond(typeSymbol, typeSymbolToCompareWith);
            if (hasRelation) {
                oneIsParentType = true;
                break;
            }
        }
        return oneIsParentType;
    }

    private boolean hasUpRelationFromNominalToNominal(
            ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        boolean hasUpRelation = areSame(actualParameterType, formalParameterType);
        if (!hasUpRelation) {
            Set<ITypeSymbol> parentTypes = actualParameterType.getParentTypeSymbols();
            for (ITypeSymbol parentType : parentTypes) {
                boolean hasRelation = hasUpRelationFromNominalToNominal(parentType, formalParameterType);
                if (hasRelation) {
                    hasUpRelation = true;
                    break;
                }
            }
        }
        return hasUpRelation;
    }
}
