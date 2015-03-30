/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.ExpressionTypeVariableSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolTest;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpressionTypeVariableSymbol_ASymbol_LSPTest extends ASymbolTest
{
    @Override
    @Test
    public void getType_OneSet_ReturnsTheOneWhichWasSet() {
        // different behaviour - ExpressionTypeVariableSymbol only supports IUnionTypeSymbol,
        // cast its ITypeSymbol IUnionTypeSymbol respectively

        ITypeSymbol typeSymbol = mock(IUnionTypeSymbol.class);
//        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        // start same as in ASymbolTest
        ASymbol symbol = createSymbol();
        symbol.setType(typeSymbol);
        ITypeSymbol result = symbol.getType();

        assertThat(result, CoreMatchers.is(typeSymbol));
        // end same as in ASymbolTest
    }

    private ASymbol createSymbol() {
        return createSymbol(mock(ITSPHPAst.class), "foo");
    }

    @Test
    public void getName_Standard_ReturnsOnePassedToConstructor() {
        // different behaviour - ExpressionTypeVariableSymbol creates an own name based on the definition ast

        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn("+");
//        ITSPHPAst ast = mock(ITSPHPAst.class);
        // start same as in ASymbolTest
        String name = "foo";

        ASymbol symbol = createSymbol(ast, name);
        String result = symbol.getName();
        // end same as in ASymbolTest

        assertThat(result, is("+@0|0"));
//        assertThat(result, CoreMatchers.is(name));
    }


    @Test
    public void getAbsoluteName_DefinitionScopeIsNull_ReturnsName() {
        // different behaviour - ExpressionTypeVariableSymbol creates an own name based on the definition ast

        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn("+");
//        ITSPHPAst ast = mock(ITSPHPAst.class);
        // start same as in ASymbolTest
        String name = "dummy";

        ASymbol symbol = createSymbol(ast, name);
        String result = symbol.getAbsoluteName();
        // end same as in ASymbolTest

        assertThat(result, is("+@0|0"));
//        assertThat(result, Is.is(name));
    }

    @Test
    public void getAbsoluteName_DefinitionScopeNotNull_ReturnsNameWithDefinitionScopeNameAsPrefix() {
        // different behaviour - ExpressionTypeVariableSymbol creates an own name based on the definition ast

        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn("+");
//        ITSPHPAst ast = mock(ITSPHPAst.class);
        // start same as in ASymbolTest
        String scopeName = "\\";
        String name = "dummy";
        IScope scope = mock(IScope.class);
        when(scope.getScopeName()).thenReturn(scopeName);

        ASymbol symbol = createSymbol(ast, name);
        symbol.setDefinitionScope(scope);
        String result = symbol.getAbsoluteName();
        // end same as in ASymbolTest

        assertThat(result, is(scopeName + "+@0|0"));
//        assertThat(result, is(scopeName + name));
    }

    @Test
    public void toString_NoTypeDefined_ReturnsName() {
        // different behaviour - ExpressionTypeVariableSymbol creates an own name based on the definition ast

        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn("+");
//        ITSPHPAst ast = mock(ITSPHPAst.class);
        // start same as in ASymbolTest
        String name = "foo";

        ASymbol symbol = createSymbol(ast, name);
        String result = symbol.toString();
        // end same as in ASymbolTest

        assertThat(result, is("+@0|0"));
//        assertThat(result, CoreMatchers.is(name));
    }

    @Test
    public void toString_TypeDefined_ReturnsNameColonTypeToString() {
        // different behaviour - ExpressionTypeVariableSymbol creates an own name based on the definition ast

        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn("+");
//        ITSPHPAst ast = mock(ITSPHPAst.class);
        // start same as in ASymbolTest
        String name = "foo";
        String typeName = "bar";
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.toString()).thenReturn(typeName);

        ASymbol symbol = createSymbol(ast, name);
        symbol.setType(typeSymbol);
        String result = symbol.toString();
        // end same as in ASymbolTest

        assertThat(result, is("+@0|0:" + typeName));
//        assertThat(result, CoreMatchers.is(name + ":" + typeName));
    }

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new ExpressionTypeVariableSymbol(definitionAst);
    }
}
