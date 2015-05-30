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
import ch.tsphp.tinsphp.common.symbols.IObservableTypeListener;
import ch.tsphp.tinsphp.common.symbols.IObservableTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class APolymorphicTypeSymbol implements ITypeSymbol, IObservableTypeSymbol
{
    private static final String ERROR_MESSAGE = "You are dealing with an AContainerTypeSymbol.";

    protected boolean hasAbsoluteNameChanged = true;
    protected String ownAbsoluteName;
    protected List<IObservableTypeListener> listeners = new ArrayList<>();

    @Override
    public String getName() {
        return getAbsoluteName();
    }

    @Override
    public String getAbsoluteName() {
        if (hasAbsoluteNameChanged) {
            ownAbsoluteName = calculateAbsoluteName();
            hasAbsoluteNameChanged = false;
        }
        return ownAbsoluteName;
    }

    protected abstract String calculateAbsoluteName();

    @Override
    public IScope getDefinitionScope() {
        return null;
    }

    @Override
    public ITypeSymbol evalSelf() {
        return this;
    }

    @Override
    public String toString() {
        return getAbsoluteName();
    }

    @Override
    public void register(IObservableTypeListener subscriberType) {
        listeners.add(subscriberType);
    }

    protected void notifyHasChanged() {
        String oldAbsoluteName = getAbsoluteName();
        hasAbsoluteNameChanged = true;
        for (IObservableTypeListener listener : listeners) {
            listener.nameOfObservableHasChanged(this, oldAbsoluteName);
        }
    }

    //-  Unsupported Methods -------------------------------------

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
