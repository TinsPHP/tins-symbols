/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraintSolver;
import ch.tsphp.tinsphp.common.symbols.IPolymorphicFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbolWithRef;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

public class PolymorphicFunctionTypeSymbol extends AFunctionTypeSymbol implements IPolymorphicFunctionTypeSymbol
{

    private final List<ITypeVariableSymbolWithRef> parameters;
    private final ITypeVariableSymbolWithRef returnTypeVariableSymbol;
    private final Deque<ITypeVariableSymbol> typeVariables;
    private final ISymbolFactory symbolFactory;
    private final IConstraintSolver constraintSolver;

    public PolymorphicFunctionTypeSymbol(
            String theName,
            List<ITypeVariableSymbolWithRef> theParameters,
            ITypeSymbol theParentTypeSymbol,
            ITypeVariableSymbolWithRef theReturnTypeVariableSymbol,
            Deque<ITypeVariableSymbol> theTypeVariables,
            ISymbolFactory theSymbolFactory,
            IConstraintSolver theConstraintSolver) {

        super(theName, getParameterNames(theParameters), theParentTypeSymbol);
        parameters = theParameters;
        returnTypeVariableSymbol = theReturnTypeVariableSymbol;
        typeVariables = theTypeVariables;
        symbolFactory = theSymbolFactory;
        constraintSolver = theConstraintSolver;
    }

    private static List<String> getParameterNames(List<ITypeVariableSymbolWithRef> theParameters) {
        List<String> names = new ArrayList<>(theParameters.size());
        for (ITypeVariableSymbol typeVariableSymbol : theParameters) {
            names.add(typeVariableSymbol.getName());
        }
        return names;
    }

    @Override
    public Deque<ITypeVariableSymbol> getTypeVariables() {
        return typeVariables;
    }

    @Override
    public Collection<ITypeVariableSymbolWithRef> getTypeVariablesWithRef() {
        return new ArrayDeque<>();
    }

    @Override
    public ITypeSymbol apply(List<ITypeVariableSymbol> arguments) {
        int numberOfParameters = parameters.size();
//        Deque<ITypeVariableSymbol> instantiatedTypeVariables
//                = new ArrayDeque<>(typeVariables.size() + numberOfParameters);

//        for (ITypeVariableSymbol typeVariableSymbol : typeVariables) {
//            ITypeVariableSymbol copy = symbolFactory.createMinimalTypeVariableSymbol(typeVariableSymbol.getName());
//            IUnionTypeSymbol unionTypeSymbol = symbolFactory.createUnionTypeSymbol();
//            unionTypeSymbol.merge(typeVariableSymbol.getType());
//            copy.setType(unionTypeSymbol);
//            instantiatedTypeVariables.add(copy);
//            copy.setConstraint(typeVariableSymbol.getConstraint());
//        }

        for (int i = 0; i < numberOfParameters; ++i) {
            IUnionTypeSymbol parameterType = parameters.get(i).getType();
            parameterType.merge(arguments.get(i).getType());
            parameterType.seal();
        }
//        IReadOnlyTypeVariableCollection collection = new FunctionTemplate(typeVariables);

        constraintSolver.solveConstraints(this);
        IUnionTypeSymbol returnType = returnTypeVariableSymbol.getCurrentTypeVariable().getType();

        returnType.seal();

        mergeByRefParameters(arguments);
        resetTypeVariables();
        return returnType;
    }

    private void mergeByRefParameters(List<ITypeVariableSymbol> arguments) {
        int size = parameters.size();
        for (int i = 0; i < size; ++i) {
            ITypeVariableSymbolWithRef parameter = parameters.get(i);
            if (!parameter.isByValue()) {
                IUnionTypeSymbol unionTypeSymbol = parameter.getCurrentTypeVariable().getType();
                arguments.get(i).setType(unionTypeSymbol);
            }
        }
    }

    private void resetTypeVariables() {
        for (ITypeVariableSymbol typeVariableSymbol : parameters) {
            typeVariableSymbol.setType(symbolFactory.createUnionTypeSymbol());
        }
        returnTypeVariableSymbol.setType(symbolFactory.createUnionTypeSymbol());
        for (ITypeVariableSymbol typeVariableSymbol : typeVariables) {
            typeVariableSymbol.setType(symbolFactory.createUnionTypeSymbol());
        }
    }
}
