/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraintSolver;
import ch.tsphp.tinsphp.common.inference.constraints.IReadOnlyTypeVariableCollection;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbolWithRef;
import ch.tsphp.tinsphp.symbols.PolymorphicFunctionTypeSymbol;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PolymorphicFunctionTypeSymbolTest
{

    @Test
    public void apply_NothingCached_UsesConstraintSolverAndReturnsTypeOfReturnTypeVariable() {
        ITypeVariableSymbolWithRef paramTypeVariable = mock(ITypeVariableSymbolWithRef.class);
        when(paramTypeVariable.getCurrentTypeVariable()).thenReturn(paramTypeVariable);
        when(paramTypeVariable.getType()).thenReturn(mock(IUnionTypeSymbol.class));
        ITypeVariableSymbolWithRef returnTypeVariable = mock(ITypeVariableSymbolWithRef.class);
        final IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        when(returnTypeVariable.getCurrentTypeVariable()).thenReturn(returnTypeVariable);
        when(returnTypeVariable.getType()).thenReturn(unionTypeSymbol);
        ITypeVariableSymbol argument = mock(ITypeVariableSymbol.class);
        when(argument.getType()).thenReturn(unionTypeSymbol);
        Deque<ITypeVariableSymbol> typeVariables = new ArrayDeque<>();
        typeVariables.add(paramTypeVariable);
        typeVariables.add(returnTypeVariable);
        IConstraintSolver constraintSolver = mock(IConstraintSolver.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol(
                asList(paramTypeVariable), typeVariables, returnTypeVariable, constraintSolver);
        ITypeSymbol result = symbol.apply(asList(argument));

        verify(constraintSolver).solveConstraints(any(IReadOnlyTypeVariableCollection.class));
        assertThat(result, is((ITypeSymbol) unionTypeSymbol));
    }

    @Test
    public void apply_SecondCall_SecondCallAlsoUsesConstraintSolver() {
        ITypeVariableSymbolWithRef paramTypeVariable = mock(ITypeVariableSymbolWithRef.class);
        when(paramTypeVariable.getCurrentTypeVariable()).thenReturn(paramTypeVariable);
        when(paramTypeVariable.getType()).thenReturn(mock(IUnionTypeSymbol.class));
        ITypeVariableSymbolWithRef returnTypeVariable = mock(ITypeVariableSymbolWithRef.class);
        final IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        when(unionTypeSymbol.getAbsoluteName()).thenReturn("int");
        when(returnTypeVariable.getCurrentTypeVariable()).thenReturn(returnTypeVariable);
        when(returnTypeVariable.getType()).thenReturn(unionTypeSymbol);
        ITypeVariableSymbol argument = mock(ITypeVariableSymbol.class);
        when(argument.getType()).thenReturn(unionTypeSymbol);
        Deque<ITypeVariableSymbol> typeVariables = new ArrayDeque<>();
        typeVariables.add(paramTypeVariable);
        typeVariables.add(returnTypeVariable);
        IConstraintSolver constraintSolver = mock(IConstraintSolver.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol(
                asList(paramTypeVariable), typeVariables, returnTypeVariable, constraintSolver);
        ITypeSymbol result1 = symbol.apply(asList(argument));
        ITypeSymbol result2 = symbol.apply(asList(argument));

        //as long as functions have side effects we cannot cache the result
        verify(constraintSolver, times(2)).solveConstraints(any(IReadOnlyTypeVariableCollection.class));
        assertThat(result1, is((ITypeSymbol) unionTypeSymbol));
        assertThat(result2, is(result1));
    }

    private IFunctionTypeSymbol createFunctionTypeSymbol(
            List<ITypeVariableSymbolWithRef> parameterIds,
            Deque<ITypeVariableSymbol> typeVariables,
            ITypeVariableSymbolWithRef returnTypeVariableSymbol,
            IConstraintSolver constraintSolver) {

        ISymbolFactory symbolFactory = mock(ISymbolFactory.class);
        when(symbolFactory.createUnionTypeSymbol()).thenReturn(mock(IUnionTypeSymbol.class));
        when(symbolFactory.createMinimalTypeVariableSymbol(anyString())).thenReturn(mock(ITypeVariableSymbol.class));
        return createFunctionTypeSymbol(
                "foo",
                parameterIds,
                null,
                typeVariables,
                returnTypeVariableSymbol,
                symbolFactory,
                constraintSolver);
    }

    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<ITypeVariableSymbolWithRef> parameterIds,
            ITypeSymbol parentTypeSymbol,
            Deque<ITypeVariableSymbol> typeVariables,
            ITypeVariableSymbolWithRef returnTypeVariableSymbol,
            ISymbolFactory symbolFactory,
            IConstraintSolver constraintSolver) {
        return new PolymorphicFunctionTypeSymbol(
                name,
                parameterIds,
                parentTypeSymbol,
                returnTypeVariableSymbol,
                typeVariables,
                symbolFactory,
                constraintSolver);
    }
}
