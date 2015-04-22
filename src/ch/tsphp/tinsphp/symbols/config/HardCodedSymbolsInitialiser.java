/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.config;

import ch.tsphp.tinsphp.common.config.ISymbolsInitialiser;
import ch.tsphp.tinsphp.common.scopes.IScopeFactory;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.scopes.ScopeFactory;
import ch.tsphp.tinsphp.symbols.scopes.ScopeHelper;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;

public class HardCodedSymbolsInitialiser implements ISymbolsInitialiser
{
    private final IModifierHelper modifierHelper;
    private final IScopeHelper scopeHelper;
    private final IOverloadResolver overloadResolver;
    private final ISymbolFactory symbolFactory;
    private final IScopeFactory scopeFactory;

    public HardCodedSymbolsInitialiser() {
        modifierHelper = new ModifierHelper();
        overloadResolver = new OverloadResolver();
        scopeHelper = new ScopeHelper();
        symbolFactory = new SymbolFactory(scopeHelper, modifierHelper, overloadResolver);
        scopeFactory = new ScopeFactory(scopeHelper);
    }


    @Override
    public IScopeHelper getScopeHelper() {
        return scopeHelper;
    }

    @Override
    public IOverloadResolver getOverloadResolver() {
        return overloadResolver;
    }

    @Override
    public IModifierHelper getModifierHelper() {
        return modifierHelper;
    }

    @Override
    public ISymbolFactory getSymbolFactory() {
        return symbolFactory;
    }

    @Override
    public IScopeFactory getScopeFactory() {
        return scopeFactory;
    }

    @Override
    public void reset() {
        //nothing to reset in this version
    }
}
