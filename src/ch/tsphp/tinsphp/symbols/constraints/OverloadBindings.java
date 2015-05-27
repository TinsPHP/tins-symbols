/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.TinsPHPConstants;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IntersectionBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.LowerBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.UpperBoundException;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.MapHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OverloadBindings implements IOverloadBindings
{
    private final ISymbolFactory symbolFactory;
    private final ITypeHelper typeHelper;
    private final ITypeSymbol mixedTypeSymbol;

    private final Map<String, IUnionTypeSymbol> lowerTypeBounds;
    private final Map<String, IIntersectionTypeSymbol> upperTypeBounds;
    private final Map<String, Set<String>> upperRefBounds;
    private final Map<String, Set<String>> lowerRefBounds;
    private final Map<String, ITypeVariableReference> variable2TypeVariable;
    private final Map<String, Set<String>> typeVariable2Variables;
    private final Map<String, IFunctionType> appliedOverloads;

    private int count = 1;

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
    }

    public OverloadBindings(OverloadBindings bindings) {
        symbolFactory = bindings.symbolFactory;
        typeHelper = bindings.typeHelper;
        mixedTypeSymbol = bindings.mixedTypeSymbol;

        count = bindings.count;

        lowerTypeBounds = new HashMap<>(bindings.lowerTypeBounds.size());
        for (Map.Entry<String, IUnionTypeSymbol> entry : bindings.lowerTypeBounds.entrySet()) {
            lowerTypeBounds.put(entry.getKey(), entry.getValue().copy());
        }
        upperTypeBounds = new HashMap<>(bindings.upperTypeBounds.size());
        for (Map.Entry<String, IIntersectionTypeSymbol> entry : bindings.upperTypeBounds.entrySet()) {
            upperTypeBounds.put(entry.getKey(), entry.getValue().copy());
        }

        lowerRefBounds = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : bindings.lowerRefBounds.entrySet()) {
            lowerRefBounds.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        upperRefBounds = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : bindings.upperRefBounds.entrySet()) {
            upperRefBounds.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        variable2TypeVariable = new HashMap<>(bindings.variable2TypeVariable.size());
        for (Map.Entry<String, ITypeVariableReference> entry : bindings.variable2TypeVariable.entrySet()) {
            ITypeVariableReference reference = entry.getValue();
            TypeVariableReference typeVariableReference = new TypeVariableReference(reference.getTypeVariable());
            ITypeVariableReference copy = typeVariableReference;
            if (reference.hasFixedType()) {
                copy = new FixedTypeVariableReference(typeVariableReference);
            }
            variable2TypeVariable.put(entry.getKey(), copy);
        }

        typeVariable2Variables = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : bindings.typeVariable2Variables.entrySet()) {
            typeVariable2Variables.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        appliedOverloads = new HashMap<>(bindings.appliedOverloads);
    }

    @Override
    public TypeVariableReference getNextTypeVariable() {
        return new TypeVariableReference("T" + count++);
    }

    @Override
    public void addVariable(String variableId, ITypeVariableReference reference) {
        if (variable2TypeVariable.containsKey(variableId)) {
            throw new IllegalArgumentException(
                    "variable with id " + variableId + " was already added to this binding.");
        }
        variable2TypeVariable.put(variableId, reference);
        addToSetInMap(typeVariable2Variables, reference.getTypeVariable(), variableId);
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
    public boolean addLowerRefBound(String typeVariable, ITypeVariableReference reference) {
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

    private boolean addLowerRefBound(String typeVariable, String refTypeVariable, boolean hasNotFixedType) {
        boolean hasChanged = false;

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

            // First, the upper bound of refTypeVariable needs to be same or a subtype of typeVariable's upper bound in
            // order that we can use the refTypeVariable instead of typeVariable in a function call. Hence we add the
            // upper bound of typeVariable to refTypeVariable's upper bound. If refTypeVariable is not yet the same
            // or a subtype, then either the newly added upper bound will specialise the upper bound of the
            // refTypeVariable or will lead to a BoundException. ...
            boolean changed;
            if (hasUpperTypeBounds(typeVariable)) {
                changed = addUpperTypeBoundAfterContainsCheck(refTypeVariable, upperTypeBounds.get(typeVariable));
                hasChanged = hasChanged || changed;
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
                changed = addLowerTypeBoundAfterContainsCheck(typeVariable, lowerTypeBounds.get(refTypeVariable));
                hasChanged = hasChanged || changed;
            }
        }

        return hasChanged;
    }

    private boolean isNotSelfReference(String typeVariable, String refTypeVariable) {
        return !typeVariable.equals(refTypeVariable);
    }

    @Override
    public boolean addLowerTypeBound(String typeVariable, ITypeSymbol typeSymbol) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\".");
        }

        return addLowerTypeBoundAfterContainsCheck(typeVariable, typeSymbol);
    }

    private boolean addLowerTypeBoundAfterContainsCheck(String typeVariable, ITypeSymbol typeSymbol) {
        checkUpperTypeBounds(typeVariable, typeSymbol);

        boolean hasChanged = addToLowerUnionTypeSymbol(typeVariable, typeSymbol);

        if (hasChanged && hasUpperRefBounds(typeVariable)) {
            for (String refTypeVariable : upperRefBounds.get(typeVariable)) {
                addLowerTypeBoundAfterContainsCheck(refTypeVariable, typeSymbol);
            }
        }
        return hasChanged;
    }

    private void checkUpperTypeBounds(String typeVariable, ITypeSymbol newLowerType) {
        if (hasUpperTypeBounds(typeVariable)) {
            IIntersectionTypeSymbol upperTypeSymbol = upperTypeBounds.get(typeVariable);
            if (!typeHelper.isFirstSameOrSubTypeOfSecond(newLowerType, upperTypeSymbol)) {
                throw new UpperBoundException("The new lower type " + newLowerType.getAbsoluteName() + " is not the "
                        + "same or a subtype of " + upperTypeSymbol.getAbsoluteName(),
                        upperTypeSymbol,
                        newLowerType);
            }
        }
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
    public boolean addUpperTypeBound(String typeVariable, ITypeSymbol typeSymbol) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("No variable has a binding for type variable \"" + typeVariable + "\".");
        }

        return addUpperTypeBoundAfterContainsCheck(typeVariable, typeSymbol);
    }

    private boolean addUpperTypeBoundAfterContainsCheck(String typeVariable, ITypeSymbol typeSymbol) {
        checkLowerTypeBounds(typeVariable, typeSymbol);


        boolean hasChanged = false;
        if (isNotConvertibleTypeWithSelfRef(typeVariable, typeSymbol)) {

            checkIfCanBeUsedInIntersectionWithOthers(typeVariable, typeSymbol);

            hasChanged = addToUpperIntersectionTypeSymbol(typeVariable, typeSymbol);

            if (hasChanged && hasLowerRefBounds(typeVariable)) {
                for (String refTypeVariable : lowerRefBounds.get(typeVariable)) {
                    addUpperTypeBoundAfterContainsCheck(refTypeVariable, typeSymbol);
                }
            }
        }

        return hasChanged;
    }

    private boolean isNotConvertibleTypeWithSelfRef(String typeVariable, ITypeSymbol typeSymbol) {
        boolean isNot = true;
        if (typeSymbol instanceof IContainerTypeSymbol) {
            Collection<ITypeSymbol> typeSymbols = ((IContainerTypeSymbol) typeSymbol).getTypeSymbols().values();
            isNot = isNotOneConvertibleTypeWithSelfRef(typeVariable, typeSymbols);
        } else if (typeSymbol instanceof IConvertibleTypeSymbol) {
            IConvertibleTypeSymbol convertibleTypeSymbol = (IConvertibleTypeSymbol) typeSymbol;
            isNot = convertibleTypeSymbol.getOverloadBindings() != this
                    || !convertibleTypeSymbol.getTypeVariable().equals(typeVariable);
        }
        return isNot;
    }

    private boolean isNotOneConvertibleTypeWithSelfRef(
            String typeVariable, Collection<ITypeSymbol> typeSymbols) {
        boolean isNotOne = true;
        for (ITypeSymbol typeSymbol : typeSymbols) {
            boolean isNot = isNotConvertibleTypeWithSelfRef(typeVariable, typeSymbol);
            if (!isNot) {
                isNotOne = false;
                break;
            }
        }
        return isNotOne;
    }


    private void checkIfCanBeUsedInIntersectionWithOthers(String typeVariable, ITypeSymbol typeSymbol) {
        if (!typeSymbol.canBeUsedInIntersection() && hasUpperTypeBounds(typeVariable)) {
            IIntersectionTypeSymbol upperBound = upperTypeBounds.get(typeVariable);
            //only need to check if we already have a type in the upper bound which cannot be used in an
            // intersection

            // along with others which cannot be used
            if (!upperBound.canBeUsedInIntersection()) {
                if (areNotInSameTypeHierarchy(typeSymbol, upperBound)) {
                    throw new IntersectionBoundException(
                            "The upper bound " + upperBound.getAbsoluteName() + " already contained a concrete type "
                                    + "and thus the new type " + typeSymbol.getAbsoluteName() + " cannot be added.",
                            upperBound, typeSymbol);
                }
            }
        }
    }

    private boolean areNotInSameTypeHierarchy(ITypeSymbol typeSymbol,
            IIntersectionTypeSymbol upperBound) {
        return !typeHelper.isFirstSameOrSubTypeOfSecond(typeSymbol, upperBound)
                && !typeHelper.isFirstSameOrSubTypeOfSecond(upperBound, typeSymbol);
    }

    private void checkLowerTypeBounds(String typeVariable, ITypeSymbol newUpperTypeBound) {
        if (hasLowerTypeBounds(typeVariable)) {
            IUnionTypeSymbol lowerTypeSymbol = lowerTypeBounds.get(typeVariable);
            if (!typeHelper.isFirstSameOrSubTypeOfSecond(lowerTypeSymbol, newUpperTypeBound)) {
                throw new LowerBoundException(
                        "The new upper bound " + newUpperTypeBound.getAbsoluteName() + " is not the same or a parent "
                                + "type of the current lower bound " + lowerTypeSymbol.getAbsoluteName(),
                        lowerTypeSymbol, newUpperTypeBound);
            }
        }
    }

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
    public IFunctionType getAppliedOverload(String variableId) {
        return appliedOverloads.get(variableId);
    }

    @Override
    public void setAppliedOverload(String variableId, IFunctionType overload) {
        if (!variable2TypeVariable.containsKey(variableId)) {
            throw new IllegalArgumentException("variable with id " + variableId + " does not exist in this binding.");
        }
        appliedOverloads.put(variableId, overload);
    }

    @Override
    public void fixType(String variableId) {
        if (!variable2TypeVariable.containsKey(variableId)) {
            throw new IllegalArgumentException("variable with id " + variableId + " does not exist in this binding.");
        }
        fixTypeAfterContainsCheck(variableId, true);
    }

    private void fixTypeAfterContainsCheck(String variableId, boolean isNotParameter) {
        //Warning! start code duplication, more or less same as in propagateOrFixParameters
        ITypeVariableReference reference = variable2TypeVariable.get(variableId);
        if (!reference.hasFixedType()) {
            String typeVariable = reference.getTypeVariable();
            fixTypeVariable(variableId, reference);
            fixTypeVariableType(isNotParameter, typeVariable);
        }
        //Warning! start code duplication, more or less same as in propagateOrFixParameters
    }

    private void fixTypeVariable(String variableId, ITypeVariableReference reference) {
        String typeVariable = reference.getTypeVariable();
        variable2TypeVariable.put(variableId, new FixedTypeVariableReference((TypeVariableReference) reference));
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
    public void tryToFix(Set<String> parameterTypeVariables) {

        Set<String> removeReturnTypeVariable = new HashSet<>();
        String returnTypeVariable = variable2TypeVariable.get(TinsPHPConstants.RETURN_VARIABLE_NAME).getTypeVariable();
        propagateReturnVariableToParameters(returnTypeVariable, parameterTypeVariables, removeReturnTypeVariable);

        Map<String, Set<String>> typeVariablesToVisit = new HashMap<>(typeVariable2Variables);
        Set<String> recursiveParameterTypeVariables = new HashSet<>();
        boolean hasConstantReturn = propagateOrFixParameters(
                returnTypeVariable, parameterTypeVariables, typeVariablesToVisit, recursiveParameterTypeVariables);

        //in case of recursion
        removeUpperRefBounds(returnTypeVariable);

        if (hasConstantReturn) {
            removeRefBounds(returnTypeVariable);
        }
        for (String refTypeVariable : removeReturnTypeVariable) {
            upperRefBounds.get(refTypeVariable).remove(returnTypeVariable);
        }

        Map<String, String> variablesToRename = identifyVariablesToRename(parameterTypeVariables, typeVariablesToVisit);
        renameTypeVariables(variablesToRename);

        renameRecursiveParameters(recursiveParameterTypeVariables);

        //upper ref bounds are no longer needed
        upperRefBounds.clear();
    }

    private void propagateReturnVariableToParameters(
            String returnTypeVariable, Set<String> parameterTypeVariables, Set<String> removeReturnTypeVariable) {
        if (hasLowerRefBounds(returnTypeVariable)) {
            for (String refTypeVariable : lowerRefBounds.get(returnTypeVariable)) {
                if (!parameterTypeVariables.contains(refTypeVariable)) {
                    //since non-parameters might be fixed we need to remove the return variable manually
                    removeReturnTypeVariable.add(refTypeVariable);
                }
                propagateTypeVariableDownwardsToParameters(
                        refTypeVariable, returnTypeVariable, parameterTypeVariables);
            }
        }
    }

    private void propagateTypeVariableDownwardsToParameters(
            String refTypeVariable,
            String returnTypeVariable,
            Set<String> parameterTypeVariables) {

        if (hasLowerRefBounds(refTypeVariable)) {
            for (String refRefTypeVariable : lowerRefBounds.get(refTypeVariable)) {
                Set<String> refRefUpperRefBounds = upperRefBounds.get(refRefTypeVariable);
                if (!refRefUpperRefBounds.contains(returnTypeVariable)) {
                    if (parameterTypeVariables.contains(refRefTypeVariable)) {
                        refRefUpperRefBounds.add(returnTypeVariable);
                    }
                    propagateTypeVariableDownwardsToParameters(
                            refRefTypeVariable, returnTypeVariable, parameterTypeVariables);
                }

            }
        }
    }

    private boolean propagateOrFixParameters(
            String returnTypeVariable,
            Set<String> parameterTypeVariables,
            Map<String, Set<String>> typeVariablesToVisit,
            Set<String> recursiveParameters) {

        boolean hasConstantReturn = true;
        for (String parameterTypeVariable : parameterTypeVariables) {
            Set<String> parameterUpperRefBounds = upperRefBounds.get(parameterTypeVariable);
            if (hasReturnTypeVariableAsUpperAndNotFixedType(parameterTypeVariable, returnTypeVariable)) {
                hasConstantReturn = false;
                for (String refTypeVariable : parameterUpperRefBounds) {
                    if (!parameterTypeVariables.contains(refTypeVariable)) {
                        propagateTypeVariableUpwards(
                                refTypeVariable, parameterTypeVariable, parameterTypeVariables, recursiveParameters);
                    }
                }
            } else {
                // if only upper type bounds (no lower type bounds) were defined for the parameter,
                // then we need to propagate those to the upper refs (if there are any) before we fix all variables
                // belonging to the type variable of the parameter, otherwise they might turn out to be mixed (which
                // is less intuitive). see TINS-449 unused ad-hoc polymorphic parameters
                if (hasUpperRefBoundAndOnlyUpperTypeBound(parameterTypeVariable)) {
                    IIntersectionTypeSymbol upperTypeBound = upperTypeBounds.get(parameterTypeVariable);
                    for (String refTypeVariable : parameterUpperRefBounds) {
                        addToLowerUnionTypeSymbol(refTypeVariable, upperTypeBound);
                    }
                }

                final boolean isNotParameter = false;
                boolean typeVariableFixed = false;
                for (String variableId : typeVariable2Variables.get(parameterTypeVariable)) {
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
            }
            typeVariablesToVisit.remove(parameterTypeVariable);
        }
        return hasConstantReturn;
    }


    private boolean hasReturnTypeVariableAsUpperAndNotFixedType(String parameterTypeVariable,
            String returnTypeVariable) {
        boolean has = false;
        if (hasUpperRefBounds(parameterTypeVariable)) {
            has = upperRefBounds.get(parameterTypeVariable).contains(returnTypeVariable);
            if (has) {
                IUnionTypeSymbol upperTypeBound = lowerTypeBounds.get(parameterTypeVariable);
                IIntersectionTypeSymbol lowerTypeBound = upperTypeBounds.get(parameterTypeVariable);
                if (lowerTypeBound != null && upperTypeBound != null) {
                    has = !typeHelper.areSame(lowerTypeBound, upperTypeBound);
                }
            }
        }
        return has;
    }

    private boolean hasUpperRefBoundAndOnlyUpperTypeBound(String parameterTypeVariable) {
        return hasUpperRefBounds(parameterTypeVariable)
                && !hasLowerTypeBounds(parameterTypeVariable)
                && hasUpperTypeBounds(parameterTypeVariable);
    }


    private void propagateTypeVariableUpwards(
            String refTypeVariable,
            String parameterTypeVariable,
            Set<String> parameterTypeVariables,
            Set<String> recursiveParameters) {
        if (hasUpperRefBounds(refTypeVariable)) {
            Set<String> refUpperRefBounds = upperRefBounds.get(refTypeVariable);
            for (String refRefTypeVariable : refUpperRefBounds) {
                Set<String> refRefLowerRefBounds = lowerRefBounds.get(refRefTypeVariable);
                refRefLowerRefBounds.remove(refTypeVariable);
                if (!refRefLowerRefBounds.contains(parameterTypeVariable)) {
                    if (isNotSelfReference(refRefTypeVariable, parameterTypeVariable)) {
                        refRefLowerRefBounds.add(parameterTypeVariable);
                        if (!parameterTypeVariables.contains(refRefTypeVariable)) {
                            propagateTypeVariableUpwards(
                                    refRefTypeVariable,
                                    parameterTypeVariable,
                                    parameterTypeVariables,
                                    recursiveParameters);
                        }
                    } else {
                        recursiveParameters.add(parameterTypeVariable);
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

    private Map<String, String> identifyVariablesToRename(
            Set<String> parameterTypeVariables, Map<String, Set<String>> typeVariablesToVisit) {
        Map<String, String> variablesToRename = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : typeVariablesToVisit.entrySet()) {
            String typeVariable = entry.getKey();
            String parameterTypeVariable = tryToReduceToParameter(
                    typeVariable, parameterTypeVariables);
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

    private String tryToReduceToParameter(String typeVariable, Set<String> parameterTypeVariables) {
        String renameTo = null;
        if (hasLowerRefBounds(typeVariable)) {
            removeNonParameterLowerRefBounds(typeVariable, parameterTypeVariables);

            String parameterTypeVariable = getParameterIfSingleLowerRefBound(typeVariable, parameterTypeVariables);
            if (parameterTypeVariable != null) {
                upperRefBounds.remove(typeVariable);
                if (haveSameLowerTypeBound(typeVariable, parameterTypeVariable)) {
                    renameTo = parameterTypeVariable;
                }
            }
        }
        return renameTo;
    }

    private void removeNonParameterLowerRefBounds(String typeVariable, Set<String> parameterTypeVariables) {
        Iterator<String> iterator = lowerRefBounds.get(typeVariable).iterator();
        while (iterator.hasNext()) {
            String refTypeVariable = iterator.next();
            if (!parameterTypeVariables.contains(refTypeVariable)) {
                iterator.remove();
                upperRefBounds.get(refTypeVariable).remove(typeVariable);
            }
        }
    }

    private String getParameterIfSingleLowerRefBound(String typeVariable, Set<String> parameterTypeVariables) {
        String parameterTypeVariable = null;
        Set<String> refTypeVariables = lowerRefBounds.get(typeVariable);
        if (refTypeVariables.size() == 1) {
            String refTypeVariable = refTypeVariables.iterator().next();
            if (parameterTypeVariables.contains(refTypeVariable)) {
                parameterTypeVariable = refTypeVariable;
            }
        }
        return parameterTypeVariable;
    }

    private boolean haveSameLowerTypeBound(String typeVariable, String parameterTypeVariable) {
        boolean canBeUnified;
        if (hasLowerTypeBounds(typeVariable)) {
            canBeUnified = hasLowerTypeBounds(parameterTypeVariable);
            if (canBeUnified) {
                canBeUnified = typeHelper.areSame(
                        lowerTypeBounds.get(typeVariable), lowerTypeBounds.get(parameterTypeVariable));
            }
        } else {
            // since the parameterTypeVariable is a lower ref bound of typeVariable it logically has also no
            // lower type bound and it can be unified
            canBeUnified = true;
        }
        return canBeUnified;
    }

    private void renameTypeVariables(Map<String, String> typeVariablesToRename) {
        for (Map.Entry<String, String> entry : typeVariablesToRename.entrySet()) {
            String typeVariable = entry.getKey();
            String parameterTypeVariable = entry.getValue();
            // need to remove the existing ref between typeVariable and parameterTypeVariable before we rename
            // otherwise we create inadvertently a self ref even though we do not have one
            lowerRefBounds.get(typeVariable).remove(parameterTypeVariable);
            renameTypeVariableAfterContainsCheck(typeVariable, parameterTypeVariable);
        }
    }

    private void renameRecursiveParameters(Set<String> recursiveParameterTypeVariables) {
        for (String parameterTypeVariable : recursiveParameterTypeVariables) {
            //could be already renamed by now
            if (hasLowerRefBounds(parameterTypeVariable)) {
                // typeVariablesToRemove is required since using an iterator does not work -- renaming could add
                // further ref bounds (lower and upper)
                Set<String> typeVariablesToRemove = new HashSet<>();
                Set<String> parameterLowerRefs = lowerRefBounds.get(parameterTypeVariable);
                for (String typeVariable : parameterLowerRefs) {
                    if (hasLowerRefBounds(typeVariable)) {
                        Set<String> typeVariableLowerRefBounds = lowerRefBounds.get(typeVariable);
                        if (typeVariableLowerRefBounds.contains(parameterTypeVariable)) {
                            // need to remove the existing ref before we rename otherwise we create a self ref (and
                            // cause a ConcurrentModificationException)
                            typeVariableLowerRefBounds.remove(parameterTypeVariable);
                            renameTypeVariableAfterContainsCheck(typeVariable, parameterTypeVariable);
                            typeVariablesToRemove.add(typeVariable);
                        }
                    }
                }

                for (String typeVariable : typeVariablesToRemove) {
                    //remove all lower otherwise we keep the cyclic ref
                    parameterLowerRefs.remove(typeVariable);
                }
                //remove potential self ref
                parameterLowerRefs.remove(parameterTypeVariable);
            }
        }
    }

    @Override
    public void renameTypeVariable(String typeVariable, String newTypeVariable) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\"");
        }

        if (!typeVariable2Variables.containsKey(newTypeVariable)) {
            throw new IllegalArgumentException(
                    "no variable has a binding for type variable \"" + newTypeVariable + "\"");
        }

        if (isNotSelfReference(typeVariable, newTypeVariable)) {
            renameTypeVariableAfterContainsCheck(typeVariable, newTypeVariable);
        }
    }

    private void renameTypeVariableAfterContainsCheck(String typeVariable, String newTypeVariable) {
        if (hasLowerTypeBounds(typeVariable)) {
            addLowerTypeBoundAfterContainsCheck(newTypeVariable, lowerTypeBounds.remove(typeVariable));
        }
        if (hasUpperTypeBounds(typeVariable)) {
            addUpperTypeBoundAfterContainsCheck(newTypeVariable, upperTypeBounds.remove(typeVariable));
        }

        if (hasLowerRefBounds(typeVariable)) {
            for (String lowerRefTypeVariable : lowerRefBounds.remove(typeVariable)) {
                addLowerRefBound(newTypeVariable, lowerRefTypeVariable, true);
                upperRefBounds.get(lowerRefTypeVariable).remove(typeVariable);
            }
        }

        if (hasUpperRefBounds(typeVariable)) {
            for (String upperRefTypeVariable : upperRefBounds.remove(typeVariable)) {
                addLowerRefBound(upperRefTypeVariable, newTypeVariable, true);
                lowerRefBounds.get(upperRefTypeVariable).remove(typeVariable);
            }
        }

        Set<String> variables = typeVariable2Variables.get(newTypeVariable);
        for (String variableId : typeVariable2Variables.remove(typeVariable)) {
            variable2TypeVariable.get(variableId).setTypeVariable(newTypeVariable);
            variables.add(variableId);
        }
    }


    private void addToSetInMap(Map<String, Set<String>> map, String key, String value) {
        Set<String> set;
        if (map.containsKey(key)) {
            set = map.get(key);
        } else {
            set = new HashSet<>();
            map.put(key, set);
        }
        set.add(value);
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

}
