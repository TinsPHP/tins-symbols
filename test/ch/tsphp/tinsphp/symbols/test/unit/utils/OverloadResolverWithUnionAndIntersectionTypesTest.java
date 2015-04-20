/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverloadResolverWithUnionAndIntersectionTypesTest extends ATypeTest
{

    @Test
    public void areSameType_IntInIntersectionToIntInUnion_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(intType);
        ITypeSymbol formal = createUnion(intType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInUnionToIntInIntersection_ReturnsTrue() {
        ITypeSymbol actual = createUnion(intType);
        ITypeSymbol formal = createIntersectionType(intType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInIntersectionToMixedInUnion_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType(intType);
        ITypeSymbol formal = createUnion(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_IntInUnionToMixedInIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooOrBarToInterfaceBAndInterfaceA_ReturnsTrue() {
        ITypeSymbol barType = mock(ITypeSymbol.class);
        when(barType.getParentTypeSymbols()).thenReturn(set(interfaceAType, interfaceBType));
        when(barType.getAbsoluteName()).thenReturn("Bar");

        ITypeSymbol actual = createUnion(fooType, barType);
        ITypeSymbol formal = createIntersectionType(interfaceBType, interfaceAType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToIntAndFloat_ReturnsFalse() {
        ITypeSymbol actual = createUnion(intType, floatType);
        ITypeSymbol formal = createIntersectionType(intType, floatType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }


    //--------------------  special case empty union and intersection

    @Test
    public void areSameType_EmptyUnionToEmptyIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }


    @Test
    public void areSameType_EmptyIntersectionToEmptyUnion_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyUnionToEmptyIntersection_ReturnsTrue() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyUnionToEmptyIntersection_ReturnsFalse() {
        ITypeSymbol actual = createUnion();
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_EmptyIntersectionToEmptyUnion_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_EmptyIntersectionToEmptyUnion_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createUnion();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }


    //--------------------  special case union with mixed only and intersection with mixed only

    @Test
    public void areSame_UnionWithMixedToIntersectionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_IntersectionWithMixedToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_EmptyIntersectionToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType();
        ITypeSymbol formal = createUnion(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSame_UnionWithMixedToEmptyIntersection_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createIntersectionType();

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_UnionWithMixedToIntersectionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_UnionWithMixedToIntersectionMixed_ReturnsTrue() {
        ITypeSymbol actual = createUnion(mixedType);
        ITypeSymbol formal = createIntersectionType(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntersectionWithMixedToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void isFirstSameOrParentTypeOfSecond_IntersectionMixedToUnionWithMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionType(mixedType);
        ITypeSymbol formal = createUnion(mixedType);

        IOverloadResolver solver = createOverloadResolverAndSetMixed();
        boolean result = solver.isFirstSameOrParentTypeOfSecond(actual, formal);

        assertThat(result, is(true));
    }

    private IUnionTypeSymbol createUnion(ITypeSymbol... typeSymbols) {
        IUnionTypeSymbol unionTypeSymbol = mock(IUnionTypeSymbol.class);
        Map<String, ITypeSymbol> map = new HashMap<>();
        for (ITypeSymbol typeSymbol : typeSymbols) {
            map.put(typeSymbol.getAbsoluteName(), typeSymbol);
        }
        when(unionTypeSymbol.getTypeSymbols()).thenReturn(map);
        return unionTypeSymbol;
    }

    private IIntersectionTypeSymbol createIntersectionType(ITypeSymbol... typeSymbols) {
        IIntersectionTypeSymbol intersectionTypeSymbol = mock(IIntersectionTypeSymbol.class);
        Map<String, ITypeSymbol> map = new HashMap<>();
        for (ITypeSymbol typeSymbol : typeSymbols) {
            map.put(typeSymbol.getAbsoluteName(), typeSymbol);
        }
        when(intersectionTypeSymbol.getTypeSymbols()).thenReturn(map);
        return intersectionTypeSymbol;
    }

    private IOverloadResolver createOverloadResolverAndSetMixed() {
        IOverloadResolver overloadResolver = createOverloadResolver();
        overloadResolver.setMixedTypeSymbol(mixedType);
        return overloadResolver;
    }

    protected IOverloadResolver createOverloadResolver() {
        return new OverloadResolver();
    }
}
