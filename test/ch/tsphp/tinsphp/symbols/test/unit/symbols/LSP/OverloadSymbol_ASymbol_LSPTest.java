/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.OverloadSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class OverloadSymbol_ASymbol_LSPTest extends ASymbolTest
{

    @Override
    public void getDefinitionAst_Standard_ReturnsOnePassedToConstructor() {
        // different behaviour - overload symbols are pre-defined which do not have a definition Ast
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

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new OverloadSymbol(name);
    }
}
