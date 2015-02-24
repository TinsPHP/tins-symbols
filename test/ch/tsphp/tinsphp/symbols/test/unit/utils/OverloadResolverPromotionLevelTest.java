/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverloadResolverPromotionLevelTest
{
    //Warning! start code duplication - same as in ConstraintSolverTest from the inference component
    private static ITypeSymbol mixedType;
    private static ITypeSymbol arrayType;
    private static ITypeSymbol scalarType;
    private static ITypeSymbol stringType;
    private static ITypeSymbol numType;
    private static ITypeSymbol floatType;
    private static ITypeSymbol intType;
    private static ITypeSymbol nothingType;

    private static ITypeSymbol interfaceAType;
    private static ITypeSymbol interfaceSubAType;
    private static ITypeSymbol interfaceBType;
    private static ITypeSymbol fooType;
    //Warning! end code duplication - same as in ConstraintSolverTest from the inference component


    @BeforeClass
    public static void init() {
        //Warning! start code duplication - same as in ConstraintSolverTest from the inference component
        mixedType = mock(ITypeSymbol.class);

        arrayType = mock(ITypeSymbol.class);
        when(arrayType.getParentTypeSymbols()).thenReturn(set(mixedType));
        when(arrayType.getAbsoluteName()).thenReturn("array");

        scalarType = mock(ITypeSymbol.class);
        when(scalarType.getParentTypeSymbols()).thenReturn(set(mixedType));
        when(scalarType.getAbsoluteName()).thenReturn("scalar");

        stringType = mock(ITypeSymbol.class);
        when(stringType.getParentTypeSymbols()).thenReturn(set(scalarType));
        when(stringType.getAbsoluteName()).thenReturn("string");

        numType = mock(ITypeSymbol.class);
        when(numType.getParentTypeSymbols()).thenReturn(set(scalarType));
        when(numType.getAbsoluteName()).thenReturn("num");

        floatType = mock(ITypeSymbol.class);
        when(floatType.getParentTypeSymbols()).thenReturn(set(numType));
        when(floatType.getAbsoluteName()).thenReturn("float");

        intType = mock(ITypeSymbol.class);
        when(intType.getParentTypeSymbols()).thenReturn(set(numType));
        when(intType.getAbsoluteName()).thenReturn("int");

        nothingType = mock(ITypeSymbol.class);
        when(nothingType.getAbsoluteName()).thenReturn("nothing");

        interfaceAType = mock(ITypeSymbol.class);
        when(interfaceAType.getParentTypeSymbols()).thenReturn(set(mixedType));
        when(interfaceAType.getAbsoluteName()).thenReturn("IA");

        interfaceSubAType = mock(ITypeSymbol.class);
        when(interfaceSubAType.getParentTypeSymbols()).thenReturn(set(interfaceAType));
        when(interfaceSubAType.getAbsoluteName()).thenReturn("ISubA");

        interfaceBType = mock(ITypeSymbol.class);
        when(interfaceBType.getParentTypeSymbols()).thenReturn(set(mixedType));
        when(interfaceBType.getAbsoluteName()).thenReturn("IB");

        fooType = mock(ITypeSymbol.class);
        when(fooType.getParentTypeSymbols()).thenReturn(set(interfaceSubAType, interfaceBType));
        when(fooType.getAbsoluteName()).thenReturn("Foo");
        //Warning! end code duplication - same as in ConstraintSolverTest from the inference component
    }

    private static HashSet<ITypeSymbol> set(ITypeSymbol... symbols) {
        return new HashSet<>(Arrays.asList(symbols));
    }

    @Test
    public void getPromotionLevelFromTo_IntToInt_Returns0() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(intType, intType);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromTo_IntToNum_Returns1() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(intType, numType);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromTo_IntToString_ReturnsMinus1() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(intType, stringType);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromTo_IntToScalar_Returns2() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(intType, scalarType);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromTo_IntToMixed_Returns3() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(intType, mixedType);

        assertThat(result, is(3));
    }

    @Test
    public void getPromotionLevelFromTo_NumToInt_ReturnsMinus1() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(numType, intType);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromTo_FooToInterface1_Returns2() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(fooType, interfaceAType);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromTo_FooToInterface2_Returns1() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(fooType, interfaceSubAType);

        assertThat(result, is(1));
    }


    @Test
    public void getPromotionLevelFromTo_FooToMixed_Returns2() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(fooType, mixedType);

        assertThat(result, is(2));
    }


    //--------------------  tests with unions on the left

    @Test
    public void getPromotionLevelFromTo_IntAndFloatToNum_Returns1() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = numType;

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromTo_StringAndIntAndFloatToScalar_Returns2() {
        ITypeSymbol actual = createUnion(stringType, intType, floatType);
        ITypeSymbol formal = scalarType;

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromTo_IntAndArrayToMixed_Returns3() {
        ITypeSymbol actual = createUnion(intType, arrayType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(3));
    }

    @Test
    public void getPromotionLevelFromTo_NumAndArrayToMixed_Returns2() {
        ITypeSymbol actual = createUnion(numType, arrayType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromTo_ArrayAndIntAndFooToMixed_Returns3() {
        ITypeSymbol actual = createUnion(arrayType, intType, fooType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(3));
    }

    @Test
    public void getPromotionLevelFromTo_IntAndFloatToInt_ReturnsMinus1() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromTo_IntAndArrayAndFloatToNum_ReturnsMinus1() {
        ITypeSymbol actual = createUnion(intType, arrayType, floatType);
        ITypeSymbol formal = numType;

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(-1));
    }

    //--------------------  tests with unions on the right

    @Test
    public void getPromotionLevelFromTo_IntToIntAndFloat_Returns0() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(intType, floatType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(0));
    }

    @Test
    public void getPromotionLevelFromTo_IntToNumAndFoo_Returns1() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(numType, fooType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromTo_IntToArrayAndStringAndNumAndFoo_Returns1() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, stringType, numType, fooType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromTo_IntToArrayAndScalar_Returns2() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, scalarType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromTo_IntToFloatAndString_ReturnsMinus1() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(floatType, stringType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(-1));
    }

    @Test
    public void getPromotionLevelFromTo_IntToArrayAndFoo_ReturnsMinus1() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, fooType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(-1));
    }

    //--------------------  tests with unions on the left and on the right

    @Test
    public void getPromotionLevelFromTo_IntAndFloatToNumAndString_Returns1() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = createUnion(numType, stringType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(1));
    }

    @Test
    public void getPromotionLevelFromTo_IntAndFooToScalarAndArrayAndIA_Returns1() {
        ITypeSymbol actual = createUnion(intType, fooType);
        ITypeSymbol formal = createUnion(scalarType, arrayType, interfaceAType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(2));
    }

    @Test
    public void getPromotionLevelFromTo_StringAndFooToNumAndArrayAndIA_ReturnsMinus1() {
        ITypeSymbol actual = createUnion(stringType, fooType);
        ITypeSymbol formal = createUnion(numType, arrayType, interfaceAType);

        IOverloadResolver solver = createOverloadResolver();
        int result = solver.getPromotionLevelFromTo(actual, formal);

        assertThat(result, is(-1));
    }

    private IUnionTypeSymbol createUnion(ITypeSymbol... typeSymbols) {
        IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        Map<String, ITypeSymbol> map = new HashMap<>();
        for (ITypeSymbol typeSymbol : typeSymbols) {
            map.put(typeSymbol.getAbsoluteName(), typeSymbol);
        }
        when(unionTypeSymbol.getTypeSymbols()).thenReturn(map);
        return unionTypeSymbol;
    }

    protected IOverloadResolver createOverloadResolver() {
        return new OverloadResolver();
    }
}
