/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.PseudoTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PseudoTypeSymbol_ASymbol_LSPTest extends ASymbolTest
{
    @Override
    public void getDefinitionAst_Standard_ReturnsOnePassedToConstructor() {
        // different behaviour - pseudo types do not have a definition ast
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

    @Test
    public void getAbsoluteName_DefinitionScopeNotNull_ReturnsNameWithDefinitionScopeNameAsPrefix() {
        // different behaviour - pseudo types do not belong to a namespace and thus are not prefix with its name

        // start same as in ASymbolTest
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String scopeName = "\\";
        String name = "dummy";
        IScope scope = mock(IScope.class);
        when(scope.getScopeName()).thenReturn(scopeName);

        ASymbol symbol = createSymbol(ast, name);
        symbol.setDefinitionScope(scope);
        String result = symbol.getAbsoluteName();
        // end same as in ASymbolTest

//        assertThat(result, Is.is(scopeName + name));
        assertThat(result, Is.is(name));
    }

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new PseudoTypeSymbol(name, null, false);
    }
}
