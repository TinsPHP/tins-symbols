/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousScopedSymbol;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class AErroneousScopedSymbolTest
{
    class DummyErroneousScopedSymbol extends AErroneousScopedSymbol
    {

        public DummyErroneousScopedSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
            super(ast, name, exception);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getScopeName_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.getScopeName();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getEnclosingScope_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.getEnclosingScope();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void defineSymbol_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.define(mock(ISymbol.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void doubleDefinitionCheck_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.doubleDefinitionCheck(mock(ISymbol.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void resolve_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.resolve(mock(ITSPHPAst.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.getSymbols();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.addModifier(1);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.removeModifier(1);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.getModifiers();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.setModifiers(mock(IModifierSet.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addToInitialisedSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.addToInitialisedSymbols(mock(ISymbol.class), true);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getInitialisedSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        symbol.getInitialisedSymbols();

        //assert in annotation
    }


    @Test
    public void isFullyInitialised_Standard_ReturnsTrue() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        boolean result = symbol.isFullyInitialised(mock(ISymbol.class));

        assertThat(result, is(true));
    }

    @Test
    public void isPartiallyInitialised_Standard_ReturnsTrue() {
        //no arrange necessary

        AErroneousScopedSymbol symbol = createSymbol();
        boolean result = symbol.isPartiallyInitialised(mock(ISymbol.class));

        assertThat(result, is(true));
    }

    private AErroneousScopedSymbol createSymbol() {
        return createSymbol(mock(ITSPHPAst.class), "foo", new TSPHPException());
    }

    protected AErroneousScopedSymbol createSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
        return new DummyErroneousScopedSymbol(ast, name, exception);

    }

}
