/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousSymbolWithModifier;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class AErroneousSymbolWithModifierTest
{
    class DummyErroneousSymbolWithModifier extends AErroneousSymbolWithModifier
    {

        public DummyErroneousSymbolWithModifier(ITSPHPAst ast, String name, TSPHPException exception) {
            super(ast, name, exception);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousSymbolWithModifier symbol = createSymbol();
        symbol.addModifier(1);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousSymbolWithModifier symbol = createSymbol();
        symbol.removeModifier(1);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousSymbolWithModifier symbol = createSymbol();
        symbol.getModifiers();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AErroneousSymbolWithModifier symbol = createSymbol();
        symbol.setModifiers(mock(IModifierSet.class));

        //assert in annotation
    }

    private AErroneousSymbolWithModifier createSymbol() {
        return createSymbol(mock(ITSPHPAst.class), "foo", new TSPHPException());
    }

    protected AErroneousSymbolWithModifier createSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
        return new DummyErroneousSymbolWithModifier(ast, name, exception);

    }

}
