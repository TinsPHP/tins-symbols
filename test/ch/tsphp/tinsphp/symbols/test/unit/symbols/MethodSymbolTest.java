/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.symbols.MethodSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodSymbolTest
{
    @Test
    public void getParameters_NonAdded_ReturnEmptyList() {
        //no arrange necessary

        IMethodSymbol methodSymbol = createMethodSymbol();
        List<IVariableSymbol> parameters = methodSymbol.getParameters();

        assertThat(parameters, is(empty()));
    }

    @Test
    public void getParameters_OneAdded_ReturnListWithIt() {
        IVariableSymbol variableSymbol = mock(IVariableSymbol.class);

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addParameter(variableSymbol);
        List<IVariableSymbol> parameters = methodSymbol.getParameters();

        assertThat(parameters, contains(variableSymbol));
    }

    @Test
    public void getParameters_TwoAdded_ReturnListWithBoth() {
        IVariableSymbol variableSymbol1 = mock(IVariableSymbol.class);
        IVariableSymbol variableSymbol2 = mock(IVariableSymbol.class);

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addParameter(variableSymbol1);
        methodSymbol.addParameter(variableSymbol2);
        List<IVariableSymbol> parameters = methodSymbol.getParameters();

        assertThat(parameters, contains(variableSymbol1, variableSymbol2));
    }

    @Test
    public void isFullyInitialised_NothingDefined_ReturnsFalse() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");

        IMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isFullyInitialised(symbol);

        assertThat(result, is(false));
    }

    @Test
    public void isFullyInitialised_PartiallyDefined_ReturnsFalse() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addToInitialisedSymbols(symbol, false);
        boolean result = methodSymbol.isFullyInitialised(symbol);

        assertThat(result, is(false));
    }

    @Test
    public void isFullyInitialised_FullyDefined_ReturnsTrue() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addToInitialisedSymbols(symbol, true);
        boolean result = methodSymbol.isFullyInitialised(symbol);

        assertThat(result, is(true));
    }

    @Test
    public void isPartiallyInitialised_NothingDefined_ReturnsFalse() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");

        IMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isPartiallyInitialised(symbol);

        assertThat(result, is(false));
    }

    @Test
    public void isPartiallyInitialised_PartiallyDefined_ReturnTrue() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addToInitialisedSymbols(symbol, false);
        boolean result = methodSymbol.isPartiallyInitialised(symbol);

        assertThat(result, is(true));
    }

    @Test
    public void isPartiallyInitialised_FullyDefined_ReturnsFalse() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addToInitialisedSymbols(symbol, true);
        boolean result = methodSymbol.isPartiallyInitialised(symbol);

        assertThat(result, is(false));
    }

    @Test
    public void getConstraints_NothingDefined_ReturnsEmptyList() {
        //nothing to arrange

        IMethodSymbol methodSymbol = createMethodSymbol();
        List<IConstraint> result = methodSymbol.getConstraints();

        Assert.assertThat(result, is(empty()));
    }

    @Test
    public void addAndGetConstraints_OneAdded_ReturnsListWithOne() {
        IConstraint constraint = mock(IConstraint.class);

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addConstraint(constraint);
        List<IConstraint> result = methodSymbol.getConstraints();

        Assert.assertThat(result, contains(constraint));
    }

    @Test
    public void getBindings_NothingDefined_ReturnsNull() {
        //nothing to arrange

        IMethodSymbol methodSymbol = createMethodSymbol();
        List<IOverloadBindings> result = methodSymbol.getBindings();

        Assert.assertThat(result, is(nullValue()));
    }

    @Test
    public void setAndGetBindings_Standard_ReturnsTheOneSet() {
        List<IOverloadBindings> bindings = new ArrayList<>();

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.setBindings(bindings);
        List<IOverloadBindings> result = methodSymbol.getBindings();

        Assert.assertThat(result, is(bindings));
    }

    @Test
    public void getOverloads_NothingDefined_ReturnsEmptyList() {
        //nothing to arrange

        IMethodSymbol methodSymbol = createMethodSymbol();
        Collection<IFunctionType> result = methodSymbol.getOverloads();

        Assert.assertThat(result, is(empty()));
    }

    @Test
    public void addAndGetOverloads_OneAdded_ReturnsListWithOne() {
        IFunctionType functionType = mock(IFunctionType.class);

        IMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addOverload(functionType);
        Collection<IFunctionType> result = methodSymbol.getOverloads();

        Assert.assertThat(result, contains(functionType));
    }

    @Test
    public void getReturnVariable_Standard_IsOnePassedInConstructor() {
        IMinimalVariableSymbol returnVariable = mock(IMinimalVariableSymbol.class);

        IMethodSymbol methodSymbol = createMethodSymbol(
                mock(IScopeHelper.class),
                mock(ITSPHPAst.class),
                new ModifierSet(),
                new ModifierSet(),
                returnVariable,
                "foo",
                mock(IScope.class));
        IMinimalVariableSymbol result = methodSymbol.getReturnVariable();

        assertThat(result, is(returnVariable));
    }

    @Test
    public void toString_NoTypeModifiersEmptyAndOneReturnTypeModifierDefined_ReturnNameInclReturnTypeModifier() {
        String name = "foo";
        int modifier = 98;
        IModifierSet modifiers = new ModifierSet();
        IModifierSet returnTypeModifiers = new ModifierSet();
        returnTypeModifiers.add(modifier);

        IMethodSymbol methodSymbol = createMethodSymbol(name, modifiers, returnTypeModifiers);
        String result = methodSymbol.toString();

        assertThat(result, is(name + "|" + modifier));
    }

    @Test
    public void toString_NoTypeOneModifierDefinedAndOneReturnTypeModifierDefined_ReturnNameInclReturnTypeModifier() {
        String name = "foo";
        int modifier = 98;
        int returnTypeModifier = 56;
        IModifierSet modifiers = new ModifierSet();
        modifiers.add(modifier);
        IModifierSet returnTypeModifiers = new ModifierSet();
        returnTypeModifiers.add(returnTypeModifier);

        IMethodSymbol methodSymbol = createMethodSymbol(name, modifiers, returnTypeModifiers);
        String result = methodSymbol.toString();

        assertThat(result, is(name + "|" + modifier + "|" + returnTypeModifier));
    }


    private IMethodSymbol createMethodSymbol() {
        return createMethodSymbol("foo", mock(IModifierSet.class), mock(IModifierSet.class));
    }

    private IMethodSymbol createMethodSymbol(String name, IModifierSet modifiers, IModifierSet returnTypeModifiers) {
        return createMethodSymbol(
                mock(IScopeHelper.class),
                mock(ITSPHPAst.class),
                modifiers,
                returnTypeModifiers,
                mock(IMinimalVariableSymbol.class),
                name, mock(IScope.class));
    }

    protected IMethodSymbol createMethodSymbol(IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet methodModifiers,
            IModifierSet returnTypeModifiers,
            IMinimalVariableSymbol returnVariable,
            String name,
            IScope enclosingScope) {
        return new MethodSymbol(
                scopeHelper,
                definitionAst,
                methodModifiers,
                returnTypeModifiers,
                returnVariable,
                name,
                enclosingScope);
    }
}
