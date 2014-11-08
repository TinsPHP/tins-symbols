/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.ATypeSymbol;

import static org.mockito.Mockito.mock;

public class ATypeSymbol_ASymbol_LSPTest extends ASymbolTest
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
    public void toString_NoTypeDefined_ReturnsName() {
        // different behaviour - appends the modifiers in addition. See ASymbolWithModifier
        // if the ModifierSet is empty (default for ATypeSymbol) then it behaves the same way

        super.toString_NoTypeDefined_ReturnsName();
    }

    @Override
    public void toString_TypeDefined_ReturnsNameColonTypeToString() {
        // different behaviour - appends the modifiers in addition. See ASymbolWithModifier
        // if the ModifierSet is empty (default for ATypeSymbol) then it behaves the same way

        super.toString_TypeDefined_ReturnsNameColonTypeToString();
    }

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new DummyTypeSymbol(definitionAst, name, mock(ITypeSymbol.class));
    }
}
