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
import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IAliasSymbol;
import ch.tsphp.tinsphp.common.symbols.IAliasTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IScalarTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousLazySymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.ILazySymbolResolver;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        IPseudoTypeSymbol result = symbolFactory.createPseudoTypeSymbol(name, mock(ITypeSymbol.class), false);

        assertThat(result.getName(), is(name));
    }


    @Test
    public void createPseudoTypeSymbol2_PassedParentTypeSymbol_ParentTypeIsPassedTypeSymbol() {
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IPseudoTypeSymbol result = symbolFactory.createPseudoTypeSymbol("", parentTypeSymbol, false);

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
    public void createUnionTypeSymbol_Standard_HasNoTypeSymbols() {
        //no arrange necessary

        ISymbolFactory symbolFactory = createSymbolFactory();
        IUnionTypeSymbol result = symbolFactory.createUnionTypeSymbol();

        assertThat(result.getTypeSymbols(), is(not(nullValue())));
        assertThat(result.getTypeSymbols().size(), is(0));
    }

    @Test
    public void createIntersectionTypeSymbol_Standard_HasNoTypeSymbols() {
        //no arrange necessary

        ISymbolFactory symbolFactory = createSymbolFactory();
        IIntersectionTypeSymbol result = symbolFactory.createIntersectionTypeSymbol();

        assertThat(result.getTypeSymbols(), is(not(nullValue())));
        assertThat(result.getTypeSymbols().size(), is(0));
    }

    @Test
    public void createOverloadSymbol_Standard_NameIsPassedName() {
        String name = "+";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IMinimalMethodSymbol result = symbolFactory.createMinimalMethodSymbol(name);

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createFunctionType_Standard_NameIsPassedName() {
        String name = "+";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IFunctionType result = symbolFactory.createFunctionType(
                name, mock(IBindingCollection.class), new ArrayList<IVariable>());

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createFunctionType_Standard_BindingCollectionIsPassedBindingCollection() {
        IBindingCollection bindingCollection = mock(IBindingCollection.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IFunctionType result = symbolFactory.createFunctionType("+", bindingCollection, new ArrayList<IVariable>());

        assertThat(result.getBindingCollection(), is(bindingCollection));
    }

    @Test
    public void createFunctionType_Standard_ParametersArePassedParameters() {
        List<IVariable> parameters = new ArrayList<>();

        ISymbolFactory symbolFactory = createSymbolFactory();
        IFunctionType result = symbolFactory.createFunctionType("+", mock(IBindingCollection.class), parameters);

        assertThat(result.getParameters(), is(parameters));
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
    public void createVariable_Standard_NameIsPassedName() {
        String name = "$dummy";

        ISymbolFactory symbolFactory = createSymbolFactory();
        IVariable result = symbolFactory.createVariable(name);

        assertThat(result.getName(), is(name));
    }

    @Test
    public void createExpressionVariableSymbol_Standard_DefinitionAstIsPassedAst() {
        ITSPHPAst ast = mock(ITSPHPAst.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IMinimalVariableSymbol result = symbolFactory.createExpressionVariableSymbol(ast);

        assertThat(result.getDefinitionAst(), is(ast));
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

    @Test
    public void createConstraint_Standard_OperatorIsPassedOperator() {
        ITSPHPAst operator = mock(ITSPHPAst.class);
        IVariable leftHandSide = mock(IVariable.class);
        List<IVariable> arguments = new ArrayList<>();
        IMinimalMethodSymbol methodSymbol = mock(IMinimalMethodSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IConstraint result = symbolFactory.createConstraint(operator, leftHandSide, arguments, methodSymbol);

        assertThat(result.getOperator(), is(operator));
    }

    @Test
    public void createConstraint_Standard_LeftHandSideIsPassedLeftHandSide() {
        ITSPHPAst operator = mock(ITSPHPAst.class);
        IVariable leftHandSide = mock(IVariable.class);
        List<IVariable> arguments = new ArrayList<>();
        IMinimalMethodSymbol methodSymbol = mock(IMinimalMethodSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IConstraint result = symbolFactory.createConstraint(operator, leftHandSide, arguments, methodSymbol);

        assertThat(result.getLeftHandSide(), is(leftHandSide));
    }


    @Test
    public void createConstraint_Standard_ArgumentsArePassedArguments() {
        ITSPHPAst operator = mock(ITSPHPAst.class);
        IVariable leftHandSide = mock(IVariable.class);
        List<IVariable> arguments = new ArrayList<>();
        IMinimalMethodSymbol methodSymbol = mock(IMinimalMethodSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IConstraint result = symbolFactory.createConstraint(operator, leftHandSide, arguments, methodSymbol);

        assertThat(result.getArguments(), is(arguments));
    }

    @Test
    public void createConstraint_Standard_MethodSymbolIsPassedMethodSymbol() {
        ITSPHPAst operator = mock(ITSPHPAst.class);
        IVariable leftHandSide = mock(IVariable.class);
        List<IVariable> arguments = new ArrayList<>();
        IMinimalMethodSymbol methodSymbol = mock(IMinimalMethodSymbol.class);

        ISymbolFactory symbolFactory = createSymbolFactory();
        IConstraint result = symbolFactory.createConstraint(operator, leftHandSide, arguments, methodSymbol);

        assertThat(result.getMethodSymbol(), is(methodSymbol));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBindingCollection_NotTheOneOfThisComponent_ThrowsIllegalArgumentException() {
        //no arrange necessary

        ISymbolFactory symbolFactory = createSymbolFactory();
        symbolFactory.createBindingCollection(mock(IBindingCollection.class));

        //assert in annotation
    }

    private ISymbolFactory createSymbolFactory() {
        return createSymbolFactory(mock(IModifierHelper.class));
    }

    private ISymbolFactory createSymbolFactory(IModifierHelper modifierHelper) {
        return createSymbolFactory(
                mock(IScopeHelper.class),
                modifierHelper,
                mock(ITypeHelper.class)
        );
    }

    protected ISymbolFactory createSymbolFactory(
            IScopeHelper theScopeHelper,
            IModifierHelper theModifierHelper,
            ITypeHelper theTypeHelper) {
        return new SymbolFactory(theScopeHelper, theModifierHelper, theTypeHelper);
    }
}
