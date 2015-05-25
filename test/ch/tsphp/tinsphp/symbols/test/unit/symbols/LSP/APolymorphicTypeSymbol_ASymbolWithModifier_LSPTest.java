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
import ch.tsphp.tinsphp.symbols.ASymbolWithModifier;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolWithModifierTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class APolymorphicTypeSymbol_ASymbolWithModifier_LSPTest extends ASymbolWithModifierTest
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
    protected ASymbolWithModifier createSymbolWithModifier(
            ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getName()).thenReturn("dummy");
        return new DummyPolymorphicTypeSymbol(null, definitionAst, modifiers, name, null, typeSymbol);
    }
}
