/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.utils.ERelation;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.TypeHelperDto;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TypeHelperWithUnionTypesTest extends ATypeHelperTest
{

    //--------------------  tests with unions on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToNum_HasRelation() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = numType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringOrIntOrFloatToScalar_HasRelation() {
        ITypeSymbol actual = createUnion(stringType, intType, floatType);
        ITypeSymbol formal = scalarType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrArrayToMixed_HasRelation() {
        ITypeSymbol actual = createUnion(intType, arrayType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_NumOrArrayToMixed_HasRelation() {
        ITypeSymbol actual = createUnion(numType, arrayType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayOrIntOrFooToMixed_HasRelation() {
        ITypeSymbol actual = createUnion(arrayType, intType, fooType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToInt_HasNoRelation() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrArrayOrFloatToNum_HasNoRelation() {
        ITypeSymbol actual = createUnion(intType, arrayType, floatType);
        ITypeSymbol formal = numType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with empty union on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToInt_HasRelation() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToFoo_HasRelation() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = fooType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToMixed_HasRelation() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToInt_ReturnFalse() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with unions on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToIntOrFloat_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToNumOrFoo_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(numType, fooType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrStringOrNumOrFoo_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, stringType, numType, fooType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrScalar_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, scalarType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToFloatOrString_HasNoRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(floatType, stringType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrFoo_HasNoRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion(arrayType, fooType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with empty union on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToEmpty_HasNoRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayToEmpty_HasNoRelation() {
        ITypeSymbol actual = arrayType;
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_InterfaceAToEmpty_HasNoRelation() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_InterfaceAToEmpty_ReturnsTrue() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //--------------------  tests with unions on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToNumOrString_HasRelation() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = createUnion(numType, stringType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFooToScalarOrArrayOrIA_HasRelation() {
        ITypeSymbol actual = createUnion(intType, fooType);
        ITypeSymbol formal = createUnion(scalarType, arrayType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringOrFooToNumOrArrayOrIA_HasNoRelation() {
        ITypeSymbol actual = createUnion(stringType, fooType);
        ITypeSymbol formal = createUnion(numType, arrayType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  special case empty union on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToEmpty_HasRelation() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //--------------------  special case union with mixed only on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_MixedToMixed_HasRelation() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

}
