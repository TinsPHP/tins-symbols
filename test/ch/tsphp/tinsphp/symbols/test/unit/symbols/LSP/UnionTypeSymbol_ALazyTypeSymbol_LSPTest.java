/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.ALazyTypeSymbol;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ALazyTypeSymbolTest;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class UnionTypeSymbol_ALazyTypeSymbol_LSPTest extends ALazyTypeSymbolTest
{

    @Override
    @Test
    public void isFalseable_Standard_ThrowsUnsupportedOperationException() {
        // different behaviour - UnionTypeSymbol returns true in case the union is ready for eval and contains false
        // otherwise false in contrast to ALazyTypeSymbol which throws an UnsupportedOperationException.
        // see UnionTypeSymbolTest for further information. For the following scenario it returns false since the
        // union is empty

        // start same as in ASymbolWithModifierTest
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        boolean result = typeSymbol.isFalseable();

        assertThat(result, is(false));
    }

    @Override
    @Test
    public void isNullable_Standard_ThrowsUnsupportedOperationException() {
        // different behaviour - UnionTypeSymbol returns true in case the union is ready for eval and contains null
        // otherwise false in contrast to ALazyTypeSymbol which throws an UnsupportedOperationException.
        // see UnionTypeSymbolTest for further information. For the following scenario it returns false since the
        // union is empty

        // start same as in ASymbolWithModifierTest
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        boolean result = typeSymbol.isNullable();

        assertThat(result, is(false));
    }

    @Override
    @Test
    public void getName_Standard_ReturnsQuestionMark() {
        // different behaviour - UnionTypeSymbol returns the set of types containing in this union if it is ready for
        // eval. Yet, since in this case it is not ready for eval it actually returns ?

        super.getName_Standard_ReturnsQuestionMark();
    }

    @Override
    @Test
    public void getAbsoluteName_Standard_ReturnsQuestionMark() {
        // different behaviour - UnionTypeSymbol returns the set of types containing in this union if it is ready for
        // eval. Yet, since in this case it is not ready for eval it actually returns ?

        super.getAbsoluteName_Standard_ReturnsQuestionMark();
    }

    @Override
    protected ALazyTypeSymbol createLazyTypeSymbol() {
        return new UnionTypeSymbol(mock(IOverloadResolver.class), new HashMap<String, ITypeSymbol>());
    }

    @Override
    protected void callNotifyForEvalReadyListeners(ALazyTypeSymbol typeSymbol) {
        ((UnionTypeSymbol) typeSymbol).seal();
    }
}
