/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AContainerTypeSymbol implements IContainerTypeSymbol
{
    private static final String ERROR_MESSAGE = "You are dealing with an AContainerTypeSymbol.";

    protected static enum ETypeRelation
    {
        NO_RELATION,
        PARENT_TYPE,
        SUB_TYPE
    }

    protected final IOverloadResolver overloadResolver;
    protected final Map<String, ITypeSymbol> typeSymbols;

    public AContainerTypeSymbol(IOverloadResolver theOverloadResolver) {
        super();
        overloadResolver = theOverloadResolver;
        typeSymbols = new HashMap<>();
    }

    @Override
    public abstract ITypeSymbol evalSelf();

    @Override
    public abstract String getName();

    @Override
    public abstract String getAbsoluteName();

    protected abstract boolean addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol);

    @Override
    public boolean addTypeSymbol(ITypeSymbol typeSymbol) {
        boolean hasChanged = false;

        String absoluteName = typeSymbol.getAbsoluteName();

        //no need to add it if it already exists in the container; ergo simplification = do not insert
        if (!typeSymbols.containsKey(absoluteName)) {
            hasChanged = addAndSimplify(absoluteName, typeSymbol);
        }
        return hasChanged;
    }

    @Override
    public Map<String, ITypeSymbol> getTypeSymbols() {
        return typeSymbols;
    }

    @Override
    public IScope getDefinitionScope() {
        return null;
    }

    @Override
    public String toString() {
        return getAbsoluteName();
    }

    //--------------------------------------------------------------
    // Unsupported Methods

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void addModifier(Integer integer) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean removeModifier(Integer integer) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public IModifierSet getModifiers() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setModifiers(IModifierSet modifierSet) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public ITSPHPAst getDefinitionAst() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setDefinitionScope(IScope iScope) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public ITypeSymbol getType() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setType(ITypeSymbol iTypeSymbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean isFalseable() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean isNullable() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }
}
