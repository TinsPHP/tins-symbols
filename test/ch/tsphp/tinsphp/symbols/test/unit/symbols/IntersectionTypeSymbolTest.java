/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void isFinal_NoTypeAdded_ReturnsFalse() {
        //no arrange necessary

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        boolean result = intersectionTypeSymbol.isFinal();

        assertThat(result, is(false));
    }

    @Test
    public void isFinal_ContainsNonFinalType_ReturnsFalse() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.isFinal()).thenReturn(false);
        when(typeSymbol.getAbsoluteName()).thenReturn("dummy");

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(typeSymbol);
        boolean result = intersectionTypeSymbol.isFinal();

        assertThat(result, is(false));
    }

    @Test
    public void isFinal_ContainsFinalType_ReturnsTrue() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.isFinal()).thenReturn(true);
        when(typeSymbol.getAbsoluteName()).thenReturn("dummy");

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(typeSymbol);
        boolean result = intersectionTypeSymbol.isFinal();

        assertThat(result, is(true));
    }

    @Test
    public void isFinal_ContainsMultipleTypes_ReturnsFalse() {
        ITypeSymbol typeSymbol1 = mock(ITypeSymbol.class);
        when(typeSymbol1.isFinal()).thenReturn(true);
        when(typeSymbol1.getAbsoluteName()).thenReturn("dummy1");
        ITypeSymbol typeSymbol2 = mock(ITypeSymbol.class);
        when(typeSymbol2.isFinal()).thenReturn(false);
        when(typeSymbol2.getAbsoluteName()).thenReturn("dummy2");

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(typeSymbol1);
        intersectionTypeSymbol.addTypeSymbol(typeSymbol2);
        boolean result = intersectionTypeSymbol.isFinal();

        assertThat(result, is(false));
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

