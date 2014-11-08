/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.ASymbolWithModifier;
import ch.tsphp.tinsphp.symbols.ATypeSymbol;

import static org.mockito.Mockito.mock;

public class ATypeSymbol_ASymbolWithModifier_LSPTest extends ASymbolWithModifierTest
{

    class DummyTypeSymbol extends ATypeSymbol
    {

        public DummyTypeSymbol(ITSPHPAst theDefinitionAst, String theName, ITypeSymbol theParentTypeSymbol) {
            super(theDefinitionAst, theName, theParentTypeSymbol);
        }

        @Override
        public ITSPHPAst getDefaultValue() {
            return null;
        }
    }

    @Override
    protected ASymbolWithModifier createSymbolWithModifier(
            ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        DummyTypeSymbol symbol = new DummyTypeSymbol(definitionAst, name, mock(ITypeSymbol.class));
        symbol.setModifiers(modifiers);
        return symbol;
    }
}
