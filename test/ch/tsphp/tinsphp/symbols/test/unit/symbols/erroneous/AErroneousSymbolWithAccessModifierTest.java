/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousSymbolWithAccessModifier;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class AErroneousSymbolWithAccessModifierTest
{

    class DummyErroneousSymbolWithAccessModifier extends AErroneousSymbolWithAccessModifier
    {
        public DummyErroneousSymbolWithAccessModifier(ITSPHPAst ast, String name, TSPHPException theException) {
            super(ast, name, theException);
        }
    }

    @Test
    public void isPublic_Standard_ReturnsTrue() {
        //no arrange necessary

        AErroneousSymbolWithAccessModifier symbol = createSymbol();
        boolean result = symbol.isPublic();

        assertThat(result, is(true));
    }

    @Test
    public void isProtected_Standard_ReturnsFalse() {
        //no arrange necessary

        AErroneousSymbolWithAccessModifier variableSymbol = createSymbol();
        boolean result = variableSymbol.isProtected();

        assertThat(result, is(false));
    }

    @Test
    public void isPrivate_Standard_ReturnsFalse() {
        //no arrange necessary

        AErroneousSymbolWithAccessModifier variableSymbol = createSymbol();
        boolean result = variableSymbol.isPrivate();

        assertThat(result, is(false));
    }

    private AErroneousSymbolWithAccessModifier createSymbol() {
        return createSymbol(mock(ITSPHPAst.class), "foo", new TSPHPException());
    }

    protected AErroneousSymbolWithAccessModifier createSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
        return new DummyErroneousSymbolWithAccessModifier(ast, name, exception);

    }

}
