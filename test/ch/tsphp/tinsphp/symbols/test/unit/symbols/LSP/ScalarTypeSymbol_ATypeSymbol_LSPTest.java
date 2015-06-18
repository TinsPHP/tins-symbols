/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ATypeSymbol;
import ch.tsphp.tinsphp.symbols.ScalarTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ATypeSymbolTest;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class ScalarTypeSymbol_ATypeSymbol_LSPTest extends ATypeSymbolTest
{

    @Override
    public void getParentTypeSymbols_StandardWithSet_ReturnsOnePassedToConstructor() {
        // different behaviour - PseudoTypeSymbol does not support multiple parent types
    }

    @Override
    public void isFinal_Standard_ReturnsFalse() {
        // different behaviour - array types are final

        // start same as in ASymbolTest
        //no arrange necessary

        ATypeSymbol typeSymbol = createTypeSymbol();
        boolean result = typeSymbol.isFinal();
        // end same as in ASymbolTest

        //assertThat(result, is(true));
        assertThat(result, is(true));
    }

    private ATypeSymbol createTypeSymbol() {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", mock(ITypeSymbol.class));
    }

    @Override
    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        return new ScalarTypeSymbol(name, parentTypeSymbol, 0, "");
    }

    @Override
    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, Set<ITypeSymbol> parentTypeSymbol) {
        throw new UnsupportedOperationException("ScalarTypeSymbol does not support multiple parent type symbols");
    }
}
