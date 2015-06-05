/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.tinsphp.common.utils.ERelation;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.TypeHelperDto;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TypeHelperTest extends ATypeHelperTest
{

    @Test
    public void areSameType_AreSame_ReturnsTrue() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(intType, intType);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_SecondIsParentType_ReturnsFalse() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(intType, numType);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_SecondIsSubtype_ReturnsFalse() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(numType, intType);

        assertThat(result, is(false));
    }

    //----------------------------

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndInt_HasRelation() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(intType, intType);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntAndInt_ReturnsTrue() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(intType, intType);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //----------------------------

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntAndNum_HasRelation() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(intType, numType);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntAndNum_ReturnsFalse() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(intType, numType);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }


    //----------------------------

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToString_HasNoRelation() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(intType, stringType);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntToString_ReturnsFalse() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(intType, stringType);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //----------------------------

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToScalar_HasRelation() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(intType, scalarType);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntToScalar_ReturnsFalse() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(intType, scalarType);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }


    //----------------------------

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToMixed_HasRelation() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(intType, mixedType);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntToMixed_ReturnsFalse() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(intType, mixedType);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //----------------------------

    @Test
    public void isFirstSameOrSubTypeOfSecond_InterfaceAToFoo_HasNoRelation() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(interfaceAType, fooType);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_InterfaceAToFoo_ReturnsTrue() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(interfaceAType, fooType);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //----------------------------

    @Test
    public void isFirstSameOrSubTypeOfSecond_MixedToNum_HasNoRelation() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(mixedType, numType);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_MixedToNum_HasRelation() {
        //no arrange necessary

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(mixedType, numType);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    //----------------------------

}
