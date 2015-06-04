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

public class TypeHelperWithUnionAndIntersectionTypesTest extends ATypeHelperTest
{

    @Test
    public void areSameType_IntInIntersectionToIntInUnion_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType);
        ITypeSymbol formal = createUnion(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInUnionToIntInIntersection_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType);
        ITypeSymbol formal = createIntersectionType(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInIntersectionToMixedInUnion_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(intType);
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_IntInUnionToMixedInIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  special case empty union and intersection

    @Test
    public void areSameType_EmptyUnionToEmptyIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createIntersectionType();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }


    @Test
    public void areSameType_EmptyIntersectionToEmptyUnion_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createUnion();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  special case union with mixed only and intersection with mixed only

    @Test
    public void areSame_UnionWithMixedToIntersectionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_IntersectionWithMixedToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_EmptyIntersectionToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createUnion(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_UnionWithMixedToEmptyIntersection_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createIntersectionType();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }
}
