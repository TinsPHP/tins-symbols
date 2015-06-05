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
import ch.tsphp.tinsphp.common.utils.ERelation;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.MapHelper;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.common.utils.TypeHelperDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.ERelation.HAS_COERCIVE_RELATION;
import static ch.tsphp.tinsphp.common.utils.ERelation.HAS_NO_RELATION;
import static ch.tsphp.tinsphp.common.utils.ERelation.HAS_RELATION;

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
    public TypeHelperDto isFirstSameOrSubTypeOfSecond(ITypeSymbol potentialSubType, ITypeSymbol typeSymbol) {
        return isFirstSameOrSubTypeOfSecond(potentialSubType, typeSymbol, true);
    }

    @Override
    public TypeHelperDto isFirstSameOrSubTypeOfSecond(
            ITypeSymbol potentialSubType, ITypeSymbol typeSymbol, boolean shallConsiderImplicitConversions) {
        TypeHelperDto dto = new TypeHelperDto(potentialSubType, typeSymbol, shallConsiderImplicitConversions);
        isFirstSameOrSubTypeOfSecond(dto);
        return dto;
    }

    @Override
    public TypeHelperDto isFirstSameOrParentTypeOfSecond(ITypeSymbol potentialParentType, ITypeSymbol typeSymbol) {
        return isFirstSameOrParentTypeOfSecond(potentialParentType, typeSymbol, true);
    }

    @Override
    public TypeHelperDto isFirstSameOrParentTypeOfSecond(
            ITypeSymbol potentialParentType, ITypeSymbol typeSymbol, boolean shallConsiderImplicitConversions) {
        TypeHelperDto dto = new TypeHelperDto(typeSymbol, potentialParentType, shallConsiderImplicitConversions);
        isFirstSameOrSubTypeOfSecond(dto);
        return dto;
    }

    private void isFirstSameOrSubTypeOfSecond(TypeHelperDto dto) {
        if (areSame(dto.fromType, dto.toType)) {
            dto.relation = HAS_RELATION;
        }
        hasUpRelationFromTo(dto);
    }

    private void hasUpRelationFromTo(TypeHelperDto dto) {
        if (dto.fromType instanceof IUnionTypeSymbol) {
            hasUpRelationFromUnionTo(dto);
        } else if (dto.fromType instanceof IIntersectionTypeSymbol) {
            hasUpRelationFromIntersectionTo(dto);
        } else if (dto.fromType instanceof IConvertibleTypeSymbol) {
            hasUpRelationFromConvertibleTo(dto);
        } else {
            hasUpRelationFromNominalTo(dto);
        }
    }

    private void hasUpRelationFromNominalTo(TypeHelperDto dto) {
        if (dto.toType instanceof IUnionTypeSymbol) {
            hasUpRelationFromNominalToUnion(dto);
        } else if (dto.toType instanceof IIntersectionTypeSymbol) {
            hasUpRelationFromNominalToIntersection(dto);
        } else if (dto.toType instanceof IConvertibleTypeSymbol) {
            hasUpRelationFromNominalToConvertible(dto);
        } else {
            hasUpRelationFromNominalToNominal(dto);
        }
    }

    private void hasUpRelationFromUnionTo(TypeHelperDto dto) {
        Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.fromType).getTypeSymbols().values();
        if (!typeSymbols.isEmpty()) {
            allAreSameOrSubtypesOfToType(typeSymbols, dto);
        } else {
            // an empty union is the bottom type of all types and hence is at least the same type as toType
            // (could also be a subtype)
            dto.relation = HAS_RELATION;
        }
    }

    private void hasUpRelationFromIntersectionTo(TypeHelperDto dto) {
        Collection<ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.fromType).getTypeSymbols().values();
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
                allAreSameOrParentTypesOfFromType(formalParameterTypes, dto);
            } else {
                isAtLeastOneSameOrSubtypeOfToType(typeSymbols, dto);
            }
        } else {
            dto.fromType = mixedTypeSymbol;
            hasUpRelationFromNominalTo(dto);
        }
    }

    private void hasUpRelationFromConvertibleTo(TypeHelperDto dto) {
        if (areSame(dto.toType, mixedTypeSymbol)) {
            dto.relation = HAS_RELATION;
        } else if (dto.toType instanceof IUnionTypeSymbol) {
            Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.toType).getTypeSymbols().values();
            isAtLeastOneSameOrParentTypeOfFromType(typeSymbols, dto);
        } else if (dto.toType instanceof IIntersectionTypeSymbol) {
            Map<String, ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.toType).getTypeSymbols();
            int size = typeSymbols.size();
            if (size == 0) {
                dto.relation = HAS_RELATION;
            } else if (size == 1) {
                dto.toType = typeSymbols.values().iterator().next();
                hasUpRelationFromConvertibleTo(dto);
            }
        } else if (dto.toType instanceof IConvertibleTypeSymbol) {
            IConvertibleTypeSymbol fromType = (IConvertibleTypeSymbol) dto.fromType;
            IConvertibleTypeSymbol toType = (IConvertibleTypeSymbol) dto.toType;
            TypeHelperDto newDto = new TypeHelperDto(fromType.getUpperTypeBounds(), toType.getUpperTypeBounds(), false);
            hasUpRelationFromTo(newDto);
            if (newDto.relation != HAS_NO_RELATION) {
                dto.relation = newDto.relation;
            }
        }
    }

    private void hasUpRelationFromNominalToUnion(TypeHelperDto dto) {
        //if union is empty, then it cannot be a subtype or the same (if actual was an empty union type then we would
        // already have stopped in hasUpRelationFromNominalToUnion and return true)

        Collection<ITypeSymbol> typeSymbols = ((IUnionTypeSymbol) dto.toType).getTypeSymbols().values();
        isAtLeastOneSameOrParentTypeOfFromType(typeSymbols, dto);
    }

    private void hasUpRelationFromNominalToIntersection(TypeHelperDto dto) {
        Collection<ITypeSymbol> typeSymbols = ((IIntersectionTypeSymbol) dto.toType).getTypeSymbols().values();
        if (!typeSymbols.isEmpty()) {
            allAreSameOrParentTypesOfFromType(typeSymbols, dto);
        } else {
            // an empty intersection is the top type of all types and hence is a parent type of all types,
            // it is represented by mixed
            dto.toType = mixedTypeSymbol;
            hasUpRelationFromNominalToNominal(dto);
        }
    }

    private void hasUpRelationFromNominalToConvertible(TypeHelperDto dto) {
        IConvertibleTypeSymbol convertibleTypeSymbol = (IConvertibleTypeSymbol) dto.toType;
        IIntersectionTypeSymbol upperTypeBounds = convertibleTypeSymbol.getUpperTypeBounds();
        String typeParameter = null;
        if (convertibleTypeSymbol.wasBound() && !convertibleTypeSymbol.isFixed()) {
            typeParameter = convertibleTypeSymbol.getTypeVariable();
        }

        if (upperTypeBounds != null) {
            TypeHelperDto copy = new TypeHelperDto(dto);
            copy.toType = upperTypeBounds;
            copy.shallConsiderImplicitConversions = false;
            hasUpRelationFromNominalToIntersection(copy);
            if (copy.relation == HAS_NO_RELATION) {
                hasConversionFromNominalToTarget(copy, typeParameter, conversionsProvider.getImplicitConversions());
            } else {
                if (typeParameter != null) {
                    MapHelper.addToListInMap(dto.lowerConstraints, typeParameter, dto.fromType);
                }
            }
            copy.shallConsiderImplicitConversions = dto.shallConsiderImplicitConversions;
            if (copy.relation == HAS_NO_RELATION) {
                hasConversionFromNominalToTarget(copy, typeParameter, conversionsProvider.getExplicitConversions());
            }
            dto.relation = copy.relation;
        } else if (!convertibleTypeSymbol.wasBound()) {
            dto.relation = HAS_RELATION;
        }
    }

    private void hasConversionFromNominalToTarget(
            TypeHelperDto dto,
            String typeParameter,
            Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionMap) {

        String fromAbsoluteName = dto.fromType.getAbsoluteName();
        String toTargetAbsoluteName = dto.toType.getAbsoluteName();

        Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions = conversionMap.get(fromAbsoluteName);
        if (conversions != null) {
            if (conversions.containsKey(toTargetAbsoluteName)) {
                if (typeParameter != null) {
                    MapHelper.addToListInMap(dto.lowerConstraints, typeParameter, dto.fromType);
                }
                dto.relation = HAS_RELATION;
            } else {
                isAtLeastOneConversionTargetSubtype(dto, typeParameter, conversions);
            }
        }
        if (dto.relation == HAS_NO_RELATION) {
            haveParentsConversionToTarget(dto, typeParameter, conversionMap);
        }
    }


    //Warning! start code duplication - very similar to isAtLeastOneSameOrParentTypeOfFromType and
    // isAtLeastOneSameOrSubtypeOfToType
    private void isAtLeastOneConversionTargetSubtype(
            TypeHelperDto dto, String typeParameter, Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions) {

        ITypeSymbol tmpFromType = dto.fromType;
        forLoop:
        for (Map.Entry<String, Pair<ITypeSymbol, IConversionMethod>> entry : conversions.entrySet()) {
            dto.fromType = entry.getValue().first;
            isFirstSameOrSubTypeOfSecond(dto);
            switch (dto.relation) {
                case HAS_RELATION:
                    if (typeParameter != null) {
                        MapHelper.addToListInMap(dto.lowerConstraints, typeParameter, dto.fromType);
                    }
                    dto.relation = HAS_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    if (typeParameter != null) {
                        MapHelper.addToListInMap(dto.lowerConstraints, typeParameter, dto.fromType);
                    }
                    dto.relation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        dto.fromType = tmpFromType;
    }
    //Warning! end code duplication - very similar to isAtLeastOneSameOrParentTypeOfFromType and
    // isAtLeastOneSameOrSubtypeOfToType


    private void haveParentsConversionToTarget(
            TypeHelperDto dto,
            String typeParameter,
            Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionMap) {

        ITypeSymbol tmpFromType = dto.fromType;
        for (ITypeSymbol typeSymbol : dto.fromType.getParentTypeSymbols()) {
            dto.fromType = typeSymbol;
            hasConversionFromNominalToTarget(dto, typeParameter, conversionMap);
            if (dto.relation == HAS_RELATION) {
                break;
            }
        }
        dto.fromType = tmpFromType;
    }

    //Warning! start code duplication - very similar to allAreSameOrParentTypesOfFromType
    private void allAreSameOrSubtypesOfToType(Collection<ITypeSymbol> typeSymbols, TypeHelperDto dto) {
        Map<String, List<ITypeSymbol>> lowerConstraints = new HashMap<>();
        Map<String, List<ITypeSymbol>> upperConstraints = new HashMap<>();

        ERelation subtypeRelation = HAS_RELATION;
        forLoop:
        for (ITypeSymbol typeSymbol : typeSymbols) {
            TypeHelperDto resultDto = isFirstSameOrSubTypeOfSecond(
                    typeSymbol, dto.toType, dto.shallConsiderImplicitConversions);
            switch (resultDto.relation) {
                case HAS_NO_RELATION:
                    subtypeRelation = HAS_NO_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    subtypeRelation = HAS_COERCIVE_RELATION;
                    //fall through on purpose
                default:
                    lowerConstraints.putAll(resultDto.lowerConstraints);
                    upperConstraints.putAll(resultDto.upperConstraints);
            }
        }

        if (subtypeRelation != HAS_NO_RELATION) {
            dto.relation = subtypeRelation;
            dto.lowerConstraints.putAll(lowerConstraints);
            dto.upperConstraints.putAll(upperConstraints);
        }
    }
    //Warning! end code duplication - very similar to allAreSameOrParentTypesOfFromType


    //Warning! start code duplication - very similar to allAreSameOrSubtypesOfToType
    private void allAreSameOrParentTypesOfFromType(Collection<ITypeSymbol> typeSymbols, TypeHelperDto dto) {
        Map<String, List<ITypeSymbol>> lowerConstraints = new HashMap<>();
        Map<String, List<ITypeSymbol>> upperConstraints = new HashMap<>();

        ERelation parentTypeRelation = HAS_RELATION;
        forLoop:
        for (ITypeSymbol typeSymbol : typeSymbols) {
            TypeHelperDto resultDto = isFirstSameOrParentTypeOfSecond(
                    typeSymbol, dto.fromType, dto.shallConsiderImplicitConversions);
            switch (resultDto.relation) {
                case HAS_NO_RELATION:
                    parentTypeRelation = HAS_NO_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    parentTypeRelation = HAS_COERCIVE_RELATION;
                    //fall through on purpose
                default:
                    lowerConstraints.putAll(resultDto.lowerConstraints);
                    upperConstraints.putAll(resultDto.upperConstraints);
            }
        }

        if (parentTypeRelation != HAS_NO_RELATION) {
            dto.relation = parentTypeRelation;
            dto.lowerConstraints.putAll(lowerConstraints);
            dto.upperConstraints.putAll(upperConstraints);
        }
    }
    //Warning! end code duplication - very similar to allAreSameOrSubtypesOfToType


    //Warning! start code duplication - very similar to isAtLeastOneSameOrParentTypeOfFromType
    // and isAtLeastOneConversionTargetSubtype
    private void isAtLeastOneSameOrSubtypeOfToType(Collection<ITypeSymbol> typeSymbols, TypeHelperDto dto) {
        ITypeSymbol tmpFromType = dto.fromType;
        forLoop:
        for (ITypeSymbol typeSymbol : typeSymbols) {
            dto.fromType = typeSymbol;
            isFirstSameOrSubTypeOfSecond(dto);
            switch (dto.relation) {
                case HAS_RELATION:
                    dto.relation = HAS_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    dto.relation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        dto.fromType = tmpFromType;
    }
    //Warning! end code duplication - very similar to isAtLeastOneSameOrParentTypeOfFromType
    // and isAtLeastOneConversionTargetSubtype


    //Warning! start code duplication - very similar to isAtLeastOneSameOrSubtypeOfToType
    // and isAtLeastOneConversionTargetSubtype
    private void isAtLeastOneSameOrParentTypeOfFromType(Collection<ITypeSymbol> typeSymbols, TypeHelperDto dto) {
        ITypeSymbol tmpToType = dto.toType;
        forLoop:
        for (ITypeSymbol typeSymbol : typeSymbols) {
            dto.toType = typeSymbol;
            isFirstSameOrSubTypeOfSecond(dto);
            switch (dto.relation) {
                case HAS_RELATION:
                    dto.relation = HAS_RELATION;
                    break forLoop;
                case HAS_COERCIVE_RELATION:
                    dto.relation = HAS_COERCIVE_RELATION;
                    break;
            }
        }
        dto.toType = tmpToType;
    }
    //Warning! end code duplication - very similar to isAtLeastOneSameOrSubtypeOfToType
    // and isAtLeastOneConversionTargetSubtype


    private void hasUpRelationFromNominalToNominal(TypeHelperDto dto) {
        hasUpRelationViaNominalSubtyping(dto);
        if (dto.relation != HAS_RELATION && dto.shallConsiderImplicitConversions && conversionsProvider != null) {
            dto.shallConsiderImplicitConversions = false;
            hasConversionFromNominalToTarget(dto, null, conversionsProvider.getImplicitConversions());
            if (dto.relation == HAS_RELATION) {
                dto.relation = ERelation.HAS_COERCIVE_RELATION;
            }
        }
    }

    private void hasUpRelationViaNominalSubtyping(TypeHelperDto dto) {
        if (!areSame(dto.fromType, dto.toType)) {
            for (ITypeSymbol parentType : dto.fromType.getParentTypeSymbols()) {
                ITypeSymbol tmp = dto.fromType;
                dto.fromType = parentType;
                hasUpRelationViaNominalSubtyping(dto);
                dto.fromType = tmp;
                if (dto.relation == HAS_RELATION) {
                    break;
                }
            }
        } else {
            dto.relation = HAS_RELATION;
        }
    }
}
