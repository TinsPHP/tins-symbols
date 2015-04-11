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
import ch.tsphp.tinsphp.symbols.ASymbolWithModifier;
import ch.tsphp.tinsphp.symbols.MethodSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolWithModifierTest;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class MethodSymbol_ASymbolWithModifier_LSPTest extends ASymbolWithModifierTest
{

    private IModifierSet returnTypeModifiers;

    @Before
    public void setUp() {
        returnTypeModifiers = mock(IModifierSet.class);
    }

    @Override
    public void toString_TypeDefinedModifiersNotEmpty_ReturnsNameColonTypeToStringPipeModifiers() {
        // different behaviour - appends the return modifiers in addition. See MethodSymbolTest
        // if the return type has no modifiers then it behaves the same way

        returnTypeModifiers = new ModifierSet();
        super.toString_TypeDefinedModifiersNotEmpty_ReturnsNameColonTypeToStringPipeModifiers();
    }

    @Override
    public void toString_noTypeDefinedAndOneModifierDefined_ReturnNameInclModifiers() {
        // different behaviour - appends the return modifiers in addition. See MethodSymbolTest
        // if the return type has no modifiers then it behaves the same way

        returnTypeModifiers = new ModifierSet();
        super.toString_noTypeDefinedAndOneModifierDefined_ReturnNameInclModifiers();
    }

    @Override
    public void toString_noTypeDefinedAndThreeModifiersDefinedInOrder_ReturnNameInclModifiersSorted() {
        // different behaviour - appends the return modifiers in addition. See MethodSymbolTest
        // if the return type has no modifiers then it behaves the same way

        returnTypeModifiers = new ModifierSet();
        super.toString_noTypeDefinedAndThreeModifiersDefinedInOrder_ReturnNameInclModifiersSorted();
    }

    @Override
    public void toString_noTypeDefinedAndThreeModifiersDefinedLastFirst_ReturnNameInclModifiersSorted() {
        // different behaviour - appends the return modifiers in addition. See MethodSymbolTest
        // if the return type has no modifiers then it behaves the same way

        returnTypeModifiers = new ModifierSet();
        super.toString_noTypeDefinedAndThreeModifiersDefinedLastFirst_ReturnNameInclModifiersSorted();
    }

    @Override
    public void toString_noTypeDefinedAndThreeModifiersDefinedFirstLast_ReturnNameInclModifiersSorted() {
        // different behaviour - appends the return modifiers in addition. See MethodSymbolTest
        // if the return type has no modifiers then it behaves the same way

        returnTypeModifiers = new ModifierSet();
        super.toString_noTypeDefinedAndThreeModifiersDefinedFirstLast_ReturnNameInclModifiersSorted();
    }

    @Override
    protected ASymbolWithModifier createSymbolWithModifier(
            ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        return new MethodSymbol(
                mock(IScopeHelper.class),
                definitionAst,
                modifiers,
                returnTypeModifiers,
                mock(IMinimalVariableSymbol.class),
                name,
                mock(IScope.class));
    }
}
