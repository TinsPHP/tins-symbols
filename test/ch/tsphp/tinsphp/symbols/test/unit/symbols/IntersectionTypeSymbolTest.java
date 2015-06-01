/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class IntersectionTypeSymbolTest extends ATypeHelperTest
{

    @Test
    public void getAbsoluteName_IsEmpty_ReturnsMixed() {
        //no arrange necessary

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        String result = intersectionTypeSymbol.getAbsoluteName();

        assertThat(result, is(PrimitiveTypeNames.MIXED));
    }

    @Test
    public void getAbsoluteName_OneTypeOnly_ReturnsTypeWithoutParenthesis() {
        //pre-act
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);

        //act
        String result = intersectionTypeSymbol.getAbsoluteName();

        //assert
        assertThat(result, is("int"));
    }

    @Test
    public void getAbsoluteName_IntAndFloat_ReturnsEmptyParenthesis() {
        //pre-act
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);
        intersectionTypeSymbol.addTypeSymbol(floatType);

        //act
        String result = intersectionTypeSymbol.getAbsoluteName();

        //assert
        assertThat(result, is("(float & int)"));
    }

    @Test
    public void getName_IsEmpty_ReturnsMixedUsedGetAbsoluteName() {
        //no arrange necessary

        IIntersectionTypeSymbol intersectionTypeSymbol = spy(createIntersectionTypeSymbol());
        String result = intersectionTypeSymbol.getName();

        assertThat(result, is(PrimitiveTypeNames.MIXED));
        verify(intersectionTypeSymbol).getAbsoluteName();
    }

    @Test
    public void toString_IsEmpty_ReturnsMixedUsedGetAbsoluteName() {
        //no arrange necessary

        IIntersectionTypeSymbol intersectionTypeSymbol = spy(createIntersectionTypeSymbol());
        String result = intersectionTypeSymbol.toString();

        assertThat(result, is(PrimitiveTypeNames.MIXED));
        verify(intersectionTypeSymbol).getAbsoluteName();
    }
}

