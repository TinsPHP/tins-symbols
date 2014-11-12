/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousSymbol;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class AErroneousSymbolTest
{
    class DummyErroneousSymbol extends AErroneousSymbol
    {

        public DummyErroneousSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
            super(ast, name, exception);
        }
    }

    @Test
    public void getException_Standard_ReturnsOnePassedToConstructor() {
        TSPHPException exception = new TSPHPException();

        AErroneousSymbol symbol = createSymbol(exception);
        TSPHPException result = symbol.getException();

        assertThat(result, is(exception));
    }

    private AErroneousSymbol createSymbol(TSPHPException exception) {
        return createSymbol(mock(ITSPHPAst.class), "foo", exception);
    }

    protected AErroneousSymbol createSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
        return new DummyErroneousSymbol(ast, name, exception);

    }

}
