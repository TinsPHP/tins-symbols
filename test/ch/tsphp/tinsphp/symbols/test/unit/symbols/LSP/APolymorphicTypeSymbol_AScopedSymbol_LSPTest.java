/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.symbols.ARecordTypeSymbol;
import ch.tsphp.tinsphp.symbols.AScopedSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.AScopedSymbolTest;
import org.junit.Test;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class APolymorphicTypeSymbol_AScopedSymbol_LSPTest extends AScopedSymbolTest
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

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void getInitialisedSymbols_NothingDefined_ReturnsEmptyMap() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        //no arrange necessary

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();
        // end same as in ASymbolTest

//        assertThat(result.size(), is(0));

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void getInitialisedSymbols_OnePartiallyDefined_ReturnsMapWithSymbolNameAndFalse() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol, false);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();
        // end same as in ASymbolTest

//        assertThat(result, hasEntry("dummy", false));

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void getInitialisedSymbols_TwoPartiallyDefined_ReturnsMapWithBothSymbolNameAndFalse() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, false);
        scopedSymbol.addToInitialisedSymbols(symbol2, false);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();
        // end same as in ASymbolTest

//        assertThat(result, hasEntry("dummy1", false));
//        assertThat(result, hasEntry("dummy2", false));

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void getInitialisedSymbols_OnFullyDefined_ReturnsMapWithSymbolNameAndTrue() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol, true);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();
        // end same as in ASymbolTest

//        assertThat(result, hasEntry("dummy", true));

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void getInitialisedSymbols_TwoFullyDefined_ReturnsMapWithBothSymbolNameAndTrue() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, true);
        scopedSymbol.addToInitialisedSymbols(symbol2, true);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();
        // end same as in ASymbolTest

//        assertThat(result, hasEntry("dummy1", true));
//        assertThat(result, hasEntry("dummy2", true));

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void getInitialisedSymbols_OnePartiallyOneFullyDefined_ReturnsMapWithBothAndCorrespondingState() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy1");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy2");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, false);
        scopedSymbol.addToInitialisedSymbols(symbol2, true);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();
        // end same as in ASymbolTest

//        assertThat(result, hasEntry("dummy1", false));
//        assertThat(result, hasEntry("dummy2", true));

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addToInitialisedSymbols_AlreadyPartiallyInitialised_DoesNotThrowError() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, false);
        scopedSymbol.addToInitialisedSymbols(symbol2, false);
        // end same as in ASymbolTest

//        //no assert necessary, just make sure no exception is thrown

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addToInitialisedSymbols_AlreadyFullyInitialised_DoesNotThrowError() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, true);
        scopedSymbol.addToInitialisedSymbols(symbol2, true);
        // end same as in ASymbolTest

//        //no assert necessary, just make sure no exception is thrown

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addToInitialisedSymbols_UpdateFromPartiallyToFullyInitialised_ReturnsTrue() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, false);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();
        // end same as in ASymbolTest

//        //assert
//        assertThat(result, hasEntry("dummy", false));
//
//        //act continued
//        scopedSymbol.addToInitialisedSymbols(symbol2, true);
//        //assert 2
//        assertThat(result, hasEntry("dummy", true));

        //assert is in annotation
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void addToInitialisedSymbols_UpdateFromFullyToPartiallyInitialised_RemainsTrue() {
        // different behaviour - polymorphic types are always initially initialised
        // and thus do not support this method

        // start same as in AScopedSymbolTest
        ISymbol symbol1 = mock(ISymbol.class);
        when(symbol1.getName()).thenReturn("dummy");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("dummy");

        AScopedSymbol scopedSymbol = createScopedSymbol("foo");
        scopedSymbol.addToInitialisedSymbols(symbol1, true);
        Map<String, Boolean> result = scopedSymbol.getInitialisedSymbols();
        // end same as in ASymbolTest

//        //assert
//        assertThat(result, hasEntry("dummy", true));
//
//        //act continued
//        scopedSymbol.addToInitialisedSymbols(symbol2, false);
//        //assert 2
//        assertThat(result, hasEntry("dummy", true));

        //assert is in annotation
    }

    @Override
    public void define_Standard_DelegatesToScopeHelper() {
        // additional behaviour - APolymorphicTypeSymbol adds the given symbol to the case insensitive symbols as well

        // start same as in AScopedSymbolTest
        IScopeHelper scopeHelper = mock(IScopeHelper.class);
        ISymbol symbol = mock(ISymbol.class);

        // start additional arrange
        when(symbol.getName()).thenReturn("dummy");
        // end additional arrange

        AScopedSymbol scopedSymbol = createScopedSymbol(scopeHelper);
        scopedSymbol.define(symbol);

        verify(scopeHelper).define(scopedSymbol, symbol);
        // end same as in ASymbolTest

        verify(symbol).getName();
    }

    private AScopedSymbol createScopedSymbol(String name) {
        return createScopedSymbol(
                mock(IScopeHelper.class), mock(ITSPHPAst.class), mock(IModifierSet.class), name, mock(IScope.class));
    }

    private AScopedSymbol createScopedSymbol(IScopeHelper scopeHelper) {
        return createScopedSymbol(
                scopeHelper, mock(ITSPHPAst.class), mock(IModifierSet.class), "foo", mock(IScope.class));
    }

    @Override
    protected AScopedSymbol createScopedSymbol(IScopeHelper scopeHelper, ITSPHPAst definitionAst,
            IModifierSet modifiers, String name, IScope enclosingScope) {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getName()).thenReturn("dummy");
        return new DummyRecordTypeSymbol(
                scopeHelper, definitionAst, modifiers, name, enclosingScope, typeSymbol);
    }
}
