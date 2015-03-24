/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class MethodSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbolWithRef;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.TypeWithModifiersDto;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

public class MethodSymbol extends AScopedSymbol implements IMethodSymbol
{

    private final List<IVariableSymbol> parameters = new ArrayList<>();
    private final IModifierSet returnTypeModifiers;
    //Warning! start code duplication - same as in GlobalNamespaceScope
    private final Deque<ITypeVariableSymbol> typeVariables = new ArrayDeque<>();
    private final Collection<ITypeVariableSymbolWithRef> typeVariableSymbolWithRefs = new ArrayDeque<>();
    //Warning! end code duplication - same as in GlobalNamespaceScope

    @SuppressWarnings("checkstyle:parameternumber")
    public MethodSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet methodModifiers,
            IModifierSet theReturnTypeModifiers,
            String name,
            IScope enclosingScope) {
        super(scopeHelper, definitionAst, methodModifiers, name, enclosingScope);
        returnTypeModifiers = theReturnTypeModifiers;
    }

    @Override
    public void addParameter(IVariableSymbol typeSymbol) {
        parameters.add(typeSymbol);
    }

    @Override
    public List<IVariableSymbol> getParameters() {
        return parameters;
    }

    @Override
    public boolean isStatic() {
        return modifiers.isStatic();
    }

    @Override
    public boolean isFinal() {
        return modifiers.isFinal();
    }

    @Override
    public boolean isAbstract() {
        return modifiers.isAbstract();
    }

    @Override
    public boolean isAlwaysCasting() {
        return returnTypeModifiers.isAlwaysCasting();
    }

    @Override
    public boolean isPublic() {
        return modifiers.isPublic();
    }

    @Override
    public boolean isProtected() {
        return modifiers.isProtected();
    }

    @Override
    public boolean isPrivate() {
        return modifiers.isPrivate();
    }

    @Override
    public boolean isFalseable() {
        return returnTypeModifiers.isFalseable();
    }

    @Override
    public boolean isNullable() {
        return returnTypeModifiers.isNullable();
    }

    @Override
    public TypeWithModifiersDto toTypeWithModifiersDto() {
        return new TypeWithModifiersDto(getType(), returnTypeModifiers);
    }

    @Override
    public String toString() {
        return super.toString() + ModifierHelper.getModifiersAsString(returnTypeModifiers);
    }

    //Warning! start code duplication - same as in GlobalNamespaceScope
    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && initialisedSymbols.get(symbolName);
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && !initialisedSymbols.get(symbolName);
    }
    //Warning! end code duplication - same as in GlobalNamespaceScope


    //Warning! start code duplication - same as in GlobalNamespaceScope
    @Override
    public Deque<ITypeVariableSymbol> getTypeVariables() {
        return typeVariables;
    }

    @Override
    public Collection<ITypeVariableSymbolWithRef> getTypeVariablesWithRef() {
        return typeVariableSymbolWithRefs;
    }

    @Override
    public void addTypeVariable(ITypeVariableSymbol typeVariableSymbol) {
        typeVariables.addLast(typeVariableSymbol);
    }

    @Override
    public void addTypeVariableWithRef(ITypeVariableSymbolWithRef typeVariableSymbol) {
        typeVariableSymbolWithRefs.add(typeVariableSymbol);
    }
    //Warning! end code duplication - same as in GlobalNamespaceScope
}
