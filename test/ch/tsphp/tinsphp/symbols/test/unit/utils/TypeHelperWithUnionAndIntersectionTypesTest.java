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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypeHelperWithUnionAndIntersectionTypesTest extends ATypeHelperTest
{

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooOrBarToInterfaceBAndInterfaceA_HasRelation() {
        ITypeSymbol barType = mock(ITypeSymbol.class);
        when(barType.getParentTypeSymbols()).thenReturn(set(interfaceAType, interfaceBType));
        when(barType.getAbsoluteName()).thenReturn("Bar");

        ITypeSymbol actual = createUnionTypeSymbol(fooType, barType);
        ITypeSymbol formal = createIntersectionTypeSymbol(interfaceBType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToIntAndFloat_HasNoRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(intType, floatType);
        ITypeSymbol formal = createIntersectionTypeSymbol(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }


    //--------------------  special case empty union and intersection

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyUnionToEmptyIntersection_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyUnionToEmptyIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyIntersectionToEmptyUnion_HasNoRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyIntersectionToEmptyUnion_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }


    //--------------------  special case union with mixed only and intersection with mixed only

    @Test
    public void isFirstSameOrSubTypeOfSecond_UnionWithMixedToIntersectionWithMixed_HasRelation() {
        ITypeSymbol actual = createUnionTypeSymbol(mixedType);
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_UnionWithMixedToIntersectionMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnionTypeSymbol(mixedType);
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntersectionWithMixedToUnionWithMixed_HasRelation() {
        ITypeSymbol actual = createIntersectionTypeSymbol(mixedType);
        ITypeSymbol formal = createUnionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntersectionMixedToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol(mixedType);
        ITypeSymbol formal = createUnionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        TypeHelperDto result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }
}
