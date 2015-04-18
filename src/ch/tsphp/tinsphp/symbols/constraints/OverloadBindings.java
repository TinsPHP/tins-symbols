/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OverloadBindings implements IOverloadBindings
{
    private final ISymbolFactory symbolFactory;
    private final IOverloadResolver overloadResolver;

    private final Map<String, IUnionTypeSymbol> lowerTypeBounds;
    private final Map<String, IIntersectionTypeSymbol> upperTypeBounds;
    private final Map<String, Set<String>> upperRefBounds;
    private final Map<String, Set<String>> lowerRefBounds;
    private final Map<String, ITypeVariableConstraint> variable2TypeVariable;
    private final Map<String, Set<String>> typeVariable2Variables;

    private int count = 1;

    public OverloadBindings(ISymbolFactory theSymbolFactory, IOverloadResolver theOverloadResolver) {
        symbolFactory = theSymbolFactory;
        overloadResolver = theOverloadResolver;

        lowerTypeBounds = new HashMap<>();
        upperTypeBounds = new HashMap<>();
        lowerRefBounds = new HashMap<>();
        upperRefBounds = new HashMap<>();
        variable2TypeVariable = new HashMap<>();
        typeVariable2Variables = new HashMap<>();
    }

    public OverloadBindings(
            ISymbolFactory theSymbolFactory,
            IOverloadResolver theOverloadResolver,
            OverloadBindings bindings) {
        symbolFactory = theSymbolFactory;
        overloadResolver = theOverloadResolver;


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
        for (Map.Entry<String, ITypeVariableConstraint> entry : bindings.variable2TypeVariable.entrySet()) {
            ITypeVariableConstraint constraint = entry.getValue();
            TypeVariableConstraint typeVariableConstraint = new TypeVariableConstraint(constraint.getTypeVariable());
            ITypeVariableConstraint copy = typeVariableConstraint;
            if (constraint.hasFixedType()) {
                copy = new FixedTypeVariableConstraint(typeVariableConstraint);
            }
            variable2TypeVariable.put(entry.getKey(), copy);
        }

        typeVariable2Variables = new HashMap<>(bindings.typeVariable2Variables);
    }

    @Override
    public TypeVariableConstraint getNextTypeVariable() {
        return new TypeVariableConstraint("T" + count++);
    }

    @Override
    public void addVariable(String variableId, ITypeVariableConstraint constraint) {
        if (variable2TypeVariable.containsKey(variableId)) {
            throw new IllegalArgumentException(
                    "variable with id " + variableId + " was already added to this binding.");
        }
        variable2TypeVariable.put(variableId, constraint);
        addToSetInMap(typeVariable2Variables, constraint.getTypeVariable(), variableId);
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
    public ITypeVariableConstraint getTypeVariableConstraint(String variableId) {
        return variable2TypeVariable.get(variableId);
    }

    @Override
    public void addLowerRefBound(String typeVariable, ITypeVariableConstraint refTypeVariableConstraint) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\".");
        }

        String refTypeVariable = refTypeVariableConstraint.getTypeVariable();

        if (!typeVariable2Variables.containsKey(refTypeVariable)) {
            throw new IllegalArgumentException(
                    "no variable has a binding for type variable \"" + refTypeVariable + "\".");
        }

        boolean hasNotFixedType = !refTypeVariableConstraint.hasFixedType();
        addLowerRefBound(typeVariable, refTypeVariable, hasNotFixedType);
    }

    private void addLowerRefBound(String typeVariable, String refTypeVariable, boolean hasNotFixedType) {
        // no need to actually add the dependency if it has a fixed type (then it is enough that we transfer the
        // type bounds)
        if (hasNotFixedType) {
            addToSetInMap(lowerRefBounds, typeVariable, refTypeVariable);
            addToSetInMap(upperRefBounds, refTypeVariable, typeVariable);
        }


        if (isNotSelfReference(typeVariable, refTypeVariable)) {

            // The refTypeVariable needs to be the same or a subtype of typeVariable or we can narrow bounds in order
            // that this property holds.

            // First, the upper bound of refTypeVariable needs to be same or a subtype of typeVariable's upper bound in
            // order that we can use the refTypeVariable instead of typeVariable in a function call. Hence we add the
            // upper bound of typeVariable to refTypeVariable's upper bound. If refTypeVariable is not yet the same
            // or a subtype, then either the newly added upper bound will specialise the upper bound of the
            // refTypeVariable or will lead to a BoundException. ...
            if (hasUpperTypeBounds(typeVariable)) {
                addUpperTypeBoundAfterContainsCheck(refTypeVariable, upperTypeBounds.get(typeVariable));
            }

            // ... Furthermore, typeVariable logically needs to be able to hold all types refTypeVariable can hold. Thus
            // the lower bound of typeVariable need to be the same or a parent type of refTypeVariable. Therefore we add
            // the lower bound of refTypeVariable to the lower bound of typeVariable. If typeVariable's lower bound is
            // not already the same or a parent type, then either the newly added lower bound will generalise the lower
            // bound of typeVariable or will lead to a BoundException.
            if (hasLowerTypeBounds(refTypeVariable)) {
                addLowerTypeBoundAfterContainsCheck(typeVariable, lowerTypeBounds.get(refTypeVariable));
            }
        }
    }

    private boolean isNotSelfReference(String typeVariable, String refTypeVariable) {
        return !typeVariable.equals(refTypeVariable);
    }

    @Override
    public void addLowerTypeBound(String typeVariable, ITypeSymbol typeSymbol) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\".");
        }

        addLowerTypeBoundAfterContainsCheck(typeVariable, typeSymbol);
    }

    private void addLowerTypeBoundAfterContainsCheck(String typeVariable, ITypeSymbol typeSymbol) {
        checkUpperTypeBounds(typeVariable, typeSymbol);

        boolean hasChanged = addToLowerUnionTypeSymbol(typeVariable, typeSymbol);

        if (hasChanged && hasUpperRefBounds(typeVariable)) {
            for (String refTypeVariable : upperRefBounds.get(typeVariable)) {
                addLowerTypeBoundAfterContainsCheck(refTypeVariable, typeSymbol);
            }
        }
    }

    private void checkUpperTypeBounds(String typeVariable, ITypeSymbol newLowerType) {
        if (hasUpperTypeBounds(typeVariable)) {
            IIntersectionTypeSymbol upperTypeSymbol = upperTypeBounds.get(typeVariable);
            if (!overloadResolver.isFirstSameOrSubTypeOfSecond(newLowerType, upperTypeSymbol)) {
                throw new UpperBoundException(
                        "The newLowerType is not the same or a subtype of the upper bound.",
                        upperTypeSymbol, newLowerType);
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
        return lowerRefBounds.containsKey(typeVariable);
    }

    @Override
    public boolean hasUpperRefBounds(String typeVariable) {
        return upperRefBounds.containsKey(typeVariable);
    }

    @Override
    public void addUpperTypeBound(String typeVariable, ITypeSymbol typeSymbol) {
        if (!typeVariable2Variables.containsKey(typeVariable)) {
            throw new IllegalArgumentException("no variable has a binding for type variable \"" + typeVariable + "\".");
        }

        addUpperTypeBoundAfterContainsCheck(typeVariable, typeSymbol);
    }

    private void addUpperTypeBoundAfterContainsCheck(String typeVariable, ITypeSymbol typeSymbol) {
        checkLowerTypeBounds(typeVariable, typeSymbol);

        if (hasUpperTypeBounds(typeVariable)) {
            IIntersectionTypeSymbol upperBound = upperTypeBounds.get(typeVariable);
            if (areNotInSameTypeHierarchyAndOneCannotBeUsedInIntersection(typeSymbol, upperBound)) {
                throw new IntersectionBoundException(
                        "The newLowerType or the upper bound cannot be used in an intersection.",
                        upperBound, typeSymbol);
            }
        }

        boolean hasChanged = addToUpperIntersectionTypeSymbol(typeVariable, typeSymbol);

        if (hasChanged && hasLowerRefBounds(typeVariable)) {
            for (String refTypeVariable : lowerRefBounds.get(typeVariable)) {
                addUpperTypeBoundAfterContainsCheck(refTypeVariable, typeSymbol);
            }
        }
    }

    private boolean areNotInSameTypeHierarchyAndOneCannotBeUsedInIntersection(ITypeSymbol typeSymbol,
            IIntersectionTypeSymbol upperBound) {
        return !overloadResolver.isFirstSameOrSubTypeOfSecond(typeSymbol, upperBound)
                && !overloadResolver.isFirstSameOrSubTypeOfSecond(upperBound, typeSymbol)
                && (!typeSymbol.canBeUsedInIntersection() || !upperBound.canBeUsedInIntersection());
    }

    private void checkLowerTypeBounds(String typeVariable, ITypeSymbol newUpperTypeBound) {
        if (hasLowerTypeBounds(typeVariable)) {
            IUnionTypeSymbol lowerTypeSymbol = lowerTypeBounds.get(typeVariable);
            if (!overloadResolver.isFirstSameOrSubTypeOfSecond(lowerTypeSymbol, newUpperTypeBound)) {
                throw new LowerBoundException("newUpperTypeBound is not a parent type of the lower bound.",
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
        return lowerTypeBounds.containsKey(typeVariable) || lowerRefBounds.containsKey(typeVariable);
    }

    @Override
    public boolean hasUpperBounds(String typeVariable) {
        return upperTypeBounds.containsKey(typeVariable) || upperRefBounds.containsKey(typeVariable);
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
    public void fixType(String variableId) {
        if (!variable2TypeVariable.containsKey(variableId)) {
            throw new IllegalArgumentException("variable with id " + variableId + " does not exist in this binding.");
        }
        fixTypeAfterContainsCheck(variableId);
    }

    private void fixTypeAfterContainsCheck(String variableId) {
        ITypeVariableConstraint constraint = variable2TypeVariable.get(variableId);
        if (!constraint.hasFixedType()) {
            variable2TypeVariable.put(variableId, new FixedTypeVariableConstraint((TypeVariableConstraint) constraint));
        }
    }

    @Override
    public void tryToFix(Set<String> typeVariablesToIgnore) {

        propagateParametersUpwards(typeVariablesToIgnore);

        for (String variableId : variable2TypeVariable.keySet()) {
            ITypeVariableConstraint constraint = variable2TypeVariable.get(variableId);
            if (!constraint.hasFixedType()) {
                String typeVariable = constraint.getTypeVariable();
                if (!typeVariablesToIgnore.contains(typeVariable)) {
                    fixTypeAfterContainsCheck(variableId);
                    lowerRefBounds.remove(typeVariable);
                    upperRefBounds.remove(typeVariable);
                }
            }
        }
    }

    private void propagateParametersUpwards(Set<String> parameterTypeVariables) {
        for (String parameterTypeVariable : parameterTypeVariables) {
            for (String refTypeVariable : upperRefBounds.get(parameterTypeVariable)) {
                propagateTypeVariableUpwards(refTypeVariable, parameterTypeVariable);
            }
        }
    }

    private void propagateTypeVariableUpwards(String refTypeVariable, String parameterTypeVariable) {
        if (hasUpperRefBounds(refTypeVariable)) {
            for (String refRefTypeVariable : upperRefBounds.get(refTypeVariable)) {
                Set<String> refRefLowerBounds = lowerRefBounds.get(refRefTypeVariable);
                if (!refRefLowerBounds.contains(parameterTypeVariable)) {
                    refRefLowerBounds.remove(refTypeVariable);
                    refRefLowerBounds.add(parameterTypeVariable);
                    propagateTypeVariableUpwards(refRefTypeVariable, parameterTypeVariable);
                }
            }
        }
    }

//    private void resolveDependencies(Set<String> typeVariablesToIgnore, Set<String> variablesToVisit) {
//        for (String typeVariableToIgnore : typeVariablesToIgnore) {
//            Set<String> refTypeVariables = lowerRefBounds.get(typeVariableToIgnore);
//            do {
//                for (String refTypeVariable : refTypeVariables) {
//                    if (!typeVariablesToIgnore.contains(refTypeVariable)) {
//                        refTypeVariables.remove(refTypeVariable);
//                        upperRefBounds.get(refTypeVariable).remove(typeVariableToIgnore);
//                        renameTypeVariableAfterContainsCheck(refTypeVariable, typeVariableToIgnore);
//                    }
//                }
//            } while (!refTypeVariables.isEmpty() && !typeVariablesToIgnore.containsAll(refTypeVariables));
//
//            if (refTypeVariables.isEmpty()) {
//                for (String variableId : typeVariable2Variables.get(typeVariableToIgnore)) {
//                    fixTypeAfterContainsCheck(variableId);
//                    variablesToVisit.remove(variableId);
//                }
//            }
//        }
//    }
//
//    @Override
//    public boolean tryToFixType(String variableId) {
//        ITypeVariableConstraint constraint = variable2TypeVariable.get(variableId);
//        String typeVariable = constraint.getTypeVariable();
//        boolean canTypeBeFixed = constraint.hasFixedType();
//
//        if (!hasLowerRefBounds(typeVariable)) {
//            canTypeBeFixed = false;
//            if (!hasLowerBounds(typeVariable) && hasUpperBounds(typeVariable)) {
//                canTypeBeFixed = true;
//                constraint = new FixedTypeVariableConstraint((TypeVariableConstraint) constraint);
//                variable2TypeVariable.put(variableId, constraint);
//                updateUpperBoundDependencies(constraint);
//            }
//        }
//        return canTypeBeFixed;
//    }

//    private void updateUpperBoundDependencies(ITypeVariableConstraint constraint) {
//        String constraintId = constraint.getId();
//        if (hasUpperRefBounds(constraintId)) {
//            // there are other type variables which have this type variable as lower bound.
//            // remove it from their lower bounds and add the upper bounds of this type variable as lower bounds
//            // to the other type variables instead
//            String typeVariable = constraint.getTypeVariable();
//            for (String refTypeVariable : upperRefBounds.get(constraintId)) {
//                Map<String, IConstraint> refLowerBounds = lowerBounds.get(refTypeVariable);
//                refLowerBounds.remove(constraintId);
//                Map<String, IConstraint> typeVariableUpperBounds = upperBounds.get(typeVariable);
//                IConstraint newLowerBound = null;
//                if (typeVariableUpperBounds.size() == 1) {
//                    newLowerBound = typeVariableUpperBounds.values().iterator().next();
//                } else {
//                    //TODO rstoll TINS-369 intersection type
//                    //need to add it as intersection type
//                }
//                refLowerBounds.put(newLowerBound.getId(), newLowerBound);
//            }
//        }
//    }

//    @Override
//    public void resolveDependencies(String variableId, Set<String> parameterTypeVariables) {
//        String typeVariable = variable2TypeVariable.get(variableId).getTypeVariable();
//        if (hasLowerBounds(typeVariable)) {
//
//            Map<String, IConstraint> constraintsToAdd = new HashMap<>();
//            Map<String, IConstraint> typeVariableLowerBounds = lowerBounds.get(typeVariable);
//            Iterator<Map.Entry<String, IConstraint>> iterator = typeVariableLowerBounds.entrySet().iterator();
//            while (iterator.hasNext()) {
//                IConstraint constraint = iterator.next().getValue();
//                if (constraint instanceof ITypeVariableConstraint) {
//                    ITypeVariableConstraint refConstraint = (ITypeVariableConstraint) constraint;
//                    if (isNotSelfRefOrParameter(refConstraint, typeVariable, parameterTypeVariables)) {
//
//                    }
//                }
//            }
//
//            typeVariableLowerBounds.putAll(constraintsToAdd);
//        }
//    }


//    private boolean isNotSelfRefOrParameter(
//            ITypeVariableConstraint refConstraint, String typeVariable, Set<String> parameterIds) {
//        return isNotSelfReference(refConstraint.getTypeVariable(), typeVariable)
//                && !parameterIds.contains(refConstraint.getId());
//    }

//    private Map<String, IConstraint> resolveLowerBoundDependency(
//            ITypeVariableConstraint refConstraint, Set<String> parameterIds) {
//        Map<String, IConstraint> constraintsToAdd = new HashMap<>();
//
//        // no need to use addLowerBound, transfer within dependencies is implicitly within bounds.
//        // Further, it is not necessary to check whether refTypeVariable has a lower bound. It needs to have one (at
//        // least implicitly null) - otherwise it is a parameter which needs to be in parameterIds
//        String refTypeVariable = refConstraint.getTypeVariable();
//        Map<String, IConstraint> refLowerBounds = lowerBounds.get(refTypeVariable);
//        Iterator<Map.Entry<String, IConstraint>> iterator = refLowerBounds.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, IConstraint> entry = iterator.next();
//            IConstraint lowerBound = entry.getValue();
//            if (!(lowerBound instanceof ITypeVariableConstraint)) {
//                constraintsToAdd.put(entry.getKey(), lowerBound);
//            } else {
//                ITypeVariableConstraint refRefConstraint = (ITypeVariableConstraint) lowerBound;
//                if (isNotSelfReference(refTypeVariable, refRefConstraint.getTypeVariable())) {
//                    if (!parameterIds.contains(refRefConstraint.getId())) {
//                        Map<String, IConstraint> constraints = resolveLowerBoundDependency(
//                                refRefConstraint, parameterIds);
//                        constraintsToAdd.putAll(constraints);
//                        iterator.remove();
//                    } else {
//                        constraintsToAdd.put(entry.getKey(), refRefConstraint);
//                    }
//                } else {
//                    iterator.remove();
//                }
//            }
//        }
//        refLowerBounds.putAll(constraintsToAdd);
//        for (Map.Entry<String, IConstraint> entry : constraintsToAdd.entrySet()) {
//            refLowerBounds.put(entry.getKey(), entry.getValue());
//        }
//
//        return constraintsToAdd;
//    }

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
            addToLowerUnionTypeSymbol(newTypeVariable, lowerTypeBounds.remove(typeVariable));
        }
        if (hasUpperTypeBounds(typeVariable)) {
            addToUpperIntersectionTypeSymbol(newTypeVariable, upperTypeBounds.remove(typeVariable));
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean isNotFirst = false;
        for (Map.Entry<String, ITypeVariableConstraint> entry : variable2TypeVariable.entrySet()) {
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
