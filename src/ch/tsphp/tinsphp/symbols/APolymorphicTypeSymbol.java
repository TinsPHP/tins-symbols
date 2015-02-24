/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class APolymorphicTypeSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.LowerCaseStringMap;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.ICanBeAbstract;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.tinsphp.common.utils.MapHelper;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides some helper methods for polymorphic types.
 */
public abstract class APolymorphicTypeSymbol extends AScopedSymbol implements IPolymorphicTypeSymbol
{

    protected Set<ITypeSymbol> parentTypeSymbols = new HashSet<>();
    protected final ILowerCaseStringMap<List<ISymbol>> symbolsCaseInsensitive = new LowerCaseStringMap<>();
    private boolean isMixedTheParentTypeSymbol = false;
    private Set<ISymbol> abstractSymbols;

    @SuppressWarnings("checkstyle:parameternumber")
    public APolymorphicTypeSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet modifiers,
            String name,
            IScope enclosingScope,
            ITypeSymbol theParentTypeSymbol) {
        super(scopeHelper, definitionAst, modifiers, name, enclosingScope);
        parentTypeSymbols.add(theParentTypeSymbol);
        isMixedTheParentTypeSymbol = theParentTypeSymbol.getName().equals("mixed");
        TypeHelper.addNullableModifier(this);
    }

    @Override
    public void define(ISymbol symbol) {
        super.define(symbol);
        MapHelper.addToListMap(symbolsCaseInsensitive, symbol.getName(), symbol);
    }

    @Override
    public ISymbol resolveCaseInsensitive(ITSPHPAst identifier) {
        ISymbol symbol = null;
        if (symbolsCaseInsensitive.containsKey(identifier.getText())) {
            symbol = symbolsCaseInsensitive.get(identifier.getText()).get(0);
        }
        return symbol;
    }

    @Override
    public ISymbol resolveWithFallbackToParent(ITSPHPAst ast) {
        ISymbol symbol = scopeHelper.resolve(this, ast);
        for (ITypeSymbol parentTypeSymbol : parentTypeSymbols) {
            if (parentTypeSymbol instanceof IPolymorphicTypeSymbol) {
                symbol = ((IPolymorphicTypeSymbol) parentTypeSymbol).resolveWithFallbackToParent(ast);
                if (symbol != null) {
                    break;
                }
            }
        }
        return symbol;
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        return parentTypeSymbols;
    }

    @Override
    public void addParentTypeSymbol(IPolymorphicTypeSymbol aParent) {
        if (isMixedTheParentTypeSymbol) {
            parentTypeSymbols = new HashSet<>();
            isMixedTheParentTypeSymbol = false;
        }
        parentTypeSymbols.add(aParent);
    }

    @Override
    public boolean isAbstract() {
        return modifiers.isAbstract();
    }

    @Override
    public Set<ISymbol> getAbstractSymbols() {
        if (abstractSymbols == null) {
            loadOwnAbstractSymbols();
            loadParentsAbstractSymbols();
        }
        return abstractSymbols;
    }

    private void loadOwnAbstractSymbols() {
        abstractSymbols = new HashSet<>();
        for (List<ISymbol> symbolList : symbols.values()) {
            ISymbol symbol = symbolList.get(0);
            if (symbol instanceof ICanBeAbstract) {
                if (((ICanBeAbstract) symbol).isAbstract()) {
                    abstractSymbols.add(symbol);
                }
            }
        }
    }

    private void loadParentsAbstractSymbols() {
        for (ITypeSymbol typeSymbol : parentTypeSymbols) {
            if (typeSymbol instanceof IPolymorphicTypeSymbol) {
                IPolymorphicTypeSymbol polymorphicTypeSymbol = (IPolymorphicTypeSymbol) typeSymbol;
                if (polymorphicTypeSymbol.isAbstract()) {
                    for (ISymbol symbol : polymorphicTypeSymbol.getAbstractSymbols()) {
                        if (!symbols.containsKey(symbol.getName())) {
                            abstractSymbols.add(symbol);
                        }
                    }
                }
            }
        }
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TokenTypes.Null, "null");
    }

    @Override
    public void addToInitialisedSymbols(ISymbol symbol, boolean isFullyInitialised) {
        throw new UnsupportedOperationException("all symbols in a polymorphic type symbol are implicitly initialised.\n"
                + "Therefore there should not be the need to call this method.");
    }

    @Override
    public Map<String, Boolean> getInitialisedSymbols() {
        throw new UnsupportedOperationException("all symbols in a polymorphic type symbol are implicitly initialised.\n"
                + "Therefore there should not be the need to call this method.");
    }

    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        //all symbols in a polymorphic type symbol are implicitly initialised as long as they exist
        return symbols.containsKey(symbol.getName());
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        //all symbols in a polymorphic type symbol are implicitly initialised
        return false;
    }

    @Override
    public boolean isFalseable() {
        return modifiers.isFalseable();
    }

    @Override
    public boolean isNullable() {
        return modifiers.isNullable();
    }

    @Override
    public void setModifiers(IModifierSet newModifiers) {
        super.setModifiers(newModifiers);
        //make sure nullable is part of the modifiers
        TypeHelper.addNullableModifier(this);
    }

    //TODO same as in ATypeSymbol chance to move it out?

    /**
     * Returns itself, override in sub-classes for another behaviour (lazy types for instance).
     */
    @Override
    public ITypeSymbol evalSelf() {
        return this;
    }
}
