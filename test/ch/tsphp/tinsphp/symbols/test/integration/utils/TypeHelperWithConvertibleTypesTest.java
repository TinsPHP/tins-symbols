/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ERelation;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.common.utils.TypeHelperDto;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.scopes.ScopeHelper;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.TypeParameterConstraintsMatcher.isConstraints;
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
    public void isFirstSameOrSubTypeOfSecond_AsIntToInt_HasNoRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = intType;

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToInt_HasNoRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = intType;

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToMixed_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = mixedType;

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToEmptyIntersection_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = createIntersectionTypeSymbol(typeHelper);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsNumInIntersection_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        IIntersectionTypeSymbol formal = createIntersectionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsNumInUnion_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsNumInIntersection_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        IIntersectionTypeSymbol formal = createIntersectionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsNumInUnion_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsNumAndStringInUnion_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(numType, symbolFactory, typeHelper));
        formal.addTypeSymbol(stringType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToBoolAndIntAndAsNumAndStringInUnion_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsIntInIntersection_HasNoRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        IIntersectionTypeSymbol formal = createIntersectionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(intType, symbolFactory, typeHelper));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsIntInUnion_HasNoRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        IUnionTypeSymbol formal = createUnionTypeSymbol(typeHelper);
        formal.addTypeSymbol(createConvertibleType(intType, symbolFactory, typeHelper));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with convertible types on the right

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringToAsIntAndNoExplicitDefined_HasNoRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_StringToAsIntAndStringToNumDefined_HasNoRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsInt_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsNum_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsFloat_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsString_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_NumToAsString_HasNoRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooToAsString_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_FooToAsScalar_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ISubAToAsStringWhereIAHasExpConversionToString_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_ISubAToAsStringWhereIAHasImpConversionToString_HasRelation() {
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
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
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
    public void areSameType_AsIntToAsNum_HasNoRelation() {
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
    public void areSameType_AsNumToAsInt_HasNoRelation() {
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
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsInt_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(intType, symbolFactory, typeHelper);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsNum_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(numType, symbolFactory, typeHelper);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsScalar_HasRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(scalarType, symbolFactory, typeHelper);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsIntToAsFloat_HasNoRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(intType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(floatType, symbolFactory, typeHelper);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_AsNumToAsFloat_HasNoRelation() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createConvertibleType(numType, symbolFactory, typeHelper);
        ITypeSymbol formal = createConvertibleType(floatType, symbolFactory, typeHelper);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //--------------------  tests with bound convertible types

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToAsNum_HasRelationAndLowerConstraintIsInt() {
        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit();
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = intType;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.addUpperTypeBound("Ta", numType);
        bindings.bind(formal, asList("Ta"));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints, isConstraints(pair("Ta", set("int"))));
        assertThat(result.upperConstraints.size(), is(0));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_BoolToAsNum_HasRelationAndLowerConstraintIsInt() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(boolType, asList(intType, stringType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = boolType;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.addUpperTypeBound("Ta", numType);
        bindings.bind(formal, asList("Ta"));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints, isConstraints(pair("Ta", set("int"))));
        assertThat(result.upperConstraints.size(), is(0));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_BoolToAsFloat_HasCoerciveRelationAndLowerConstraintIsInt() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(boolType, asList(intType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = boolType;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.addUpperTypeBound("Ta", floatType);
        bindings.bind(formal, asList("Ta"));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
        assertThat(result.lowerConstraints, isConstraints(pair("Ta", set("int"))));
        assertThat(result.upperConstraints.size(), is(0));
    }

    @Test
    public void
    isFirstSameOrSubTypeOfSecond_FloatOrIntToAsTWhereTLowerStringAndIntAsWellAsFloatHaveExplToString_LowerConstraintIsString() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(intType, asList(stringType)), pair(floatType, asList(stringType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = createUnionTypeSymbol(intType, floatType);
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.addUpperTypeBound("Ta", stringType);
        bindings.bind(formal, asList("Ta"));

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints, isConstraints(pair("Ta", set("string"))));
        assertThat(result.upperConstraints.size(), is(0));
    }

    //see TINS-498 convertible types without upper bound
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_BoolToAsTWhereBoolLowerTAndIntToBoolIsExpl_HasRelationAndUpperConstraintIsBool() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = boolType;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        bindings.addLowerTypeBound("Ta", boolType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("bool"))));
    }

    //see TINS-498 convertible types without upper bound
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IntToAsTWhereBoolLowerTAndIntToBoolIsExpl_HasRelationAndUpperConstraintIsBool() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(intType, asList(boolType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = intType;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        bindings.addLowerTypeBound("Ta", boolType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("bool"))));
    }

    //see TINS-498 convertible types without upper bound
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_BoolToAsTWhereFloatLowerT_HasCoerciveRelationAndUpperConstraintIsFloat() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(boolType, asList(intType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = boolType;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        bindings.addLowerTypeBound("Ta", floatType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("float"))));
    }

    //see TINS-498 convertible types without upper bound
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IntToAsTWhereIALowerTAndIntToFooIsImpl_HasRelationAndUpperConstraintIsIA() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(fooType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();


        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = intType;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        bindings.addLowerTypeBound("Ta", interfaceAType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("IA"))));
    }

    //see TINS-498 convertible types without upper bound
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IntToAsTWhereIALowerTAndIntToFooIsExpl_HasRelationAndUpperConstraintIsIA() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(intType, asList(fooType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = intType;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        bindings.addLowerTypeBound("Ta", interfaceAType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("IA"))));
    }

    //TINS-503 multiple constraints are not propagated
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_NumOrStringToAsTWhereTUpperIsNumOrString_HasRelationAndLowerConstraintsNumAndString() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        IUnionTypeSymbol numOrString = createUnionTypeSymbol(typeHelper, numType, stringType);
        ITypeSymbol actual = numOrString;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        bindings.addUpperTypeBound("Ta", numOrString);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints, isConstraints(pair("Ta", set("num", "string"))));
        assertThat(result.upperConstraints.size(), is(0));
    }

    //see TINS-504 intersection type as lower type constraint of parametric type
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IBAndISubAToAsTWhereTUpperIsA_HasRelationAndLowerConstraintsIBAndISubA() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        IIntersectionTypeSymbol IBAndISubA = createIntersectionTypeSymbol(typeHelper, interfaceBType,
                interfaceSubAType);
        ITypeSymbol actual = IBAndISubA;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        bindings.addUpperTypeBound("Ta", interfaceAType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints, isConstraints(pair("Ta", set("(IB & ISubA)"))));
        assertThat(result.upperConstraints.size(), is(0));
    }

    //see TINS-504 intersection type as lower type constraint of parametric type
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IBAndISubAToAsTWhereTUpperIsIAOrIB_HasRelationAndLowerConstraintsIBAndISubA() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        IIntersectionTypeSymbol IBAndISubA = createIntersectionTypeSymbol(typeHelper, interfaceBType,
                interfaceSubAType);
        ITypeSymbol actual = IBAndISubA;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        IUnionTypeSymbol isIAOrIB = createUnionTypeSymbol(typeHelper, interfaceBType, interfaceAType);
        bindings.addUpperTypeBound("Ta", isIAOrIB);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints, isConstraints(pair("Ta", set("(IB & ISubA)"))));
        assertThat(result.upperConstraints.size(), is(0));
    }

    //see TINS-504 intersection type as lower type constraint of parametric type
    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IBAndAsISubAOrIAToAsTWhereTUpperIsIAOrIB_HasRelationAndLowerConstraintsIBAndISubA() {
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        IConvertibleTypeSymbol asISubA = createConvertibleType(interfaceSubAType, symbolFactory, typeHelper);
        IIntersectionTypeSymbol IBAndAsISubA = createIntersectionTypeSymbol(typeHelper, interfaceBType, asISubA);
        IUnionTypeSymbol IBAndAsISubAOrIA = createUnionTypeSymbol(typeHelper, IBAndAsISubA, interfaceAType);
        ITypeSymbol actual = IBAndAsISubAOrIA;
        IParametricTypeSymbol formal = createConvertibleType(symbolFactory, typeHelper);
        IOverloadBindings bindings = symbolFactory.createOverloadBindings();
        bindings.addVariable("$a", new TypeVariableReference("Ta"));
        bindings.bind(formal, asList("Ta"));
        IUnionTypeSymbol isIAOrIB = createUnionTypeSymbol(typeHelper, interfaceBType, interfaceAType);
        bindings.addUpperTypeBound("Ta", isIAOrIB);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_RELATION));
        assertThat(result.lowerConstraints, isConstraints(pair("Ta", set("(IB & {as ISubA})", "IA"))));
        assertThat(result.upperConstraints.size(), is(0));
    }

    private ISymbolFactory createSymbolFactory(ITypeHelper typeHelper) {
        return createSymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);
    }
}
