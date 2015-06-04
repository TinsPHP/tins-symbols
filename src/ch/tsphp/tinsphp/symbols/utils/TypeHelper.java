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
import ch.tsphp.tinsphp.common.utils.ETypeHelperResult;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;

import java.util.Collection;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.ETypeHelperResult.HAS_COERCIVE_RELATION;
import static ch.tsphp.tinsphp.common.utils.ETypeHelperResult.HAS_NO_RELATION;
import static ch.tsphp.tinsphp.common.utils.ETypeHelperResult.HAS_RELATION;

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
    public ETypeHelperResult isFirstSameOrSubTypeOfSecond(ITypeSymbol potentialSubType, ITypeSymbol typeSymbol) {
        return isFirstSameOrSubTypeOfSecond(potentialSubType, typeSymbol, true);
    }

    @Override
    public ETypeHelperResult isFirstSameOrSubTypeOfSecond(
            ITypeSymbol potentialSubType, ITypeSymbol typeSymbol, boolean shallConsiderImplicitConversions) {
        TypeHelperDto dto = new TypeHelperDto(potentialSubType, typeSymbol, shallConsiderImplicitConversions);
        return isFirstSameOrSubTypeOfSecond(dto);
    }

    @Override
    public ETypeHelperResult isFirstSameOrParentTypeOfSecond(ITypeSymbol potentialParentType, ITypeSymbol typeSymbol) {
        return isFirstSameOrParentTypeOfSecond(potentialParentType, typeSymbol, true);
    }

    @Override
    public ETypeHelperResult isFirstSameOrParentTypeOfSecond(
            ITypeSymbol potentialParentType, ITypeSymbol typeSymbol, boolean shallConsiderImplicitConversions) {
        TypeHelperDto dto = new TypeHelperDto(typeSymbol, potentialParentType, shallConsiderImplicitConversions);
        return isFirstSameOrSubTypeOfSecond(dto);
    }

    private ETypeHelperResult isFirstSameOrSubTypeOfSecond(TypeHelperDto dto) {
        if (areSame(dto.fromType, dto.toType)) {
            return HAS_RELATION;
        }
        return hasUpRelationFromTo(dto);
    }

    private ETypeHelperResult hasUpRelationFromTo(TypeHelperDto dto) {
        if (dto.fromType instanceof IUnionTypeSymbol) {
            return hasUpRelationFromUnionTo(dto);
        } else if (dto.fromType instanceof IIntersectionTypeSymbol) {
            return hasUpRelationFromIntersectionTo(dto);
        } else if (dto.fromType instanceof IConvertibleTypeSymbol) {
            return hasUpRelationFromConvertibleTo(dto);
        }
        return hasUpRelationFromNominalTo(dto);
    }

    private ETypeHelperResult hasUpRelationFromNominalTo(TypeHelperDto dto) {
        if (dto.toType instanceof IUnionTypeSymbol) {
            return hasUpRelationFromNominalToUnion(dto);
        } else if (dto.toType instanceof IIntersectionTypeSymbol) {
            return hasUpRelationFromNominalToIntersection(dto);
        } else if (dto.toType instanceof IConvertibleTypeSymbol) {
            return hasUpRelationFromNominalToConvertible(dto);
        }
        return hasUpRelationFromNominalToNominal(dto);
    }

    private ETypeHelperResult hasUpRelationFromUnionTo(TypeHelperDto dto) {
        Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.fromType).getTypeSymbols().values();
        if (!typeSymbols.isEmpty()) {
            return allAreSameOrSubtypes(typeSymbols, dto.toType, dto.shallConsiderImplicitConversions);
        }
        // an empty union is the bottom type of all types and hence is at least the same type as toType
        // (could also be a subtype)
        return HAS_RELATION;
    }

    private ETypeHelperResult hasUpRelationFromIntersectionTo(TypeHelperDto dto) {
        Collection<ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.fromType).getTypeSymbols().values();

        ETypeHelperResult upRelation;

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

                upRelation = allAreSameOrParentTypes(
                        formalParameterTypes, dto.fromType, dto.shallConsiderImplicitConversions);
            } else {
                upRelation = isAtLeastOneSameOrSubtype(typeSymbols, dto.toType,
                        dto.shallConsiderImplicitConversions);
            }
        } else {
            dto.fromType = mixedTypeSymbol;
            upRelation = hasUpRelationFromNominalTo(dto);
        }

        return upRelation;
    }

    private ETypeHelperResult hasUpRelationFromConvertibleTo(TypeHelperDto dto) {

        ETypeHelperResult upRelation = HAS_NO_RELATION;

        if (areSame(dto.toType, mixedTypeSymbol)) {
            upRelation = HAS_RELATION;
        } else if (dto.toType instanceof IUnionTypeSymbol) {
            Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.toType).getTypeSymbols().values();
            upRelation
                    = isAtLeastOneSameOrParentType(typeSymbols, dto.fromType, dto.shallConsiderImplicitConversions);
        } else if (dto.toType instanceof IIntersectionTypeSymbol) {
            Map<String, ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.toType).getTypeSymbols();
            int size = typeSymbols.size();
            if (size == 0) {
                upRelation = HAS_RELATION;
            } else if (size == 1) {
                dto.toType = typeSymbols.values().iterator().next();
                upRelation = hasUpRelationFromConvertibleTo(dto);
            }
        } else if (dto.toType instanceof IConvertibleTypeSymbol) {
            IConvertibleTypeSymbol fromType = (IConvertibleTypeSymbol) dto.fromType;
            IConvertibleTypeSymbol toType = (IConvertibleTypeSymbol) dto.toType;
            TypeHelperDto newDto = new TypeHelperDto(
                    fromType.getUpperTypeBounds(), toType.getUpperTypeBounds(), dto.shallConsiderImplicitConversions);
            upRelation = hasUpRelationFromTo(newDto);
        }
        return upRelation;
    }

    private ETypeHelperResult hasUpRelationFromNominalToUnion(TypeHelperDto dto) {
        //if union is empty, then it cannot be a subtype or the same (if actual was an empty union type then we would
        // already have stopped in hasUpRelationFromNominalToUnion and return true)

        Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.toType).getTypeSymbols().values();
        return isAtLeastOneSameOrParentType(typeSymbols, dto.fromType, dto.shallConsiderImplicitConversions);
    }

    private ETypeHelperResult hasUpRelationFromNominalToIntersection(TypeHelperDto dto) {

        Collection<ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.toType).getTypeSymbols().values();

        ETypeHelperResult upRelation;
        if (!typeSymbols.isEmpty()) {
            upRelation = allAreSameOrParentTypes(typeSymbols, dto.fromType, dto.shallConsiderImplicitConversions);
        } else {
            // an empty intersection is the top type of all types and hence is a parent type of all types,
            // it is represented by mixed
            dto.toType = mixedTypeSymbol;
            upRelation = hasUpRelationFromNominalToNominal(dto);
        }

        return upRelation;
    }

    private ETypeHelperResult hasUpRelationFromNominalToConvertible(TypeHelperDto dto) {
        IConvertibleTypeSymbol convertibleTypeSymbol = (IConvertibleTypeSymbol) dto.toType;
        IIntersectionTypeSymbol upperTypeBounds = convertibleTypeSymbol.getUpperTypeBounds();

        ETypeHelperResult upRelation = HAS_NO_RELATION;
        if (upperTypeBounds != null) {
            dto.toType = upperTypeBounds;
            boolean shallConsider = dto.shallConsiderImplicitConversions;
            dto.shallConsiderImplicitConversions = false;
            upRelation = hasUpRelationFromNominalToIntersection(dto);
            if (upRelation == HAS_NO_RELATION) {
                upRelation = hasConversionFromNominalToTarget(dto, conversionsProvider.getImplicitConversions());
            }
            dto.shallConsiderImplicitConversions = shallConsider;
            if (upRelation == HAS_NO_RELATION) {
                upRelation = hasConversionFromNominalToTarget(dto, conversionsProvider.getExplicitConversions());
            }
        } else if (!convertibleTypeSymbol.wasBound()) {
            upRelation = HAS_RELATION;
        }
        return upRelation;
    }

    private ETypeHelperResult hasConversionFromNominalToTarget(
            TypeHelperDto dto, Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionMap) {

        String fromAbsoluteName = dto.fromType.getAbsoluteName();
        String toTargetAbsoluteName = dto.toType.getAbsoluteName();

        ETypeHelperResult upRelation = HAS_NO_RELATION;
        Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions = conversionMap.get(fromAbsoluteName);
        if (conversions != null) {
            if (conversions.containsKey(toTargetAbsoluteName)) {
                upRelation = HAS_RELATION;
            } else {
                upRelation = isAtLeastOneConversionTargetSameOrSubtype(dto, conversions);
            }
        }
        if (upRelation == HAS_NO_RELATION) {
            upRelation = haveParentsConversionToTarget(dto, conversionMap);
        }

        return upRelation;
    }


    //Warning! start code duplication - very similar to isAtLeastOneSameOrParentType and isAtLeastOneSameOrSubtype
    private ETypeHelperResult isAtLeastOneConversionTargetSameOrSubtype(
            TypeHelperDto dto, Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions) {

        ETypeHelperResult upRelation = HAS_NO_RELATION;
        forLoop:
        for (Map.Entry<String, Pair<ITypeSymbol, IConversionMethod>> entry : conversions.entrySet()) {
            TypeHelperDto newDto = new TypeHelperDto(dto);
            newDto.fromType = entry.getValue().first;
            ETypeHelperResult relation = isFirstSameOrSubTypeOfSecond(newDto);
            switch (relation) {
                case HAS_RELATION:
                    upRelation = HAS_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    upRelation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        return upRelation;
    }
    //Warning! end code duplication - very similar to isAtLeastOneSameOrParentType and isAtLeastOneSameOrSubtype


    private ETypeHelperResult haveParentsConversionToTarget(
            TypeHelperDto dto, Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionMap) {

        ETypeHelperResult upRelation = HAS_NO_RELATION;
        forLoop:
        for (ITypeSymbol typeSymbol : dto.fromType.getParentTypeSymbols()) {
            TypeHelperDto parentDto = new TypeHelperDto(dto);
            parentDto.fromType = typeSymbol;
            ETypeHelperResult relation = hasConversionFromNominalToTarget(parentDto, conversionMap);
            switch (relation) {
                case HAS_RELATION:
                    upRelation = HAS_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    upRelation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        return upRelation;
    }

    //Warning! start code duplication - very similar to allAreSameOrParentTypes
    private ETypeHelperResult allAreSameOrSubtypes(
            Collection<ITypeSymbol> typeSymbols,
            ITypeSymbol typeSymbolToCompareWith,
            boolean shallConsiderImplicitConversions) {

        ETypeHelperResult subtypeRelation = HAS_RELATION;
        forLoop:
        for (ITypeSymbol typeSymbol : typeSymbols) {
            ETypeHelperResult relation = isFirstSameOrSubTypeOfSecond(
                    typeSymbol, typeSymbolToCompareWith, shallConsiderImplicitConversions);
            switch (relation) {
                case HAS_NO_RELATION:
                    subtypeRelation = HAS_NO_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    subtypeRelation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        return subtypeRelation;
    }
    //Warning! end code duplication - very similar to allAreSameOrParentTypes


    //Warning! start code duplication - very similar to allAreSameOrSubtypes
    private ETypeHelperResult allAreSameOrParentTypes(
            Collection<ITypeSymbol> typeSymbols,
            ITypeSymbol typeSymbolToCompareWith,
            boolean shallConsiderImplicitConversions) {

        ETypeHelperResult parentTypeRelation = HAS_RELATION;
        forLoop:
        for (ITypeSymbol typeSymbol : typeSymbols) {
            ETypeHelperResult relation = isFirstSameOrParentTypeOfSecond(
                    typeSymbol, typeSymbolToCompareWith, shallConsiderImplicitConversions);
            switch (relation) {
                case HAS_NO_RELATION:
                    parentTypeRelation = HAS_NO_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    parentTypeRelation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        return parentTypeRelation;
    }
    //Warning! end code duplication - very similar to allAreSameOrSubtypes


    //Warning! start code duplication - very similar to isAtLeastOneSameOrParentType
    // and isAtLeastOneConversionTargetSameOrSubtype
    private ETypeHelperResult isAtLeastOneSameOrSubtype(
            Collection<ITypeSymbol> typeSymbols,
            ITypeSymbol typeSymbolToCompareWith,
            boolean shallConsiderImplicitConversions) {

        ETypeHelperResult subtypeRelation = HAS_NO_RELATION;
        forLoop:
        for (ITypeSymbol typeSymbol : typeSymbols) {
            ETypeHelperResult relation = isFirstSameOrSubTypeOfSecond(
                    typeSymbol, typeSymbolToCompareWith, shallConsiderImplicitConversions);
            switch (relation) {
                case HAS_RELATION:
                    subtypeRelation = HAS_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    subtypeRelation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        return subtypeRelation;
    }
    //Warning! end code duplication - very similar to isAtLeastOneSameOrParentType
    // and isAtLeastOneConversionTargetSameOrSubtype


    //Warning! start code duplication - very similar to isAtLeastOneSameOrSubtype
    // and isAtLeastOneConversionTargetSameOrSubtype
    private ETypeHelperResult isAtLeastOneSameOrParentType(
            Collection<ITypeSymbol> typeSymbols,
            ITypeSymbol typeSymbolToCompareWith,
            boolean shallConsiderImplicitConversions) {

        ETypeHelperResult parentTypeRelation = HAS_NO_RELATION;
        forLoop:
        for (ITypeSymbol typeSymbol : typeSymbols) {
            ETypeHelperResult relation = isFirstSameOrParentTypeOfSecond(
                    typeSymbol, typeSymbolToCompareWith, shallConsiderImplicitConversions);
            switch (relation) {
                case HAS_RELATION:
                    parentTypeRelation = HAS_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    parentTypeRelation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        return parentTypeRelation;
    }
    //Warning! end code duplication - very similar to isAtLeastOneSameOrSubtype
    // and isAtLeastOneConversionTargetSameOrSubtype


    private ETypeHelperResult hasUpRelationFromNominalToNominal(TypeHelperDto dto) {
        ETypeHelperResult upRelation = hasUpRelationViaNominalSubtyping(dto);
        if (upRelation != HAS_RELATION && dto.shallConsiderImplicitConversions && conversionsProvider != null) {
            dto.shallConsiderImplicitConversions = false;
            ETypeHelperResult relation
                    = hasConversionFromNominalToTarget(dto, conversionsProvider.getImplicitConversions());
            if (relation == HAS_RELATION) {
                upRelation = ETypeHelperResult.HAS_COERCIVE_RELATION;
            }
        }
        return upRelation;
    }

    private ETypeHelperResult hasUpRelationViaNominalSubtyping(TypeHelperDto dto) {
        ETypeHelperResult upRelation = HAS_NO_RELATION;
        if (!areSame(dto.fromType, dto.toType)) {
            for (ITypeSymbol parentType : dto.fromType.getParentTypeSymbols()) {
                TypeHelperDto parentDto = new TypeHelperDto(dto);
                parentDto.fromType = parentType;
                ETypeHelperResult relation = hasUpRelationViaNominalSubtyping(parentDto);
                if (relation == HAS_RELATION) {
                    upRelation = HAS_RELATION;
                    break;
                }
            }
        } else {
            upRelation = HAS_RELATION;
        }
        return upRelation;
    }
}
