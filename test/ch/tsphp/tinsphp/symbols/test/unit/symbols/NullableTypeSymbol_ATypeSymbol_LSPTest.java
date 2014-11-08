/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ATypeSymbol;
import ch.tsphp.tinsphp.symbols.NullTypeSymbol;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class NullableTypeSymbol_ATypeSymbol_LSPTest extends ATypeSymbolTest
{

    @Override
    public void isNullable_NothingDefined_ReturnsFalse() {
        // different behaviour - ANullableTypeSymbol has always the nullable modifier

        // start same as in ATypeSymbol
        //no arrange necessary

        ATypeSymbol typeSymbol = createTypeSymbol();
        boolean result = typeSymbol.isNullable();
        // end same as in ATypeSymbol

//        assertThat(result, is(false));
        assertThat(result, is(true));
    }

    @Override
    public void getParentTypeSymbols_StandardWithType_ReturnsOnePassedToConstructor() {
        // different behaviour - ANullableTypeSymbol has always the nullable modifier

        // start same as in ATypeSymbol
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);

        ATypeSymbol typeSymbol = createTypeSymbol(parentTypeSymbol);
        Set<ITypeSymbol> result = typeSymbol.getParentTypeSymbols();
        // end same as in ATypeSymbol

//        assertThat(result, IsIterableContainingInAnyOrder.containsInAnyOrder(parentTypeSymbol));
        assertThat(result.size(), is(0));
    }

    @Override
    public void getParentTypeSymbols_StandardWithSet_ReturnsOnePassedToConstructor() {
        // different behaviour - ANullableTypeSymbol has always the nullable modifier

        // start same as in ATypeSymbol
        ITypeSymbol parentTypeSymbol = mock(ITypeSymbol.class);
        Set<ITypeSymbol> parentTypeSymbols = new HashSet<>();
        parentTypeSymbols.add(parentTypeSymbol);

        ATypeSymbol typeSymbol = createTypeSymbol(parentTypeSymbols);
        Set<ITypeSymbol> result = typeSymbol.getParentTypeSymbols();
        // end same as in ATypeSymbol

//        assertThat(result, is(parentTypeSymbols));
        assertThat(result.size(), is(0));
    }

    private ATypeSymbol createTypeSymbol() {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", mock(ITypeSymbol.class));
    }

    private ATypeSymbol createTypeSymbol(Set<ITypeSymbol> parentTypeSymbols) {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", parentTypeSymbols);
    }

    private ATypeSymbol createTypeSymbol(ITypeSymbol parentTypeSymbol) {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", parentTypeSymbol);
    }

    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        return new NullTypeSymbol();
    }

    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, Set<ITypeSymbol> parentTypeSymbol) {
        return new NullTypeSymbol();
    }
}
