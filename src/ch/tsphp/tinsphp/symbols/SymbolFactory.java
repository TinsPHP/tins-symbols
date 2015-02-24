/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class SymbolFactory from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */


package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ILazyTypeSymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IAliasSymbol;
import ch.tsphp.tinsphp.common.symbols.IAliasTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.INullTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IScalarTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousLazySymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.ILazySymbolResolver;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousLazySymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousMethodSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousTypeSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousVariableSymbol;

import java.util.Map;

public class SymbolFactory implements ISymbolFactory
{
    private final IScopeHelper scopeHelper;
    private final IModifierHelper modifierHelper;
    private final IOverloadResolver overloadResolver;
    private ITypeSymbol mixedTypeSymbol = null;

    public SymbolFactory(
            IScopeHelper theScopeHelper, IModifierHelper theModifierHelper, IOverloadResolver theOverloadResolver) {
        scopeHelper = theScopeHelper;
        modifierHelper = theModifierHelper;
        overloadResolver = theOverloadResolver;
    }

    @Override
    public void setMixedTypeSymbol(ITypeSymbol typeSymbol) {
        mixedTypeSymbol = typeSymbol;
    }

    public ITypeSymbol getMixedTypeSymbol() {
        return mixedTypeSymbol;
    }

    @Override
    public INullTypeSymbol createNullTypeSymbol() {
        return new NullTypeSymbol();
    }

    //
//    @Override
//    public IVoidTypeSymbol createVoidTypeSymbol() {
//        return new VoidTypeSymbol();
//
//    }
//
    @Override
    @SuppressWarnings("checkstyle:parameternumber")
    public IScalarTypeSymbol createScalarTypeSymbol(
            String name,
            ITypeSymbol parentTypeSymbol,
            int defaultValueTokenType,
            String defaultValue) {

        return new ScalarTypeSymbol(name, parentTypeSymbol, defaultValueTokenType, defaultValue);
    }

    @Override
    public IArrayTypeSymbol createArrayTypeSymbol(String name, ITypeSymbol keyValue, ITypeSymbol valueType) {
        return new ArrayTypeSymbol(name, keyValue, valueType, mixedTypeSymbol);
    }

    @Override
    public IPseudoTypeSymbol createPseudoTypeSymbol(String name) {
        return createPseudoTypeSymbol(name, mixedTypeSymbol);
    }

    @Override
    public IPseudoTypeSymbol createPseudoTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        return new PseudoTypeSymbol(name, parentTypeSymbol);
    }

    @Override
    public IAliasSymbol createAliasSymbol(ITSPHPAst useDefinition, String alias) {
        return new AliasSymbol(useDefinition, alias);
    }

    @Override
    public IAliasTypeSymbol createAliasTypeSymbol(ITSPHPAst definitionAst, String name) {
        return new AliasTypeSymbol(definitionAst, name, mixedTypeSymbol);
    }

    //    @Override
//    public IInterfaceTypeSymbol createInterfaceTypeSymbol(ITSPHPAst modifier, ITSPHPAst identifier,
//            IScope currentScope) {
//        return new InterfaceTypeSymbol(
//                scopeHelper,
//                identifier,
//                modifierHelper.getModifiers(modifier),
//                identifier.getText(),
//                currentScope,
//                mixedTypeSymbol);
//    }
//
    @Override
    public IClassTypeSymbol createClassTypeSymbol(
            ITSPHPAst classModifierAst, ITSPHPAst identifier, IScope currentScope) {
        return new ClassTypeSymbol(
                scopeHelper,
                identifier,
                modifierHelper.getModifiers(classModifierAst),
                identifier.getText(),
                currentScope,
                mixedTypeSymbol);
    }

    @Override
    public IUnionTypeSymbol createUnionTypeSymbol() {
        return new UnionTypeSymbol(overloadResolver);
    }

    @Override
    public IUnionTypeSymbol createUnionTypeSymbol(Map<String, ITypeSymbol> types) {
        return new UnionTypeSymbol(overloadResolver, types);
    }

    @Override
    public IMethodSymbol createMethodSymbol(ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier,
            ITSPHPAst identifier, IScope currentScope) {
        return new MethodSymbol(
                scopeHelper,
                identifier,
                modifierHelper.getModifiers(methodModifier),
                modifierHelper.getModifiers(returnTypeModifier),
                identifier.getText(),
                currentScope);
    }
//
//    @Override
//    public IVariableSymbol createThisSymbol(ITSPHPAst variableId, IPolymorphicTypeSymbol polymorphicTypeSymbol) {
//        return new ThisSymbol(variableId, variableId.getText(), polymorphicTypeSymbol);
//    }

    @Override
    public IVariableSymbol createVariableSymbol(ITSPHPAst typeModifier, ITSPHPAst variableId) {
        return new VariableSymbol(variableId, modifierHelper.getModifiers(typeModifier), variableId.getText());
    }

    @Override
    public ILazyTypeSymbol createLazyTypeSymbol() {
        return new LazyTypeSymbol();
    }

    @Override
    public IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPAst ast, TSPHPException exception) {
        IErroneousMethodSymbol methodSymbol = createErroneousMethodSymbol(ast, exception);
        return new ErroneousTypeSymbol(ast, ast.getText(), exception, methodSymbol);
    }

    @Override
    public IErroneousMethodSymbol createErroneousMethodSymbol(ITSPHPAst ast, TSPHPException ex) {
        return new ErroneousMethodSymbol(ast, ast.getText(), ex);
    }

    @Override
    public IErroneousVariableSymbol createErroneousVariableSymbol(ITSPHPAst ast,
            TSPHPException exception) {
        IErroneousVariableSymbol variableSymbol = new ErroneousVariableSymbol(ast, ast.getText(), exception);
        variableSymbol.setType(createErroneousTypeSymbol(ast, exception));
        return variableSymbol;
    }

    @Override
    public IErroneousLazySymbol createErroneousLazySymbol(
            ILazySymbolResolver symbolResolver, ITSPHPAst ast, TSPHPException exception) {
        return new ErroneousLazySymbol(ast, ast.getText(), exception, symbolResolver);
    }

}