/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.MinimalTypeVariableSymbolWithRef;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class MinimalTypeVariableSymbolWithRef_ASymbol_LSPTest extends ASymbolTest
{

    @Override
    @Test
    public void getType_OneSet_ReturnsTheOneWhichWasSet() {
        // different behaviour - MinimalTypeVariable only supports IUnionTypeSymbol,
        // cast its ITypeSymbol to IUnionTypeSymbol respectively

        ITypeSymbol typeSymbol = mock(IUnionTypeSymbol.class);
//        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        // start same as in ASymbolTest
        ASymbol symbol = createSymbol();
        symbol.setType(typeSymbol);
        ITypeSymbol result = symbol.getType();

        assertThat(result, is(typeSymbol));
        // end same as in ASymbolTest
    }

    @Override
    public void getDefinitionAst_Standard_ReturnsOnePassedToConstructor() {
        // different behaviour - MinimalTypeVariable is used for predefined types which do not have a definition Ast
        // therefore it always returns null.

        // start same as in ASymbolTest
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";

        ASymbol symbol = createSymbol(ast, name);
        ITSPHPAst result = symbol.getDefinitionAst();
        // end same as in ASymbolTest

        //assertThat(result, is(ast));
        assertThat(result, is(nullValue()));
    }

    private ASymbol createSymbol() {
        return createSymbol(mock(ITSPHPAst.class), "foo");
    }

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new MinimalTypeVariableSymbolWithRef(name);
    }
}
