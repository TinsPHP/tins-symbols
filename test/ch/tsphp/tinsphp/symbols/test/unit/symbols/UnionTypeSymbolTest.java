/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.Map;

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
    public void addTypeSymbol_EmptyAndIntersectionContainingIBAndIA_ReturnsTrueAndUnionContainsIntersection() {
        IIntersectionTypeSymbol intersectionTypeSymbol = new IntersectionTypeSymbol(new OverloadResolver());
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);

        //act
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("(IA & IB)"));
    }

    @Test
    public void addTypeSymbol_IntAndIntersectionContainingIBAndIA_ReturnsTrueAndUnionContainsIntAndIntersection() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        IIntersectionTypeSymbol intersectionTypeSymbol = new IntersectionTypeSymbol(new OverloadResolver());
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("(IA & IB)", "int"));
    }

    @Test
    public void addTypeSymbol_IntAndIAIntersectionContainingIBAndIA_ReturnsFalseAndUnionContainsIntAndIA() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(interfaceAType);

        IIntersectionTypeSymbol intersectionTypeSymbol = new IntersectionTypeSymbol(new OverloadResolver());
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("IA", "int"));
    }

    //see TINS-406 add single type in intersection to union
    @Test
    public void addTypeSymbol_EmptyAndIntersectionContainingFloat_ReturnsTrueAndUnionContainsIntAndFloatAsSingleTypes
    () {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        IIntersectionTypeSymbol intersectionTypeSymbol = new IntersectionTypeSymbol(new OverloadResolver());
        intersectionTypeSymbol.addTypeSymbol(floatType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("float", "int"));
        assertThat(symbols.get("float"), is(floatType));
        assertThat(symbols.get("int"), is(intType));
    }

    //see TINS-406 add single type in intersection to union
    @Test
    public void addTypeSymbol_IntAndIntersectionContainingFloat_ReturnsTrueAndUnionContainsFloatAsSingleType() {
        IIntersectionTypeSymbol intersectionTypeSymbol = new IntersectionTypeSymbol(new OverloadResolver());
        intersectionTypeSymbol.addTypeSymbol(floatType);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("float"));
        assertThat(symbols.get("float"), is(floatType));
    }

    //--------- merging unions test

    @Test
    public void addTypeSymbol_EmptyAndIsUnionContainingIntAndFloat_ReturnsTrueAndUnionContainsUnion() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(intType);
        unionTypeSymbol2.addTypeSymbol(floatType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAndMergeWithIntAndFloat_ReturnsTrueAndUnionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(intType);

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(intType);
        unionTypeSymbol2.addTypeSymbol(floatType);

        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatMergeWithFloatAndInt_ReturnsFalseAndUnionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(floatType);
        unionTypeSymbol2.addTypeSymbol(intType);

        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_NumAndStringMergeWithFloatAndInt_ReturnsFalseAndUnionContainsNumAndString() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(numType);
        unionTypeSymbol.addTypeSymbol(stringType);

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(intType);
        unionTypeSymbol2.addTypeSymbol(floatType);

        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("num", "string"));
    }

    @Test
    public void addTypeSymbol_BoolAndIntAndFloatAndFooMergeWithNumAndIA_ReturnsTrueAndUnionContainsBoolAndNumAndIA() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(boolType);
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(numType);
        unionTypeSymbol2.addTypeSymbol(interfaceAType);

        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("num", "IA"));
    }

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

    private IUnionTypeSymbol createUnionTypeSymbol() {
        return createUnionTypeSymbol(new OverloadResolver());
    }

    protected IUnionTypeSymbol createUnionTypeSymbol(IOverloadResolver overloadResolver) {
        return new UnionTypeSymbol(overloadResolver);
    }

}
