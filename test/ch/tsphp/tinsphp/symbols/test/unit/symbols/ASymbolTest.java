/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ASymbol;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ASymbolTest
{
    class DummySymbol extends ASymbol
    {

        protected DummySymbol(ITSPHPAst theDefinitionAst, String theName) {
            super(theDefinitionAst, theName);
        }
    }

    @Test
    public void getDefinitionAst_Standard_ReturnsOnePassedToConstructor() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";

        ASymbol symbol = createSymbol(ast, name);
        ITSPHPAst result = symbol.getDefinitionAst();

        assertThat(result, is(ast));
    }

    @Test
    public void getName_Standard_ReturnsOnePassedToConstructor() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";

        ASymbol symbol = createSymbol(ast, name);
        String result = symbol.getName();

        assertThat(result, is(name));
    }

    @Test
    public void getAbsoluteName_DefinitionScopeIsNull_ReturnsName() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "dummy";

        ASymbol symbol = createSymbol(ast, name);
        String result = symbol.getAbsoluteName();

        assertThat(result, Is.is(name));
    }

    @Test
    public void getAbsoluteName_DefinitionScopeNotNull_ReturnsNameWithDefinitionScopeNameAsPrefix() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String scopeName = "\\";
        String name = "dummy";
        IScope scope = mock(IScope.class);
        when(scope.getScopeName()).thenReturn(scopeName);

        ASymbol symbol = createSymbol(ast, name);
        symbol.setDefinitionScope(scope);
        String result = symbol.getAbsoluteName();

        assertThat(result, Is.is(scopeName + name));
    }

    @Test
    public void getDefinitionScope_NothingDefined_ReturnsNull() {
        //no arrange necessary

        ASymbol symbol = createSymbol();
        IScope result = symbol.getDefinitionScope();

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getDefinitionScope_OneSet_ReturnsTheOneWhichWasSet() {
        IScope scope = mock(IScope.class);

        ASymbol symbol = createSymbol();
        symbol.setDefinitionScope(scope);
        IScope result = symbol.getDefinitionScope();

        assertThat(result, is(scope));
    }

    @Test
    public void getType_NothingDefined_ReturnsNull() {
        //no arrange necessary

        ASymbol symbol = createSymbol();
        ITypeSymbol result = symbol.getType();

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getType_OneSet_ReturnsTheOneWhichWasSet() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        ASymbol symbol = createSymbol();
        symbol.setType(typeSymbol);
        ITypeSymbol result = symbol.getType();

        assertThat(result, is(typeSymbol));
    }

    @Test
    public void toString_NoTypeDefined_ReturnsName() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";

        ASymbol symbol = createSymbol(ast, name);
        String result = symbol.toString();

        assertThat(result, is(name));
    }

    @Test
    public void toString_TypeDefined_ReturnsNameColonTypeToString() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        String name = "foo";
        String typeName = "bar";
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.toString()).thenReturn(typeName);

        ASymbol symbol = createSymbol(ast, name);
        symbol.setType(typeSymbol);
        String result = symbol.toString();

        assertThat(result, is(name + ":" + typeName));
    }

    private ASymbol createSymbol() {
        return createSymbol(mock(ITSPHPAst.class), "foo");
    }

    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new DummySymbol(definitionAst, name);
    }
}
