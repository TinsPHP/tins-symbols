/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.IForEvalReadyListener;
import ch.tsphp.common.symbols.ILazyTypeSymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Set;

public abstract class ALazyTypeSymbol implements ILazyTypeSymbol
{
    private static final String ERROR_MESSAGE = "You are dealing with an ILazyTypeSymbol.";

    private Collection<IForEvalReadyListener> listeners = new ArrayDeque<>();
    private boolean isReadyForEval = false;

    @Override
    public abstract ITypeSymbol evalSelf();

    @Override
    public void addForEvalReadyListener(IForEvalReadyListener listener) {
        if (!isReadyForEval()) {
            listeners.add(listener);
        } else {
            listener.notifyReadyForEval();
        }
    }

    protected void notifyForEvalReadyListeners() {
        if (isReadyForEval) {
            throw new IllegalStateException("LazyTypeSymbol is already ready for eval");
        }

        isReadyForEval = true;
        for (IForEvalReadyListener listener : listeners) {
            listener.notifyReadyForEval();
        }
        //free space
        listeners = null;
    }

    @Override
    public boolean isReadyForEval() {
        return isReadyForEval;
    }

    @Override
    public String getName() {
        return "?";
    }

    @Override
    public String getAbsoluteName() {
        return "?";
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
