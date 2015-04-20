/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OverloadResolverTest extends ATypeTest
{

    @Test
    public void areSameType_AreSame_ReturnsTrue() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.areSame(intType, intType);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_SecondIsParentType_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.areSame(intType, numType);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_SecondIsSubtype_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.areSame(numType, intType);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstSubTypeOfSecond_IntAndInt_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(intType, intType);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndInt_ReturnsTrue() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(intType, intType);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntAndInt_ReturnsTrue() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(intType, intType);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_IntAndInt_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(intType, intType);
//
//        assertThat(result, is(false));
//    }

    //----------------------------

//    @Test
//    public void isFirstSubTypeOfSecond_IntAndNum_ReturnsTrue() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(intType, numType);
//
//        assertThat(result, is(true));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndNum_ReturnsTrue() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(intType, numType);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntAndNum_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(intType, numType);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_IntAndNum_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(intType, numType);
//
//        assertThat(result, is(false));
//    }

    //----------------------------

//    @Test
//    public void isFirstSubTypeOfSecond_IntToString_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(intType, stringType);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToString_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(intType, stringType);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntToString_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(intType, stringType);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_IntToString_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(intType, stringType);
//
//        assertThat(result, is(false));
//    }

    //----------------------------

//    @Test
//    public void isFirstSubTypeOfSecond_IntToScalar_ReturnsTrue() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(intType, scalarType);
//
//        assertThat(result, is(true));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToScalar_ReturnsTrue() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(intType, scalarType);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntToScalar_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(intType, scalarType);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_IntToScalar_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(intType, scalarType);
//
//        assertThat(result, is(false));
//    }

    //----------------------------

//    @Test
//    public void isFirstSubTypeOfSecond_IntToMixed_ReturnsTrue() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(intType, mixedType);
//
//        assertThat(result, is(true));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToMixed_ReturnsTrue() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(intType, mixedType);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntToMixed_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(intType, mixedType);

        assertThat(result, is(false));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_IntToMixed_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(intType, mixedType);
//
//        assertThat(result, is(false));
//    }

    //----------------------------


//    @Test
//    public void isFirstSubTypeOfSecond_InterfaceAToFoo_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(interfaceAType, fooType);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_InterfaceAToFoo_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(interfaceAType, fooType);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_InterfaceAToFoo_ReturnsTrue() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(interfaceAType, fooType);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_InterfaceAToFoo_ReturnsTrue() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(interfaceAType, fooType);
//
//        assertThat(result, is(true));
//    }

    //----------------------------


//    @Test
//    public void isFirstSubTypeOfSecond_MixedToNum_ReturnsFalse() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstSubTypeOfSecond(mixedType, numType);
//
//        assertThat(result, is(false));
//    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_MixedToNum_ReturnsFalse() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(mixedType, numType);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_MixedToNum_ReturnsTrue() {
        //no arrange necessary

        IOverloadResolver solver = createOverloadResolver();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(mixedType, numType);

        assertThat(result, is(true));
    }

//    @Test
//    public void isFirstParentTypeOfSecond_MixedToNum_ReturnsTrue() {
//        //no arrange necessary
//
//        IOverloadResolver solver = createOverloadResolverAndSetMixed();
//        boolean result = solver.isFirstParentTypeOfSecond(mixedType, numType);
//
//        assertThat(result, is(true));
//    }

    //----------------------------

    protected IOverloadResolver createOverloadResolver() {
        return new OverloadResolver();
    }
}
