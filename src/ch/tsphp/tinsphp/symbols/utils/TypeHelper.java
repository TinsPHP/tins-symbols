/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.core.IConversionsProvider;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;

import java.util.Collection;
import java.util.Map;

public class TypeHelper implements ITypeHelper
{

    private ITypeSymbol mixedTypeSymbol;
    private IConversionsProvider conversionsProvider;

    @Override
    public void setConversionsProvider(IConversionsProvider theConversionProvider) {
        conversionsProvider = theConversionProvider;
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
        return isFirstSameOrSubTypeOfSecond(potentialSubType, typeSymbol, true);
    }

    @Override
    public boolean isFirstSameOrSubTypeOfSecond(
            ITypeSymbol potentialSubType, ITypeSymbol typeSymbol, boolean shallConsiderImplicitConversions) {
        TypeHelperDto dto = new TypeHelperDto(potentialSubType, typeSymbol, shallConsiderImplicitConversions);
        return isFirstSameOrSubTypeOfSecond(dto);
    }

    @Override
    public boolean isFirstSameOrParentTypeOfSecond(ITypeSymbol potentialParentType, ITypeSymbol typeSymbol) {
        return isFirstSameOrParentTypeOfSecond(potentialParentType, typeSymbol, true);
    }

    @Override
    public boolean isFirstSameOrParentTypeOfSecond(
            ITypeSymbol potentialParentType, ITypeSymbol typeSymbol, boolean shallConsiderImplicitConversions) {
        TypeHelperDto dto = new TypeHelperDto(typeSymbol, potentialParentType, shallConsiderImplicitConversions);
        return isFirstSameOrSubTypeOfSecond(dto);
    }

    private boolean isFirstSameOrSubTypeOfSecond(TypeHelperDto dto) {
        return areSame(dto.fromType, dto.toType) || hasUpRelationFromTo(dto);
    }

    private boolean hasUpRelationFromTo(TypeHelperDto dto) {
        if (dto.fromType instanceof IUnionTypeSymbol) {
            return hasUpRelationFromUnionTo(dto);
        } else if (dto.fromType instanceof IIntersectionTypeSymbol) {
            return hasUpRelationFromIntersectionTo(dto);
        } else if (dto.fromType instanceof IConvertibleTypeSymbol) {
            return hasUpRelationFromConvertibleTo(dto);
        }
        return hasUpRelationFromNominalTo(dto);
    }

    private boolean hasUpRelationFromNominalTo(TypeHelperDto dto) {
        if (dto.toType instanceof IUnionTypeSymbol) {
            return hasUpRelationFromNominalToUnion(dto);
        } else if (dto.toType instanceof IIntersectionTypeSymbol) {
            return hasUpRelationFromNominalToIntersection(dto);
        } else if (dto.toType instanceof IConvertibleTypeSymbol) {
            return hasUpRelationFromNominalToConvertible(dto);
        }
        return hasUpRelationFromNominalToNominal(dto);
    }

