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
        if (isNotAlreadyLowerBoundAndHasUpperBounds(typeVariable, newLowerBoundConstraint)) {
            for (IConstraint upperBoundConstraint : upperBounds.get(typeVariable).values()) {
                if (upperBoundConstraint instanceof TypeConstraint) {
                    ITypeSymbol upperTypeSymbol = ((TypeConstraint) upperBoundConstraint).getTypeSymbol();
                    if (newLowerBoundConstraint instanceof TypeConstraint) {
                        ITypeSymbol newLowerType = ((TypeConstraint) newLowerBoundConstraint).getTypeSymbol();
                        if (!overloadResolver.isFirstSameOrSubTypeOfSecond(newLowerType, upperTypeSymbol)) {
                            throw new LowerBoundException(newLowerType, upperTypeSymbol);
                        }
                    }
                }
            }
        }
        addToMapMap(lowerBounds, typeVariable, newLowerBoundConstraint);
    }

    private boolean isNotAlreadyLowerBoundAndHasUpperBounds(String typeVariable,
            IConstraint newLowerBoundConstraint) {
        return (!lowerBounds.containsKey(typeVariable)
                || !lowerBounds.get(typeVariable).containsKey(newLowerBoundConstraint.getId()))
                && upperBounds.containsKey(typeVariable);
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
        if (isNotAlreadyUpperBoundAndHasLowerBounds(typeVariable, newUpperBoundConstraint)) {
            for (IConstraint lowerBoundConstraint : lowerBounds.get(typeVariable).values()) {
                if (lowerBoundConstraint instanceof TypeConstraint) {
                    ITypeSymbol lowerTypeSymbol = ((TypeConstraint) lowerBoundConstraint).getTypeSymbol();
                    if (newUpperBoundConstraint instanceof TypeConstraint) {
                        ITypeSymbol newUpperType = ((TypeConstraint) newUpperBoundConstraint).getTypeSymbol();
                        if (!overloadResolver.isFirstSameOrParentTypeOfSecond(newUpperType, lowerTypeSymbol)) {
                            throw new UpperBoundException(newUpperType, lowerTypeSymbol);
                        }
                    }
                }
            }
        }
        addToMapMap(upperBounds, typeVariable, newUpperBoundConstraint);
    }

    private boolean isNotAlreadyUpperBoundAndHasLowerBounds(
            String typeVariable, IConstraint newUpperBoundConstraint) {
        return (!upperBounds.containsKey(typeVariable)
                || !upperBounds.get(typeVariable).containsKey(newUpperBoundConstraint.getId()))
                && lowerBounds.containsKey(typeVariable);
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
