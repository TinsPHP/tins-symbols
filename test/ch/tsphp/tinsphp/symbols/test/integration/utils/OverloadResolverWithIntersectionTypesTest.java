/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.AOverloadResolverTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OverloadResolverWithIntersectionTypesTest extends AOverloadResolverTest
{

    //--------------------  tests with intersection types on the left

    @Test
    public void areSameType_IntInIntersectionToInt_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType);
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInIntersectionToMixed_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(intType);
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_MixedInIntersectionToInt_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty intersection on the left

    @Test
    public void areSameType_EmptyIntersectionToInt_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = intType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_EmptyIntersectionToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = mixedType;

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(true));
    }

    //--------------------  tests with intersection types on the right

    @Test
    public void areSameType_IntToIntInIntersection_ReturnsTrue() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionType(intType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_MixedToIntInIntersection_ReturnsFalse() {
        ITypeSymbol actual = mixedType;
        ITypeSymbol formal = createIntersectionType(intType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_IntToMixedInIntersection_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionType(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty intersection on the right

    @Test
    public void areSameType_IntToEmptyIntersection_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_MixedToEmptyIntersection_ReturnsTrue() {
        ITypeSymbol actual = mixedType;
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(true));
    }

    //--------------------  tests with intersection types on the left and on the right

    @Test
    public void areSameType_IntToInt_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType);
        ITypeSymbol formal = createIntersectionType(intType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IAAndIBToIAAndIB_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(interfaceAType, interfaceBType);
        ITypeSymbol formal = createIntersectionType(interfaceAType, interfaceBType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IAndIBToIBAndIA_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(interfaceAType, interfaceBType);
        ITypeSymbol formal = createIntersectionType(interfaceBType, interfaceAType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_ISubAndIBToIBAndIA_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(interfaceSubAType, interfaceBType);
        ITypeSymbol formal = createIntersectionType(interfaceBType, interfaceAType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }


    @Test
    public void areSameType_IAndIBToMixedInIntersection_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(interfaceAType, interfaceBType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_MixedInIntersectionToIAAndIB_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createIntersectionType(interfaceBType, interfaceAType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  special case empty intersection on the left and on the right

    @Test
    public void areSameType_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(true));
    }


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


    protected OverloadResolver createOverloadResolver() {
        return new OverloadResolver();
    }
}
