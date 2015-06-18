/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IRecordTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.symbols.ARecordTypeSymbol;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ARecordTypeSymbolTest
{
    class DummyRecordTypeSymbol extends ARecordTypeSymbol
    {

        public DummyRecordTypeSymbol(IScopeHelper scopeHelper, ITSPHPAst definitionAst, IModifierSet modifiers,
                String name, IScope enclosingScope, ITypeSymbol theParentTypeSymbol) {
            super(scopeHelper, definitionAst, modifiers, name, enclosingScope, theParentTypeSymbol);
        }

        @Override
        public boolean canBeUsedInIntersection() {
            return false;
        }
    }


    @Test
    public void resolveCaseInsensitive_NothingDefined_ReturnsNull() {
        ITSPHPAst ast = createAst("astText");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol();
        ISymbol result = typeSymbol.resolveCaseInsensitive(ast);

        assertNull(result);
    }

    @Test
    public void resolveCaseInsensitive_Defined_ReturnsSymbol() {
        ISymbol symbol = createSymbol("dummy");
        ITSPHPAst ast = createAst("dummy");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol();
        typeSymbol.define(symbol);
        ISymbol result = typeSymbol.resolveCaseInsensitive(ast);

        assertThat(result, is(symbol));
    }

    @Test
    public void resolveCaseInsensitive_WrongCase_ReturnsSymbol() {
        ISymbol symbol = createSymbol("dummy");
        ITSPHPAst ast = createAst("DUmmy");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol();
        typeSymbol.define(symbol);
        ISymbol result = typeSymbol.resolveCaseInsensitive(ast);

        assertThat(result, is(symbol));
    }

    @Test
    public void resolveWithFallbackToParent_Standard_DelegatesToScopeHelperAndReturnsFoundSymbol() {
        ITSPHPAst ast = createAst("dummy");
        IScopeHelper scopeHelper = mock(IScopeHelper.class);
        ISymbol symbol = mock(ISymbol.class);
        when(scopeHelper.resolve(any(IScope.class), any(ITSPHPAst.class))).thenReturn(symbol);


        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper);
        ISymbol result = typeSymbol.resolveWithFallbackToParent(ast);

        verify(scopeHelper).resolve(typeSymbol, ast);
        assertThat(result, is(symbol));
    }

    @Test
    public void resolveWithFallbackToParent_SymbolInParent_DelegatesToScopeHelperAndToParentAndReturnsFoundSymbol() {
        ITSPHPAst ast = createAst("dummy");
        IScopeHelper scopeHelper = mock(IScopeHelper.class);
        IRecordTypeSymbol parentTypeSymbol = mock(IRecordTypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("ParentClass");
        ISymbol symbol = mock(ISymbol.class);
        when(parentTypeSymbol.resolveWithFallbackToParent(ast)).thenReturn(symbol);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper, parentTypeSymbol);
        ISymbol result = typeSymbol.resolveWithFallbackToParent(ast);

        verify(scopeHelper).resolve(typeSymbol, ast);
        verify(parentTypeSymbol).resolveWithFallbackToParent(ast);
        assertThat(result, is(symbol));
    }

    @Test
    public void resolveWithFallbackToParent_NonExistingSymbolAndParentIsMixed_DelegatesToParentAndReturnsNull() {
        ITSPHPAst ast = createAst("dummy");
        IScopeHelper scopeHelper = mock(IScopeHelper.class);
        ISymbol symbol = mock(ISymbol.class);
        when(scopeHelper.resolve(any(IScope.class), any(ITSPHPAst.class))).thenReturn(symbol);
        IPseudoTypeSymbol parentTypeSymbol = mock(IPseudoTypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("mixed");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper, parentTypeSymbol);
        ISymbol result = typeSymbol.resolveWithFallbackToParent(ast);

        verify(scopeHelper).resolve(typeSymbol, ast);
        verify(parentTypeSymbol).getName();
        verifyNoMoreInteractions(parentTypeSymbol);
        assertThat(result, is(symbol));
    }

    @Test
    public void resolveWithFallbackToParent_NonExistingSymbolAndParentIsPolymorphic_DelegatesToParentAndReturnsNull() {
        ITSPHPAst ast = createAst("dummy");
        IScopeHelper scopeHelper = mock(IScopeHelper.class);
        ISymbol symbol = mock(ISymbol.class);
        when(scopeHelper.resolve(any(IScope.class), any(ITSPHPAst.class))).thenReturn(symbol);
        IRecordTypeSymbol parentTypeSymbol = mock(IRecordTypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("ParentClass");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper, parentTypeSymbol);
        ISymbol result = typeSymbol.resolveWithFallbackToParent(ast);

        verify(scopeHelper).resolve(typeSymbol, ast);
        verify(parentTypeSymbol).resolveWithFallbackToParent(ast);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void getParentTypeSymbols_Standard_ReturnsSetWithParentTypeSymbolPassedInConstructor() {
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("ParentClass");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(parentTypeSymbol);
        Set<ITypeSymbol> result = typeSymbol.getParentTypeSymbols();

        assertThat(result, containsInAnyOrder(parentTypeSymbol));
    }

    @Test
    public void addParentTypeSymbol_IfParentWasMixed_ReturnsSetWithPassedTypeSymbolOnly() {
        ITypeSymbol mixed = mock(ITypeSymbol.class);
        when(mixed.getName()).thenReturn("mixed");
        IRecordTypeSymbol parentTypeSymbol = mock(IRecordTypeSymbol.class);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(mixed);
        typeSymbol.addParentTypeSymbol(parentTypeSymbol);
        Set<ITypeSymbol> result = typeSymbol.getParentTypeSymbols();

        assertThat(result, containsInAnyOrder((ITypeSymbol) parentTypeSymbol));
    }

    @Test
    public void addParentTypeSymbol_IfParentWasNotMixed_ReturnsSetWithOldAndNewParentTypeSymbol() {
        ITypeSymbol parentTypeSymbol1 = mock(ITypeSymbol.class);
        when(parentTypeSymbol1.getName()).thenReturn("ParentType");
        IRecordTypeSymbol parentTypeSymbol2 = mock(IRecordTypeSymbol.class);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(parentTypeSymbol1);
        typeSymbol.addParentTypeSymbol(parentTypeSymbol2);
        Set<ITypeSymbol> result = typeSymbol.getParentTypeSymbols();

        assertThat(result, containsInAnyOrder(parentTypeSymbol1, parentTypeSymbol2));
    }

    @Test
    public void getAbstractSymbols_NothingDefinedAndParentIsNotPolymorphic_ReturnsEmptySet() {
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("mixed");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(parentTypeSymbol);
        Set<ISymbol> result = typeSymbol.getAbstractSymbols();

        verify(parentTypeSymbol).getName();
        verifyNoMoreInteractions(parentTypeSymbol);
        assertThat(result, empty());
    }

    @Test
    public void getAbstractSymbols_NothingDefinedAndParentIsPolymorphicButNotAbstract_ReturnsEmptySet() {
        IRecordTypeSymbol parentTypeSymbol = mock(IRecordTypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("ParentClass");
        when(parentTypeSymbol.isAbstract()).thenReturn(false);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(parentTypeSymbol);
        Set<ISymbol> result = typeSymbol.getAbstractSymbols();

        verify(parentTypeSymbol).getName();
        verify(parentTypeSymbol).isAbstract();
        verifyNoMoreInteractions(parentTypeSymbol);
        assertThat(result, empty());
    }

    @Test
    public void getAbstractSymbols_NothingDefinedAndParentIsPolymorphicAndAbstract_DelegatesParentAndReturnsNull() {
        IRecordTypeSymbol parentTypeSymbol = mock(IRecordTypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("ParentClass");
        when(parentTypeSymbol.isAbstract()).thenReturn(true);
        when(parentTypeSymbol.getAbstractSymbols()).thenReturn(new HashSet<ISymbol>());

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(parentTypeSymbol);
        Set<ISymbol> result = typeSymbol.getAbstractSymbols();

        verify(parentTypeSymbol).getAbstractSymbols();
        assertThat(result, empty());
    }

    @Test
    public void
    getAbstractSymbols_OneAbstractDefinedAndParentIsNotPolymorphic_ReturnsSetWithSymbol() {
        IScopeHelper scopeHelper = createScopeHelper();
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("mixed");
        IMethodSymbol symbol = mock(IMethodSymbol.class);
        when(symbol.getName()).thenReturn("dummy");
        when(symbol.isAbstract()).thenReturn(true);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper, parentTypeSymbol);
        typeSymbol.define(symbol);
        Set<ISymbol> result = typeSymbol.getAbstractSymbols();

        verify(scopeHelper).define(typeSymbol, symbol);
        assertThat(result, containsInAnyOrder((ISymbol) symbol));
    }

    @Test
    public void
    getAbstractSymbols_TwoAbstractDefinedAndParentIsNotPolymorphic_ReturnsSetWithBothSymbols() {
        IScopeHelper scopeHelper = createScopeHelper();
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("mixed");
        IMethodSymbol symbol1 = mock(IMethodSymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        when(symbol1.isAbstract()).thenReturn(true);
        IMethodSymbol symbol2 = mock(IMethodSymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");
        when(symbol2.isAbstract()).thenReturn(true);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper, parentTypeSymbol);
        typeSymbol.define(symbol1);
        typeSymbol.define(symbol2);
        Set<ISymbol> result = typeSymbol.getAbstractSymbols();

        verify(scopeHelper).define(typeSymbol, symbol1);
        verify(scopeHelper).define(typeSymbol, symbol2);
        assertThat(result, containsInAnyOrder((ISymbol) symbol1, symbol2));
    }


    @Test
    public void
    getAbstractSymbols_OneNoneAbstractAndTwoAbstractDefinedAndParentIsNotPolymorphic_ReturnsSetWithTwoSymbols() {
        IScopeHelper scopeHelper = createScopeHelper();
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("mixed");
        IMethodSymbol symbol1 = mock(IMethodSymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        when(symbol1.isAbstract()).thenReturn(true);
        IMethodSymbol symbol2 = mock(IMethodSymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");
        when(symbol2.isAbstract()).thenReturn(true);
        IMethodSymbol nonAbstract = mock(IMethodSymbol.class);
        when(nonAbstract.getName()).thenReturn("dummy3");
        when(nonAbstract.isAbstract()).thenReturn(false);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper, parentTypeSymbol);
        typeSymbol.define(symbol1);
        typeSymbol.define(nonAbstract);
        typeSymbol.define(symbol2);
        Set<ISymbol> result = typeSymbol.getAbstractSymbols();

        verify(scopeHelper).define(typeSymbol, symbol1);
        verify(scopeHelper).define(typeSymbol, nonAbstract);
        verify(scopeHelper).define(typeSymbol, symbol2);
        assertThat(result, containsInAnyOrder((ISymbol) symbol1, symbol2));
    }

    @Test
    public void
    getAbstractSymbols_AbstractAndNonAbstractDefinedAndParentIsPolymorphic_MergesSymbols() {
        IScopeHelper scopeHelper = createScopeHelper();

        //own symbols
        IMethodSymbol symbol1 = mock(IMethodSymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        when(symbol1.isAbstract()).thenReturn(true);
        IMethodSymbol symbol2 = mock(IMethodSymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");
        when(symbol2.isAbstract()).thenReturn(true);
        IMethodSymbol nonAbstract1 = mock(IMethodSymbol.class);
        when(nonAbstract1.getName()).thenReturn("dummy3");
        when(nonAbstract1.isAbstract()).thenReturn(false);
        IVariableSymbol nonAbstract2 = mock(IVariableSymbol.class);
        when(nonAbstract2.getName()).thenReturn("dummy4");

        //parent symbols
        IRecordTypeSymbol parentTypeSymbol = mock(IRecordTypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("ParentClass");
        when(parentTypeSymbol.isAbstract()).thenReturn(true);
        IMethodSymbol parentSymbol1 = mock(IMethodSymbol.class);
        when(parentSymbol1.getName()).thenReturn("dummy1");
        IMethodSymbol parentSymbol2 = mock(IMethodSymbol.class);
        when(parentSymbol2.getName()).thenReturn("dummy5");
        Set<ISymbol> abstractSymbols = new HashSet<>();
        abstractSymbols.add(parentSymbol1);
        abstractSymbols.add(parentSymbol2);
        when(parentTypeSymbol.getAbstractSymbols()).thenReturn(abstractSymbols);


        //act
        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper, parentTypeSymbol);
        typeSymbol.define(symbol1);
        typeSymbol.define(nonAbstract1);
        typeSymbol.define(symbol2);
        typeSymbol.define(nonAbstract2);
        Set<ISymbol> result = typeSymbol.getAbstractSymbols();


        verify(scopeHelper).define(typeSymbol, symbol1);
        verify(scopeHelper).define(typeSymbol, nonAbstract1);
        verify(scopeHelper).define(typeSymbol, symbol2);
        verify(scopeHelper).define(typeSymbol, nonAbstract2);
        assertThat(result, containsInAnyOrder((ISymbol) symbol1, symbol2, parentSymbol2));
    }

    @Test
    public void getAbstractSymbols_CalledASecondTime_DoesNotRecalculate() {
        IRecordTypeSymbol parentTypeSymbol = mock(IRecordTypeSymbol.class);
        when(parentTypeSymbol.getName()).thenReturn("ParentClass");
        when(parentTypeSymbol.isAbstract()).thenReturn(true);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(parentTypeSymbol);
        Set<ISymbol> result1 = typeSymbol.getAbstractSymbols();
        Set<ISymbol> result2 = typeSymbol.getAbstractSymbols();

        verify(parentTypeSymbol, times(1)).getAbstractSymbols();
        assertThat(result1, is(result2));
    }

    @Test
    public void getDefaultValue_Standard_ReturnsNull() {
        //no arrange necessary

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol();
        ITSPHPAst result = typeSymbol.getDefaultValue();

        assertThat(result.getType(), is(TokenTypes.Null));
        assertThat(result.getText(), is("null"));
    }


    @Test
    public void isFullyInitialised_NothingDefined_ReturnsFalse() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol();
        boolean result = typeSymbol.isFullyInitialised(symbol);

        assertThat(result, is(false));
    }

    @Test
    public void isFullyInitialised_FullyDefined_ReturnsTrue() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");
        IScopeHelper scopeHelper = createScopeHelper();

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper);
        typeSymbol.define(symbol);
        boolean result = typeSymbol.isFullyInitialised(symbol);

        assertThat(result, is(true));
    }

    @Test
    public void isPartiallyInitialised_NothingDefined_ReturnsFalse() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol();
        boolean result = typeSymbol.isPartiallyInitialised(symbol);

        assertThat(result, is(false));
    }

    @Test
    public void isPartiallyInitialised_FullyDefined_ReturnsFalse() {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("foo");
        IScopeHelper scopeHelper = createScopeHelper();

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(scopeHelper);
        typeSymbol.define(symbol);
        boolean result = typeSymbol.isPartiallyInitialised(symbol);

        assertThat(result, is(false));
    }

    private ISymbol createSymbol(String name) {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn(name);
        return symbol;
    }

    private ITSPHPAst createAst(String text) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn(text);
        return ast;
    }

    private IScopeHelper createScopeHelper() {
        IScopeHelper scopeHelper = mock(IScopeHelper.class);
        doAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                ISymbol symbol = (ISymbol) args[1];
                ((IScope) args[0]).getSymbols().put(symbol.getName(), Arrays.asList(symbol));
                return null;
            }
        }).when(scopeHelper).define(any(IScope.class), any(ISymbol.class));
        return scopeHelper;
    }

    private IRecordTypeSymbol createPolymorphicTypeSymbol() {
        return createPolymorphicTypeSymbol(mock(IScopeHelper.class));
    }

    private IRecordTypeSymbol createPolymorphicTypeSymbol(IScopeHelper scopeHelper) {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getName()).thenReturn("dummy");
        return createPolymorphicTypeSymbol(scopeHelper, typeSymbol);
    }

    private IRecordTypeSymbol createPolymorphicTypeSymbol(ITypeSymbol parentTypeSymbol) {
        return createPolymorphicTypeSymbol(
                mock(IScopeHelper.class),
                parentTypeSymbol
        );
    }

    private IRecordTypeSymbol createPolymorphicTypeSymbol(
            IScopeHelper scopeHelper, ITypeSymbol parentTypeSymbol) {
        return createPolymorphicTypeSymbol(
                scopeHelper,
                mock(ITSPHPAst.class),
                mock(IModifierSet.class),
                "foo",
                mock(IScope.class),
                parentTypeSymbol
        );
    }

    protected IRecordTypeSymbol createPolymorphicTypeSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet modifiers,
            String name,
            IScope enclosingScope,
            ITypeSymbol parentTypeSymbol) {
        return new DummyRecordTypeSymbol(
                scopeHelper, definitionAst, modifiers, name, enclosingScope, parentTypeSymbol);
    }
}
