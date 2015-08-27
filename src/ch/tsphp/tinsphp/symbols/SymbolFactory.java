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
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.TinsPHPConstants;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IAliasSymbol;
import ch.tsphp.tinsphp.common.symbols.IAliasTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IExpressionVariableSymbol;
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
import ch.tsphp.tinsphp.symbols.constraints.BindingCollection;
import ch.tsphp.tinsphp.symbols.constraints.Constraint;
import ch.tsphp.tinsphp.symbols.constraints.FunctionType;
import ch.tsphp.tinsphp.symbols.constraints.Variable;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousLazySymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousMethodSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousTypeSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousVariableSymbol;

import java.util.List;

public class SymbolFactory implements ISymbolFactory
{
    private final IScopeHelper scopeHelper;
    private final IModifierHelper modifierHelper;
    private final ITypeHelper typeHelper;
    private ITypeSymbol mixedTypeSymbol = null;

    public SymbolFactory(
            IScopeHelper theScopeHelper,
            IModifierHelper theModifierHelper,
            ITypeHelper theTypeHelper) {
        scopeHelper = theScopeHelper;
        modifierHelper = theModifierHelper;
        typeHelper = theTypeHelper;
    }

    @Override
    public void setMixedTypeSymbol(ITypeSymbol typeSymbol) {
        mixedTypeSymbol = typeSymbol;
        typeHelper.setMixedTypeSymbol(mixedTypeSymbol);
    }

    public ITypeSymbol getMixedTypeSymbol() {
        return mixedTypeSymbol;
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
        return createPseudoTypeSymbol(name, mixedTypeSymbol, false);
    }

    @Override
    public IPseudoTypeSymbol createPseudoTypeSymbol(String name, boolean isFinal) {
        return createPseudoTypeSymbol(name, mixedTypeSymbol, isFinal);
    }

    @Override
    public IPseudoTypeSymbol createPseudoTypeSymbol(String name, ITypeSymbol parentTypeSymbol, boolean isFinal) {
        return new PseudoTypeSymbol(name, parentTypeSymbol, isFinal);
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
        return new UnionTypeSymbol(typeHelper);
    }

    @Override
    public IIntersectionTypeSymbol createIntersectionTypeSymbol() {
        return new IntersectionTypeSymbol(typeHelper);
    }

    @Override
    public IConvertibleTypeSymbol createConvertibleTypeSymbol() {
        return new ConvertibleTypeSymbol(createBindingCollection());
    }

    @Override
    public IMinimalMethodSymbol createMinimalMethodSymbol(String name) {
        return new MinimalMethodSymbol(name);
    }

    @Override
    public IFunctionType createFunctionType(
            String name, IBindingCollection bindingCollection, List<IVariable> parameterTypeVariables) {
        return new FunctionType(name, bindingCollection, parameterTypeVariables);
    }

    @Override
    public IMethodSymbol createMethodSymbol(
            ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier, ITSPHPAst identifier, IScope currentScope) {
        return new MethodSymbol(
                scopeHelper,
                identifier,
                modifierHelper.getModifiers(methodModifier),
                modifierHelper.getModifiers(returnTypeModifier),
                new MinimalVariableSymbol(identifier, TinsPHPConstants.RETURN_VARIABLE_NAME),
                identifier.getText(),
                currentScope);
    }
//
//    @Override
//    public IVariableSymbol createThisSymbol(ITSPHPAst variableId, IPolymorphicTypeSymbol polymorphicTypeSymbol) {
//        return new ThisSymbol(variableId, variableId.getText(), polymorphicTypeSymbol);
//    }

    @Override
    public IVariable createVariable(String name) {
        return new Variable(name);
    }

    @Override
    public IExpressionVariableSymbol createExpressionVariableSymbol(ITSPHPAst exprAst) {
        return new ExpressionVariableSymbol(exprAst);
    }

    @Override
    public IMinimalVariableSymbol createMinimalVariableSymbol(ITSPHPAst identifier, String name) {
        return new MinimalVariableSymbol(identifier, name);
    }

    @Override
    public IVariableSymbol createVariableSymbol(ITSPHPAst typeModifier, ITSPHPAst variableId) {
        return new VariableSymbol(variableId, modifierHelper.getModifiers(typeModifier), variableId.getText());
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
    public IErroneousVariableSymbol createErroneousVariableSymbol(ITSPHPAst ast, TSPHPException exception) {
        IErroneousVariableSymbol variableSymbol = new ErroneousVariableSymbol(ast, ast.getText(), exception);
        variableSymbol.setType(createErroneousTypeSymbol(ast, exception));
        return variableSymbol;
    }

    @Override
    public IErroneousLazySymbol createErroneousLazySymbol(
            ILazySymbolResolver symbolResolver, ITSPHPAst ast, TSPHPException exception) {
        return new ErroneousLazySymbol(ast, ast.getText(), exception, symbolResolver);
    }

    @Override
    public IConstraint createConstraint(
            ITSPHPAst operator, IVariable leftHandSide, List<IVariable> arguments, IMinimalMethodSymbol methodSymbol) {
        return new Constraint(operator, leftHandSide, arguments, methodSymbol);
    }

    @Override
    public IBindingCollection createBindingCollection() {
        return new BindingCollection(this, typeHelper);
    }

    @Override
    public IBindingCollection createBindingCollection(IBindingCollection bindingCollectionToCopy) {
        if (!(bindingCollectionToCopy instanceof BindingCollection)) {
            throw new IllegalArgumentException("only supports " + BindingCollection.class.getName() + " or a subclass");
        }
        return new BindingCollection((BindingCollection) bindingCollectionToCopy);
    }
}