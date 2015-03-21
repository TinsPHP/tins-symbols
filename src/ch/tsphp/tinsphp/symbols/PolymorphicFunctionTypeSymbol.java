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
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolymorphicFunctionTypeSymbol extends AFunctionTypeSymbol implements IFunctionTypeSymbol
{

    private final Map<String, ITypeVariableSymbol> typeVariables;
    private final IConstraintSolver constraintSolver;
    private final Map<String, ITypeSymbol> cachedReturnTypes = new HashMap<>();

    public PolymorphicFunctionTypeSymbol(
            String theName,
            List<String> theParameterIds,
            ITypeSymbol theParentTypeSymbol,
            Map<String, ITypeVariableSymbol> theTypeVariables,
            IConstraintSolver theConstraintSolver) {

        super(theName, theParameterIds, theParentTypeSymbol);
        typeVariables = theTypeVariables;
        constraintSolver = theConstraintSolver;
    }

    @Override
    public ITypeSymbol apply(List<IUnionTypeSymbol> arguments) {
        String key = getKey(arguments);
        ITypeSymbol returnTypeSymbol = cachedReturnTypes.get(key);
        if (returnTypeSymbol == null) {
            returnTypeSymbol = solveConstraints(arguments);
            cachedReturnTypes.put(key, returnTypeSymbol);
        }
        return returnTypeSymbol;
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
        HashMap<String, ITypeVariableSymbol> typeVariablesAfterInstantiation = new HashMap<>(typeVariables);

        int size = indexToName.size();
        for (int i = 0; i < size; ++i) {
            typeVariablesAfterInstantiation.get(indexToName.get(i)).setType(arguments.get(i));
        }

        FunctionTemplate functionTemplate = new FunctionTemplate(typeVariablesAfterInstantiation);
        constraintSolver.solveConstraints(functionTemplate);
        Map<String, ITypeVariableSymbol> typeVariablesAfterInference = functionTemplate.getTypeVariables();

        return typeVariablesAfterInference.get("return").getType();
    }

    private class FunctionTemplate implements IReadOnlyTypeVariableCollection
    {
        private final Map<String, ITypeVariableSymbol> typeVariables;

        public FunctionTemplate(Map<String, ITypeVariableSymbol> theTypeVariables) {
            typeVariables = theTypeVariables;
        }

        @Override
        public Map<String, ITypeVariableSymbol> getTypeVariables() {
            return typeVariables;
        }
    }
}
