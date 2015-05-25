/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypeHelperWithUnionAndIntersectionTypesTest extends ATypeHelperTest
{

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooOrBarToInterfaceBAndInterfaceA_ReturnsTrue() {
        ITypeSymbol barType = mock(ITypeSymbol.class);
        when(barType.getParentTypeSymbols()).thenReturn(set(interfaceAType, interfaceBType));
        when(barType.getAbsoluteName()).thenReturn("Bar");

        ITypeSymbol actual = createUnion(fooType, barType);
        ITypeSymbol formal = createIntersectionType(interfaceBType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToIntAndFloat_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = createIntersectionType(intType, floatType);

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }


    //--------------------  special case empty union and intersection

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyUnionToEmptyIntersection_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createIntersectionType();

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyUnionToEmptyIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createIntersectionType();

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyIntersectionToEmptyUnion_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyIntersectionToEmptyUnion_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }


    //--------------------  special case union with mixed only and intersection with mixed only

    @Test
    public void isFirstSameOrSubTypeOfSecond_UnionWithMixedToIntersectionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_UnionWithMixedToIntersectionMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntersectionWithMixedToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntersectionMixedToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndSetMixed();
        boolean result = typeHelper.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }
}
