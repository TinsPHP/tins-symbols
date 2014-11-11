/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.ASymbolWithModifier;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolTest;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class ASymbolWithModifier_ASymbol_LSPTest extends ASymbolTest
{

    class DummySymbolWithModifier extends ASymbolWithModifier
    {

        public DummySymbolWithModifier(ITSPHPAst definitionAst, IModifierSet theModifiers, String name) {
            super(definitionAst, theModifiers, name);
        }
    }

    private IModifierSet modifiersSet;

    @Before
    public void setUp() {
        modifiersSet = mock(IModifierSet.class);
    }

    @Override
    public void toString_NoTypeDefined_ReturnsName() {
        // different behaviour - appends the modifiers in addition. See ASymbolWithModifierTest
        // if the ModifierSet is empty then it behaves the same way

        modifiersSet = new ModifierSet();
        super.toString_NoTypeDefined_ReturnsName();
    }

    @Override
    public void toString_TypeDefined_ReturnsNameColonTypeToString() {
        // different behaviour - appends the modifiers in addition. See ASymbolWithModifierTest
        // if the ModifierSet is empty then it behaves the same way

        modifiersSet = new ModifierSet();
        super.toString_TypeDefined_ReturnsNameColonTypeToString();
    }

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new DummySymbolWithModifier(definitionAst, modifiersSet, name);
    }
}
