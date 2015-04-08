/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.common.inference.constraints.LowerBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.UpperBoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeVariableCollection implements ITypeVariableCollection
{
    private final IOverloadResolver overloadResolver;

    private final Map<String, Map<String, IConstraint>> lowerBounds = new HashMap<>();
    private final Map<String, Map<String, IConstraint>> upperBounds = new HashMap<>();

    public TypeVariableCollection(IOverloadResolver theOverloadResolver) {
        overloadResolver = theOverloadResolver;
    }

    public TypeVariableCollection(IOverloadResolver theOverloadResolver, TypeVariableCollection collection) {
        overloadResolver = theOverloadResolver;

        for (Map.Entry<String, Map<String, IConstraint>> entry : collection.lowerBounds.entrySet()) {
            lowerBounds.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        for (Map.Entry<String, Map<String, IConstraint>> entry : collection.upperBounds.entrySet()) {
            upperBounds.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
    }

    @Override
    public void addLowerBound(String typeVariable, IConstraint newLowerBoundConstraint) throws LowerBoundException {
        if (isNotAlreadyLowerBound(typeVariable, newLowerBoundConstraint)) {
            if (upperBounds.containsKey(typeVariable)) {
                if (newLowerBoundConstraint instanceof TypeConstraint) {
                    checkUpperBounds(typeVariable, newLowerBoundConstraint);
                } else if (newLowerBoundConstraint instanceof TypeVariableConstraint) {
                    TypeVariableConstraint typeVariableConstraint = (TypeVariableConstraint) newLowerBoundConstraint;
                    if (isNotSelfReference(typeVariable, typeVariableConstraint)) {
                        addConstraintsToRef(typeVariable, typeVariableConstraint);
                    }
                }
            }
            addToMapMap(lowerBounds, typeVariable, newLowerBoundConstraint);
        }
    }

    private boolean isNotAlreadyLowerBound(String typeVariable, IConstraint newLowerBoundConstraint) {
        return (!lowerBounds.containsKey(typeVariable)
                || !lowerBounds.get(typeVariable).containsKey(newLowerBoundConstraint.getId()));
    }

    private boolean isNotSelfReference(String typeVariable, TypeVariableConstraint typeVariableConstraint) {
        return !typeVariable.equals(typeVariableConstraint.getTypeVariable());
    }

    private void addConstraintsToRef(String typeVariable, TypeVariableConstraint newLowerBoundConstraint) {
        // if the refTypeVariable is within the bounds of the typeVariable, then the newly added constraints do
        // not narrow the refTypeVariable. Yet, if it is not within the bounds then it is narrowed and
        // ultimately a BoundException is thrown when narrowing is not possible.
        String refTypeVariable = newLowerBoundConstraint.getTypeVariable();
        if (upperBounds.containsKey(typeVariable)) {
            for (IConstraint upperBoundConstraint : upperBounds.get(typeVariable).values()) {
                addUpperBound(refTypeVariable, upperBoundConstraint);
            }
        }
        if (lowerBounds.containsKey(typeVariable)) {
            for (IConstraint lowerBoundConstraint : lowerBounds.get(typeVariable).values()) {
                addLowerBound(refTypeVariable, lowerBoundConstraint);
            }
        }
    }

    private void checkUpperBounds(String typeVariable, IConstraint newLowerBoundConstraint) {
        ITypeSymbol newLowerType = ((TypeConstraint) newLowerBoundConstraint).getTypeSymbol();
        for (IConstraint upperBoundConstraint : upperBounds.get(typeVariable).values()) {
            // we do not support type variables as upper bounds of other variables. Such constraints should be
            // defined as lower bounds. Hence we can safely cast to TypeConstraint here
            ITypeSymbol upperTypeSymbol = ((TypeConstraint) upperBoundConstraint).getTypeSymbol();
            if (!overloadResolver.isFirstSameOrSubTypeOfSecond(newLowerType, upperTypeSymbol)) {
                throw new LowerBoundException(newLowerType, upperTypeSymbol);
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
    public void addUpperBound(String typeVariable, IConstraint newUpperBoundConstraint) throws UpperBoundException {
        if (isNotAlreadyUpperBound(typeVariable, newUpperBoundConstraint)) {
            if (lowerBounds.containsKey(typeVariable)) {
                checkLowerBounds(typeVariable, newUpperBoundConstraint);
            }
            addToMapMap(upperBounds, typeVariable, newUpperBoundConstraint);
        }
    }

    private boolean isNotAlreadyUpperBound(String typeVariable, IConstraint newUpperBoundConstraint) {
        return (!upperBounds.containsKey(typeVariable)
                || !upperBounds.get(typeVariable).containsKey(newUpperBoundConstraint.getId()));
    }

    private void checkLowerBounds(String typeVariable, IConstraint newUpperBoundConstraint) {
        // we do not support type variables as upper bounds of other variables. Such constraints should be defined
        // as lower bounds. Hence we can safely cast to TypeConstraint here
        TypeConstraint newUpperBoundTypeConstraint = (TypeConstraint) newUpperBoundConstraint;
        ITypeSymbol newUpperType = newUpperBoundTypeConstraint.getTypeSymbol();
        for (IConstraint lowerBoundConstraint : lowerBounds.get(typeVariable).values()) {
            if (lowerBoundConstraint instanceof TypeConstraint) {
                ITypeSymbol lowerTypeSymbol = ((TypeConstraint) lowerBoundConstraint).getTypeSymbol();
                if (!overloadResolver.isFirstSameOrParentTypeOfSecond(newUpperType, lowerTypeSymbol)) {
                    throw new UpperBoundException(newUpperType, lowerTypeSymbol);
                }
            } else if (lowerBoundConstraint instanceof TypeVariableConstraint) {
                TypeVariableConstraint typeVariableConstraint = (TypeVariableConstraint) lowerBoundConstraint;
                if (isNotSelfReference(typeVariable, typeVariableConstraint)) {
                    //looks like the current type variable has another type variable as its lower bound.
                    //Hence we need to make sure that the other type variable is updated as well.
                    String refTypeVariable = typeVariableConstraint.getTypeVariable();
                    addUpperBound(refTypeVariable, newUpperBoundConstraint);
                } else {
                    // we need to check whether the new upper bound clashes with the current upper bounds,
                    // since they are used as lower bounds as well. Following an example: T x T -> T \ T < num, T > T
                    // we are not allowed to add bool to the upper bound since it clashes with num,
                    // bool is not the same or a subtype of num respectively.
                    checkUpperBounds(typeVariable, newUpperBoundTypeConstraint);
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
}
