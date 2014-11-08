/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ATypeSymbol;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class ATypeSymbolTest
{
    class DummyTypeSymbol extends ATypeSymbol
    {

        public DummyTypeSymbol(ITSPHPAst theDefinitionAst, String theName, ITypeSymbol theParentTypeSymbol) {
            super(theDefinitionAst, theName, theParentTypeSymbol);
        }

        public DummyTypeSymbol(ITSPHPAst theDefinitionAst, String theName, Set<ITypeSymbol> theParentTypeSymbol) {
            super(theDefinitionAst, theName, theParentTypeSymbol);
        }

        @Override
        public ITSPHPAst getDefaultValue() {
            return null;
        }
    }

    @Test
    public void getParentTypeSymbols_StandardWithType_ReturnsOnePassedToConstructor() {
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);

        ATypeSymbol typeSymbol = createTypeSymbol(parentTypeSymbol);
        Set<ITypeSymbol> result = typeSymbol.getParentTypeSymbols();

        assertThat(result, IsIterableContainingInAnyOrder.containsInAnyOrder(parentTypeSymbol));
    }

    @Test
    public void getParentTypeSymbols_StandardWithSet_ReturnsOnePassedToConstructor() {
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentTypeSymbols = new HashSet<>();
        parentTypeSymbols.add(parentTypeSymbol);

        ATypeSymbol typeSymbol = createTypeSymbol(parentTypeSymbols);
        Set<ITypeSymbol> result = typeSymbol.getParentTypeSymbols();

        assertThat(result, is(parentTypeSymbols));
    }

    @Test
    public void isFalseable_NothingDefined_ReturnsFalse() {
        //no arrange necessary

        ATypeSymbol typeSymbol = createTypeSymbol();
        boolean result = typeSymbol.isFalseable();

        assertThat(result, is(false));
    }

    @Test
    public void isFalseable_FalseableAdded_ReturnsTrue() {
        //no arrange necessary

        ATypeSymbol typeSymbol = createTypeSymbol();
        TypeHelper.addFalseableModifier(typeSymbol);
        boolean result = typeSymbol.isFalseable();

        assertThat(result, is(true));
    }

    @Test
    public void isNullable_NothingDefined_ReturnsFalse() {
        //no arrange necessary

        ATypeSymbol typeSymbol = createTypeSymbol();
        boolean result = typeSymbol.isNullable();

        assertThat(result, is(false));
    }

    @Test
    public void isNullable_NullableAdded_ReturnsTrue() {
        //no arrange necessary

        ATypeSymbol typeSymbol = createTypeSymbol();
        TypeHelper.addNullableModifier(typeSymbol);
        boolean result = typeSymbol.isNullable();

        assertThat(result, is(true));
    }

    private ATypeSymbol createTypeSymbol() {
        return createTypeSymbol(mock(ITypeSymbol.class));
    }

    private ATypeSymbol createTypeSymbol(ITypeSymbol parentTypeSymbol) {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", parentTypeSymbol);
    }

    private ATypeSymbol createTypeSymbol(Set<ITypeSymbol> parentTypeSymbols) {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", parentTypeSymbols);
    }

    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        return new DummyTypeSymbol(definitionAst, name, parentTypeSymbol);
    }

    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, Set<ITypeSymbol> parentTypeSymbol) {
        return new DummyTypeSymbol(definitionAst, name, parentTypeSymbol);
    }
}