    private boolean hasUpRelationFromUnionTo(TypeHelperDto dto) {
        Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.fromType).getTypeSymbols().values();
        if (!typeSymbols.isEmpty()) {
            return allAreSameOrSubtypes(typeSymbols, dto.toType, dto.shallConsiderImplicitConversions);
        }
        // an empty union is the bottom type of all types and hence is at least the same type as toType
        // (could also be a subtype)
        return true;
    }

    private boolean hasUpRelationFromIntersectionTo(TypeHelperDto dto) {
        Collection<ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.fromType).getTypeSymbols().values();

        boolean hasUpRelation;

        if (!typeSymbols.isEmpty()) {
            if (dto.toType instanceof IIntersectionTypeSymbol) {
                // in this case, each type in the intersection type of the formal parameter must be a parent type of
                // the actual parameter type. Following an example: int & float & bool <: int & bool because:
                //   int & float & bool <: int
                //   int & float & bool <: bool
                // Another example which does not meet these criteria int & float < int & string is wrong since:
                //   int & float </: string (is not a subtype)
                //
                Collection<ITypeSymbol> formalParameterTypes
                        = ((IIntersectionTypeSymbol) dto.toType).getTypeSymbols().values();

                hasUpRelation = allAreSameOrParentTypes(
                        formalParameterTypes, dto.fromType, dto.shallConsiderImplicitConversions);
            } else {
                hasUpRelation = isAtLeastOneSameOrSubtype(typeSymbols, dto.toType,
                        dto.shallConsiderImplicitConversions);
            }
        } else {
            dto.fromType = mixedTypeSymbol;
            hasUpRelation = hasUpRelationFromNominalTo(dto);
        }

        return hasUpRelation;
    }

    private boolean hasUpRelationFromConvertibleTo(TypeHelperDto dto) {

        boolean hasUpRelation = false;

        if (areSame(dto.toType, mixedTypeSymbol)) {
            hasUpRelation = true;
        } else if (dto.toType instanceof IUnionTypeSymbol) {
            Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.toType).getTypeSymbols().values();
            hasUpRelation
                    = isAtLeastOneSameOrParentType(typeSymbols, dto.fromType, dto.shallConsiderImplicitConversions);
        } else if (dto.toType instanceof IIntersectionTypeSymbol) {
            Map<String, ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.toType).getTypeSymbols();
            int size = typeSymbols.size();
            if (size == 0) {
                hasUpRelation = true;
            } else if (size == 1) {
                dto.toType = typeSymbols.values().iterator().next();
                hasUpRelation = hasUpRelationFromConvertibleTo(dto);
            }
        } else if (dto.toType instanceof IConvertibleTypeSymbol) {
            IConvertibleTypeSymbol fromType = (IConvertibleTypeSymbol) dto.fromType;
            IConvertibleTypeSymbol toType = (IConvertibleTypeSymbol) dto.toType;
            TypeHelperDto newDto = new TypeHelperDto(
                    fromType.getUpperTypeBounds(), toType.getUpperTypeBounds(), dto.shallConsiderImplicitConversions);
            hasUpRelation = hasUpRelationFromTo(newDto);
        }
        return hasUpRelation;
    }

    private boolean hasUpRelationFromNominalToUnion(TypeHelperDto dto) {
        //if union is empty, then it cannot be a subtype or the same (if actual was an empty union type then we would
        // already have stopped in hasUpRelationFromNominalToUnion and return true)

        Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.toType).getTypeSymbols().values();
        return isAtLeastOneSameOrParentType(typeSymbols, dto.fromType, dto.shallConsiderImplicitConversions);
    }

    private boolean hasUpRelationFromNominalToIntersection(TypeHelperDto dto) {

        Collection<ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.toType).getTypeSymbols().values();

        boolean hasUpRelation;

        if (!typeSymbols.isEmpty()) {
            hasUpRelation = allAreSameOrParentTypes(typeSymbols, dto.fromType, dto.shallConsiderImplicitConversions);
        } else {
            // an empty intersection is the top type of all types and hence is a parent type of all types,
            // it is represented by mixed
            dto.toType = mixedTypeSymbol;
            hasUpRelation = hasUpRelationFromNominalToNominal(dto);
        }

        return hasUpRelation;
    }

    private boolean hasUpRelationFromNominalToConvertible(TypeHelperDto dto) {
        IConvertibleTypeSymbol convertibleTypeSymbol = (IConvertibleTypeSymbol) dto.toType;
        IIntersectionTypeSymbol upperTypeBounds = convertibleTypeSymbol.getUpperTypeBounds();
        boolean canBeConverted;
        if (upperTypeBounds != null) {
            dto.toType = upperTypeBounds;
            canBeConverted = hasUpRelationFromNominalToIntersection(dto);
            if (!canBeConverted) {
                canBeConverted = hasConversionFromNominalToTarget(dto, conversionsProvider.getExplicitConversions());
            }
        } else {
            canBeConverted = !convertibleTypeSymbol.wasBound();
        }
        return canBeConverted;
    }

    private boolean hasConversionFromNominalToTarget(
            TypeHelperDto dto, Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionMap) {
        String fromAbsoluteName = dto.fromType.getAbsoluteName();
        String toTargetAbsoluteName = dto.toType.getAbsoluteName();

        boolean canBeConverted;
        Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions = conversionMap.get(fromAbsoluteName);
        canBeConverted = conversions != null && conversions.containsKey(toTargetAbsoluteName);

        if (!canBeConverted) {
            if (conversions != null) {
                canBeConverted = isAtLeastOneSameOrSubtype(dto, conversions);
            }
            if (!canBeConverted) {
                canBeConverted = hasParentsConversionToTarget(dto, conversionMap);
            }
        }
        return canBeConverted;
    }

    private boolean isAtLeastOneSameOrSubtype(
            TypeHelperDto dto, Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions) {

        boolean oneIsSameOrSubtype = false;
        for (Map.Entry<String, Pair<ITypeSymbol, IConversionMethod>> entry : conversions.entrySet()) {
            TypeHelperDto newDto = new TypeHelperDto(dto);
            newDto.fromType = entry.getValue().first;
            boolean hasRelation = isFirstSameOrSubTypeOfSecond(newDto);
            if (hasRelation) {
                oneIsSameOrSubtype = true;
                break;
            }
        }
        return oneIsSameOrSubtype;
    }

    private boolean hasParentsConversionToTarget(
            TypeHelperDto dto, Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionMap) {
        boolean oneCanBeConverted = false;
        for (ITypeSymbol typeSymbol : dto.fromType.getParentTypeSymbols()) {
            TypeHelperDto parentDto = new TypeHelperDto(dto);
            parentDto.fromType = typeSymbol;
            boolean canBeConverted = hasConversionFromNominalToTarget(parentDto, conversionMap);
            if (canBeConverted) {
                oneCanBeConverted = true;
                break;
            }
        }
        return oneCanBeConverted;
    }


    private boolean allAreSameOrParentTypes(
            Collection<ITypeSymbol> typeSymbols,
            ITypeSymbol typeSymbolToCompareWith,
            boolean shallConsiderImplicitConversions) {

        boolean areAllSameOrParentTypes = true;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = isFirstSameOrParentTypeOfSecond(
                    typeSymbol, typeSymbolToCompareWith, shallConsiderImplicitConversions);
            if (!hasRelation) {
                areAllSameOrParentTypes = false;
                break;
            }
        }
        return areAllSameOrParentTypes;
    }

    private boolean allAreSameOrSubtypes(
            Collection<ITypeSymbol> typeSymbols,
            ITypeSymbol typeSymbolToCompareWith,
            boolean shallConsiderImplicitConversions) {

        boolean areAllSameOrSubtypes = true;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = isFirstSameOrSubTypeOfSecond(
                    typeSymbol, typeSymbolToCompareWith, shallConsiderImplicitConversions);
            if (!hasRelation) {
                areAllSameOrSubtypes = false;
                break;
            }
        }
        return areAllSameOrSubtypes;
    }

    private boolean isAtLeastOneSameOrSubtype(
            Collection<ITypeSymbol> typeSymbols,
            ITypeSymbol typeSymbolToCompareWith,
            boolean shallConsiderImplicitConversions) {

        boolean oneIsSameOrSubtype = false;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = isFirstSameOrSubTypeOfSecond(
                    typeSymbol, typeSymbolToCompareWith, shallConsiderImplicitConversions);
            if (hasRelation) {
                oneIsSameOrSubtype = true;
                break;
            }
        }
        return oneIsSameOrSubtype;
    }

    private boolean isAtLeastOneSameOrParentType(
            Collection<ITypeSymbol> typeSymbols,
            ITypeSymbol typeSymbolToCompareWith,
            boolean shallConsiderImplicitConversions) {

        boolean oneIsParentType = false;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean hasRelation = isFirstSameOrParentTypeOfSecond(
                    typeSymbol, typeSymbolToCompareWith, shallConsiderImplicitConversions);
            if (hasRelation) {
                oneIsParentType = true;
                break;
            }
        }
        return oneIsParentType;
    }

    private boolean hasUpRelationFromNominalToNominal(TypeHelperDto dto) {
        boolean hasUpRelation = hasUpRelationViaNominalSubtyping(dto);
        if (!hasUpRelation && dto.shallConsiderImplicitConversions && conversionsProvider != null) {
            dto.shallConsiderImplicitConversions = false;
            hasUpRelation = hasConversionFromNominalToTarget(dto, conversionsProvider.getImplicitConversions());
        }
        return hasUpRelation;
    }

    private boolean hasUpRelationViaNominalSubtyping(TypeHelperDto dto) {
        boolean hasUpRelation = areSame(dto.fromType, dto.toType);
        if (!hasUpRelation) {
            for (ITypeSymbol parentType : dto.fromType.getParentTypeSymbols()) {
                TypeHelperDto parentDto = new TypeHelperDto(dto);
                parentDto.fromType = parentType;
                boolean hasRelation = hasUpRelationViaNominalSubtyping(parentDto);
                if (hasRelation) {
                    hasUpRelation = true;
                    break;
                }
            }
        }
        return hasUpRelation;
    }
}
