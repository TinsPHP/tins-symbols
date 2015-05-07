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
import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.symbols.APolymorphicTypeSymbol;
import ch.tsphp.tinsphp.symbols.ASymbolWithModifier;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolWithModifierTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
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
    public void getModifiers_NothingDefined_ReturnEmptyModifierSet() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier as well

        // start same as in ASymbolWithModifierTest
        //no arrange necessary

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(new ModifierSet());
        IModifierSet result = symbolWithModifier.getModifiers();
        // end same as in ASymbolWithModifierTest

        //assertThat(result.size(), is(0));
        assertThat(result, containsInAnyOrder(TokenTypes.QuestionMark));
    }

    @Override
    public void getModifiers_OneDefined_ReturnModifierSetWithModifier() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier as well

        // start same as in ASymbolWithModifierTest
        int modifier = 12;

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(new ModifierSet());
        symbolWithModifier.addModifier(modifier);
        IModifierSet result = symbolWithModifier.getModifiers();
        // end same as in ASymbolWithModifierTest

        //assertThat(result, containsInAnyOrder(modifier));
        assertThat(result, containsInAnyOrder(modifier, TokenTypes.QuestionMark));
    }

    @Override
    public void getModifiers_TwoDefined_ReturnModifierSetWithTwoModifier() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier as well

        // start same as in ASymbolWithModifierTest
        int modifier1 = 12;
        int modifier2 = 34;

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(new ModifierSet());
        symbolWithModifier.addModifier(modifier1);
        symbolWithModifier.addModifier(modifier2);
        IModifierSet result = symbolWithModifier.getModifiers();
        // end same as in ASymbolWithModifierTest

        //assertThat(result, containsInAnyOrder(modifier1, modifier2));
        assertThat(result, containsInAnyOrder(modifier1, modifier2, TokenTypes.QuestionMark));
    }

    @Override
    public void removeModifiers_Defined_ReturnsSetWithoutModifier() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier as well

        // start same as in ASymbolWithModifierTest
        int modifier1 = 12;
        int modifier2 = 34;

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(new ModifierSet());
        symbolWithModifier.addModifier(modifier1);
        symbolWithModifier.addModifier(modifier2);
        boolean result = symbolWithModifier.removeModifier(modifier1);
        IModifierSet set = symbolWithModifier.getModifiers();

        assertThat(result, is(true));
        // end same as in ASymbolWithModifierTest

        //assertThat(set, containsInAnyOrder(modifier2));
        assertThat(set, containsInAnyOrder(modifier2, TokenTypes.QuestionMark));
    }

    @Override
    public void setModifiers_Standard_ReturnSameSet() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier as well

        // start same as in ASymbolWithModifierTest
        IModifierSet set = new ModifierSet();
        set.add(12);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier();
        symbolWithModifier.setModifiers(set);
        IModifierSet result = symbolWithModifier.getModifiers();
        // end same as in ASymbolWithModifierTest

//        assertThat(result, containsInAnyOrder(12));
        assertThat(result, containsInAnyOrder(12, TokenTypes.QuestionMark));
    }

    @Override
    public void toString_TypeDefinedModifiersNotEmpty_ReturnsNameColonTypeToStringPipeModifiers() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier

        // start same as in ASymbolWithModifierTest
        String name = "foo";
        String typeName = "bar";
        int modifier = 1023;
        IModifierSet set = new ModifierSet();
        set.add(modifier);
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.toString()).thenReturn(typeName);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        symbolWithModifier.setType(typeSymbol);
        String result = symbolWithModifier.toString();
        // end same as in ASymbolWithModifierTest

        //assertThat(result, is(name + ":" + typeName + "|" + modifier));
        assertThat(result, is(name + ":" + typeName + "|" + TokenTypes.QuestionMark + ", " + modifier));
    }

    @Override
    public void toString_noTypeDefinedAndOneModifierDefined_ReturnNameInclModifiers() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier

        // start same as in ASymbolWithModifierTest
        String name = "bar";
        int modifier = 1002;
        IModifierSet set = new ModifierSet();
        set.add(modifier);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        String result = symbolWithModifier.toString();
        // end same as in ASymbolWithModifierTest

        //assertThat(result, is(name + "|" + modifier));
        assertThat(result, is(name + "|" + TokenTypes.QuestionMark + ", " + modifier));
    }

    @Override
    public void toString_noTypeDefinedAndThreeModifiersDefinedInOrder_ReturnNameInclModifiersSorted() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier

        // start same as in ASymbolWithModifierTest
        String name = "bar";
        IModifierSet set = new ModifierSet();
        set.add(1200);
        set.add(2400);
        set.add(3000);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        String result = symbolWithModifier.toString();
        // end same as in ASymbolWithModifierTest

        //assertThat(result, is(name + "|" + 1200 + ", " + 2400  + ", " + 3000));
        assertThat(result, is(name + "|" + TokenTypes.QuestionMark + ", " + 1200 + ", " + 2400 + ", " + 3000));
    }

    @Override
    public void toString_noTypeDefinedAndThreeModifiersDefinedLastFirst_ReturnNameInclModifiersSorted() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier

        // start same as in ASymbolWithModifierTest
        String name = "bar";
        IModifierSet set = new ModifierSet();
        set.add(3000);
        set.add(1200);
        set.add(2400);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        String result = symbolWithModifier.toString();
        // end same as in ASymbolWithModifierTest

        //assertThat(result, is(name + "|" + 1200 + ", " + 2400  + ", " + 3000));
        assertThat(result, is(name + "|" + TokenTypes.QuestionMark + ", " + 1200 + ", " + 2400 + ", " + 3000));
    }

    @Override
    public void toString_noTypeDefinedAndThreeModifiersDefinedFirstLast_ReturnNameInclModifiersSorted() {
        // different behaviour - APolymorphicTypeSymbol has always the nullable modifier

        // start same as in ASymbolWithModifierTest
        String name = "bar";
        IModifierSet set = new ModifierSet();
        set.add(2400);
        set.add(3000);
        set.add(1200);

        ASymbolWithModifier symbolWithModifier = createSymbolWithModifier(name, set);
        String result = symbolWithModifier.toString();
        // end same as in ASymbolWithModifierTest

        //assertThat(result, is(name + "|" + 1200 + ", " + 2400  + ", " + 3000));
        assertThat(result, is(name + "|" + TokenTypes.QuestionMark + ", " + 1200 + ", " + 2400 + ", " + 3000));
    }

    private ASymbolWithModifier createSymbolWithModifier() {
        return createSymbolWithModifier(new ModifierSet());
    }

    private ASymbolWithModifier createSymbolWithModifier(IModifierSet modifierSet) {
        return createSymbolWithModifier("foo", modifierSet);
    }

    private ASymbolWithModifier createSymbolWithModifier(String name, IModifierSet modifierSet) {
        return createSymbolWithModifier(mock(ITSPHPAst.class), modifierSet, name);
    }

    @Override
    protected ASymbolWithModifier createSymbolWithModifier(
            ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getName()).thenReturn("dummy");
        return new DummyPolymorphicTypeSymbol(null, definitionAst, modifiers, name, null, typeSymbol);
    }
}
