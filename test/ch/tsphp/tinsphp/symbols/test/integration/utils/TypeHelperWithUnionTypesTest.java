/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TypeHelperWithUnionTypesTest extends ATypeHelperTest
{

    //--------------------  tests with unions on the left

    @Test
    public void areSameType_IntInUnionToInt_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType);
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInUnionToMixed_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_MixedInUnionToInt_ReturnsFalse() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty union on the left

    @Test
    public void areSameType_EmptyUnionToMixed_ReturnsFalse() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with unions on the right

    @Test
    public void areSameType_IntToIntInUnion_ReturnsTrue() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_MixedToIntInUnion_ReturnsFalse() {
        ITypeSymbol actual = mixedType;
        ITypeSymbol formal = createUnion(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_IntToMixedInUnion_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty union on the right

    @Test
    public void areSameType_MixedToEmptyUnion_ReturnsFalse() {
        ITypeSymbol actual = mixedType;
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with unions on the left and on the right


    @Test
    public void areSameType_IntToInt_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType);
        ITypeSymbol formal = createUnion(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntAndFloatToIntAndFloat_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = createUnion(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntAndFloatToFloatAndInt_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = createUnion(floatType, intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }


    @Test
    public void areSameType_IntAndBoolToIntAndFloat_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType, boolType);
        ITypeSymbol formal = createUnion(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_IntAndFloatToMixedInUnion_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_MixedInUnionToIntAndFloat_ReturnsFalse() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createUnion(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  special case empty union on the left and on the right

    @Test
    public void areSameType_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    //--------------------  special case union with mixed only on the left and on the right

    @Test
    public void areSameType_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }
}
