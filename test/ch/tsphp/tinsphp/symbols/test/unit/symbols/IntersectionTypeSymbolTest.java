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

public class IntersectionTypeSymbolTest extends ATypeTest
{

    @Test
    public void addTypeSymbol_EmptyAddInt_ReturnsTrueAndIntersectionContainsInt() {
        //no arrange necessary

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        boolean result = intersectionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int"));
    }

    @Test
    public void addTypeSymbol_IntAddFloat_ReturnsTrueAndIntersectionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(floatType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAddAnotherInt_ReturnsFalseAndIntersectionContainsOneIntOnly() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("int"));
    }

    @Test
    public void addTypeSymbol_IntAddNum_ReturnsFalseAndIntersectionContainsIntOnly() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(numType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("int"));
    }

    @Test
    public void addTypeSymbol_InterfaceAAndInterfaceBAddFoo_ReturnsTrueAndIntersectionContainsFooOnly() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(fooType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("Foo"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatAndIAAndIBAddFoo_ReturnsTrueAndIntersectionContainsIntAndFloatAndFoo() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);
        intersectionTypeSymbol.addTypeSymbol(floatType);
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(fooType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float", "Foo"));
    }

    @Test
    public void addTypeSymbol_EmptyAndIsUnionContainingIntAndFloat_ReturnsTrueAndIntersectionContainsUnion() {
        IUnionTypeSymbol unionTypeSymbol = new UnionTypeSymbol(new OverloadResolver());
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //act
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        boolean result = intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("(float | int)"));
    }

    @Test
    public void addTypeSymbol_IntAndIsUnionContainingIntAndBool_ReturnsFalseAndIntersectionContainsIntOnly() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);
        IUnionTypeSymbol unionTypeSymbol = new UnionTypeSymbol(new OverloadResolver());
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(boolType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("int"));
    }

    //--------- merging intersection types tests

    @Test
    public void addTypeSymbol_EmptyAndIsIntersectionContainingIntAndFloat_ReturnsTrueAndIntersectionContainsUnion() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(intType);
        intersectionTypeSymbol2.addTypeSymbol(floatType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntMergeWithIntAndFloat_ReturnsTrueAndIntersectionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(intType);

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(intType);
        intersectionTypeSymbol2.addTypeSymbol(floatType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatMergeWithFloatAndInt_ReturnsFalseAndIntersectionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(intType);
        intersectionTypeSymbol.addTypeSymbol(floatType);

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(floatType);
        intersectionTypeSymbol2.addTypeSymbol(intType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_NumAndStringMergeWithFloat_ReturnsTrueAndIntersectionContainsFloatAndString() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(numType);
        intersectionTypeSymbol.addTypeSymbol(stringType);

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(floatType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("float", "string"));
    }

    @Test
    public void addTypeSymbol_IBAndIAAndBoolMergeWithFooAndInt_ReturnsTrueAndIntersectionContainsFooAndBoolAndInt() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);
        intersectionTypeSymbol.addTypeSymbol(boolType);

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(fooType);
        intersectionTypeSymbol2.addTypeSymbol(intType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("Foo", "bool", "int"));
    }

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

    private IIntersectionTypeSymbol createIntersectionTypeSymbol() {
        return createIntersectionTypeSymbol(new OverloadResolver());
    }

    protected IIntersectionTypeSymbol createIntersectionTypeSymbol(IOverloadResolver overloadResolver) {
        return new IntersectionTypeSymbol(overloadResolver);
    }

}

