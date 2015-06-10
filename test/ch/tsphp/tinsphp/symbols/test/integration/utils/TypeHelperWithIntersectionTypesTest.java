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

public class TypeHelperWithIntersectionTypesTest extends ATypeHelperTest
{

    //--------------------  tests with intersection types on the left

    @Test
    public void areSameType_IntInIntersectionToInt_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType);
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IntInIntersectionToMixed_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType);
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_MixedInIntersectionToInt_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionTypeSymbol(mixedType);
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty intersection on the left

    @Test
    public void areSameType_EmptyIntersectionToInt_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = intType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_EmptyIntersectionToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = mixedType;

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    //--------------------  tests with intersection types on the right

    @Test
    public void areSameType_IntToIntInIntersection_ReturnsTrue() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionTypeSymbol(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_MixedToIntInIntersection_ReturnsFalse() {
        ITypeSymbol actual = mixedType;
        ITypeSymbol formal = createIntersectionTypeSymbol(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_IntToMixedInIntersection_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  tests with empty intersection on the right

    @Test
    public void areSameType_IntToEmptyIntersection_ReturnsFalse() {
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_MixedToEmptyIntersection_ReturnsTrue() {
        ITypeSymbol actual = mixedType;
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    //--------------------  tests with intersection types on the left and on the right

    @Test
    public void areSameType_IntToInt_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol(intType);
        ITypeSymbol formal = createIntersectionTypeSymbol(intType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IAAndIBToIAAndIB_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol(interfaceAType, interfaceBType);
        ITypeSymbol formal = createIntersectionTypeSymbol(interfaceAType, interfaceBType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_IAndIBToIBAndIA_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol(interfaceAType, interfaceBType);
        ITypeSymbol formal = createIntersectionTypeSymbol(interfaceBType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_ISubAndIBToIBAndIA_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionTypeSymbol(interfaceSubAType, interfaceBType);
        ITypeSymbol formal = createIntersectionTypeSymbol(interfaceBType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }


    @Test
    public void areSameType_IAndIBToMixedInIntersection_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionTypeSymbol(interfaceAType, interfaceBType);
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_MixedInIntersectionToIAAndIB_ReturnsFalse() {
        ITypeSymbol actual = createIntersectionTypeSymbol(mixedType);
        ITypeSymbol formal = createIntersectionTypeSymbol(interfaceBType, interfaceAType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    //--------------------  special case empty intersection on the left and on the right

    @Test
    public void areSameType_EmptyToEmpty_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol();
        ITypeSymbol formal = createIntersectionTypeSymbol();

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    //--------------------  special case intersection with mixed only on the left and on the right

    @Test
    public void areSame_MixedToMixed_ReturnsTrue() {
        ITypeSymbol actual = createIntersectionTypeSymbol(mixedType);
        ITypeSymbol formal = createIntersectionTypeSymbol(mixedType);

        ITypeHelper typeHelper = createTypeHelperAndInit();
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }
}
