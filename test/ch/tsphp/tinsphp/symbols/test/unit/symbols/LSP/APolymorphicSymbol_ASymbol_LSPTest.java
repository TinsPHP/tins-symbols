/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.symbols.APolymorphicTypeSymbol;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class APolymorphicSymbol_ASymbol_LSPTest extends ASymbolTest
{

    class DummyPolymorphicTypeSymbol extends APolymorphicTypeSymbol
    {

        public DummyPolymorphicTypeSymbol(IScopeHelper scopeHelper, ITSPHPAst definitionAst, IModifierSet modifiers,
                String name, IScope enclosingScope, ITypeSymbol theParentTypeSymbol) {
            super(scopeHelper, definitionAst, modifiers, name, enclosingScope, theParentTypeSymbol);
        }

        @Override
        public boolean canBeUsedInIntersection() {
            return false;
        }
    }

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getName()).thenReturn("dummy");
        return new DummyPolymorphicTypeSymbol(
                null, definitionAst, new ModifierSet(), name, null, typeSymbol);
    }
}
