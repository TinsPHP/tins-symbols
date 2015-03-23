/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraintSolver;
import ch.tsphp.tinsphp.common.inference.constraints.IReadOnlyTypeVariableCollection;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolymorphicFunctionTypeSymbol extends AFunctionTypeSymbol
        implements IFunctionTypeSymbol, IReadOnlyTypeVariableCollection
{

    private final Map<String, ITypeVariableSymbol> typeVariables;
    private final ISymbolFactory symbolFactory;
    private final IConstraintSolver constraintSolver;
    private final Map<String, ITypeSymbol> cachedReturnTypes = new HashMap<>();

    public PolymorphicFunctionTypeSymbol(
            String theName,
            List<String> theParameterIds,
            ITypeSymbol theParentTypeSymbol,
            Map<String, ITypeVariableSymbol> theTypeVariables,
            ISymbolFactory theSymbolFactory,
            IConstraintSolver theConstraintSolver) {

        super(theName, theParameterIds, theParentTypeSymbol);
        typeVariables = theTypeVariables;
        symbolFactory = theSymbolFactory;
        constraintSolver = theConstraintSolver;
    }

    @Override
    public Map<String, ITypeVariableSymbol> getTypeVariables() {
        return typeVariables;
    }

    @Override
    public Collection<ITypeVariableSymbol> getTypeVariablesWhichNeedToBeSealed() {
        return new ArrayDeque<>();
    }

    @Override
    public ITypeSymbol apply(List<IUnionTypeSymbol> arguments) {
        return solveConstraints(arguments);
        //caching only works for functions without side effects. Before I do not have a flag which denotes that the
        //current function does not have side effects, I cannot use the cache
//        String key = getKey(arguments);
//        ITypeSymbol returnTypeSymbol = cachedReturnTypes.get(key);
//        if (returnTypeSymbol == null) {
//            returnTypeSymbol = solveConstraints(arguments);
//            cachedReturnTypes.put(key, returnTypeSymbol);
//        }
//        return returnTypeSymbol;
    }

    private String getKey(List<IUnionTypeSymbol> arguments) {
        StringBuilder sb = new StringBuilder();

        int size = indexToName.size();
        if (size > 0) {
            sb.append(arguments.get(0).getAbsoluteName());
        }
        for (int i = 1; i < size; ++i) {
            sb.append(" x ").append(arguments.get(i).getAbsoluteName());
        }
        return sb.toString();
    }

    private ITypeSymbol solveConstraints(List<IUnionTypeSymbol> arguments) {
        instantiateParameters(arguments);

        constraintSolver.solveConstraints(this);
        IUnionTypeSymbol returnType = typeVariables.get("return").getType();

        mergeByRefParameters(arguments);

        resetParameters();
        return returnType;
    }

    private void instantiateParameters(List<IUnionTypeSymbol> arguments) {
        int size = indexToName.size();
        for (int i = 0; i < size; ++i) {
            ITypeVariableSymbol parameter = typeVariables.get(indexToName.get(i));
            parameter.getType().merge(arguments.get(i));
        }
    }

    private void mergeByRefParameters(List<IUnionTypeSymbol> arguments) {
        int size = indexToName.size();
        for (int i = 0; i < size; ++i) {
            ITypeVariableSymbol parameter = typeVariables.get(indexToName.get(i));
            if (!parameter.isByValue()) {
                arguments.get(i).merge(parameter.getType());
            }
        }
    }

    private void resetParameters() {
        for (String parameterId : nameToIndexMap.keySet()) {
            typeVariables.get(parameterId).setType(symbolFactory.createUnionTypeSymbol());
        }
        typeVariables.get("return").setType(symbolFactory.createUnionTypeSymbol());
    }


}
