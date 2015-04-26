/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverloadResolverWithIntersectionTypesTest extends ATypeTest
{

    //--------------------  tests with intersection types on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatToNum_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType, floatType);
        ITypeSymbol formal = numType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringAndIntAndFloatToScalar_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(stringType, intType, floatType);
        ITypeSymbol formal = scalarType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndArrayToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType, arrayType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_NumAndArrayToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(numType, arrayType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayAndIntAndFooToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(arrayType, intType, fooType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatToInt_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType, floatType);
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndArrayAndFloatToNum_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType, arrayType, floatType);
        ITypeSymbol formal = numType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ScalarAndBoolToNum_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(scalarType, boolType);
        ITypeSymbol formal = numType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooAndInterfaceBToArray_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(fooType, interfaceBType);
        ITypeSymbol formal = arrayType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty intersection on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToInt_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToFoo_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = fooType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstSubTypeOfSecond_EmptyToInt_ReturnTrue() {
//        ITypeSymbol actual = createIntersectionType();
//        ITypeSymbol formal = intType;
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(true));
//    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToInt_ReturnTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToFoo_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = fooType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_EmptyToInt_ReturnFalse() {
//        ITypeSymbol actual = createIntersectionType();
//        ITypeSymbol formal = intType;
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    //--------------------  tests with intersection types on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooToInterfaceBAndInterfaceA_ReturnsTrue() {
        ITypeSymbol actual = fooType;
        ITypeSymbol formal = createIntersectionType(interfaceBType, interfaceAType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooToInterfaceBAndInterfaceSubA_ReturnsTrue() {
        ITypeSymbol actual = fooType;
        ITypeSymbol formal = createIntersectionType(interfaceBType, interfaceSubAType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToIntAndFloat_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionType(intType, floatType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToNumAndFoo_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionType(numType, fooType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty intersection on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToEmpty_ReturnsTrue() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayToEmpty_ReturnsTrue() {
        ITypeSymbol actual = arrayType;
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_InterfaceAToEmpty_ReturnsTrue() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstSubTypeOfSecond_InterfaceAToEmpty_ReturnsFalse() {
//        ITypeSymbol actual = interfaceAType;
//        ITypeSymbol formal = createIntersectionType();
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_InterfaceAToEmpty_ReturnsFalse() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_InterfaceAToEmpty_ReturnsTrue() {
//        ITypeSymbol actual = interfaceAType;
//        ITypeSymbol formal = createIntersectionType();
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(true));
//    }

    //--------------------  tests with intersection types on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatAndBoolToIntAndFloat_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType, floatType, boolType);
        ITypeSymbol formal = createIntersectionType(intType, floatType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatAndStringToNumAndString_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType, floatType, stringType);
        ITypeSymbol formal = createIntersectionType(numType, stringType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ScalarAndArrayAndIAToIntAndFoo_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(scalarType, arrayType, interfaceAType);
        ITypeSymbol formal = createIntersectionType(intType, fooType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatToNumAndString_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(intType, floatType);
        ITypeSymbol formal = createIntersectionType(numType, stringType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFooToScalarAndArrayAndIA_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(intType, fooType);
        ITypeSymbol formal = createIntersectionType(scalarType, arrayType, interfaceAType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstSubTypeOfSecond_IntAndFloatToIntAndFloat_ReturnsFalse() {
//        ITypeSymbol actual = createIntersectionType(intType, floatType);
//        ITypeSymbol formal = createIntersectionType(intType, floatType);
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }
//
//    @Test
//    public void isFirstParentTypeOfSecond_IntAndFloatToIntAndFloat_ReturnsFalse() {
//        ITypeSymbol actual = createIntersectionType(intType, floatType);
//        ITypeSymbol formal = createIntersectionType(intType, floatType);
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    //--------------------  special case empty intersection on the left and on the right

//    @Test
//    public void isFirstSubTypeOfSecond_EmptyToEmpty_ReturnsFalse() {
//        ITypeSymbol actual = createIntersectionType();
//        ITypeSymbol formal = createIntersectionType();
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_EmptyToEmpty_ReturnsFalse() {
//        ITypeSymbol actual = createIntersectionType();
//        ITypeSymbol formal = createIntersectionType();
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }


    //--------------------  special case intersection with mixed only on the left and on the right

//    @Test
//    public void isFirstSubTypeOfSecond_MixedToMixed_ReturnsFalse() {
//         ITypeSymbol actual = createIntersectionType(mixedType);
//        ITypeSymbol formal = createIntersectionType(mixedType);
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_MixedToMixed_ReturnsFalse() {
//        ITypeSymbol actual = createIntersectionType(mixedType);
//        ITypeSymbol formal = createIntersectionType(mixedType);
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(actual, formal);
//
//        assertThat(result, is(false));
//    }

    private IIntersectionTypeSymbol createIntersectionType(ITypeSymbol... typeSymbols) {
        IIntersectionTypeSymbol intersectionTypeSymbol = mock(IIntersectionTypeSymbol.class);
        SortedMap<String, ITypeSymbol> map = new TreeMap<>();
        for (ITypeSymbol typeSymbol : typeSymbols) {
            map.put(typeSymbol.getAbsoluteName(), typeSymbol);
        }
        when(intersectionTypeSymbol.getTypeSymbols()).thenReturn(map);
        return intersectionTypeSymbol;
    }

    private IOverloadResolver createOverloadResolverAndSetMixed() {
        OverloadResolver overloadResolver = createOverloadResolver();
        overloadResolver.setMixedTypeSymbol(mixedType);
        return overloadResolver;
    }

    protected OverloadResolver createOverloadResolver() {
        return new OverloadResolver();
    }
}
