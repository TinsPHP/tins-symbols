/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousTypeSymbol;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ErroneousTypeSymbolTest
{

    @Test(expected = UnsupportedOperationException.class)
    public void getParentTypeSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        typeSymbol.getParentTypeSymbols();

        //assert in annotation
    }

    @Test
    public void isFalseable_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        boolean result = typeSymbol.isFalseable();

        assertThat(result, is(true));
    }

    @Test
    public void isNullable_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        boolean result = typeSymbol.isNullable();

        assertThat(result, is(true));
    }

    @Test
    public void getDefaultValue_Standard_UsesAstHelpRegistry() {
        IAstHelper astHelper = mock(IAstHelper.class);
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(astHelper.createAst(TokenTypes.Null, "null")).thenReturn(ast);
        AstHelperRegistry.set(astHelper);

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        ITSPHPAst result = typeSymbol.getDefaultValue();

        assertThat(result, is(ast));
    }

    @Test
    public void evalSelf_Standard_ReturnsThis() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        ITypeSymbol result = typeSymbol.evalSelf();

        assertThat(result, is((ITypeSymbol) typeSymbol));
    }

    @Test
    public void canBeUsedInIntersection_Standard_ReturnsFalse() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        boolean result = typeSymbol.canBeUsedInIntersection();

        assertThat(result, is(false));
    }

    private IErroneousTypeSymbol createTypeSymbol() {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", new TSPHPException(), mock(IMethodSymbol.class));
    }

    protected IErroneousTypeSymbol createTypeSymbol(
            ITSPHPAst ast, String name, TSPHPException exception, IMethodSymbol methodSymbol) {
        return new ErroneousTypeSymbol(ast, name, exception, methodSymbol);

    }

}
