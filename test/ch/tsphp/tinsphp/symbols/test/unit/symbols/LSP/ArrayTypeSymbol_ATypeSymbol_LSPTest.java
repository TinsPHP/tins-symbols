/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ATypeSymbol;
import ch.tsphp.tinsphp.symbols.ArrayTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ATypeSymbolTest;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class ArrayTypeSymbol_ATypeSymbol_LSPTest extends ATypeSymbolTest
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
    public void getParentTypeSymbols_StandardWithSet_ReturnsOnePassedToConstructor() {
        // different behaviour - ArrayTypeSymbol does not support multiple parent types
    }


    private ATypeSymbol createTypeSymbol() {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", mock(ITypeSymbol.class));
    }

    @Override
    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        return new ArrayTypeSymbol(name, null, null, parentTypeSymbol);
    }

    @Override
    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, Set<ITypeSymbol> parentTypeSymbol) {
        throw new UnsupportedOperationException("ArrayTypeSymbol does not support multiple parent type symbols");
    }
}
