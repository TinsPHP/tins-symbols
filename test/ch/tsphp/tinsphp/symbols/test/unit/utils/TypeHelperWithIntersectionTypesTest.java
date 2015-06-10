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

public class TypeHelperWithIntersectionTypesTest extends ATypeHelperTest
{

    //--------------------  tests with intersection types on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatToNum_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType, floatType);
        ITypeSymbol formal = numType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringAndIntAndFloatToScalar_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(stringType, intType, floatType);
        ITypeSymbol formal = scalarType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndArrayToMixed_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType, arrayType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_NumAndArrayToMixed_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(numType, arrayType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayAndIntAndFooToMixed_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(arrayType, intType, fooType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatToInt_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType, floatType);
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndArrayAndFloatToNum_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType, arrayType, floatType);
        ITypeSymbol formal = numType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ScalarAndBoolToNum_HasNoRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(scalarType, boolType);
        ITypeSymbol formal = numType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooAndInterfaceBToArray_HasNoRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(fooType, interfaceBType);
        ITypeSymbol formal = arrayType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with empty intersection on the left

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToInt_HasNoRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToFoo_HasNoRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = fooType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToMixed_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToInt_ReturnTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToFoo_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = fooType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //--------------------  tests with intersection types on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooToInterfaceBAndInterfaceA_HasRelation() {
        ITypeSymbol actual = fooType;
        ITypeSymbol formal = createIntersectionTypeSymbol(interfaceBType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooToInterfaceBAndInterfaceSubA_HasRelation() {
        ITypeSymbol actual = fooType;
        ITypeSymbol formal = createIntersectionTypeSymbol(interfaceBType, interfaceSubAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToIntAndFloat_HasNoRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionTypeSymbol(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToNumAndFoo_HasNoRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionTypeSymbol(numType, fooType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with empty intersection on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToEmpty_HasRelation() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ArrayToEmpty_HasRelation() {
        ITypeSymbol actual = arrayType;
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_InterfaceAToEmpty_HasRelation() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_InterfaceAToEmpty_ReturnsFalse() {
        ITypeSymbol actual = interfaceAType;
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with intersection types on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatAndBoolToIntAndFloat_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType, floatType, boolType);
        ITypeSymbol formal = createIntersectionTypeSymbol(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatAndStringToNumAndString_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType, floatType, stringType);
        ITypeSymbol formal = createIntersectionTypeSymbol(numType, stringType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ScalarAndArrayAndIAToIntAndFoo_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(scalarType, arrayType, interfaceAType);
        ITypeSymbol formal = createIntersectionTypeSymbol(intType, fooType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFloatToNumAndString_HasNoRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType, floatType);
        ITypeSymbol formal = createIntersectionTypeSymbol(numType, stringType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndFooToScalarAndArrayAndIA_HasNoRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType, fooType);
        ITypeSymbol formal = createIntersectionTypeSymbol(scalarType, arrayType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  special case empty intersection on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyToEmpty_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //--------------------  special case intersection with mixed only on the left and on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_MixedToMixed_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(mixedType);
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol(mixedType);
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

}
