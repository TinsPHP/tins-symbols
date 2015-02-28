/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IConstraint;
import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.symbols.AScopedSymbol;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AScopedSymbolTest
{
    class DummyScopedSymbol extends AScopedSymbol
    {
        public DummyScopedSymbol(IScopeHelper theScopeHelper, ITSPHPAst definitionAst, IModifierSet modifiers, String
                name, IScope theEnclosingScope) {
            super(theScopeHelper, definitionAst, modifiers, name, theEnclosingScope);
        }

        @Override
        public boolean isFullyInitialised(ISymbol symbol) {
            return false;
        }

        @Override
        public boolean isPartiallyInitialised(ISymbol symbol) {
            return false;
        }
    }

    @Test
    public void define_Standard_DelegatesToScopeHelper() {
        IScopeHelper scopeHelper = mock(IScopeHelper.class);
        ISymbol symbol = mock(ISymbol.class);

        AScopedSymbol scopedSymbol = createScopedSymbol(scopeHelper);
        scopedSymbol.define(symbol);

        verify(scopeHelper).define(scopedSymbol, symbol);
    }

    @Test
    public void resolve_Standard_DelegatesToScopeHelper() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        ISymbol symbol = mock(ISymbol.class);
        IScopeHelper scopeHelper = mock(IScopeHelper.class);
        when(scopeHelper.resolve(any(IScope.class), eq(ast))).thenReturn(symbol);

        AScopedSymbol scopedSymbol = createScopedSymbol(scopeHelper);
        ISymbol result = scopedSymbol.resolve(ast);

        verify(scopeHelper).resolve(scopedSymbol, ast);
        assertThat(result, is(symbol));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void doubleDefinitionCheck_Standard_ThrowsUnsupportedOperationException() {
        ISymbol symbol = mock(ISymbol.class);
        IScopeHelper scopeHelper = mock(IScopeHelper.class);

        AScopedSymbol scopedSymbol = createScopedSymbol(scopeHelper);
        scopedSymbol.doubleDefinitionCheck(symbol);

        //assert in annotations
    }

    @Test
    public void getEnclosingScope_Standard_ReturnsScopePassedInConstructor() {
        IScope scope = mock(IScope.class);

        AScopedSymbol scopedSymbol = createScopedSymbol(scope);
        IScope result = scopedSymbol.getEnclosingScope();

        assertThat(result, is(scope));
    }

    @Test
    public void getScopeName_Standard_ReturnsNamePassedInConstructor() {
        String name = "foo";

        AScopedSymbol scopedSymbol = createScopedSymbol(name);
        String result = scopedSymbol.getScopeName();

        assertThat(result, is(name));
    }

    @Test
    public void getSymbols_NothingDefined_ReturnsEmptyMap() {
        //no arrange necessary

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        Map<String, List<ISymbol>> result = scopedSymbol.getSymbols();

        assertThat(result.size(), is(0));
    }

    @Test
    public void getInitialisedSymbols_NothingDefined_ReturnsEmptyMap() {
        //no arrange necessary

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();

        assertThat(result.size(), is(0));
    }

    @Test
    public void getInitialisedSymbols_OnePartiallyDefined_ReturnsMapWithSymbolNameAndFalse() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol, false);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();

        assertThat(result, hasEntry("dummy", false));
    }

    @Test
    public void getInitialisedSymbols_TwoPartiallyDefined_ReturnsMapWithBothSymbolNameAndFalse() {
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, false);
        scopedSymbol.addToInitialisedSymbols(symbol2, false);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();

        assertThat(result, hasEntry("dummy1", false));
        assertThat(result, hasEntry("dummy2", false));
    }

    @Test
    public void getInitialisedSymbols_OnFullyDefined_ReturnsMapWithSymbolNameAndTrue() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol, true);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();

        assertThat(result, hasEntry("dummy", true));
    }

    @Test
    public void getInitialisedSymbols_TwoFullyDefined_ReturnsMapWithBothSymbolNameAndTrue() {
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, true);
        scopedSymbol.addToInitialisedSymbols(symbol2, true);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();

        assertThat(result, hasEntry("dummy1", true));
        assertThat(result, hasEntry("dummy2", true));
    }

    @Test
    public void getInitialisedSymbols_OnePartiallyOneFullyDefined_ReturnsMapWithBothAndCorrespondingState() {
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, false);
        scopedSymbol.addToInitialisedSymbols(symbol2, true);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();

        assertThat(result, hasEntry("dummy1", false));
        assertThat(result, hasEntry("dummy2", true));
    }

    @Test
    public void addToInitialisedSymbols_AlreadyPartiallyInitialised_DoesNotThrowError() {
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, false);
        scopedSymbol.addToInitialisedSymbols(symbol2, false);

        //no assert necessary, just make sure no exception is thrown
    }

    @Test
    public void addToInitialisedSymbols_AlreadyFullyInitialised_DoesNotThrowError() {
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, true);
        scopedSymbol.addToInitialisedSymbols(symbol2, true);

        //no assert necessary, just make sure no exception is thrown
    }

    @Test
    public void addToInitialisedSymbols_UpdateFromPartiallyToFullyInitialised_ReturnsTrue() {
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, false);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();

        //assert
        assertThat(result, hasEntry("dummy", false));

        //act continued
        scopedSymbol.addToInitialisedSymbols(symbol2, true);
        //assert 2
        assertThat(result, hasEntry("dummy", true));
    }

    @Test
    public void addToInitialisedSymbols_UpdateFromFullyToPartiallyInitialised_RemainsTrue() {
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, true);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();

        //assert
        assertThat(result, hasEntry("dummy", true));

        //act continued
        scopedSymbol.addToInitialisedSymbols(symbol2, false);
        //assert 2
        assertThat(result, hasEntry("dummy", true));
    }

    @Test
    public void getConstraints_NothingDefined_ReturnsEmptyMap() {

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        Map<String, List<IConstraint>> result = scopedSymbol.getConstraints();

        assertThat(result.size(), is(0));
    }

    @Test
    public void addAndgetConstraints_AddedOneFor$a_ReturnsMapWithCorrespondingConstraint() {
        IConstraint constraint = mock(IConstraint.class);

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addConstraint("$a", constraint);
        Map<String, List<IConstraint>> result = scopedSymbol.getConstraints();

        assertThat(result.size(), is(1));
        assertThat(result, hasKey("$a"));
        assertThat(result.get("$a"), hasItems(constraint));
    }

    @Test
    public void getConstraintsForVariable_NothingDefined_ReturnsEmptyMap() {

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        List<IConstraint> result = scopedSymbol.getConstraintsForVariable("$b");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void addAndGetConstraintsForVariable_Get$bAddedOneFor$a_ReturnsNull() {
        IConstraint constraint = mock(IConstraint.class);

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addConstraint("$a", constraint);
        List<IConstraint> result = scopedSymbol.getConstraintsForVariable("$b");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void addAndGetConstraintsForVariable_Get$bAddedOneFor$b_ReturnsConstraint() {
        IConstraint constraint = mock(IConstraint.class);

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addConstraint("$b", constraint);
        List<IConstraint> result = scopedSymbol.getConstraintsForVariable("$b");

        assertThat(result, hasItems(constraint));
    }

    @Test
    public void addAndGetConstraintsForVariable_Get$bAddedTwoFor$b_ReturnsBothConstraint() {
        IConstraint constraint1 = mock(IConstraint.class);
        IConstraint constraint2 = mock(IConstraint.class);

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addConstraint("$b", constraint1);
        scopedSymbol.addConstraint("$b", constraint2);
        List<IConstraint> result = scopedSymbol.getConstraintsForVariable("$b");

        assertThat(result, hasItems(constraint1, constraint2));
    }

    @Test
    public void getResultOfConstraintSolving_NothingDefined_ReturnsNull() {
        //no arrange necessary

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        IUnionTypeSymbol result = scopedSymbol.getResultOfConstraintSolving("$a");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void setAndGetResultOfConstraintSolving_$aSetAndGet$b_ReturnsNull() {
        IUnionTypeSymbol resultTypeSymbol = mock(IUnionTypeSymbol.class);

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.setResultOfConstraintSolving("$a", resultTypeSymbol);
        IUnionTypeSymbol result = scopedSymbol.getResultOfConstraintSolving("$b");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void setAndGetResultOfConstraintSolving_$aSetAndGet$a_ReturnsUnionTypeSymbol() {
        IUnionTypeSymbol resultTypeSymbol = mock(IUnionTypeSymbol.class);

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.setResultOfConstraintSolving("$a", resultTypeSymbol);
        IUnionTypeSymbol result = scopedSymbol.getResultOfConstraintSolving("$a");

        assertThat(result, is(resultTypeSymbol));
    }

    @Test(expected = IllegalStateException.class)
    public void setResultOfConstraintSolving_AlreadySet_ThrowsIllegalStateException() {
        //no arrange necessary

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.setResultOfConstraintSolving("$a", mock(IUnionTypeSymbol.class));
        scopedSymbol.setResultOfConstraintSolving("$a", mock(IUnionTypeSymbol.class));

        //assert in annotation
    }

    private AScopedSymbol createScopedSymbol(String name) {
        return createScopedSymbol(
                mock(IScopeHelper.class), mock(ITSPHPAst.class), mock(IModifierSet.class), name, mock(IScope.class));
    }

    private AScopedSymbol createScopedSymbol(IScope scope) {
        return createScopedSymbol(
                mock(IScopeHelper.class), mock(ITSPHPAst.class), mock(IModifierSet.class), "foo", scope);
    }

    private AScopedSymbol createScopedSymbol(IScopeHelper scopeHelper) {
        return createScopedSymbol(
                scopeHelper, mock(ITSPHPAst.class), mock(IModifierSet.class), "foo", mock(IScope.class));
    }

    protected AScopedSymbol createScopedSymbol(IScopeHelper scopeHelper, ITSPHPAst definitionAst,
            IModifierSet modifiers, String name, IScope enclosingScope) {
        return new DummyScopedSymbol(
                scopeHelper, definitionAst, modifiers, name, enclosingScope);
    }
}
