/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.ArrayTypeSymbol;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArrayTypeSymbol_ASymbol_LSPTest extends ASymbolTest
{

    @Override
    public void getDefinitionAst_Standard_ReturnsOnePassedToConstructor() {
        // different behaviour - nullable types are pre-defined types which do not have a definition Ast
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
    public void toString_NoTypeDefined_ReturnsName() {
        // different behaviour - appends the modifiers in addition. See ASymbolWithModifier
        // Since ANullableTypeSymbol has always the nullable modifier it is different

        // start same as in ASymbolTest
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";

        ASymbol symbol = createSymbol(ast, name);
        String result = symbol.toString();
        // end same as in ASymbolTest

        //assertThat(result, is(name));
        assertThat(result, is(name + "|" + TokenTypes.QuestionMark));
    }

    @Override
    public void toString_TypeDefined_ReturnsNameColonTypeToString() {
        // different behaviour - appends the modifiers in addition. See ASymbolWithModifier
        // Since ANullableTypeSymbol has always the nullable modifier it is different

        // start same as in ASymbolTest
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";
        String typeName = "bar";
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.toString()).thenReturn(typeName);

        ASymbol symbol = createSymbol(ast, name);
        symbol.setType(typeSymbol);
        String result = symbol.toString();
        // end same as in ASymbolTest

        //assertThat(result, is(name + ":" + typeName));
        assertThat(result, is(name + ":" + typeName + "|" + TokenTypes.QuestionMark));
    }

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new ArrayTypeSymbol(name, null, null, null);
    }
}
