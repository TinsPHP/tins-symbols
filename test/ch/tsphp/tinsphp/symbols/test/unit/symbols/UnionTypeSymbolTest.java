/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class UnionTypeSymbolTest extends ATypeHelperTest
{

    @Test
    public void getAbsoluteName_IsEmpty_ReturnsNothing() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        String result = unionTypeSymbol.getAbsoluteName();

        assertThat(result, is(PrimitiveTypeNames.NOTHING));
    }

    @Test
    public void getAbsoluteName_OneTypeOnly_ReturnsTypeWithoutParenthesis() {
        //pre-act
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        //act
        String result = unionTypeSymbol.getAbsoluteName();

        //assert
        assertThat(result, is("int"));
    }

    @Test
    public void getAbsoluteName_IntAndFloat_ReturnsEmptyParenthesis() {
        //pre-act
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //act
        String result = unionTypeSymbol.getAbsoluteName();

        //assert
        assertThat(result, is("(float | int)"));
    }

    @Test
    public void getName_IsEmpty_ReturnsNothingUsedGetName() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = spy(createUnionTypeSymbol());
        String result = unionTypeSymbol.getName();

        assertThat(result, is(PrimitiveTypeNames.NOTHING));
        verify(unionTypeSymbol).getAbsoluteName();
    }

    @Test
    public void toString_IsEmpty_ReturnsNothingUsedGetAbsoluteName() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = spy(createUnionTypeSymbol());
        String result = unionTypeSymbol.toString();

        assertThat(result, is(PrimitiveTypeNames.NOTHING));
        verify(unionTypeSymbol).getAbsoluteName();
    }
}
