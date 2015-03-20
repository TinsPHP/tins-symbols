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
import ch.tsphp.common.symbols.IForEvalReadyListener;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousTypeSymbol;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.hamcrest.core.Is;
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

        assertThat(result, Is.is((ITypeSymbol) typeSymbol));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getTypeSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        typeSymbol.getTypeSymbols();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addTypeSymbol_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        typeSymbol.addTypeSymbol(mock(ITypeSymbol.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void merge_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        typeSymbol.merge(mock(IUnionTypeSymbol.class));

        //assert in annotation
    }


    @Test(expected = UnsupportedOperationException.class)
    public void seal_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        typeSymbol.seal();

        //assert in annotation
    }


    @Test(expected = UnsupportedOperationException.class)
    public void addForEvalReadyListener_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        typeSymbol.addForEvalReadyListener(mock(IForEvalReadyListener.class));

        //assert in annotation
    }

    @Test
    public void isReadyForEval_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousTypeSymbol typeSymbol = createTypeSymbol();
        boolean result = typeSymbol.isReadyForEval();

        assertThat(result, is(true));
    }

    private IErroneousTypeSymbol createTypeSymbol() {
        return createTypeSymbol(mock(ITSPHPAst.class), "foo", new TSPHPException(), mock(IMethodSymbol.class));
    }

    protected IErroneousTypeSymbol createTypeSymbol(
            ITSPHPAst ast, String name, TSPHPException exception, IMethodSymbol methodSymbol) {
        return new ErroneousTypeSymbol(ast, name, exception, methodSymbol);

    }

}
