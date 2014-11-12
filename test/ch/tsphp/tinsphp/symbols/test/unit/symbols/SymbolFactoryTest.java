/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IAliasSymbol;
import ch.tsphp.tinsphp.common.symbols.IAliasTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousVariableSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SymbolFactoryTest
{
    @Test
    public void createAliaSymbol_Standard_DefinitionAstIsPassedAstAndNameIsPassedName() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IAliasSymbol result = symbolFactory.createAliasSymbol(ast, name);

        assertThat(result.getDefinitionAst(), is(ast));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void createAliasTypeSymbol_Standard_HasMixedAsParent() {
        ITypeSymbol mixed = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        symbolFactory.setMixedTypeSymbol(mixed);
        IAliasTypeSymbol result = symbolFactory.createAliasTypeSymbol(mock(ITSPHPAst.class), "foo");

        assertThat(result.getParentTypeSymbols(), containsInAnyOrder(mixed));
    }

    @Test
    public void createAliasTypeSymbol_Standard_DefinitionAstIsPassedAstAndNameIsPassedName() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IAliasTypeSymbol result = symbolFactory.createAliasTypeSymbol(ast, name);

        assertThat(result.getDefinitionAst(), is(ast));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void createMethodSymbol_Standard_DefinitionAstIsPassedIdentifierAndNameIsTextOfPassedIdentifier() {
        ITSPHPAst identifier = mock(ITSPHPAst.class);
        String name = "foo";
        when(identifier.getText()).thenReturn(name);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IMethodSymbol result = symbolFactory.createMethodSymbol(
                mock(ITSPHPAst.class), mock(ITSPHPAst.class), identifier, mock(IScope.class));

        assertThat(result.getDefinitionAst(), is(identifier));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void createMethodSymbol_Standard_EnclosingScopeIsPassedScope() {
        IScope scope = mock(IScope.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IMethodSymbol result = symbolFactory.createMethodSymbol(
                mock(ITSPHPAst.class), mock(ITSPHPAst.class), mock(ITSPHPAst.class), scope);

        assertThat(result.getEnclosingScope(), is(scope));
    }

    @Test
    public void createMethodSymbol_Standard_ModifiersAreCalculatedUsingModifierHelper() {
        ITSPHPAst modifiers = mock(ITSPHPAst.class);
        IModifierHelper modifierHelper = mock(IModifierHelper.class);
        IModifierSet modifierSet = mock(IModifierSet.class);
        when(modifierHelper.getModifiers(modifiers)).thenReturn(modifierSet);

        ISymbolFactory symbolFactory = createSymbolFactory(modifierHelper);
        IMethodSymbol result = symbolFactory.createMethodSymbol(
                modifiers, mock(ITSPHPAst.class), mock(ITSPHPAst.class), mock(IScope.class));

        verify(modifierHelper).getModifiers(modifiers);
        assertThat(result.getModifiers(), is(modifierSet));
    }

    @Test
    public void createMethodSymbol_Standard_ReturnModifiersAreCalculatedUsingModifierHelper() {
        ITSPHPAst returnModifiers = mock(ITSPHPAst.class);
        IModifierHelper modifierHelper = mock(IModifierHelper.class);
        when(modifierHelper.getModifiers(returnModifiers))
                .thenReturn(new ModifierSet(
                        Arrays.asList(TokenTypes.Cast, TokenTypes.QuestionMark, TokenTypes.LogicNot)));

        ISymbolFactory symbolFactory = createSymbolFactory(modifierHelper);
        IMethodSymbol result = symbolFactory.createMethodSymbol(
                mock(ITSPHPAst.class), returnModifiers, mock(ITSPHPAst.class), mock(IScope.class));

        verify(modifierHelper).getModifiers(returnModifiers);
        assertThat(result.isFalseable(), is(true));
        assertThat(result.isNullable(), is(true));
        assertThat(result.isAlwaysCasting(), is(true));
    }

    @Test
    public void createVariableSymbol_Standard_DefinitionAstIsPassedVariableIdAndNameIsVariableIdText() {
        ITSPHPAst variableId = mock(ITSPHPAst.class);
        String name = "foo";
        when(variableId.getText()).thenReturn(name);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IVariableSymbol result = symbolFactory.createVariableSymbol(mock(ITSPHPAst.class), variableId);

        assertThat(result.getDefinitionAst(), is(variableId));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void createVariableSymbol_Standard_ModifiersAreCalculatedUsingModifierHelper() {
        ITSPHPAst modifiers = mock(ITSPHPAst.class);
        IModifierHelper modifierHelper = mock(IModifierHelper.class);
        IModifierSet modifierSet = mock(IModifierSet.class);
        when(modifierHelper.getModifiers(modifiers)).thenReturn(modifierSet);

        ISymbolFactory symbolFactory = createSymbolFactory(modifierHelper);
        IVariableSymbol result = symbolFactory.createVariableSymbol(modifiers, mock(ITSPHPAst.class));

        verify(modifierHelper).getModifiers(modifiers);
        assertThat(result.getModifiers(), is(modifierSet));
    }

    @Test
    public void createErroneousTypeSymbol_Standard_DefinitionAstIsPassedVariableIdAndNameIsVariableIdText() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";
        when(ast.getText()).thenReturn(name);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousTypeSymbol result = symbolFactory.createErroneousTypeSymbol(ast, new TSPHPException());

        assertThat(result.getDefinitionAst(), is(ast));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void
    createErroneousTypeSymbol_Standard_ExceptionIsPassedException() {
        TSPHPException exception = new TSPHPException();

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousTypeSymbol result = symbolFactory.createErroneousTypeSymbol(mock(ITSPHPAst.class), exception);

        assertThat(result.getException(), is(exception));
    }

    @Test
    public void createErroneousMethodSymbol_Standard_DefinitionAstIsPassedVariableIdAndNameIsVariableIdText() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";
        when(ast.getText()).thenReturn(name);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousMethodSymbol result = symbolFactory.createErroneousMethodSymbol(ast, new TSPHPException());

        assertThat(result.getDefinitionAst(), is(ast));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void createErroneousMethodSymbol_Standard_ExceptionIsPassedException() {
        TSPHPException exception = new TSPHPException();

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousMethodSymbol result = symbolFactory.createErroneousMethodSymbol(mock(ITSPHPAst.class), exception);

        assertThat(result.getException(), is(exception));
    }

    @Test
    public void createErroneousVariableSymbol_Standard_DefinitionAstIsPassedVariableIdAndNameIsVariableIdText() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";
        when(ast.getText()).thenReturn(name);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousVariableSymbol result = symbolFactory.createErroneousVariableSymbol(ast, new TSPHPException());

        assertThat(result.getDefinitionAst(), is(ast));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void createErroneousVariableSymbol_Standard_ExceptionIsPassedException() {
        TSPHPException exception = new TSPHPException();

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousVariableSymbol result = symbolFactory.createErroneousVariableSymbol(mock(ITSPHPAst.class), exception);

        assertThat(result.getException(), is(exception));
    }

    private ISymbolFactory createSymbolFactory() {
        return createSymbolFactory(mock(IModifierHelper.class));
    }

    private ISymbolFactory createSymbolFactory(IModifierHelper modifierHelper) {
        return createSymbolFactory(mock(IScopeHelper.class), modifierHelper);
    }

    protected ISymbolFactory createSymbolFactory(IScopeHelper theScopeHelper, IModifierHelper theModifierHelper) {
        return new SymbolFactory(theScopeHelper, theModifierHelper);
    }
}
