/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.common.utils.MapHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OverloadBindings implements IOverloadBindings
{
    private final IOverloadResolver overloadResolver;

    private final Map<String, Map<String, IConstraint>> lowerBounds = new HashMap<>();
    private final Map<String, Map<String, IConstraint>> upperBounds = new HashMap<>();
    private final Map<String, ITypeVariableConstraint> variable2TypeVariable = new HashMap<>();
    private final Map<String, List<String>> upperBoundDependencies = new HashMap<>();
    private int count = 1;

    public OverloadBindings(IOverloadResolver theOverloadResolver) {
        overloadResolver = theOverloadResolver;
    }

    public OverloadBindings(
            IOverloadResolver theOverloadResolver,
            OverloadBindings bindings) {
        overloadResolver = theOverloadResolver;
        count = bindings.count;

        Map<String, ITypeVariableConstraint> mapping = new HashMap<>();
        for (Map.Entry<String, ITypeVariableConstraint> entry : bindings.variable2TypeVariable.entrySet()) {
            ITypeVariableConstraint constraint = createOrGetConstraint(mapping, entry.getValue());
            variable2TypeVariable.put(entry.getKey(), constraint);
        }

        for (Map.Entry<String, Map<String, IConstraint>> entry : bindings.lowerBounds.entrySet()) {
            Map<String, IConstraint> constraints = new HashMap<>(entry.getValue().size());
            for (Map.Entry<String, IConstraint> typeVariableEntry : entry.getValue().entrySet()) {
                IConstraint constraint = typeVariableEntry.getValue();
                String constraintId = constraint.getId();
                if (!mapping.containsKey(constraintId)) {
                    constraints.put(constraintId, constraint);
                } else {
                    constraints.put(constraintId, mapping.get(constraintId));
                }
            }
            lowerBounds.put(entry.getKey(), constraints);
        }
        for (Map.Entry<String, Map<String, IConstraint>> entry : bindings.upperBounds.entrySet()) {
            upperBounds.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
    }

    private ITypeVariableConstraint createOrGetConstraint(
            Map<String, ITypeVariableConstraint> mapping,
            ITypeVariableConstraint typeVariableConstraint) {
        ITypeVariableConstraint constraint;
        String constraintId = typeVariableConstraint.getId();
        boolean containsKey = mapping.containsKey(constraintId);
        if (containsKey) {
            constraint = mapping.get(constraintId);
        } else {
            constraint = new TypeVariableConstraint(typeVariableConstraint.getTypeVariable());
            mapping.put(constraintId, constraint);
        }

        if (typeVariableConstraint.hasFixedType()) {
            constraint = new FixedTypeVariableConstraint((TypeVariableConstraint) constraint);
        }
        return constraint;
    }

    @Override
    public Map<String, ITypeVariableConstraint> getVariable2TypeVariable() {
        return variable2TypeVariable;
    }

    @Override
    public boolean tryToFixateType(String variableId) {
        ITypeVariableConstraint constraint = variable2TypeVariable.get(variableId);
        String typeVariable = constraint.getTypeVariable();
        boolean canTypeBeFixed;
        if (constraint.hasFixedType()) {
            canTypeBeFixed = true;
            if (hasLowerBounds(typeVariable)) {
                Map<String, IConstraint> typeVariableLowerBounds = lowerBounds.get(typeVariable);
                String constraintId = constraint.getId();
                if (typeVariableLowerBounds.containsKey(constraintId)) {
                    //remove self ref, no longer needed
                    if (typeVariableLowerBounds.size() == 1) {
                        //only lower bound, can remove the whole list
                        lowerBounds.remove(typeVariable);
                    } else {
                        typeVariableLowerBounds.remove(constraintId);
                    }
                }
            }
        } else {
            canTypeBeFixed = false;
            if (!hasLowerBounds(typeVariable) && hasUpperBounds(typeVariable)) {
                canTypeBeFixed = true;
                constraint = new FixedTypeVariableConstraint((TypeVariableConstraint) constraint);
                variable2TypeVariable.put(variableId, constraint);
                updateUpperBoundDependencies(constraint);
            }
        }
        return canTypeBeFixed;
    }

    private void updateUpperBoundDependencies(ITypeVariableConstraint constraint) {
        String constraintId = constraint.getId();
        if (upperBoundDependencies.containsKey(constraintId)) {
            // there are other type variables which have this type variable as lower bound.
            // remove it from their lower bounds and add the upper bounds of this type variable as lower bounds
            // to the other type variables instead
            String typeVariable = constraint.getTypeVariable();
            for (String refTypeVariable : upperBoundDependencies.get(constraintId)) {
                Map<String, IConstraint> refLowerBounds = lowerBounds.get(refTypeVariable);
                refLowerBounds.remove(constraintId);
                Map<String, IConstraint> typeVariableUpperBounds = upperBounds.get(typeVariable);
                IConstraint newLowerBound = null;
                if (typeVariableUpperBounds.size() == 1) {
                    newLowerBound = typeVariableUpperBounds.values().iterator().next();
                } else {
                    //TODO rstoll TINS-369 intersection type
                    //need to add it as intersection type
                }
                refLowerBounds.put(newLowerBound.getId(), newLowerBound);
            }
        }
    }

    @Override
    public void resolveDependencies(String variableId, Set<String> parameterIds) {
        //TODO that is wrong :(
        //see last test in FunctionDeclarationBinding
        //bindings should be:
        // y < a < x < b < rtn
        // $x : T1  \ i|T2 < T1 < n
        // $a : T2  \ T2 < n
        // $y : T2
        // $b : T1
        // rtn : T1

        String typeVariable = variable2TypeVariable.get(variableId).getTypeVariable();
        if (hasLowerBounds(typeVariable)) {

            Map<String, IConstraint> constraintsToAdd = new HashMap<>();
            Map<String, IConstraint> typeVariableLowerBounds = lowerBounds.get(typeVariable);
            Iterator<Map.Entry<String, IConstraint>> iterator = typeVariableLowerBounds.entrySet().iterator();
            while (iterator.hasNext()) {
                IConstraint constraint = iterator.next().getValue();
                if (constraint instanceof ITypeVariableConstraint) {
                    ITypeVariableConstraint refConstraint = (ITypeVariableConstraint) constraint;
                    if (isNotSelfRefOrParameter(refConstraint, typeVariable, parameterIds)) {
                        Map<String, IConstraint> constraints = resolveLowerBoundDependency(
                                refConstraint, parameterIds);
                        constraintsToAdd.putAll(constraints);
                        iterator.remove();
                    }
                }
            }

            typeVariableLowerBounds.putAll(constraintsToAdd);
        }
    }

    private boolean isNotSelfRefOrParameter(
            ITypeVariableConstraint refConstraint, String typeVariable, Set<String> parameterIds) {
        return isNotSelfReference(refConstraint.getTypeVariable(), typeVariable)
                && !parameterIds.contains(refConstraint.getId());
    }

    private Map<String, IConstraint> resolveLowerBoundDependency(
            ITypeVariableConstraint refConstraint, Set<String> parameterIds) {
        Map<String, IConstraint> constraintsToAdd = new HashMap<>();

        // no need to use addLowerBound, transfer within dependencies is implicitly within bounds.
        // Further, it is not necessary to check whether refTypeVariable has a lower bound. It needs to have one (at
        // least implicitly null) - otherwise it is a parameter which needs to be in parameterIds
        String refTypeVariable = refConstraint.getTypeVariable();
        Map<String, IConstraint> refLowerBounds = lowerBounds.get(refTypeVariable);
        Iterator<Map.Entry<String, IConstraint>> iterator = refLowerBounds.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, IConstraint> entry = iterator.next();
            IConstraint lowerBound = entry.getValue();
            if (!(lowerBound instanceof ITypeVariableConstraint)) {
                constraintsToAdd.put(entry.getKey(), lowerBound);
            } else {
                ITypeVariableConstraint refRefConstraint = (ITypeVariableConstraint) lowerBound;
                if (isNotSelfReference(refTypeVariable, refRefConstraint.getTypeVariable())) {
                    if (!parameterIds.contains(refRefConstraint.getId())) {
                        Map<String, IConstraint> constraints = resolveLowerBoundDependency(
                                refRefConstraint, parameterIds);
                        constraintsToAdd.putAll(constraints);
                        iterator.remove();
                    } else {
                        constraintsToAdd.put(entry.getKey(), refRefConstraint);
                    }
                } else {
                    iterator.remove();
                }
            }
        }
        refLowerBounds.putAll(constraintsToAdd);
        for (Map.Entry<String, IConstraint> entry : constraintsToAdd.entrySet()) {
            refLowerBounds.put(entry.getKey(), entry.getValue());
        }

        return constraintsToAdd;
    }

    @Override
    public void renameTypeVariable(ITypeVariableConstraint typeVariableConstraint, String newName) {
        String typeVariable = typeVariableConstraint.getTypeVariable();
        transferConstraintsFromTo(typeVariable, newName);

        String oldConstraintId = typeVariableConstraint.getId();
        typeVariableConstraint.setTypeVariable(newName);
        if (upperBoundDependencies.containsKey(oldConstraintId)) {
            String newConstraintId = typeVariableConstraint.getId();
            for (String refTypeVariable : upperBoundDependencies.get(oldConstraintId)) {
                Map<String, IConstraint> refLowerBounds = lowerBounds.get(refTypeVariable);
                IConstraint lowerBound = refLowerBounds.remove(oldConstraintId);
                //no need to use addLowerBound, is only a renaming, bound does not change
                refLowerBounds.put(newConstraintId, lowerBound);
            }
            upperBoundDependencies.remove(oldConstraintId);
        }
    }

    private void transferConstraintsFromTo(String rhs, String lhs) {
        if (hasUpperBounds(rhs)) {
            Map<String, IConstraint> rhsUpperBounds = upperBounds.remove(rhs);
            for (IConstraint upperBound : rhsUpperBounds.values()) {
                addUpperBound(lhs, upperBound);
            }
        }
        if (hasLowerBounds(rhs)) {
            Map<String, IConstraint> rhsLowerBounds = lowerBounds.remove(rhs);
            for (IConstraint upperBound : rhsLowerBounds.values()) {
                addLowerBound(lhs, upperBound);
            }
        }
    }


    @Override
    public TypeVariableConstraint getNextTypeVariable() {
        return new TypeVariableConstraint("T" + count++);
    }

    @Override
    public void addLowerBound(String typeVariable, IConstraint newLowerBoundConstraint) {
        if (isNotAlreadyLowerBound(typeVariable, newLowerBoundConstraint)) {
            if (newLowerBoundConstraint instanceof TypeConstraint) {
                checkUpperBounds(typeVariable, newLowerBoundConstraint);
                addToMapMap(lowerBounds, typeVariable, newLowerBoundConstraint);
            } else if (newLowerBoundConstraint instanceof ITypeVariableConstraint) {
                ITypeVariableConstraint typeVariableConstraint = (ITypeVariableConstraint) newLowerBoundConstraint;
                if (typeVariableConstraint.hasFixedType()) {
                    transferLowerBoundOfConstant(typeVariable, typeVariableConstraint);
                } else {
                    checkReferenceTypeVariable(typeVariable, typeVariableConstraint);
                    //we also add a dependency in order that fixate types is more efficient
                    MapHelper.addToListMap(upperBoundDependencies, newLowerBoundConstraint.getId(), typeVariable);
                    addToMapMap(lowerBounds, typeVariable, newLowerBoundConstraint);
                }
            } else {
                throw new UnsupportedOperationException(newLowerBoundConstraint.getClass().getName()
                        + " is not supported as constraint");
            }
        }
    }

    private void transferLowerBoundOfConstant(String typeVariable, ITypeVariableConstraint typeVariableConstraint) {
        for (IConstraint constraint : getLowerBounds(typeVariableConstraint.getTypeVariable())) {
            if (constraint instanceof TypeConstraint) {
                addLowerBound(typeVariable, constraint);
            }
        }
    }

    private void checkReferenceTypeVariable(String typeVariable, ITypeVariableConstraint typeVariableConstraint) {
        String refTypeVariable = typeVariableConstraint.getTypeVariable();
        if (isNotSelfReference(typeVariable, refTypeVariable)) {
            addConstraintsToRef(typeVariable, refTypeVariable);
        } else if (hasUpperBounds(typeVariable) && upperBounds.get(typeVariable).size() > 1) {
            // self reference is not possible if there is more than one upper - otherwise we have incompatible
            // intersection types which cannot be used as lower bound in a signature
            throw new LowerBoundTypeVariableException(typeVariable, getUpperBounds(typeVariable));
            //TODO rstoll TINS-369 intersection type
            // I do not think this is entirely correct, I can have multiple upper bounds but the new added lower type
            // needs to fulfil all upper bounds.
        }
    }

    private boolean isNotAlreadyLowerBound(String typeVariable, IConstraint newLowerBoundConstraint) {
        return (!lowerBounds.containsKey(typeVariable)
                || !lowerBounds.get(typeVariable).containsKey(newLowerBoundConstraint.getId()));
    }

    private void checkUpperBounds(String typeVariable, IConstraint newLowerBoundConstraint) {
        if (upperBounds.containsKey(typeVariable)) {
            ITypeSymbol newLowerType = ((TypeConstraint) newLowerBoundConstraint).getTypeSymbol();
            for (IConstraint upperBoundConstraint : getUpperBounds(typeVariable)) {
                // we do not support type variables as upper bounds of other variables. Such constraints should be
                // defined as lower bounds. Hence we can safely cast to TypeConstraint here
                ITypeSymbol upperTypeSymbol = ((TypeConstraint) upperBoundConstraint).getTypeSymbol();
                if (!overloadResolver.isFirstSameOrSubTypeOfSecond(newLowerType, upperTypeSymbol)) {
                    throw new LowerBoundException(newLowerType, upperTypeSymbol);
                }
            }
        }
    }

    private boolean isNotSelfReference(String typeVariable, String refTypeVariable) {
        return !typeVariable.equals(refTypeVariable);
    }

    private void addConstraintsToRef(String typeVariable, String refTypeVariable) {
        // if the refTypeVariable is within the bounds of the typeVariable, then the newly added constraints do
        // not narrow the refTypeVariable. Yet, if it is not within the bounds then it is narrowed and
        // ultimately a BoundException is thrown when narrowing is not possible.
        if (hasUpperBounds(typeVariable)) {
            for (IConstraint upperBoundConstraint : getUpperBounds(typeVariable)) {
                addUpperBound(refTypeVariable, upperBoundConstraint);
            }
        }
        if (hasLowerBounds(typeVariable)) {
            for (IConstraint lowerBoundConstraint : getLowerBounds(typeVariable)) {
                if (lowerBoundConstraint instanceof TypeConstraint) {
                    addLowerBound(refTypeVariable, lowerBoundConstraint);
                }
            }
        }
    }

    private void addToMapMap(Map<String, Map<String, IConstraint>> map, String typeVariable, IConstraint constraint) {
        Map<String, IConstraint> mapInMap;
        if (map.containsKey(typeVariable)) {
            mapInMap = map.get(typeVariable);
        } else {
            mapInMap = new HashMap<>();
            map.put(typeVariable, mapInMap);
        }
        mapInMap.put(constraint.getId(), constraint);
    }

    @Override
    public void addUpperBound(String typeVariable, IConstraint newUpperBoundConstraint) {
        // we do not support type variables as upper bounds of other variables. Such constraints should be defined
        // as lower bounds. Hence we can safely cast to TypeConstraint here
        TypeConstraint newUpperBoundTypeConstraint = (TypeConstraint) newUpperBoundConstraint;
        if (isNotAlreadyUpperBound(typeVariable, newUpperBoundTypeConstraint)) {
            if (hasLowerBounds(typeVariable)) {
                checkLowerBounds(typeVariable, newUpperBoundTypeConstraint);
            }
            addToMapMap(upperBounds, typeVariable, newUpperBoundTypeConstraint);
        }
    }

    private boolean isNotAlreadyUpperBound(String typeVariable, IConstraint newUpperBoundConstraint) {
        return (!upperBounds.containsKey(typeVariable)
                || !upperBounds.get(typeVariable).containsKey(newUpperBoundConstraint.getId()));
    }

    private void checkLowerBounds(String typeVariable, TypeConstraint newUpperBoundConstraint) {
        ITypeSymbol newUpperType = newUpperBoundConstraint.getTypeSymbol();
        for (IConstraint lowerBoundConstraint : getLowerBounds(typeVariable)) {
            if (lowerBoundConstraint instanceof TypeConstraint) {
                ITypeSymbol lowerTypeSymbol = ((TypeConstraint) lowerBoundConstraint).getTypeSymbol();
                if (!overloadResolver.isFirstSameOrParentTypeOfSecond(newUpperType, lowerTypeSymbol)) {
                    throw new UpperBoundException(newUpperType, lowerTypeSymbol);
                }
            } else if (lowerBoundConstraint instanceof ITypeVariableConstraint) {
                ITypeVariableConstraint typeVariableConstraint = (ITypeVariableConstraint) lowerBoundConstraint;
                String refTypeVariable = typeVariableConstraint.getTypeVariable();
                if (isNotSelfReference(typeVariable, refTypeVariable)) {
                    //looks like the current type variable has another type variable as its lower bound.
                    //Hence we need to make sure that the other type variable is updated as well.
                    addUpperBound(refTypeVariable, newUpperBoundConstraint);
                } else {
                    // we need to check whether the new upper bound clashes with the current upper bounds,
                    // since they are used as lower bounds as well. Following an example: T x T -> T \ T < num, T > T
                    // we are not allowed to add bool to the upper bound since it clashes with num,
                    // bool is not the same or a subtype of num respectively.
                    checkUpperBounds(typeVariable, newUpperBoundConstraint);
                }
            }
        }
    }

    @Override
    public boolean hasLowerBounds(String typeVariable) {
        return lowerBounds.containsKey(typeVariable);
    }

    @Override
    public boolean hasUpperBounds(String typeVariable) {
        return upperBounds.containsKey(typeVariable);
    }

    @Override
    public Collection<IConstraint> getLowerBounds(String typeVariable) {
        return lowerBounds.get(typeVariable).values();
    }

    @Override
    public Collection<IConstraint> getUpperBounds(String typeVariable) {
        return upperBounds.get(typeVariable).values();
    }

    @Override
    public Set<String> getLowerBoundConstraintIds(String typeVariable) {
        return lowerBounds.get(typeVariable).keySet();
    }

    @Override
    public Set<String> getUpperBoundConstraintIds(String typeVariable) {
        return upperBounds.get(typeVariable).keySet();
    }

    @Override
    public Set<String> getTypeVariablesWithLowerBounds() {
        return lowerBounds.keySet();
    }

    @Override
    public Set<String> getTypeVariablesWithUpperBounds() {
        return upperBounds.keySet();
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
                    .append(hasLowerBounds(typeVariable)
                            ? getLowerBoundConstraintIds(typeVariable).toString() : "[]")
                    .append(",")
                    .append(hasUpperBounds(typeVariable)
                            ? getUpperBoundConstraintIds(typeVariable).toString() : "[]")
                    .append(">");
            if (entry.getValue().hasFixedType()) {
                sb.append("#");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
