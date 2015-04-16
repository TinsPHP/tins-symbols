/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverloadResolverWithUnionTypesTest extends ATypeTest
{

    //--------------------  tests with unions on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToNum_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = numType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringOrIntOrFloatToScalar_ReturnsTrue() {
        ITypeSymbol actual = createUnion(stringType, intType, floatType);
        ITypeSymbol formal = scalarType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrArrayToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType, arrayType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_NumOrArrayToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(numType, arrayType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayOrIntOrFooToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(arrayType, intType, fooType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToInt_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrArrayOrFloatToNum_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType, arrayType, floatType);
        ITypeSymbol formal = numType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty union on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToInt_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToFoo_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = fooType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstSubTypeOfSecond_EmptyToInt_ReturnTrue() {
//        ITypeSymbol actual = createUnion();
//        ITypeSymbol formal = intType;
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(true));
//    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToInt_ReturnFalse() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_EmptyToInt_ReturnFalse() {
//        ITypeSymbol actual = createUnion();
//        ITypeSymbol formal = intType;
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    //--------------------  tests with unions on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToIntOrFloat_ReturnsTrue() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(intType, floatType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToNumOrFoo_ReturnsTrue() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(numType, fooType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrStringOrNumOrFoo_ReturnsTrue() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, stringType, numType, fooType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrScalar_ReturnsTrue() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, scalarType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToFloatOrString_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(floatType, stringType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrFoo_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, fooType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty union on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToEmpty_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayToEmpty_ReturnsFalse() {
        ITypeSymbol actual = arrayType;
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_InterfaceAToEmpty_ReturnsFalse() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstSubTypeOfSecond_InterfaceAToEmpty_ReturnsFalse() {
//        ITypeSymbol actual = interfaceAType;
//        ITypeSymbol formal = createUnion();
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_InterfaceAToEmpty_ReturnsTrue() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_InterfaceAToEmpty_ReturnsTrue() {
//        ITypeSymbol actual = interfaceAType;
//        ITypeSymbol formal = createUnion();
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(true));
//    }

    //--------------------  tests with unions on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToNumOrString_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = createUnion(numType, stringType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFooToScalarOrArrayOrIA_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType, fooType);
        ITypeSymbol formal = createUnion(scalarType, arrayType, interfaceAType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringOrFooToNumOrArrayOrIA_ReturnsFalse() {
        ITypeSymbol actual = createUnion(stringType, fooType);
        ITypeSymbol formal = createUnion(numType, arrayType, interfaceAType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstSubTypeOfSecond_IntOrFloatToIntOrFloat_ReturnsFalse() {
//        ITypeSymbol actual = createUnion(intType, floatType);
//        ITypeSymbol formal = createUnion(intType, floatType);
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }
//
//    @Test
//    public void isFirstParentTypeOfSecond_IntOrFloatToIntOrFloat_ReturnsFalse() {
//        ITypeSymbol actual = createUnion(intType, floatType);
//        ITypeSymbol formal = createUnion(intType, floatType);
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    //--------------------  special case empty union on the left and on the right

//    @Test
//    public void isFirstSubTypeOfSecond_EmptyToEmpty_ReturnsFalse() {
//        ITypeSymbol actual = createUnion();
//        ITypeSymbol formal = createUnion();
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_EmptyToEmpty_ReturnsFalse() {
//        ITypeSymbol actual = createUnion();
//        ITypeSymbol formal = createUnion();
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    //--------------------  special case union with mixed only on the left and on the right


//    @Test
//    public void isFirstSubTypeOfSecond_MixedToMixed_ReturnsFalse() {
//        ITypeSymbol actual = createUnion(mixedType);
//        ITypeSymbol formal = createUnion(mixedType);
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_MixedToMixed_ReturnsFalse() {
//        ITypeSymbol actual = createUnion(mixedType);
//        ITypeSymbol formal = createUnion(mixedType);
//
//        IOverloadResolver solver = createOverloadResolverOrSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

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
