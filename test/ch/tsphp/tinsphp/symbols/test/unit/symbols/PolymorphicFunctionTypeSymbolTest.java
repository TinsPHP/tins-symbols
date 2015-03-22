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
import ch.tsphp.tinsphp.symbols.PolymorphicFunctionTypeSymbol;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ITypeVariableSymbol parameterTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        String name = "$x";
        ITypeVariableSymbol returnTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        final IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        when(unionTypeSymbol.getAbsoluteName()).thenReturn("int");
        when(returnTypeSymbolVariable.getType()).thenReturn(unionTypeSymbol);
        Map<String, ITypeVariableSymbol> map = new HashMap<>();
        map.put(name, parameterTypeSymbolVariable);
        map.put("return", returnTypeSymbolVariable);
        IConstraintSolver constraintSolver = mock(IConstraintSolver.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol(asList(name), map, constraintSolver);
        ITypeSymbol result = symbol.apply(asList(unionTypeSymbol));

        verify(constraintSolver).solveConstraints(any(IReadOnlyTypeVariableCollection.class));
        assertThat(result, is((ITypeSymbol) unionTypeSymbol));
    }

    @Test
    public void apply_SecondCall_SecondCallDoesNotUseConstraintSolverIsSameResultAsFirstCall() {
        ITypeVariableSymbol parameterTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        String name = "$x";
        ITypeVariableSymbol returnTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        final IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        when(unionTypeSymbol.getAbsoluteName()).thenReturn("int");
        when(returnTypeSymbolVariable.getType()).thenReturn(unionTypeSymbol);
        Map<String, ITypeVariableSymbol> map = new HashMap<>();
        map.put(name, parameterTypeSymbolVariable);
        map.put("return", returnTypeSymbolVariable);
        IConstraintSolver constraintSolver = mock(IConstraintSolver.class);

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol(asList(name), map, constraintSolver);
        ITypeSymbol result1 = symbol.apply(asList(unionTypeSymbol));
        ITypeSymbol result2 = symbol.apply(asList(unionTypeSymbol));

        verify(constraintSolver, times(1)).solveConstraints(any(IReadOnlyTypeVariableCollection.class));
        assertThat(result1, is((ITypeSymbol) unionTypeSymbol));
        assertThat(result2, is(result1));
    }

    @Test
    public void
    apply_SecondCallWithoutParameterAndTwoArguments_SecondCallDoesNotUseConstraintSolverIsSameResultAsFirstCall() {
        ITypeVariableSymbol returnTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        final IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        when(unionTypeSymbol.getAbsoluteName()).thenReturn("int");
        when(returnTypeSymbolVariable.getType()).thenReturn(unionTypeSymbol);
        Map<String, ITypeVariableSymbol> map = new HashMap<>();
        map.put("return", returnTypeSymbolVariable);
        IConstraintSolver constraintSolver = mock(IConstraintSolver.class);
        IUnionTypeSymbol additionalArgument = mock(IUnionTypeSymbol.class);
        when(additionalArgument.getAbsoluteName()).thenReturn("additionalType");

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol(
                new ArrayList<String>(), map, constraintSolver);
        ITypeSymbol result1 = symbol.apply(asList(unionTypeSymbol));
        ITypeSymbol result2 = symbol.apply(asList(unionTypeSymbol, additionalArgument));

        verify(constraintSolver, times(1)).solveConstraints(any(IReadOnlyTypeVariableCollection.class));
        assertThat(result1, is((ITypeSymbol) unionTypeSymbol));
        assertThat(result2, is(result1));
    }

    @Test
    public void
    apply_SecondCallOneParameterAndTwoArguments_SecondCallDoesNotUseConstraintSolverIsSameResultAsFirstCall() {
        ITypeVariableSymbol parameterTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        String name = "$x";
        ITypeVariableSymbol returnTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        final IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        when(unionTypeSymbol.getAbsoluteName()).thenReturn("int");
        when(returnTypeSymbolVariable.getType()).thenReturn(unionTypeSymbol);
        Map<String, ITypeVariableSymbol> map = new HashMap<>();
        map.put(name, parameterTypeSymbolVariable);
        map.put("return", returnTypeSymbolVariable);
        IConstraintSolver constraintSolver = mock(IConstraintSolver.class);
        IUnionTypeSymbol additionalArgument = mock(IUnionTypeSymbol.class);
        when(additionalArgument.getAbsoluteName()).thenReturn("additionalType");

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol(asList(name), map, constraintSolver);
        ITypeSymbol result1 = symbol.apply(asList(unionTypeSymbol));
        ITypeSymbol result2 = symbol.apply(asList(unionTypeSymbol, additionalArgument));

        verify(constraintSolver, times(1)).solveConstraints(any(IReadOnlyTypeVariableCollection.class));
        assertThat(result1, is((ITypeSymbol) unionTypeSymbol));
        assertThat(result2, is(result1));
    }

    @Test
    public void
    apply_SecondCallTwoParameterAndThreeArguments_SecondCallDoesNotUseConstraintSolverIsSameResultAsFirstCall() {
        ITypeVariableSymbol parameterTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        String name1 = "$x";
        String name2 = "$y";
        ITypeVariableSymbol returnTypeSymbolVariable = mock(ITypeVariableSymbol.class);
        final IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        when(unionTypeSymbol.getAbsoluteName()).thenReturn("int");
        when(returnTypeSymbolVariable.getType()).thenReturn(unionTypeSymbol);
        Map<String, ITypeVariableSymbol> map = new HashMap<>();
        map.put(name1, parameterTypeSymbolVariable);
        map.put(name2, parameterTypeSymbolVariable);
        map.put("return", returnTypeSymbolVariable);
        IConstraintSolver constraintSolver = mock(IConstraintSolver.class);
        IUnionTypeSymbol additionalArgument = mock(IUnionTypeSymbol.class);
        when(additionalArgument.getAbsoluteName()).thenReturn("additionalType");

        IFunctionTypeSymbol symbol = createFunctionTypeSymbol(asList(name1, name2), map, constraintSolver);
        ITypeSymbol result1 = symbol.apply(asList(unionTypeSymbol, unionTypeSymbol));
        ITypeSymbol result2 = symbol.apply(asList(unionTypeSymbol, unionTypeSymbol, additionalArgument));

        verify(constraintSolver, times(1)).solveConstraints(any(IReadOnlyTypeVariableCollection.class));
        assertThat(result1, is((ITypeSymbol) unionTypeSymbol));
        assertThat(result2, is(result1));
    }

    private IFunctionTypeSymbol createFunctionTypeSymbol(
            List<String> parameterIds,
            Map<String, ITypeVariableSymbol> typeVariables,
            IConstraintSolver constraintSolver) {

        ISymbolFactory symbolFactory = mock(ISymbolFactory.class);
        when(symbolFactory.createMinimalTypeVariableSymbol(anyString())).thenReturn(mock(ITypeVariableSymbol.class));
        return createFunctionTypeSymbol(
                "foo",
                parameterIds,
                null,
                typeVariables,
                symbolFactory,
                constraintSolver);
    }

    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<String> parameterIds,
            ITypeSymbol parentTypeSymbol,
            Map<String, ITypeVariableSymbol> typeVariables,
            ISymbolFactory symbolFactory,
            IConstraintSolver constraintSolver) {
        return new PolymorphicFunctionTypeSymbol(
                name,
                parameterIds,
                parentTypeSymbol,
                typeVariables,
                symbolFactory,
                constraintSolver);
    }
}
