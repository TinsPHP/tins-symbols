/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class UnionTypeSymbolTest extends ATypeTest
{

    @Test
    public void addTypeSymbol_EmptyAddInt_ReturnsTrueAndUnionContainsInt() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int"));
    }

    @Test
    public void addTypeSymbol_IntAddFloat_ReturnsTrueAndUnionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(floatType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAddAnotherInt_ReturnsFalseAndUnionContainsOneIntOnly() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("int"));
    }

    @Test
    public void addTypeSymbol_NumAddInt_ReturnsFalseAndUnionContainsNumOnly() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(numType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("num"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatAddNum_ReturnsTrueAndUnionContainsNumOnly() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(numType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("num"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatAndStringAndBoolAddNum_ReturnsTrueAndUnionContainsNumAndStringAndBool() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);
        unionTypeSymbol.addTypeSymbol(stringType);
        unionTypeSymbol.addTypeSymbol(boolType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(numType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("num", "string", "bool"));
    }


    @Test
    public void addTypeSymbol_EmptyAndIsUnionContainingIntAndFloat_ReturnsTrueAndUnionContainsUnion() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol2.addTypeSymbol(intType);
        unionTypeSymbol2.addTypeSymbol(floatType);

        //act
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("(int | float)"));
    }

//    @Test
//    public void merge_EmptyMergeWithIntAndFloat_ReturnsTrueAndUnionContainsIntAndFloat() {
//        Map<String, ITypeSymbol> map2 = createMapWithTypes(intType, floatType);
//        IUnionTypeSymbol unionTypeSymbol2 = createIntersectionTypeSymbol(map2);
//
//        IUnionTypeSymbol unionTypeSymbol = createIntersectionTypeSymbol();
//        boolean result = unionTypeSymbol.merge(unionTypeSymbol2);
//        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();
//
//        assertThat(result, is(true));
//        assertThat(symbols.keySet(), hasItems("int", "float"));
//    }
//
//    @Test
//    public void merge_IntAndFloatMergeWithFloatAndInt_ReturnsFalseAndUnionContainsIntAndFloat() {
//        Map<String, ITypeSymbol> map = createMapWithTypes(intType, floatType);
//        Map<String, ITypeSymbol> map2 = createMapWithTypes(intType, floatType);
//        IUnionTypeSymbol unionTypeSymbol2 = createIntersectionTypeSymbol(map2);
//
//        IUnionTypeSymbol unionTypeSymbol = createIntersectionTypeSymbol(map);
//        boolean result = unionTypeSymbol.merge(unionTypeSymbol2);
//        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();
//
//        assertThat(result, is(false));
//        assertThat(symbols.keySet(), hasItems("int", "float"));
//    }
//
//    @Test
//    public void merge_NumAndStringMergeWithFloatAndInt_ReturnsFalseAndUnionContainsNumAndString() {
//        Map<String, ITypeSymbol> map = createMapWithTypes(numType, stringType);
//        Map<String, ITypeSymbol> map2 = createMapWithTypes(intType, floatType);
//        IUnionTypeSymbol unionTypeSymbol2 = createIntersectionTypeSymbol(map2);
//
//        IUnionTypeSymbol unionTypeSymbol = createIntersectionTypeSymbol(map);
//        boolean result = unionTypeSymbol.merge(unionTypeSymbol2);
//        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();
//
//        assertThat(result, is(false));
//        assertThat(symbols.keySet(), hasItems("num", "string"));
//    }
//
//    @Test
//    public void merge_BoolAndIntAndFloatAndFooMergeWithNumAndIA_ReturnsTrueAndUnionContainsBoolAndNumAndIA() {
//        Map<String, ITypeSymbol> map = createMapWithTypes(boolType, intType, floatType);
//        Map<String, ITypeSymbol> map2 = createMapWithTypes(numType, interfaceAType);
//        IUnionTypeSymbol unionTypeSymbol2 = createIntersectionTypeSymbol(map2);
//
//        IUnionTypeSymbol unionTypeSymbol = createIntersectionTypeSymbol(map);
//        boolean result = unionTypeSymbol.merge(unionTypeSymbol2);
//        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();
//
//        assertThat(result, is(true));
//        assertThat(symbols.keySet(), hasItems("num", "IA"));
//    }

    @Test
    public void getName_IsEmpty_ReturnsNothing() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        String result = unionTypeSymbol.getName();

        assertThat(result, is("nothing"));
    }

    @Test
    public void getName_OneTypeOnly_ReturnsTypeWithoutParenthesis() {
        //pre-act
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        //act
        String result = unionTypeSymbol.getName();

        //assert
        assertThat(result, is("int"));
    }

    @Test
    public void getName_IntAndFloat_ReturnsEmptyParenthesis() {
        //pre-act
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //act
        String result = unionTypeSymbol.getName();

        //assert
        assertThat(result, anyOf(is("(int | float)"), is("(float | int)")));
    }

    @Test
    public void getAbsoluteName_IsEmpty_ReturnsNothingUsedGetName() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = spy(createUnionTypeSymbol());
        String result = unionTypeSymbol.getAbsoluteName();

        assertThat(result, is("nothing"));
        verify(unionTypeSymbol).getName();
    }

    @Test
    public void toString_IsEmpty_ReturnsNothingUsedGetAbsoluteName() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = spy(createUnionTypeSymbol());
        String result = unionTypeSymbol.toString();

        assertThat(result, is("nothing"));
        verify(unionTypeSymbol).getAbsoluteName();
    }

    private IUnionTypeSymbol createUnionTypeSymbol() {
        return createUnionTypeSymbol(new OverloadResolver());
    }

    protected IUnionTypeSymbol createUnionTypeSymbol(IOverloadResolver overloadResolver) {
        return new UnionTypeSymbol(overloadResolver);
    }

}
