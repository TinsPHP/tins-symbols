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
        ITypeSymbol actual = createUnionTypeSymbol(intType, floatType);
        ITypeSymbol formal = numType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringOrIntOrFloatToScalar_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(stringType, intType, floatType);
        ITypeSymbol formal = scalarType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrArrayToMixed_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(intType, arrayType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_NumOrArrayToMixed_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(numType, arrayType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayOrIntOrFooToMixed_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(arrayType, intType, fooType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToInt_HasNoRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(intType, floatType);
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrArrayOrFloatToNum_HasNoRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(intType, arrayType, floatType);
        ITypeSymbol formal = numType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with empty union on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToInt_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToFoo_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = fooType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToMixed_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToInt_ReturnFalse() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with unions on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToIntOrFloat_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnionTypeSymbol(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToNumOrFoo_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnionTypeSymbol(numType, fooType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrStringOrNumOrFoo_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnionTypeSymbol(arrayType, stringType, numType, fooType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrScalar_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnionTypeSymbol(arrayType, scalarType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToFloatOrString_HasNoRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnionTypeSymbol(floatType, stringType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToArrayOrFoo_HasNoRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnionTypeSymbol(arrayType, fooType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with empty union on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToEmpty_HasNoRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayToEmpty_HasNoRelation() {
        ITypeSymbol actual = arrayType;
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_InterfaceAToEmpty_HasNoRelation() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_InterfaceAToEmpty_ReturnsTrue() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //--------------------  tests with unions on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToNumOrString_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(intType, floatType);
        ITypeSymbol formal = createUnionTypeSymbol(numType, stringType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFooToScalarOrArrayOrIA_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(intType, fooType);
        ITypeSymbol formal = createUnionTypeSymbol(scalarType, arrayType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringOrFooToNumOrArrayOrIA_HasNoRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(stringType, fooType);
        ITypeSymbol formal = createUnionTypeSymbol(numType, arrayType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  special case empty union on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToEmpty_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //--------------------  special case union with mixed only on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_MixedToMixed_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(mixedType);
        ITypeSymbol formal = createUnionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnionTypeSymbol(mixedType);
        ITypeSymbol formal = createUnionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

}
