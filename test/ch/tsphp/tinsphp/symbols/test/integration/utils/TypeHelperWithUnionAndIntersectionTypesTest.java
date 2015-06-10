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
        ITypeSymbol actual = createIntersectionTypeSymbol(intType);
        ITypeSymbol formal = createUnionTypeSymbol(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInUnionToIntInIntersection_ReturnsTrue() {
        ITypeSymbol actual = createUnionTypeSymbol(intType);
        ITypeSymbol formal = createIntersectionTypeSymbol(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInIntersectionToMixedInUnion_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType);
        ITypeSymbol formal = createUnionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_IntInUnionToMixedInIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnionTypeSymbol(intType);
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  special case empty union and intersection

    @Test
    public void areSameType_EmptyUnionToEmptyIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnionTypeSymbol();
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }


    @Test
    public void areSameType_EmptyIntersectionToEmptyUnion_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = createUnionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  special case union with mixed only and intersection with mixed only

    @Test
    public void areSame_UnionWithMixedToIntersectionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnionTypeSymbol(mixedType);
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_IntersectionWithMixedToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol(mixedType);
        ITypeSymbol formal = createUnionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_EmptyIntersectionToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = createUnionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_UnionWithMixedToEmptyIntersection_ReturnsTrue() {
        ITypeSymbol actual = createUnionTypeSymbol(mixedType);
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }
}
