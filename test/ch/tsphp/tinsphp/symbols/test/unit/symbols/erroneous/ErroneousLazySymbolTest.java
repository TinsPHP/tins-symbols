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
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousLazySymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.ILazySymbolResolver;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousLazySymbol;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ErroneousLazySymbolTest
{
    @Test
    public void isStatic_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        boolean result = lazySymbol.isStatic();

        assertThat(result, is(true));
    }

    @Test
    public void isAlwaysCasting_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        boolean result = lazySymbol.isAlwaysCasting();

        assertThat(result, is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void toTypeWithModifiersDto_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        lazySymbol.toTypeWithModifiersDto();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getParentTypeSymbols_Standard_ThrowsUnsupportedOeprationException() {
        //no arrange necessary

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        lazySymbol.getParentTypeSymbols();

        //assert in annotation
    }

    @Test
    public void isFalseable_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        boolean result = lazySymbol.isFalseable();

        assertThat(result, is(true));
    }

    @Test
    public void isNullable_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        boolean result = lazySymbol.isNullable();

        assertThat(result, is(true));
    }

    @Test
    public void getDefaultValue_Standard_UsesAstHelpRegistry() {
        IAstHelper astHelper = mock(IAstHelper.class);
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(astHelper.createAst(TokenTypes.Null, "null")).thenReturn(ast);
        AstHelperRegistry.set(astHelper);

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        ITSPHPAst result = lazySymbol.getDefaultValue();

        assertThat(result, is(ast));
    }

    @Test
    public void isPublic_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousLazySymbol symbol = createLazySymbol();
        boolean result = symbol.isPublic();

        assertThat(result, is(true));
    }

    @Test
    public void isProtected_Standard_ReturnsFalse() {
        //no arrange necessary

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        boolean result = lazySymbol.isProtected();

        assertThat(result, is(false));
    }

    @Test
    public void isPrivate_Standard_ReturnsFalse() {
        //no arrange necessary

        IErroneousLazySymbol lazySymbol = createLazySymbol();
        boolean result = lazySymbol.isPrivate();

        assertThat(result, is(false));
    }

    private IErroneousLazySymbol createLazySymbol() {
        return createVariableSymbol(
                mock(ILazySymbolResolver.class), mock(ITSPHPAst.class), "foo", new TSPHPException());
    }

    protected IErroneousLazySymbol createVariableSymbol(
            ILazySymbolResolver lazySymbolResolver, ITSPHPAst ast, String name, TSPHPException exception) {
        return new ErroneousLazySymbol(ast, name, exception, lazySymbolResolver);
    }
}
