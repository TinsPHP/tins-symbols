/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ETypeHelperResult;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.scopes.ScopeHelper;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TypeHelperWithConvertibleTypesTest extends ATypeHelperTest
{

    //--------------------  tests with convertible types on the left

    @Test
    public void areSameType_AsIntToInt_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = intType;

        //act
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_AsIntToMixed_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = mixedType;

        //act
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToInt_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = intType;

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToInt_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = intType;

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToMixed_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = mixedType;

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToEmptyIntersection_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = createIntersectionTypeSymbol(typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsNumInIntersection_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        IIntersectionTypeSymbol formal = createIntersectionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsNumInUnion_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsNumInIntersection_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        IIntersectionTypeSymbol formal = createIntersectionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsNumInUnion_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsNumAndStringInUnion_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));
        formal.addTypeSymbol(stringType);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToBoolAndIntAndAsNumAndStringInUnion_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(boolType);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));
        formal.addTypeSymbol(stringType);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsIntInIntersection_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        IIntersectionTypeSymbol formal = createIntersectionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(intType, symbolFactory, typeHelper));

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsIntInUnion_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(intType, symbolFactory, typeHelper));

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    //--------------------  tests with convertible types on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringToAsIntAndNoExplicitDefined_ReturnsFalse() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = stringType;
        ITypeSymbol formal = createConvertibleType(intType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringToAsIntAndStringToNumDefined_ReturnsFalse() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions =
                createConversions(pair(stringType, asList(floatType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = stringType;
        ITypeSymbol formal = createConvertibleType(intType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsInt_ReturnsTrue() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createConvertibleType(intType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsNum_ReturnsTrue() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createConvertibleType(numType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsFloat_ReturnsTrue() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createConvertibleType(floatType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsString_ReturnsTrue() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(intType, asList(stringType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = intType;
        ITypeSymbol formal = createConvertibleType(stringType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_NumToAsString_ReturnsFalse() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(intType, asList(stringType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = numType;
        ITypeSymbol formal = createConvertibleType(stringType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooToAsString_ReturnsTrue() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(fooType, asList(stringType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = fooType;
        ITypeSymbol formal = createConvertibleType(stringType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooToAsScalar_ReturnsTrue() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(fooType, asList(stringType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = fooType;
        ITypeSymbol formal = createConvertibleType(scalarType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ISubAToAsStringWhereIAHasExpConversionToString_ReturnsTrue() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(interfaceAType, asList(stringType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = interfaceSubAType;
        ITypeSymbol formal = createConvertibleType(stringType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ISubAToAsStringWhereIAHasImpConversionToString_ReturnsTrue() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(interfaceAType, asList(stringType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = interfaceSubAType;
        ITypeSymbol formal = createConvertibleType(stringType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    //--------------------  tests with convertible types on the left and right

    @Test
    public void areSameType_AsIntToAsInt_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(intType, symbolFactory, typeHelper);

        //act
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(true));
    }

    @Test
    public void areSameType_AsIntToAsNum_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(numType, symbolFactory, typeHelper);

        //act
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void areSameType_AsNumToAsInt_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(intType, symbolFactory, typeHelper);

        //act
        boolean result = typeHelper.areSame(actual, formal);

        assertThat(result, is(false));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsInt_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(intType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsNum_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(numType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsScalar_ReturnsTrue() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(scalarType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsFloat_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(floatType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsFloat_ReturnsFalse() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(floatType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    private ISymbolFactory createSymbolFactory(ITypeHelper typeHelper) {
        return createSymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);
    }
}
