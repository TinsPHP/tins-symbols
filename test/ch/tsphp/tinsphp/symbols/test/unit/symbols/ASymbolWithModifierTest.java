/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.ASymbolWithModifier;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ASymbolWithModifierTest
{
    class DummySymbolWithModifier extends ASymbolWithModifier
    {

        public DummySymbolWithModifier(ITSPHPAst definitionAst, IModifierSet theModifiers, String name) {
            super(definitionAst, theModifiers, name);
        }
    }

    @Test
    public void getModifiers_NothingDefined_ReturnEmptyModifierSet() {
        //no arrange necessary

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier();
        IModifierSet result = symbolWithModifier.getModifiers();

        assertThat(result.size(), is(0));
    }

    @Test
    public void getModifiers_OneDefined_ReturnModifierSetWithModifier() {
        int modifier = 12;

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(new ModifierSet());
        symbolWithModifier.addModifier(modifier);
        IModifierSet result = symbolWithModifier.getModifiers();

        assertThat(result, containsInAnyOrder(modifier));
    }

    @Test
    public void getModifiers_TwoDefined_ReturnModifierSetWithTwoModifier() {
        int modifier1 = 12;
        int modifier2 = 34;

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(new ModifierSet());
        symbolWithModifier.addModifier(modifier1);
        symbolWithModifier.addModifier(modifier2);
        IModifierSet result = symbolWithModifier.getModifiers();

        assertThat(result, containsInAnyOrder(modifier1, modifier2));
    }

    @Test
    public void removeModifiers_RemoveNotDefined_ReturnsFalse() {
        //no arrange necessary

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier();
        boolean result = symbolWithModifier.removeModifier(12);

        assertThat(result, is(false));
    }

    @Test
    public void removeModifiers_Defined_ReturnsSetWithoutModifier() {
        int modifier1 = 12;
        int modifier2 = 34;

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(new ModifierSet());
        symbolWithModifier.addModifier(modifier1);
        symbolWithModifier.addModifier(modifier2);
        boolean result = symbolWithModifier.removeModifier(modifier1);
        IModifierSet set = symbolWithModifier.getModifiers();

        assertThat(result, is(true));
        assertThat(set, containsInAnyOrder(modifier2));
    }

    @Test
    public void setModifiers_Standard_ReturnSameSet() {
        IModifierSet set = new ModifierSet();
        set.add(12);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier();
        symbolWithModifier.setModifiers(set);
        IModifierSet result = symbolWithModifier.getModifiers();

        assertThat(result, is(set));
    }

    @Test
    public void toString_TypeDefinedModifiersNotEmpty_ReturnsNameColonTypeToStringPipeModifiers() {
        String name = "foo";
        String typeName = "bar";
        int modifier = 123;
        IModifierSet set = new ModifierSet();
        set.add(modifier);
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.toString()).thenReturn(typeName);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        symbolWithModifier.setType(typeSymbol);
        String result = symbolWithModifier.toString();

        assertThat(result, is(name + ":" + typeName+"|"+modifier));
    }

    @Test
    public void toString_noTypeDefinedAndOneModifierDefined_ReturnNameInclModifiers() {
        String name = "bar";
        int modifier = 12;
        IModifierSet set = new ModifierSet();
        set.add(modifier);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        String result = symbolWithModifier.toString();

        assertThat(result, is(name + "|" + modifier));
    }

    @Test
    public void toString_noTypeDefinedAndThreeModifiersDefinedInOrder_ReturnNameInclModifiersSorted() {
        String name = "bar";
        IModifierSet set = new ModifierSet();
        set.add(3);
        set.add(12);
        set.add(24);


        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        String result = symbolWithModifier.toString();

        assertThat(result, is(name + "|" + 3 + ", " + 12 + ", " + 24));
    }

    @Test
    public void toString_noTypeDefinedAndThreeModifiersDefinedLastFirst_ReturnNameInclModifiersSorted() {
        IModifierSet set = new ModifierSet();
        set.add(24);
        set.add(3);
        set.add(12);
        String name = "bar";

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        String result = symbolWithModifier.toString();

        assertThat(result, is(name + "|" + 3 + ", " + 12 + ", " + 24));
    }

    @Test
    public void toString_noTypeDefinedAndThreeModifiersDefinedFirstLast_ReturnNameInclModifiersSorted() {
        IModifierSet set = new ModifierSet();
        set.add(12);
        set.add(24);
        set.add(3);
        String name = "bar";

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        String result = symbolWithModifier.toString();

        assertThat(result, is(name + "|" + 3 + ", " + 12 + ", " + 24));
    }

    private ASymbolWithModifier createSymbolWithModifier() {
        return createSymbolWithModifier(mock(IModifierSet.class));
    }

    private ASymbolWithModifier createSymbolWithModifier(IModifierSet modifierSet) {
        return createSymbolWithModifier("foo", modifierSet);
    }

    private ASymbolWithModifier createSymbolWithModifier(String name, IModifierSet modifierSet) {
        return createSymbolWithModifier(mock(ITSPHPAst.class), modifierSet, name);
    }

    protected ASymbolWithModifier createSymbolWithModifier(
            ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        return new DummySymbolWithModifier(definitionAst, modifiers, name);
    }
}