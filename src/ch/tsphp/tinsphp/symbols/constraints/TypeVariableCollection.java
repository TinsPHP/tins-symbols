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
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;

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

    public TypeVariableCollection(
            IOverloadResolver theOverloadResolver,
            TypeVariableCollection collection,
            Map<String, TypeVariableConstraint> mapping) {
        overloadResolver = theOverloadResolver;

        for (Map.Entry<String, Map<String, IConstraint>> entry : collection.lowerBounds.entrySet()) {
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
        for (Map.Entry<String, Map<String, IConstraint>> entry : collection.upperBounds.entrySet()) {
            upperBounds.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
    }

    @Override
    public void addLowerBound(String typeVariable, IConstraint newLowerBoundConstraint) {
        if (isNotAlreadyLowerBound(typeVariable, newLowerBoundConstraint)) {
            if (newLowerBoundConstraint instanceof TypeConstraint) {
                checkUpperBounds(typeVariable, newLowerBoundConstraint);
                addToMapMap(lowerBounds, typeVariable, newLowerBoundConstraint);
            } else if (newLowerBoundConstraint instanceof TypeVariableConstraint) {
                TypeVariableConstraint typeVariableConstraint = (TypeVariableConstraint) newLowerBoundConstraint;
                if (typeVariableConstraint.hasFixedType()) {
                    transferLowerBoundOfConstant(typeVariable, typeVariableConstraint);
                } else {
                    checkReferenceTypeVariable(typeVariable, typeVariableConstraint);
                    addToMapMap(lowerBounds, typeVariable, newLowerBoundConstraint);
                }
            } else {
                throw new UnsupportedOperationException(newLowerBoundConstraint.getClass().getName()
                        + " is not supported as constraint");
            }
        }
    }

    private void transferLowerBoundOfConstant(String typeVariable, TypeVariableConstraint typeVariableConstraint) {
        for (IConstraint constraint : lowerBounds.get(typeVariableConstraint.getTypeVariable()).values()) {
            addLowerBound(typeVariable, constraint);
        }
    }

    private void checkReferenceTypeVariable(String typeVariable, TypeVariableConstraint typeVariableConstraint) {
        String refTypeVariable = typeVariableConstraint.getTypeVariable();
        if (isNotSelfReference(typeVariable, refTypeVariable)) {
            addConstraintsToRef(typeVariable, refTypeVariable);
        } else if (hasUpperBounds(typeVariable) && upperBounds.get(typeVariable).size() > 1) {
            // self reference is not possible if there is more than one upper - otherwise we have incompatible
            // intersection types which cannot be used as lower bound in a signature
            throw new LowerBoundTypeVariableException(typeVariable, upperBounds.get(typeVariable).values());
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
            for (IConstraint upperBoundConstraint : upperBounds.get(typeVariable).values()) {
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
            for (IConstraint upperBoundConstraint : upperBounds.get(typeVariable).values()) {
                addUpperBound(refTypeVariable, upperBoundConstraint);
            }
        }
        if (hasLowerBounds(typeVariable)) {
            for (IConstraint lowerBoundConstraint : lowerBounds.get(typeVariable).values()) {
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
        for (IConstraint lowerBoundConstraint : lowerBounds.get(typeVariable).values()) {
            if (lowerBoundConstraint instanceof TypeConstraint) {
                ITypeSymbol lowerTypeSymbol = ((TypeConstraint) lowerBoundConstraint).getTypeSymbol();
                if (!overloadResolver.isFirstSameOrParentTypeOfSecond(newUpperType, lowerTypeSymbol)) {
                    throw new UpperBoundException(newUpperType, lowerTypeSymbol);
                }
            } else if (lowerBoundConstraint instanceof TypeVariableConstraint) {
                TypeVariableConstraint typeVariableConstraint = (TypeVariableConstraint) lowerBoundConstraint;
                String refTypeVariable = typeVariableConstraint.getTypeVariable();
                if (isNotSelfReference(typeVariable, refTypeVariable)) {
                    //looks like the current type variable has another type variable as its lower bound.
                    //Hence we need to make sure that the other type variable is updated as well.
                    if (!typeVariableConstraint.hasFixedType()) {
                        addUpperBound(refTypeVariable, newUpperBoundConstraint);
                    }
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
}
