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
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IAliasSymbol;
import ch.tsphp.tinsphp.common.symbols.IAliasTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.INullTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IOverloadSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IScalarTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousLazySymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.ILazySymbolResolver;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SymbolFactoryTest
{
    @Test
    public void getMixedTypeSymbol_NothingSet_ReturnsNull() {
        //no arrange necessary

        ISymbolFactory symbolFactory = createSymbolFactory();
        ITypeSymbol result = symbolFactory.getMixedTypeSymbol();

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getMixedTypeSymbol_TypeSymbolSet_ReturnsTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        symbolFactory.setMixedTypeSymbol(typeSymbol);
        ITypeSymbol result = symbolFactory.getMixedTypeSymbol();

        assertThat(result, is(typeSymbol));
    }

    @Test
    public void createNullTypeSymbol_Standard_ReturnsAlwaysTheSameObject() {
        //no arrange necessary

        ISymbolFactory symbolFactory = createSymbolFactory();
        INullTypeSymbol result1 = symbolFactory.createNullTypeSymbol();
        INullTypeSymbol result2 = symbolFactory.createNullTypeSymbol();

        assertThat(result1, is(result2));
    }

    @Test
    public void createNullTypeSymbol_MixedNotYetSet_ReturnsNull() {
        //see TINS-350 null is not subtype of mixed

        //no arrange necessary

        ISymbolFactory symbolFactory = createSymbolFactory();
        INullTypeSymbol result = symbolFactory.createNullTypeSymbol();

        assertThat(result, is(nullValue()));
    }

    @Test
    public void createNullTypeSymbol_MixedSet_MixedIsParent() {
        //see TINS-350 null is not subtype of mixed

        ITypeSymbol mixedTypeSymbol = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        symbolFactory.setMixedTypeSymbol(mixedTypeSymbol);
        INullTypeSymbol result = symbolFactory.createNullTypeSymbol();

        assertThat(result.getParentTypeSymbols(), hasItems(mixedTypeSymbol));
        assertThat(result.getParentTypeSymbols().size(), is(1));
    }

    @Test
    public void createScalarTypeSymbol_Standard_NameIsPassedName() {
        String name = "foo";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IScalarTypeSymbol result = symbolFactory.createScalarTypeSymbol(name, null, 0, "");

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createScalarTypeSymbol_Standard_ParentTypeSymbolIsPassedTypeSymbol() {
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IScalarTypeSymbol result = symbolFactory.createScalarTypeSymbol("", parentTypeSymbol, 0, "");

        assertThat(result.getParentTypeSymbols(), containsInAnyOrder(parentTypeSymbol));
    }


    @Test
    public void createScalarTypeSymbol_Standard_GetDefaultValueIsBuiltOutOfPassedDefaultTokenAndValue() {
        int defaultTokenType = 389;
        String defaultValue = "hello";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IScalarTypeSymbol result = symbolFactory.createScalarTypeSymbol("", null, defaultTokenType, defaultValue);

        ITSPHPAst ast = result.getDefaultValue();
        assertThat(ast.getType(), is(defaultTokenType));
        assertThat(ast.getText(), is(defaultValue));
    }

    @Test
    public void createArrayTypeSymbol_Standard_NameIsPassedName() {
        String name = "foo";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IArrayTypeSymbol result = symbolFactory.createArrayTypeSymbol(name, null, null);

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createArrayTypeSymbol_Standard_KeyTypeSymbolIsPassedTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IArrayTypeSymbol result = symbolFactory.createArrayTypeSymbol("", typeSymbol, null);

        assertThat(result.getKeyTypeSymbol(), is(typeSymbol));
    }

    @Test
    public void createArrayTypeSymbol_Standard_ValueTypeSymbolIsPassedTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IArrayTypeSymbol result = symbolFactory.createArrayTypeSymbol("", null, typeSymbol);

        assertThat(result.getValueTypeSymbol(), is(typeSymbol));
    }

    @Test
    public void createPseudoTypeSymbol1_Standard_NameIsPassedName() {
        String name = "foo";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IPseudoTypeSymbol result = symbolFactory.createPseudoTypeSymbol(name);

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createPseudoTypeSymbol1_Standard_ParentTypeIsDefinedMixed() {
        ITypeSymbol mixed = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        symbolFactory.setMixedTypeSymbol(mixed);
        IPseudoTypeSymbol result = symbolFactory.createPseudoTypeSymbol("");

        assertThat(result.getParentTypeSymbols(), containsInAnyOrder(mixed));
    }

    @Test
    public void createPseudoTypeSymbol2_Standard_NameIsPassedName() {
        String name = "foo";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IPseudoTypeSymbol result = symbolFactory.createPseudoTypeSymbol(name, mock(ITypeSymbol.class));

        assertThat(result.getName(), is(name));
    }


    @Test
    public void createPseudoTypeSymbol2_PassedParentTypeSymbol_ParentTypeIsPassedTypeSymbol() {
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IPseudoTypeSymbol result = symbolFactory.createPseudoTypeSymbol("", parentTypeSymbol);

        assertThat(result.getParentTypeSymbols(), containsInAnyOrder(parentTypeSymbol));
    }


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
    public void createClassTypeSymbol_Standard_DefinitionAstIsPassedIdentifierAndNameIsTextOfIdentifier() {
        ITSPHPAst identifier = mock(ITSPHPAst.class);
        String name = "foo";
        when(identifier.getText()).thenReturn(name);
        //required that we do not get NullPointers for this test
        ITypeSymbol mixed = mock(ITypeSymbol.class);
        when(mixed.getName()).thenReturn("mixed");
        IModifierHelper modifierHelper = mock(IModifierHelper.class);
        when(modifierHelper.getModifiers(any(ITSPHPAst.class))).thenReturn(new ModifierSet());

        ISymbolFactory symbolFactory = createSymbolFactory(modifierHelper);
        symbolFactory.setMixedTypeSymbol(mixed);
        IClassTypeSymbol result = symbolFactory.createClassTypeSymbol(null, identifier, null);

        assertThat(result.getDefinitionAst(), is(identifier));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void createClassTypeSymbol_Standard_EnclosingScopeIsPassedScope() {
        IScope scope = mock(IScope.class);
        //required that we do not get NullPointers for this test
        ITypeSymbol mixed = mock(ITypeSymbol.class);
        when(mixed.getName()).thenReturn("mixed");
        IModifierHelper modifierHelper = mock(IModifierHelper.class);
        when(modifierHelper.getModifiers(any(ITSPHPAst.class))).thenReturn(new ModifierSet());

        ISymbolFactory symbolFactory = createSymbolFactory(modifierHelper);
        symbolFactory.setMixedTypeSymbol(mixed);
        IClassTypeSymbol result = symbolFactory.createClassTypeSymbol(null, mock(ITSPHPAst.class), scope);

        assertThat(result.getEnclosingScope(), is(scope));
    }

    @Test
    public void createClassTypeSymbol_Standard_ParentTypeIsDefinedMixed() {
        IScope scope = mock(IScope.class);
        //required that we do not get NullPointers for this test
        ITypeSymbol mixed = mock(ITypeSymbol.class);
        when(mixed.getName()).thenReturn("mixed");
        IModifierHelper modifierHelper = mock(IModifierHelper.class);
        when(modifierHelper.getModifiers(any(ITSPHPAst.class))).thenReturn(new ModifierSet());

        ISymbolFactory symbolFactory = createSymbolFactory(modifierHelper);
        symbolFactory.setMixedTypeSymbol(mixed);
        IClassTypeSymbol result = symbolFactory.createClassTypeSymbol(null, mock(ITSPHPAst.class), scope);

        assertThat(result.getParentTypeSymbols(), containsInAnyOrder(mixed));
    }

    @Test
    public void createClassTypeSymbol_Standard_ModifiersAreResultOfPassedModifierAstToModifierHelper() {
        IScope scope = mock(IScope.class);
        //required that we do not get NullPointers for this test
        ITypeSymbol mixed = mock(ITypeSymbol.class);
        when(mixed.getName()).thenReturn("mixed");
        IModifierHelper modifierHelper = mock(IModifierHelper.class);
        IModifierSet set = new ModifierSet();
        when(modifierHelper.getModifiers(any(ITSPHPAst.class))).thenReturn(set);

        ISymbolFactory symbolFactory = createSymbolFactory(modifierHelper);
        symbolFactory.setMixedTypeSymbol(mixed);
        IClassTypeSymbol result = symbolFactory.createClassTypeSymbol(null, mock(ITSPHPAst.class), scope);

        assertThat(result.getModifiers(), is(set));
    }


    @Test
    public void createUnionTypeSymbol_WithoutHashMap_StillReturnsAnEmptyMap() {
        //no arrange necessary

        ISymbolFactory symbolFactory = createSymbolFactory();
        IUnionTypeSymbol result = symbolFactory.createUnionTypeSymbol();

        assertThat(result.getTypeSymbols(), is(not(nullValue())));
        assertThat(result.getTypeSymbols().size(), is(0));
    }

    @Test
    public void createUnionTypeSymbol_Standard_TypesArePassedTypes() {
        Map<String, ITypeSymbol> types = new HashMap<>();

        ISymbolFactory symbolFactory = createSymbolFactory();
        IUnionTypeSymbol result = symbolFactory.createUnionTypeSymbol(types);

        assertThat(result.getTypeSymbols(), is(types));
    }

    @Test
    public void createOverloadSymbol_Standard_NameIsPassedName() {
        String name = "+";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IOverloadSymbol result = symbolFactory.createOverloadSymbol(name);

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createFunctionTypeSymbol_Standard_NameIsPassedName() {
        String name = "+";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IFunctionTypeSymbol result = symbolFactory.createFunctionTypeSymbol(
                name, mock(ITypeVariableCollection.class), new ArrayList<IVariable>(), mock(IVariable.class));

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createFunctionTypeSymbol_Standard_TypeVariablesArePassedTypeVariables() {
        ITypeVariableCollection typeVariables = mock(ITypeVariableCollection.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IFunctionTypeSymbol result = symbolFactory.createFunctionTypeSymbol(
                "+", typeVariables, new ArrayList<IVariable>(), mock(IVariable.class));

        assertThat(result.getTypeVariables(), is(typeVariables));
    }

    @Test
    public void createFunctionTypeSymbol_Standard_ParametersArePassedParameters() {
        List<IVariable> parameters = new ArrayList<>();

        ISymbolFactory symbolFactory = createSymbolFactory();
        IFunctionTypeSymbol result = symbolFactory.createFunctionTypeSymbol(
                "+", mock(ITypeVariableCollection.class), parameters, mock(IVariable.class));

        assertThat(result.getParameters(), is(parameters));
    }

    @Test
    public void createFunctionTypeSymbol_Standard_ReturnTypeVariableIsPassedTypeVariable() {
        IVariable returnVariable = mock(IVariable.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IFunctionTypeSymbol result = symbolFactory.createFunctionTypeSymbol(
                "+", mock(ITypeVariableCollection.class), new ArrayList<IVariable>(), returnVariable);

        assertThat(result.getReturnVariable(), is(returnVariable));
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

    @Test
    public void createErroneousLazySymbol_Standard_LazyResolverIsPassedResolver() {
        ILazySymbolResolver lazySymbolResolver = mock(ILazySymbolResolver.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousLazySymbol result = symbolFactory.createErroneousLazySymbol(
                lazySymbolResolver, mock(ITSPHPAst.class), new TSPHPException());
        result.resolveSymbolLazily();

        verify(lazySymbolResolver).resolve();
    }

    @Test
    public void createErroneousLazySymbol_Standard_DefinitionAstIsPassedAstAndNameIsAstText() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";
        when(ast.getText()).thenReturn(name);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousLazySymbol result = symbolFactory.createErroneousLazySymbol(
                mock(ILazySymbolResolver.class), ast, new TSPHPException());

        assertThat(result.getDefinitionAst(), is(ast));
        assertThat(result.getName(), is(name));
    }

    @Test
    public void createErroneousLazySymbol_Standard_ExceptionIsPassedException() {
        TSPHPException exception = new TSPHPException();

        ISymbolFactory symbolFactory = createSymbolFactory();
        IErroneousLazySymbol result = symbolFactory.createErroneousLazySymbol(
                mock(ILazySymbolResolver.class), mock(ITSPHPAst.class), exception);

        assertThat(result.getException(), is(exception));
    }


    private ISymbolFactory createSymbolFactory() {
        return createSymbolFactory(mock(IModifierHelper.class));
    }

    private ISymbolFactory createSymbolFactory(IModifierHelper modifierHelper) {
        return createSymbolFactory(
                mock(IScopeHelper.class),
                modifierHelper,
                mock(IOverloadResolver.class)
        );
    }

    protected ISymbolFactory createSymbolFactory(
            IScopeHelper theScopeHelper,
            IModifierHelper theModifierHelper,
            IOverloadResolver overloadResolver) {
        return new SymbolFactory(theScopeHelper, theModifierHelper, overloadResolver);
    }
}
