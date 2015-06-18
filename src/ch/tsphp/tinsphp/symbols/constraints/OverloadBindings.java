/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.TinsPHPConstants;
import ch.tsphp.tinsphp.common.inference.constraints.BoundResultDto;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IParametricType;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IntersectionBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.LowerBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.OverloadApplicationDto;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.UpperBoundException;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ERelation;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.MapHelper;
import ch.tsphp.tinsphp.common.utils.TypeHelperDto;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OverloadBindings implements IOverloadBindings
{
    private static final String HELPER_VARIABLE_PREFIX = "!help";

    private final ISymbolFactory symbolFactory;
    private final ITypeHelper typeHelper;
    private final ITypeSymbol mixedTypeSymbol;

    private final Map<String, IUnionTypeSymbol> lowerTypeBounds;
    private final Map<String, IIntersectionTypeSymbol> upperTypeBounds;
    private final Map<String, Set<String>> upperRefBounds;
    private final Map<String, Set<String>> lowerRefBounds;
    private final Map<String, ITypeVariableReference> variable2TypeVariable;
    private final Map<String, Set<String>> typeVariable2Variables;
    private final Map<String, OverloadApplicationDto> appliedOverloads;
    private final Map<String, Set<IParametricType>> typeVariable2BoundTypes;
    private final Map<String, Set<String>> typeVariablesWithLowerConvertible;
    private final Map<String, Set<String>> typeVariablesWithUpperConvertible;

    private int count = 1;
    private int helperVariableCount = 0;
    private int numberOfConvertibleApplications = 0;
    private EMode mode = EMode.Normal;

    @SuppressWarnings("checkstyle:parameternumber")
    public OverloadBindings(ISymbolFactory theSymbolFactory, ITypeHelper theTypeHelper) {
        symbolFactory = theSymbolFactory;
        typeHelper = theTypeHelper;
        mixedTypeSymbol = symbolFactory.getMixedTypeSymbol();

        lowerTypeBounds = new HashMap<>();
        upperTypeBounds = new HashMap<>();
        lowerRefBounds = new HashMap<>();
        upperRefBounds = new HashMap<>();
        variable2TypeVariable = new HashMap<>();
        typeVariable2Variables = new HashMap<>();
        appliedOverloads = new HashMap<>();
        typeVariable2BoundTypes = new HashMap<>();
        typeVariablesWithLowerConvertible = new HashMap<>();
        typeVariablesWithUpperConvertible = new HashMap<>();
    }

    public OverloadBindings(OverloadBindings bindings) {
        symbolFactory = bindings.symbolFactory;
        typeHelper = bindings.typeHelper;
        mixedTypeSymbol = bindings.mixedTypeSymbol;

        count = bindings.count;
        helperVariableCount = bindings.helperVariableCount;
        numberOfConvertibleApplications = bindings.numberOfConvertibleApplications;
        mode = bindings.mode;

        //type variables need to be copied first since a lower or upper type bound might contain a parametric
        // polymorphic type which is bound to one of the type variables
        variable2TypeVariable = new HashMap<>(bindings.variable2TypeVariable.size());
        typeVariable2Variables = new HashMap<>(bindings.typeVariable2Variables.size());
        copyVariablesAndTypeVariables(bindings);

        lowerTypeBounds = new HashMap<>(bindings.lowerTypeBounds.size());
        upperTypeBounds = new HashMap<>(bindings.upperTypeBounds.size());
        lowerRefBounds = new HashMap<>(bindings.lowerRefBounds.size());
        upperRefBounds = new HashMap<>(bindings.upperRefBounds.size());
        typeVariable2BoundTypes = new HashMap<>(bindings.typeVariable2BoundTypes.size());
        typeVariablesWithUpperConvertible = new HashMap<>(bindings.typeVariablesWithUpperConvertible.size());
        typeVariablesWithLowerConvertible = new HashMap<>(bindings.typeVariablesWithLowerConvertible.size());
        Set<IParametricTypeSymbol> rebindParametricTypeSymbols = new HashSet<>();
        copyBounds(bindings, rebindParametricTypeSymbols);

        appliedOverloads = new HashMap<>(bindings.appliedOverloads);

        //rebound parametric polymorphic types
        for (IParametricTypeSymbol parametricTypeSymbol : rebindParametricTypeSymbols) {
            parametricTypeSymbol.rebind(this);
        }

    }

    private void copyVariablesAndTypeVariables(OverloadBindings bindings) {
        for (Map.Entry<String, ITypeVariableReference> entry : bindings.variable2TypeVariable.entrySet()) {
            ITypeVariableReference reference = entry.getValue();
            TypeVariableReference typeVariableReference = new TypeVariableReference(reference.getTypeVariable());
            ITypeVariableReference copy = typeVariableReference;
            if (reference.hasFixedType()) {
                copy = new FixedTypeVariableReference(typeVariableReference);
            }
            variable2TypeVariable.put(entry.getKey(), copy);
        }

        for (Map.Entry<String, Set<String>> entry : bindings.typeVariable2Variables.entrySet()) {
            typeVariable2Variables.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    private void copyBounds(
            OverloadBindings bindings, Set<IParametricTypeSymbol> rebindParametricTypeSymbols) {
        for (Map.Entry<String, IUnionTypeSymbol> entry : bindings.lowerTypeBounds.entrySet()) {
            Collection<IParametricTypeSymbol> parametricTypeSymbols = new ArrayDeque<>();
            IUnionTypeSymbol copy = entry.getValue().copy(parametricTypeSymbols);
            String lowerTypeVariable = entry.getKey();
            for (IParametricTypeSymbol parametricTypeSymbol : parametricTypeSymbols) {
                for (String typeVariable : parametricTypeSymbol.getTypeParameters()) {
                    MapHelper.addToSetInMap(typeVariable2BoundTypes, typeVariable, parametricTypeSymbol);
                    MapHelper.addToSetInMap(typeVariablesWithLowerConvertible, lowerTypeVariable, typeVariable);
                }
                rebindParametricTypeSymbols.add(parametricTypeSymbol);
            }
            lowerTypeBounds.put(lowerTypeVariable, copy);
        }

        for (Map.Entry<String, IIntersectionTypeSymbol> entry : bindings.upperTypeBounds.entrySet()) {
            Collection<IParametricTypeSymbol> parametricTypeSymbols = new ArrayDeque<>();
            IIntersectionTypeSymbol copy = entry.getValue().copy(parametricTypeSymbols);
            String upperTypeVariable = entry.getKey();
            for (IParametricTypeSymbol parametricTypeSymbol : parametricTypeSymbols) {
                for (String typeVariable : parametricTypeSymbol.getTypeParameters()) {
                    MapHelper.addToSetInMap(typeVariable2BoundTypes, typeVariable, parametricTypeSymbol);
                    MapHelper.addToSetInMap(typeVariablesWithUpperConvertible, upperTypeVariable, typeVariable);
                }
                rebindParametricTypeSymbols.add(parametricTypeSymbol);
            }
            upperTypeBounds.put(upperTypeVariable, copy);
        }

        for (Map.Entry<String, Set<String>> entry : bindings.lowerRefBounds.entrySet()) {
            lowerRefBounds.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        for (Map.Entry<String, Set<String>> entry : bindings.upperRefBounds.entrySet()) {
            upperRefBounds.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    @Override
    public ITypeVariableReference createHelperVariable() {
        ITypeVariableReference nextTypeVariable = getNextTypeVariable();
        addVariable(HELPER_VARIABLE_PREFIX + helperVariableCount++, nextTypeVariable);
        return nextTypeVariable;
    }

    @Override
    public ITypeVariableReference getNextTypeVariable() {
        return new TypeVariableReference("V" + count++);
    }

    @Override
    public void addVariable(String variableId, ITypeVariableReference reference) {
        if (variable2TypeVariable.containsKey(variableId)) {
            throw new IllegalArgumentException(
                    "variable with id " + variableId + " was already added to this binding.");
        }
        variable2TypeVariable.put(variableId, reference);
        MapHelper.addToSetInMap(typeVariable2Variables, reference.getTypeVariable(), variableId);
    }

    @Override
    public boolean containsVariable(String variableId) {
        return variable2TypeVariable.containsKey(variableId);
    }

    @Override
    public Set<String> getVariableIds() {
        return variable2TypeVariable.keySet();
    }

    @Override
    public ITypeVariableReference getTypeVariableReference(String variableId) {
        return variable2TypeVariable.get(variableId);
    }

    @Override
    public String getTypeVariable(String variableId) {
        return variable2TypeVariable.get(variableId).getTypeVariable();
    }

    @Override
    public BoundResultDto addLowerRefBound(String typeVariable, ITypeVariableReference reference) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\".");
        }

        String refTypeVariable = reference.getTypeVariable();

        if (!typeVariable2Variables.containsKey(refTypeVariable)) {
            throw new IllegalArgumentException(
                    "no variable has a binding for type variable \"" + refTypeVariable + "\".");
        }

        boolean hasNotFixedType = !reference.hasFixedType();
        return addLowerRefBound(typeVariable, refTypeVariable, hasNotFixedType);
    }

    private BoundResultDto addLowerRefBound(String typeVariable, String refTypeVariable, boolean hasNotFixedType) {
        boolean hasChanged = false;
        boolean hasChangedOtherBounds = false;
        boolean usedImplicitConversions = false;
        ITypeSymbol implicitConversionProvider = null;

        // no need to actually add the dependency if it has a fixed type (then it is enough that we transfer the
        // type bounds)
        if (hasNotFixedType) {
            hasChanged = !lowerRefBounds.containsKey(typeVariable);
            if (!hasChanged) {
                Set<String> set = lowerRefBounds.get(typeVariable);
                if (!set.contains(refTypeVariable)) {
                    hasChanged = true;
                    set.add(refTypeVariable);
                }
            } else {
                Set<String> set = new HashSet<>();
                set.add(refTypeVariable);
                lowerRefBounds.put(typeVariable, set);
            }
            MapHelper.addToSetInMap(upperRefBounds, refTypeVariable, typeVariable);
        }

        if (isNotSelfReference(typeVariable, refTypeVariable)) {

            // The refTypeVariable needs to be the same or a subtype of typeVariable or we can narrow bounds in order
            // that this property holds.

            BoundResultDto result;

            // First, the upper bound of refTypeVariable needs to be same or a subtype of typeVariable's upper bound in
            // order that we can use the refTypeVariable instead of typeVariable in a function call. Hence we add the
            // upper bound of typeVariable to refTypeVariable's upper bound. If refTypeVariable is not yet the same
            // or a subtype, then either the newly added upper bound will specialise the upper bound of the
            // refTypeVariable or will lead to a BoundException. ...
            if (hasUpperTypeBounds(typeVariable)) {
                result = addUpperTypeBoundAfterContainsCheck(refTypeVariable, upperTypeBounds.get(typeVariable));
                hasChanged = hasChanged || result.hasChanged;
                hasChangedOtherBounds = hasChangedOtherBounds || result.hasChangedOtherBounds;
                usedImplicitConversions = usedImplicitConversions || result.usedImplicitConversion;
                if (implicitConversionProvider == null) {
                    implicitConversionProvider = result.implicitConversionProvider;
                } else if (result.implicitConversionProvider != null) {
                    throw new UnsupportedOperationException("more than one conversion provider");
                }
            }

            // ... Furthermore, typeVariable logically needs to be able to hold all types refTypeVariable can hold. Thus
            // the lower bound of typeVariable need to be the same or a parent type of refTypeVariable. Therefore we add
            // the lower bound of refTypeVariable to the lower bound of typeVariable.
            //
            // Actually, this is already implicitly given otherwise we would have get a BoundException above or we would
            // get one when we add an incompatible upper bound to typeVariable latter on. Anwyay,
            // it is beneficial to propagate the lower bound upwards since we need to check later on,
            // if variables have the same lower bound as parameters and thus can be unified.
            if (hasLowerTypeBounds(refTypeVariable)) {
                result = addLowerTypeBoundAfterContainsCheck(typeVariable, lowerTypeBounds.get(refTypeVariable));
                hasChanged = hasChanged || result.hasChanged;
                hasChangedOtherBounds = hasChangedOtherBounds || result.hasChangedOtherBounds;
                usedImplicitConversions = usedImplicitConversions || result.usedImplicitConversion;
                if (implicitConversionProvider == null) {
                    implicitConversionProvider = result.implicitConversionProvider;
                } else if (result.implicitConversionProvider != null) {
                    throw new UnsupportedOperationException("more than one conversion provider");
                }
            }
        }

        return new BoundResultDto(
                hasChanged,
                hasChangedOtherBounds,
                usedImplicitConversions,
                implicitConversionProvider);
    }

    private boolean isNotSelfReference(String typeVariable, String refTypeVariable) {
        return !typeVariable.equals(refTypeVariable);
    }

    @Override
    public BoundResultDto addLowerTypeBound(String typeVariable, ITypeSymbol typeSymbol) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\".");
        }

        return addLowerTypeBoundAfterContainsCheck(typeVariable, typeSymbol);
    }

    private BoundResultDto addLowerTypeBoundAfterContainsCheck(String typeVariable, ITypeSymbol typeSymbol) {
        BoundResultDto checkUpperResult = checkUpperTypeBounds(typeVariable, typeSymbol);
        boolean hasChanged = false;

        ITypeSymbol newTypeSymbol = checkForAndRegisterConvertibleType(
                typeVariable, typeSymbol, typeVariablesWithLowerConvertible);

        if (newTypeSymbol != null) {
            hasChanged = addToLowerUnionTypeSymbol(typeVariable, typeSymbol);

            if (checkUpperResult.implicitConversionProvider != null) {
                narrowUpperTypeBound(typeVariable, checkUpperResult);
            }

            if (hasChanged && hasUpperRefBounds(typeVariable)) {
                for (String refTypeVariable : upperRefBounds.get(typeVariable)) {
                    addLowerTypeBoundAfterContainsCheck(refTypeVariable, typeSymbol);
                }
            }
        }
        return new BoundResultDto(
                hasChanged,
                checkUpperResult.hasChangedOtherBounds,
                checkUpperResult.usedImplicitConversion,
                checkUpperResult.implicitConversionProvider);
    }

    private void narrowUpperTypeBound(String typeVariable, BoundResultDto checkUpperResult) {
        IIntersectionTypeSymbol currentUpperTypeBounds = upperTypeBounds.remove(typeVariable);
        addUpperTypeBoundAfterContainsCheck(typeVariable, checkUpperResult.implicitConversionProvider, false);
        for (ITypeSymbol upperTypeBound : currentUpperTypeBounds.getTypeSymbols().values()) {
            try {
                addUpperTypeBoundAfterContainsCheck(typeVariable, upperTypeBound, false);
            } catch (IntersectionBoundException ex) {
                //that is fine, must be the type which required the implicit conversion,
                // we do not want to add it as upper bound (we narrow now)
            }
        }

        if (hasLowerRefBounds(typeVariable)) {
            for (String refTypeVariable : lowerRefBounds.get(typeVariable)) {
                narrowUpperTypeBound(refTypeVariable, checkUpperResult);
            }
        }
    }


    //Warning! start code duplication - very similar to checkLowerTypeBounds
    private BoundResultDto checkUpperTypeBounds(String typeVariable, ITypeSymbol newLowerType) {
        boolean usedImplicitConversion = false;
        ITypeSymbol implicitConversionProvider = null;
        boolean hasChangedConvertibleType = false;

        if (hasUpperTypeBounds(typeVariable)) {
            IIntersectionTypeSymbol upperTypeSymbol = upperTypeBounds.get(typeVariable);
            TypeHelperDto dto = typeHelper.isFirstSameOrSubTypeOfSecond(newLowerType, upperTypeSymbol, typeVariable);
            switch (dto.relation) {
                case HAS_COERCIVE_RELATION:
                    usedImplicitConversion = true;
                    if (dto.upperConstraints.containsKey(typeVariable)) {
                        List<ITypeSymbol> remove = dto.upperConstraints.remove(typeVariable);
                        if (remove.size() == 1) {
                            implicitConversionProvider = remove.get(0);
                        } else {
                            throw new UnsupportedOperationException("more than one conversion provider");
                        }
                    }
                    break;
                case HAS_NO_RELATION:
                    throw new UpperBoundException(
                            "The new lower type " + newLowerType.getAbsoluteName()
                                    + " is not the same or a subtype of " + upperTypeSymbol.getAbsoluteName(),
                            upperTypeSymbol,
                            newLowerType);
            }
            hasChangedConvertibleType = applyTypeParameterConstraints(dto);
        }
        return new BoundResultDto(false, hasChangedConvertibleType, usedImplicitConversion, implicitConversionProvider);
    }

    private boolean applyTypeParameterConstraints(TypeHelperDto dto) {
        boolean hasChanged = false;
        for (Map.Entry<String, List<ITypeSymbol>> entry : dto.lowerConstraints.entrySet()) {
            String typeParameter = entry.getKey();
            for (ITypeSymbol typeSymbol : entry.getValue()) {
                BoundResultDto resultDto = addLowerTypeBoundAfterContainsCheck(typeParameter, typeSymbol);
                hasChanged = hasChanged || resultDto.hasChanged;
            }
        }
        for (Map.Entry<String, List<ITypeSymbol>> entry : dto.upperConstraints.entrySet()) {
            String typeParameter = entry.getKey();
            for (ITypeSymbol typeSymbol : entry.getValue()) {
                BoundResultDto resultDto = addUpperTypeBoundAfterContainsCheck(typeParameter, typeSymbol);
                hasChanged = hasChanged || resultDto.hasChanged;
            }
        }
        return hasChanged;
    }
    //Warning! end code duplication - very similar to checkLowerTypeBounds

    /**
     * Removes convertible types with self references and registers others which are bound to this overload bindings
     * into given map.
     *
     * @return The typeSymbol which shall be added or null if it is a self ref (or a container which is empty after
     * removing self references)
     */
    private ITypeSymbol checkForAndRegisterConvertibleType(
            String typeVariable, ITypeSymbol typeSymbol, Map<String, Set<String>> typeVariablesWithConvertible) {
        ITypeSymbol nonConvertibleType = null;
        if (typeSymbol instanceof IContainerTypeSymbol) {
            IContainerTypeSymbol containerTypeSymbol = (IContainerTypeSymbol) typeSymbol;
            if (containerTypeSymbol.isFixed()) {
                nonConvertibleType = containerTypeSymbol;
            } else {
                Map<String, ITypeSymbol> typeSymbols = containerTypeSymbol.getTypeSymbols();
                Set<String> absoluteNames = new HashSet<>();
                for (Map.Entry<String, ITypeSymbol> entry : typeSymbols.entrySet()) {
                    ITypeSymbol innerTypeSymbol = entry.getValue();
                    ITypeSymbol nonConvertibleInnerType = checkForAndRegisterConvertibleType(
                            typeVariable, innerTypeSymbol, typeVariablesWithConvertible);
                    if (nonConvertibleInnerType == null) {
                        absoluteNames.add(entry.getKey());
                    }
                }
                for (String absoluteName : absoluteNames) {
                    containerTypeSymbol.remove(absoluteName);
                }
                int size = typeSymbols.size();
                if (size == 1) {
                    nonConvertibleType = typeSymbols.values().iterator().next();
                } else if (size > 1) {
                    nonConvertibleType = containerTypeSymbol;
                }
            }
        } else if (typeSymbol instanceof IConvertibleTypeSymbol) {
            IConvertibleTypeSymbol convertibleTypeSymbol = (IConvertibleTypeSymbol) typeSymbol;
            if (convertibleTypeSymbol.getOverloadBindings() == this) {
                String convertibleTypeVariable = convertibleTypeSymbol.getTypeVariable();
                if (!convertibleTypeVariable.equals(typeVariable)) {
                    nonConvertibleType = typeSymbol;
                    MapHelper.addToSetInMap(typeVariablesWithConvertible, typeVariable, convertibleTypeVariable);
                }
            } else {
                nonConvertibleType = typeSymbol;
            }
        } else {
            nonConvertibleType = typeSymbol;
        }
        return nonConvertibleType;
    }

    private boolean addToLowerUnionTypeSymbol(String typeVariable, ITypeSymbol typeSymbol) {
        boolean hasChanged;
        if (hasLowerTypeBounds(typeVariable)) {
            hasChanged = lowerTypeBounds.get(typeVariable).addTypeSymbol(typeSymbol);
        } else {
            IUnionTypeSymbol unionTypeSymbol = symbolFactory.createUnionTypeSymbol();
            hasChanged = unionTypeSymbol.addTypeSymbol(typeSymbol);
            lowerTypeBounds.put(typeVariable, unionTypeSymbol);
        }
        return hasChanged;
    }

    @Override
    public boolean hasLowerTypeBounds(String typeVariable) {
        return lowerTypeBounds.containsKey(typeVariable);
    }

    @Override
    public boolean hasUpperTypeBounds(String typeVariable) {
        return upperTypeBounds.containsKey(typeVariable);
    }

    @Override
    public boolean hasLowerRefBounds(String typeVariable) {
        return lowerRefBounds.containsKey(typeVariable) && !lowerRefBounds.get(typeVariable).isEmpty();
    }

    @Override
    public boolean hasUpperRefBounds(String typeVariable) {
        return upperRefBounds.containsKey(typeVariable) && !upperRefBounds.get(typeVariable).isEmpty();
    }

    @Override
    public BoundResultDto addUpperTypeBound(String typeVariable, ITypeSymbol typeSymbol) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("No variable has a binding for type variable \"" + typeVariable + "\".");
        }

        return addUpperTypeBoundAfterContainsCheck(typeVariable, typeSymbol);
    }

    private BoundResultDto addUpperTypeBoundAfterContainsCheck(String typeVariable, ITypeSymbol typeSymbol) {
        return addUpperTypeBoundAfterContainsCheck(typeVariable, typeSymbol, true);
    }

    private BoundResultDto addUpperTypeBoundAfterContainsCheck(
            String typeVariable, ITypeSymbol typeSymbol, boolean propagateToLower) {
        BoundResultDto lowerCheckResult = checkLowerTypeBounds(typeVariable, typeSymbol);
        boolean hasChanged = false;

        ITypeSymbol newTypeSymbol;
        if (lowerCheckResult.implicitConversionProvider == null) {
            newTypeSymbol = checkForAndRegisterConvertibleType(
                    typeVariable, typeSymbol, typeVariablesWithUpperConvertible);
        } else {
            newTypeSymbol = lowerCheckResult.implicitConversionProvider;
        }

        if (newTypeSymbol != null) {
            checkIfCanBeUsedInIntersectionWithOthers(typeVariable, newTypeSymbol);

            hasChanged = addToUpperIntersectionTypeSymbol(typeVariable, newTypeSymbol);

            if (!hasChanged && !hasLowerTypeBounds(typeVariable) && newTypeSymbol instanceof IConvertibleTypeSymbol) {
                IConvertibleTypeSymbol convertibleTypeSymbol = (IConvertibleTypeSymbol) newTypeSymbol;
                if (convertibleTypeSymbol.getOverloadBindings() == this) {
                    addLowerRefBound(convertibleTypeSymbol.getTypeVariable(), typeVariable, true);
                }
            }

            if (propagateToLower && hasChanged && hasLowerRefBounds(typeVariable)) {
                for (String refTypeVariable : lowerRefBounds.get(typeVariable)) {
                    addUpperTypeBoundAfterContainsCheck(refTypeVariable, newTypeSymbol);
                }
            }
        }

        return new BoundResultDto(
                hasChanged,
                lowerCheckResult.hasChangedOtherBounds,
                lowerCheckResult.usedImplicitConversion,
                lowerCheckResult.implicitConversionProvider);
    }

    private void checkIfCanBeUsedInIntersectionWithOthers(String typeVariable, ITypeSymbol typeSymbol) {
        if (hasUpperTypeBounds(typeVariable)) {
            IIntersectionTypeSymbol upperBound = upperTypeBounds.get(typeVariable);
            //if the current upper type is final or the current and the new both cannot be used in an intersection with
            // others which cannot be used in an intersection, then the current and the new need to be in the same
            // type hierarchy
            if ((typeSymbol.isFinal() || upperBound.isFinal()) && areNotInSameTypeHierarchy(typeSymbol, upperBound)) {
                throw new IntersectionBoundException(
                        "The upper bound " + upperBound.getAbsoluteName() + " is final and the new type "
                                + typeSymbol.getAbsoluteName() + " is not in the same type hierarchy.",
                        upperBound, typeSymbol);
            } else if (!typeSymbol.canBeUsedInIntersection()
                    && !upperBound.canBeUsedInIntersection()
                    && areNotInSameTypeHierarchy(typeSymbol, upperBound)) {
                throw new IntersectionBoundException(
                        "The upper bound " + upperBound.getAbsoluteName() + " already contained a concrete type "
                                + "and thus the new type " + typeSymbol.getAbsoluteName() + " cannot be added.",
                        upperBound, typeSymbol);
            }
        }
    }

    private boolean areNotInSameTypeHierarchy(ITypeSymbol typeSymbol, IIntersectionTypeSymbol upperBound) {
        boolean areNot = true;
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(typeSymbol, upperBound, false);
        if (result.relation != ERelation.HAS_NO_RELATION) {
            areNot = false;
        } else {
            result = typeHelper.isFirstSameOrSubTypeOfSecond(upperBound, typeSymbol, false);
            if (result.relation != ERelation.HAS_NO_RELATION) {
                areNot = false;
            }
        }
        return areNot;
    }

    //Warning! start code duplication - very similar to checkUpperTypeBounds
    private BoundResultDto checkLowerTypeBounds(String typeVariable, ITypeSymbol newUpperTypeBound) {
        boolean usedImplicitConversion = false;
        ITypeSymbol implicitConversionProvider = null;
        boolean hasChangedConvertibleType = false;

        if (hasLowerTypeBounds(typeVariable)) {
            IUnionTypeSymbol lowerTypeSymbol = lowerTypeBounds.get(typeVariable);
            TypeHelperDto dto = typeHelper.isFirstSameOrSubTypeOfSecond(
                    lowerTypeSymbol, newUpperTypeBound, typeVariable);
            switch (dto.relation) {
                case HAS_COERCIVE_RELATION:
                    usedImplicitConversion = true;
                    if (dto.upperConstraints.containsKey(typeVariable)) {
                        List<ITypeSymbol> remove = dto.upperConstraints.remove(typeVariable);
                        if (remove.size() == 1) {
                            implicitConversionProvider = remove.get(0);
                        } else {
                            throw new UnsupportedOperationException("more than one conversion provider");
                        }
                    }
                    break;
                case HAS_NO_RELATION:
                    throw new LowerBoundException(
                            "The new upper bound " + newUpperTypeBound.getAbsoluteName()
                                    + " is not the same or a parent type of the current lower bound "
                                    + lowerTypeSymbol.getAbsoluteName(),
                            lowerTypeSymbol, newUpperTypeBound);
            }
            hasChangedConvertibleType = applyTypeParameterConstraints(dto);
        }
        return new BoundResultDto(false, hasChangedConvertibleType, usedImplicitConversion, implicitConversionProvider);
    }
    //Warning! end code duplication - very similar to checkUpperTypeBounds

    private boolean addToUpperIntersectionTypeSymbol(String typeVariable, ITypeSymbol typeSymbol) {
        boolean hasChanged;
        if (hasUpperTypeBounds(typeVariable)) {
            hasChanged = upperTypeBounds.get(typeVariable).addTypeSymbol(typeSymbol);
        } else {
            IIntersectionTypeSymbol intersectionTypeSymbol = symbolFactory.createIntersectionTypeSymbol();
            hasChanged = intersectionTypeSymbol.addTypeSymbol(typeSymbol);
            upperTypeBounds.put(typeVariable, intersectionTypeSymbol);
        }
        return hasChanged;
    }

    @Override
    public boolean hasLowerBounds(String typeVariable) {
        return hasLowerTypeBounds(typeVariable) || hasLowerRefBounds(typeVariable);
    }

    @Override
    public boolean hasUpperBounds(String typeVariable) {
        return hasUpperTypeBounds(typeVariable) || hasUpperRefBounds(typeVariable);
    }

    @Override
    public IUnionTypeSymbol getLowerTypeBounds(String typeVariable) {
        return lowerTypeBounds.get(typeVariable);
    }

    @Override
    public IIntersectionTypeSymbol getUpperTypeBounds(String typeVariable) {
        return upperTypeBounds.get(typeVariable);
    }

    @Override
    public Set<String> getLowerRefBounds(String typeVariable) {
        return lowerRefBounds.get(typeVariable);
    }

    @Override
    public Set<String> getUpperRefBounds(String typeVariable) {
        return upperRefBounds.get(typeVariable);
    }

    @Override
    public Set<String> getLowerBoundConstraintIds(String typeVariable) {
        Set<String> ids = new HashSet<>();
        if (hasLowerTypeBounds(typeVariable)) {
            ids.addAll(lowerTypeBounds.get(typeVariable).getTypeSymbols().keySet());
        }
        if (hasLowerRefBounds(typeVariable)) {
            for (String refTypeVariable : lowerRefBounds.get(typeVariable)) {
                ids.add("@" + refTypeVariable);
            }
        }
        return ids;
    }

    @Override
    public Set<String> getUpperBoundConstraintIds(String typeVariable) {
        Set<String> ids = new HashSet<>();
        if (hasUpperTypeBounds(typeVariable)) {
            ids.addAll(upperTypeBounds.get(typeVariable).getTypeSymbols().keySet());
        }
        if (hasUpperRefBounds(typeVariable)) {
            for (String refTypeVariable : upperRefBounds.get(typeVariable)) {
                ids.add("@" + refTypeVariable);
            }
        }
        return ids;
    }

    @Override
    public OverloadApplicationDto getAppliedOverload(String variableId) {
        return appliedOverloads.get(variableId);
    }

    @Override
    public void setAppliedOverload(String variableId, OverloadApplicationDto overloadApplicationDto) {
        if (!variable2TypeVariable.containsKey(variableId)) {
            throw new IllegalArgumentException("variable with id " + variableId + " does not exist in this binding.");
        }

        if (appliedOverloads.containsKey(variableId)) {
            OverloadApplicationDto dto = appliedOverloads.get(variableId);
            if (dto.overload != null && dto.overload.hasConvertibleParameterTypes()) {
                --numberOfConvertibleApplications;
            }
        }

        appliedOverloads.put(variableId, overloadApplicationDto);

        if (overloadApplicationDto.overload != null
                && overloadApplicationDto.overload.hasConvertibleParameterTypes()) {
            ++numberOfConvertibleApplications;
        }
    }

    @Override
    public void fixType(String variableId) {
        if (!variable2TypeVariable.containsKey(variableId)) {
            throw new IllegalArgumentException("variable with id " + variableId + " does not exist in this binding.");
        }
        fixTypeAfterContainsCheck(variableId, true);
    }


    private void fixTypeAfterContainsCheck(String variableId, boolean isNotParameter) {
        //Warning! start code duplication, more or less same as in fixTypeParameter
        ITypeVariableReference reference = variable2TypeVariable.get(variableId);
        if (!reference.hasFixedType()) {
            String typeVariable = reference.getTypeVariable();
            fixTypeVariable(variableId, reference);
            fixTypeVariableType(isNotParameter, typeVariable);
            informBoundTypes(typeVariable);
        }
        //Warning! start code duplication, more or less same as in fixTypeParameter
    }

    private void fixTypeVariable(String variableId, ITypeVariableReference reference) {
        String typeVariable = reference.getTypeVariable();
        variable2TypeVariable.put(variableId, new FixedTypeVariableReference(reference));
        removeRefBounds(typeVariable);
    }

    private void fixTypeVariableType(boolean isNotParameter, String typeVariable) {
        //TODO TINS-407 - store fixed type only in lower bound

        //parameters should be hold as general as possible where local variables should be as specific as possible.
        //therefore we add the lower type bound to the upper type bound if it is not a parameter and vice versa
        if (isNotParameter && hasLowerTypeBounds(typeVariable)) {
            addToUpperIntersectionTypeSymbol(typeVariable, lowerTypeBounds.get(typeVariable));
        } else if (hasUpperTypeBounds(typeVariable)) {
            addToLowerUnionTypeSymbol(typeVariable, upperTypeBounds.get(typeVariable));
        } else if (!isNotParameter && hasLowerTypeBounds(typeVariable)) {
            //only need to add it if it is a parameter, otherwise we already did it above
            addToUpperIntersectionTypeSymbol(typeVariable, lowerTypeBounds.get(typeVariable));
        } else {
            //must be a parameter which is not involved in the function body at all
            addToLowerUnionTypeSymbol(typeVariable, mixedTypeSymbol);
            addToUpperIntersectionTypeSymbol(typeVariable, mixedTypeSymbol);
        }
    }


    @Override
    public void fixTypeParameter(String typeParameter) {
        // if only upper type bounds (no lower type bounds) were defined for the parameter,
        // then we need to propagate those to the upper refs (if there are any) before we fix all variables
        // belonging to the type variable of the parameter, otherwise they might turn out to be mixed (which
        // is less intuitive). see TINS-449 unused ad-hoc polymorphic parameters
        if (hasUpperRefBoundAndOnlyUpperTypeBound(typeParameter)) {
            IIntersectionTypeSymbol upperTypeBound = upperTypeBounds.get(typeParameter);
            for (String refTypeVariable : upperRefBounds.get(typeParameter)) {
                addToLowerUnionTypeSymbol(refTypeVariable, upperTypeBound);
            }
        }

        final boolean isNotParameter = false;
        boolean typeVariableFixed = false;
        for (String variableId : typeVariable2Variables.get(typeParameter)) {
            //Warning! start code duplication, more or less same as in fixTypeAfterContainsCheck
            ITypeVariableReference reference = variable2TypeVariable.get(variableId);
            if (!reference.hasFixedType()) {
                fixTypeVariable(variableId, reference);
                //no need to fix it multiple times, once is enough
                if (!typeVariableFixed) {
                    String typeVariable = reference.getTypeVariable();
                    fixTypeVariableType(isNotParameter, typeVariable);
                    typeVariableFixed = true;
                }
            }
            //Warning! end code duplication, more or less same as in fixTypeAfterContainsCheck
        }

        informBoundTypes(typeParameter);
    }

    private boolean hasUpperRefBoundAndOnlyUpperTypeBound(String parameterTypeVariable) {
        return hasUpperRefBounds(parameterTypeVariable)
                && !hasLowerTypeBounds(parameterTypeVariable)
                && hasUpperTypeBounds(parameterTypeVariable);
    }

    private void informBoundTypes(String typeParameter) {
        //inform bound parametric types that type variable was fixed
        if (typeVariable2BoundTypes.containsKey(typeParameter)) {
            for (IParametricType parametricTypeSymbol : typeVariable2BoundTypes.get(typeParameter)) {
                parametricTypeSymbol.fix(typeParameter);
            }
        }
    }

    @Override
    public void fixTypeParameters() {
        for (String typeParameter : typeVariable2BoundTypes.keySet()) {
            fixTypeParameter(typeParameter);
        }
    }

    @Override
    public Set<String> tryToFix(Set<String> parameterTypeVariables) {

        String returnTypeVariable = variable2TypeVariable.get(TinsPHPConstants.RETURN_VARIABLE_NAME).getTypeVariable();
        Map<String, Set<String>> typeVariablesToVisit = new HashMap<>(typeVariable2Variables);
        Set<String> typeParameters = new HashSet<>();
        Set<String> recursiveTypeParameters = new HashSet<>();
        Set<String> removeReturnTypeVariable = new HashSet<>();
        PropagationDto dto = new PropagationDto(
                returnTypeVariable,
                parameterTypeVariables,
                typeVariablesToVisit,
                typeParameters,
                recursiveTypeParameters,
                removeReturnTypeVariable);

        collectTypeParameters(dto);

        if (returnIsNotFixed(returnTypeVariable)) {
            propagateReturnTypeVariableToParameters(dto);
        } else {
            fixType(TinsPHPConstants.RETURN_VARIABLE_NAME);
        }

        boolean hasConstantReturn = propagateOrFixTypeParameters(dto);

        //in case of recursion
        removeUpperRefBounds(returnTypeVariable);

        returnTypeVariable = renameToRecursiveTypeParameters(dto);

        //if we had a recursive type parameter (hence return type variable changed), then we already did this
        if (returnTypeVariable.equals(dto.returnTypeVariable)) {
            Set<String> returnVariableLowerRefs = lowerRefBounds.get(dto.returnTypeVariable);
            for (String refTypeVariable : dto.removeReturnTypeVariable) {
                upperRefBounds.get(refTypeVariable).remove(dto.returnTypeVariable);
                returnVariableLowerRefs.remove(refTypeVariable);
            }

            if (hasConstantReturn) {
                removeRefBounds(returnTypeVariable);
            }
        }

        Map<String, String> variablesToRename = identifyVariablesToRename(dto);
        renameTypeVariables(variablesToRename);

        return dto.typeParameters;
    }

    private void collectTypeParameters(PropagationDto dto) {
        for (String parameterTypeVariable : dto.parameterTypeVariables) {
            //the type variables of parameters are potential type parameters as well,
            // they are removed in propagateOrFixParameters if the parameter is fixed
            dto.typeParameters.add(parameterTypeVariable);
            if (hasLowerTypeBounds(parameterTypeVariable)) {
                IUnionTypeSymbol containerType = lowerTypeBounds.get(parameterTypeVariable);
                searchForParametricTypes(dto, containerType);
            }

            if (hasUpperTypeBounds(parameterTypeVariable)) {
                IIntersectionTypeSymbol containerType = upperTypeBounds.get(parameterTypeVariable);
                searchForParametricTypes(dto, containerType);
            }
        }
    }

    private void searchForParametricTypes(PropagationDto dto, IContainerTypeSymbol containerType) {
        if (!containerType.isFixed()) {
            for (ITypeSymbol typeSymbol : containerType.getTypeSymbols().values()) {
                if (typeSymbol instanceof IContainerTypeSymbol) {
                    searchForParametricTypes(dto, (IContainerTypeSymbol) typeSymbol);
                } else if (typeSymbol instanceof IParametricTypeSymbol) {
                    IParametricTypeSymbol parametricTypeSymbol = (IParametricTypeSymbol) typeSymbol;
                    if (!parametricTypeSymbol.isFixed() && parametricTypeSymbol.getOverloadBindings() == this) {
                        dto.typeParameters.addAll(parametricTypeSymbol.getTypeParameters());
                    }
                }
            }
        }
    }

    private boolean returnIsNotFixed(String returnTypeVariable) {
        boolean isNotFixed = !hasLowerTypeBounds(returnTypeVariable) || !hasUpperTypeBounds(returnTypeVariable);
        if (!isNotFixed) {
            isNotFixed = !typeHelper.areSame(
                    lowerTypeBounds.get(returnTypeVariable), upperTypeBounds.get(returnTypeVariable));
        }
        return isNotFixed;
    }

    private void propagateReturnTypeVariableToParameters(final PropagationDto dto) {
        if (hasLowerRefBounds(dto.returnTypeVariable)) {
            for (String refTypeVariable : lowerRefBounds.get(dto.returnTypeVariable)) {
                boolean passedATypeParameter = false;
                if (!dto.typeParameters.contains(refTypeVariable)) {
                    //since normal type variables might be fixed we need to remove the return variable manually
                    dto.removeReturnTypeVariable.add(refTypeVariable);
                } else {
                    passedATypeParameter = true;
                }
                propagateReturnTypeVariableDownwardsToParameters(refTypeVariable, dto, passedATypeParameter);
            }
        }
    }

    private void propagateReturnTypeVariableDownwardsToParameters(
            final String refTypeVariable, final PropagationDto dto, boolean passedATypeParameter) {
        if (hasLowerRefBounds(refTypeVariable)) {
            for (String refRefTypeVariable : lowerRefBounds.get(refTypeVariable)) {
                boolean tmpPassedATypeParameter = passedATypeParameter;
                Set<String> refRefUpperRefBounds = upperRefBounds.get(refRefTypeVariable);
                if (!refRefUpperRefBounds.contains(dto.returnTypeVariable)) {
                    if (dto.typeParameters.contains(refRefTypeVariable)) {
                        //a type parameter which has the return type variable as upper bound only through another
                        // parameter can be removed from lower ref bounds of the return type variable
                        if (passedATypeParameter && !refRefUpperRefBounds.contains(dto.returnTypeVariable)) {
                            dto.removeReturnTypeVariable.add(refRefTypeVariable);
                        } else {
                            //since it might be added onto the list before above we remove it now since it has the
                            // return type variable as upper bound also via another path (without parameter)
                            dto.removeReturnTypeVariable.remove(refRefTypeVariable);
                        }

                        passedATypeParameter = true;
                        refRefUpperRefBounds.add(dto.returnTypeVariable);
                    }
                    propagateReturnTypeVariableDownwardsToParameters(refRefTypeVariable, dto, passedATypeParameter);
                }
                passedATypeParameter = tmpPassedATypeParameter;
            }
        }
    }

    private boolean propagateOrFixTypeParameters(final PropagationDto dto) {
        boolean hasConstantReturn = true;

        Iterator<String> iterator = dto.typeParameters.iterator();
        while (iterator.hasNext()) {
            String typeParameter = iterator.next();
            Set<String> parameterUpperRefBounds = upperRefBounds.get(typeParameter);
            if (doesContributeToTheReturnType(typeParameter, dto.returnTypeVariable)) {
                hasConstantReturn = false;

                Set<String> addToUpperRef = new HashSet<>();
                for (String refTypeVariable : parameterUpperRefBounds) {
                    if (!dto.typeParameters.contains(refTypeVariable)) {
                        propagateTypeParameterUpwards(refTypeVariable, typeParameter, addToUpperRef, dto);
                    }
                }

                for (String refTypeVariable : addToUpperRef) {
                    parameterUpperRefBounds.add(refTypeVariable);
                }

            } else {
                dto.removeReturnTypeVariable.remove(typeParameter);
                fixTypeParameter(typeParameter);
                iterator.remove();
            }
            dto.typeVariablesToVisit.remove(typeParameter);
        }
        return hasConstantReturn;
    }

    private boolean doesContributeToTheReturnType(String parameterTypeVariable, String returnTypeVariable) {
        boolean doesContribute = false;
        if (hasUpperRefBounds(parameterTypeVariable)) {
            doesContribute = upperRefBounds.get(parameterTypeVariable).contains(returnTypeVariable);
            if (doesContribute) {
                IIntersectionTypeSymbol upperTypeBound = upperTypeBounds.get(parameterTypeVariable);
                IUnionTypeSymbol lowerTypeBound = lowerTypeBounds.get(parameterTypeVariable);
                if (lowerTypeBound != null && upperTypeBound != null) {
                    doesContribute = !typeHelper.areSame(lowerTypeBound, upperTypeBound);
                }

                //When a function has multiple returns then the type parameter does not contribute to the return type
                // if the return type variable's lower type bound is a parent type of the type parameters' upper type
                // bound of the
                if (doesContribute && upperTypeBound != null && lowerTypeBounds.containsKey(returnTypeVariable)) {
                    IUnionTypeSymbol returnLowerTypeBound = lowerTypeBounds.get(returnTypeVariable);
                    TypeHelperDto dto = typeHelper.isFirstSameOrSubTypeOfSecond(
                            upperTypeBound, returnLowerTypeBound, false);
                    doesContribute = dto.relation == ERelation.HAS_NO_RELATION;
                }
            }
        }
        return doesContribute;
    }

    private void propagateTypeParameterUpwards(
            String refTypeVariable, String typeParameter, Set<String> addToUpperRef, PropagationDto dto) {

        if (hasUpperRefBounds(refTypeVariable)) {
            Set<String> refUpperRefBounds = upperRefBounds.get(refTypeVariable);
            for (String refRefTypeVariable : refUpperRefBounds) {
                Set<String> refRefLowerRefBounds = lowerRefBounds.get(refRefTypeVariable);
                //we remove non type parameters, they are no longer required
                if (!dto.typeParameters.contains(refTypeVariable)) {
                    refRefLowerRefBounds.remove(refTypeVariable);
                }
                if (!refRefLowerRefBounds.contains(typeParameter)) {
                    if (isNotSelfReference(refRefTypeVariable, typeParameter)) {
                        refRefLowerRefBounds.add(typeParameter);
                        addToUpperRef.add(refRefTypeVariable);
                        if (!dto.typeParameters.contains(refRefTypeVariable)) {
                            propagateTypeParameterUpwards(refRefTypeVariable, typeParameter, addToUpperRef, dto);
                        }
                    } else {
                        dto.recursiveTypeParameters.add(typeParameter);
                    }
                }
            }
        }
    }

    private void removeRefBounds(String typeVariable) {
        if (hasLowerRefBounds(typeVariable)) {
            for (String lowerRefTypeVariable : lowerRefBounds.remove(typeVariable)) {
                upperRefBounds.get(lowerRefTypeVariable).remove(typeVariable);
            }
        }
        removeUpperRefBounds(typeVariable);
    }

    private void removeUpperRefBounds(String typeVariable) {
        if (hasUpperRefBounds(typeVariable)) {
            for (String upperRefTypeVariable : upperRefBounds.remove(typeVariable)) {
                lowerRefBounds.get(upperRefTypeVariable).remove(typeVariable);
            }
        }
    }

    private Map<String, String> identifyVariablesToRename(PropagationDto dto) {
        Map<String, String> variablesToRename = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : dto.typeVariablesToVisit.entrySet()) {
            String typeVariable = entry.getKey();
            String parameterTypeVariable = tryToReduceToTypeParameter(typeVariable, dto);
            if (parameterTypeVariable != null) {
                variablesToRename.put(typeVariable, parameterTypeVariable);
            } else if (!hasLowerRefBounds(typeVariable)) {
                final boolean isNotParameter = true;
                for (String variableId : entry.getValue()) {
                    fixTypeAfterContainsCheck(variableId, isNotParameter);
                }
            }
        }
        return variablesToRename;
    }

    private String tryToReduceToTypeParameter(String typeVariable, PropagationDto dto) {
        String renameTo = null;
        if (hasLowerRefBounds(typeVariable)) {
            removeNonParameterLowerRefBounds(typeVariable, dto);
            //only remove upper ref if type variable has at least one type parameter as lower ref
            if (hasLowerRefBounds(typeVariable)) {
                upperRefBounds.remove(typeVariable);
            }

            String typeParameter = getTypeParameterIfSingleLowerRefBound(typeVariable, dto);
            if (typeParameter != null) {
                if (haveSameLowerTypeBound(typeVariable, typeParameter)) {
                    renameTo = typeParameter;
                }
            }
        }
        return renameTo;
    }

    private void removeNonParameterLowerRefBounds(String typeVariable, PropagationDto dto) {
        Iterator<String> iterator = lowerRefBounds.get(typeVariable).iterator();
        while (iterator.hasNext()) {
            String refTypeVariable = iterator.next();
            if (!dto.typeParameters.contains(refTypeVariable)) {
                iterator.remove();
                upperRefBounds.get(refTypeVariable).remove(typeVariable);
            }
        }
    }

    private String getTypeParameterIfSingleLowerRefBound(String typeVariable, PropagationDto dto) {
        String parameterTypeVariable = null;
        Set<String> refTypeVariables = lowerRefBounds.get(typeVariable);
        if (refTypeVariables.size() == 1) {
            String refTypeVariable = refTypeVariables.iterator().next();
            if (dto.typeParameters.contains(refTypeVariable)) {
                parameterTypeVariable = refTypeVariable;
            }
        }
        return parameterTypeVariable;
    }

    private boolean haveSameLowerTypeBound(String typeVariable, String parameterTypeVariable) {
        boolean canBeMerged;
        if (hasLowerTypeBounds(typeVariable)) {
            canBeMerged = hasLowerTypeBounds(parameterTypeVariable);
            if (canBeMerged) {
                canBeMerged = typeHelper.areSame(
                        lowerTypeBounds.get(typeVariable), lowerTypeBounds.get(parameterTypeVariable));
            }
        } else {
            // since the parameterTypeVariable is a lower ref bound of typeVariable it logically has also no
            // lower type bound and it can be merged
            canBeMerged = true;
        }
        return canBeMerged;
    }

    private void renameTypeVariables(Map<String, String> typeVariablesToRename) {
        for (Map.Entry<String, String> entry : typeVariablesToRename.entrySet()) {
            String typeVariable = entry.getKey();
            String parameterTypeVariable = entry.getValue();
            // need to remove the existing ref between typeVariable and parameterTypeVariable before we rename
            // otherwise we create inadvertently a self ref even though we do not have one
            lowerRefBounds.get(typeVariable).remove(parameterTypeVariable);
            upperRefBounds.get(parameterTypeVariable).remove(typeVariable);
            mergeFirstIntoSecondAfterContainsCheck(typeVariable, parameterTypeVariable, false);
        }
    }

    private String renameToRecursiveTypeParameters(PropagationDto dto) {
        String returnTypeVariable = dto.returnTypeVariable;
        boolean needToRemoveReturnTypeVariable = true;

        for (String typeParameter : dto.recursiveTypeParameters) {
            //could be already renamed by now
            if (hasUpperBounds(typeParameter)) {
                Set<String> parameterUpperRefs = upperRefBounds.get(typeParameter);
                boolean mergedOne = true;
                //
                while (mergedOne) {
                    mergedOne = false;
                    for (String typeVariable : parameterUpperRefs) {
                        boolean isReturnTypeVariable = typeVariable.equals(returnTypeVariable);
                        if (isReturnTypeVariable || hasReturnTypeVariableAsUpper(typeVariable, returnTypeVariable)) {
                            dto.typeVariablesToVisit.remove(typeVariable);
                            if (isReturnTypeVariable) {
                                if (needToRemoveReturnTypeVariable) {
                                    Set<String> returnVariableLowerRefs = lowerRefBounds.get(dto.returnTypeVariable);
                                    for (String refTypeVariable : dto.removeReturnTypeVariable) {
                                        if (hasUpperRefBounds(refTypeVariable)) {
                                            upperRefBounds.get(refTypeVariable).remove(dto.returnTypeVariable);
                                            returnVariableLowerRefs.remove(refTypeVariable);
                                        }
                                    }
                                    needToRemoveReturnTypeVariable = false;
                                }
                                returnTypeVariable = typeParameter;
                            }

                            //could be a type parameter hence remove it, does not matter if it was not,
                            // then it is not on the list (fast then using contains first)
                            dto.typeParameters.remove(typeVariable);

                            mergeFirstIntoSecondAfterContainsCheck(typeVariable, typeParameter, true);
                            mergedOne = true;

                            //need to break since parameterUpperRef might have changed
                            break;
                        }
                    }
                }
            }
        }
        return returnTypeVariable;
    }

    private boolean hasReturnTypeVariableAsUpper(String typeVariable, String returnTypeVariable) {
        return hasUpperRefBounds(typeVariable)
                && upperRefBounds.get(typeVariable).contains(returnTypeVariable);
    }

    @Override
    public void mergeFirstIntoSecond(String firstTypeVariable, String secondTypeVariable) {
        if (!typeVariable2Variables.containsKey(firstTypeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable "
                    + "\"" + firstTypeVariable + "\"");
        }

        if (!typeVariable2Variables.containsKey(secondTypeVariable)) {
            throw new IllegalArgumentException(
                    "no variable has a binding for type variable \"" + secondTypeVariable + "\"");
        }

        if (isNotSelfReference(firstTypeVariable, secondTypeVariable)) {
            mergeFirstIntoSecondAfterContainsCheck(firstTypeVariable, secondTypeVariable, false);
        }
    }

    @Override
    public void renameTypeVariable(String typeVariable, String newName) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\"");
        }

        if (typeVariable2Variables.containsKey(newName)) {
            throw new IllegalArgumentException("cannot use \"" + newName + "\" as name of the type parameter "
                    + "since it is already used as type variable in this binding");
        }

        if (isNotSelfReference(typeVariable, newName)) {
            renameTypeVariableAfterContainsCheck(typeVariable, newName);
        }
    }

    private void renameTypeVariableAfterContainsCheck(String typeVariable, String newName) {
        IUnionTypeSymbol lowerBound = lowerTypeBounds.remove(typeVariable);
        if (lowerBound != null) {
            lowerTypeBounds.put(newName, lowerBound);
        }
        IIntersectionTypeSymbol upperBound = upperTypeBounds.remove(typeVariable);
        if (upperBound != null) {
            upperTypeBounds.put(newName, upperBound);
        }

        if (hasLowerRefBounds(typeVariable)) {
            Set<String> refBounds = lowerRefBounds.remove(typeVariable);
            lowerRefBounds.put(newName, refBounds);
            for (String lowerRefTypeVariable : refBounds) {
                Set<String> refRefBounds = upperRefBounds.get(lowerRefTypeVariable);
                refRefBounds.remove(typeVariable);
                refRefBounds.add(newName);
            }
        }
        if (hasUpperRefBounds(typeVariable)) {
            Set<String> refBounds = upperRefBounds.remove(typeVariable);
            upperRefBounds.put(newName, refBounds);
            for (String upperRefTypeVariable : refBounds) {
                Set<String> refRefBounds = lowerRefBounds.get(upperRefTypeVariable);
                refRefBounds.remove(typeVariable);
                refRefBounds.add(newName);
            }
        }

        Set<String> variables = typeVariable2Variables.remove(typeVariable);
        typeVariable2Variables.put(newName, variables);
        for (String variableId : variables) {
            variable2TypeVariable.get(variableId).setTypeVariable(newName);
        }

        if (typeVariable2BoundTypes.containsKey(typeVariable)) {
            Set<IParametricType> boundTypes = typeVariable2BoundTypes.remove(typeVariable);
            typeVariable2BoundTypes.put(newName, boundTypes);
            for (IParametricType parametricType : boundTypes) {
                parametricType.renameTypeParameter(typeVariable, newName);
            }
        }
    }

    @Override
    public void bind(IParametricType parametricType, List<String> typeVariables) {
        int size = typeVariables.size();
        for (int i = 0; i < size; ++i) {
            String typeVariable = typeVariables.get(i);
            if (!typeVariable2Variables.containsKey(typeVariable)) {
                for (int j = 0; j < i; ++j) {
                    typeVariable2BoundTypes.get(typeVariables.get(j)).remove(parametricType);
                }
                throw new IllegalArgumentException("no variable has a binding for type variable"
                        + " \"" + typeVariable + "\"");
            }
            MapHelper.addToSetInMap(typeVariable2BoundTypes, typeVariable, parametricType);
        }

        try {
            parametricType.bindTo(this, typeVariables);
        } catch (IllegalArgumentException ex) {
            //remove registration before throwing the exception further
            for (String typeVariable : typeVariables) {
                typeVariable2BoundTypes.get(typeVariable).remove(parametricType);
            }
            throw ex;
        }
    }

    @Override
    public int getNumberOfConvertibleApplications() {
        return numberOfConvertibleApplications;
    }

    @Override
    public void changeToSoftTypingMode() {
        mode = EMode.SoftTyping;
    }

    @Override
    public void changeToModificationMode() {
        mode = EMode.Modification;
    }

    @Override
    public void changeToNormalMode() {
        mode = EMode.Normal;
    }

    @Override
    public boolean isNotInSoftTypingMode() {
        return mode != EMode.SoftTyping;
    }

    @Override
    public void setLowerTypeBound(String typeVariable, IUnionTypeSymbol lowerTypeBound) {
        if (mode != EMode.Modification) {
            throw new IllegalStateException("Can only set a lower type bound in moficiation mode");
        }

        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\"");
        }

        lowerTypeBounds.put(typeVariable, lowerTypeBound);
    }

    @Override
    public void setUpperTypeBound(String typeVariable, IIntersectionTypeSymbol upperTypeBound) {
        if (mode != EMode.Modification) {
            throw new IllegalStateException("Can only set an upper type bound in moficiation mode");
        }

        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\"");
        }

        upperTypeBounds.put(typeVariable, upperTypeBound);
    }

    @Override
    public void removeLowerTypeBound(String typeVariable) {
        if (mode != EMode.Modification) {
            throw new IllegalStateException("Can only remove a lower type bound in moficiation mode");
        }

        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\"");
        }

        lowerTypeBounds.remove(typeVariable);
    }

    @Override
    public void removeUpperTypeBound(String typeVariable) {
        if (mode != EMode.Modification) {
            throw new IllegalStateException("Can only remove an upper type bound in moficiation mode");
        }

        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\"");
        }

        upperTypeBounds.remove(typeVariable);
    }

    private void mergeFirstIntoSecondAfterContainsCheck(String typeVariable, String newTypeVariable,
            boolean avoidSelfRef) {
        if (hasLowerTypeBounds(typeVariable)) {
            addLowerTypeBoundAfterContainsCheck(newTypeVariable, lowerTypeBounds.remove(typeVariable));
        }
        if (hasUpperTypeBounds(typeVariable)) {
            addUpperTypeBoundAfterContainsCheck(newTypeVariable, upperTypeBounds.remove(typeVariable));
        }

        if (hasLowerRefBounds(typeVariable)) {
            for (String lowerRefTypeVariable : lowerRefBounds.remove(typeVariable)) {
                if (!avoidSelfRef || !newTypeVariable.equals(lowerRefTypeVariable)) {
                    addLowerRefBound(newTypeVariable, lowerRefTypeVariable, true);
                }
                upperRefBounds.get(lowerRefTypeVariable).remove(typeVariable);
            }
        }
        if (hasUpperRefBounds(typeVariable)) {
            for (String upperRefTypeVariable : upperRefBounds.remove(typeVariable)) {
                if (!avoidSelfRef || !newTypeVariable.equals(upperRefTypeVariable)) {
                    addLowerRefBound(upperRefTypeVariable, newTypeVariable, true);
                }
                lowerRefBounds.get(upperRefTypeVariable).remove(typeVariable);
            }
        }

        Set<String> variables = typeVariable2Variables.get(newTypeVariable);
        for (String variableId : typeVariable2Variables.remove(typeVariable)) {
            variable2TypeVariable.get(variableId).setTypeVariable(newTypeVariable);
            variables.add(variableId);
        }

        if (typeVariable2BoundTypes.containsKey(typeVariable)) {
            Set<IParametricType> boundTypes = typeVariable2BoundTypes.get(newTypeVariable);
            if (boundTypes == null) {
                boundTypes = new HashSet<>();
                typeVariable2BoundTypes.put(newTypeVariable, boundTypes);
            }

            for (IParametricType parametricType : typeVariable2BoundTypes.remove(typeVariable)) {
                parametricType.renameTypeParameter(typeVariable, newTypeVariable);
                boundTypes.add(parametricType);
            }
        }

        //we are not interested in registering convertible types again hence we provide a dummy map which is not used
        // afterwards instead of typeVariablesWithLowerConvertible and typeVariablesWithUpperConvertible
        Map<String, Set<String>> dummyTypesWithConvertible = new HashMap<>();
        if (hasFirstConvertibleToSecond(typeVariablesWithLowerConvertible, newTypeVariable, typeVariable)) {
            typeVariablesWithLowerConvertible.get(newTypeVariable).remove(typeVariable);
            ITypeSymbol newLowerBound = checkForAndRegisterConvertibleType(
                    newTypeVariable, lowerTypeBounds.get(newTypeVariable), dummyTypesWithConvertible);
            if (newLowerBound == null) {
                lowerTypeBounds.remove(newTypeVariable);
            }
        }

        if (hasFirstConvertibleToSecond(typeVariablesWithUpperConvertible, newTypeVariable, typeVariable)) {
            typeVariablesWithUpperConvertible.get(newTypeVariable).remove(typeVariable);
            ITypeSymbol newUpperBound = checkForAndRegisterConvertibleType(
                    newTypeVariable, upperTypeBounds.get(newTypeVariable), dummyTypesWithConvertible);
            if (newUpperBound == null) {
                upperTypeBounds.remove(newTypeVariable);
            }
        }
    }

    private boolean hasFirstConvertibleToSecond(Map<String, Set<String>> typeVariablesWithConvertible,
            String firstTypeVariable, String secondTypeVariable) {
        return typeVariablesWithConvertible.containsKey(firstTypeVariable)
                && typeVariablesWithConvertible.get(firstTypeVariable).contains(secondTypeVariable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean isNotFirst = false;
        for (Map.Entry<String, ITypeVariableReference> entry : variable2TypeVariable.entrySet()) {
            if (isNotFirst) {
                sb.append(", ");
            } else {
                isNotFirst = true;
            }
            sb.append(entry.getKey()).append(":");
            String typeVariable = entry.getValue().getTypeVariable();
            sb.append(typeVariable)
                    .append("<")
                    .append(getLowerBoundConstraintIds(typeVariable).toString())
                    .append(",")
                    .append(getUpperBoundConstraintIds(typeVariable).toString())
                    .append(">");
            if (entry.getValue().hasFixedType()) {
                sb.append("#");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private enum EMode
    {
        Normal,
        SoftTyping,
        Modification
    }
}
