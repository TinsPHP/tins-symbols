/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousVariableSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousVariableSymbol;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ErroneousVariableSymbolTest
{

    @Test
    public void isStatic_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousVariableSymbol variableSymbol = createVariableSymbol();
        boolean result = variableSymbol.isStatic();

        assertThat(result, is(true));
    }

    @Test
    public void isAlwaysCasting_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousVariableSymbol variableSymbol = createVariableSymbol();
        boolean result = variableSymbol.isAlwaysCasting();

        assertThat(result, is(true));
    }

    @Test
    public void isFalseable_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousVariableSymbol variableSymbol = createVariableSymbol();
        boolean result = variableSymbol.isFalseable();

        assertThat(result, is(true));
    }

    @Test
    public void isNullable_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousVariableSymbol variableSymbol = createVariableSymbol();
        boolean result = variableSymbol.isNullable();

        assertThat(result, is(true));
    }

    private IErroneousVariableSymbol createVariableSymbol() {
        return createVariableSymbol(mock(ITSPHPAst.class), "foo", new TSPHPException());
    }

    protected IErroneousVariableSymbol createVariableSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
        return new ErroneousVariableSymbol(ast, name, exception);
    }
}