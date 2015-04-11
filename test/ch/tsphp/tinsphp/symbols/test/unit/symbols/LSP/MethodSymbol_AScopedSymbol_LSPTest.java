/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;
import ch.tsphp.tinsphp.symbols.AScopedSymbol;
import ch.tsphp.tinsphp.symbols.MethodSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.AScopedSymbolTest;

import static org.mockito.Mockito.mock;

public class MethodSymbol_AScopedSymbol_LSPTest extends AScopedSymbolTest
{
    @Override
    protected AScopedSymbol createScopedSymbol(IScopeHelper scopeHelper, ITSPHPAst definitionAst,
            IModifierSet modifiers, String name, IScope enclosingScope) {
        return new MethodSymbol(
                scopeHelper,
                definitionAst,
                modifiers,
                mock(IModifierSet.class),
                mock(IMinimalVariableSymbol.class),
                name,
                enclosingScope);
    }
}
