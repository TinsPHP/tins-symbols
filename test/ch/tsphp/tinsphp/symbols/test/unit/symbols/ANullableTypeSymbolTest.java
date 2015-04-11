/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ANullableTypeSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ANullableTypeSymbolTest
{
    class DummyNullableTypeSymbol extends ANullableTypeSymbol
    {
        public DummyNullableTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
            super(name, parentTypeSymbol);
        }

        public DummyNullableTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbols) {
            super(name, parentTypeSymbols);
        }
    }

    @Test
    public void getDefaultValue_Standard_ReturnsNull() {
        //no arrange necessary

        ANullableTypeSymbol symbol = createNullableTypeSymbol();
        ITSPHPAst result = symbol.getDefaultValue();

        assertThat(result.getType(), is(TokenTypes.Null));
        assertThat(result.getText(), is("null"));
    }

    @Test
    public void removeModifier_TryToRemoveNullableModifier_ReturnsFalseIsNullableRemainsTrue() {
        //no arrange necessary

        ANullableTypeSymbol symbol = createNullableTypeSymbol();
        boolean result = symbol.removeModifier(TokenTypes.QuestionMark);
        boolean isNullable = symbol.isNullable();

        assertThat(result, is(false));
        assertThat(isNullable, is(true));
    }

    /**
     * Different behaviour than ATypeSymbol
     */
    @Test
    public void isNullable_NothingDefinedParentType_ReturnsTrue() {
        //no arrange necessary

        ANullableTypeSymbol symbol = createNullableTypeSymbol("foo", mock(ITypeSymbol.class));
        boolean result = symbol.isNullable();

        assertThat(result, is(true));
    }

    /**
     * Different behaviour than ATypeSymbol
     */
    @Test
    public void isNullable_NothingDefinedParents_ReturnsTrue() {
        //no arrange necessary

        ANullableTypeSymbol symbol = createNullableTypeSymbol("foo", new HashSet<ITypeSymbol>());
        boolean result = symbol.isNullable();

        assertThat(result, is(true));
    }

    /**
     * Different behaviour than ASymbolWithModifier
     */
    @Test
    public void setModifiers_EmptySet_ContainsNullableAnyway() {
        //no arrange necessary

        ANullableTypeSymbol symbol = createNullableTypeSymbol();
        symbol.setModifiers(new ModifierSet());
        boolean result = symbol.isNullable();

        assertThat(result, is(true));
    }

    private ANullableTypeSymbol createNullableTypeSymbol() {
        return createNullableTypeSymbol("foo", mock(ITypeSymbol.class));
    }

    protected ANullableTypeSymbol createNullableTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        return new DummyNullableTypeSymbol(name, parentTypeSymbol);
    }

    protected ANullableTypeSymbol createNullableTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbol) {
        return new DummyNullableTypeSymbol(name, parentTypeSymbol);
    }
}
