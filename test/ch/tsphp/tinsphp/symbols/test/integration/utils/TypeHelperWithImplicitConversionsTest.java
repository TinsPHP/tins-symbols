/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
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

public class TypeHelperWithImplicitConversionsTest extends ATypeHelperTest
{

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToFloatWithoutImplicitConversion_HasNoRelation() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = intType;
        ITypeSymbol formal = floatType;

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToFloatWithImplicitConversion_HasCoerciveRelation() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = intType;
        ITypeSymbol formal = floatType;

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_BoolToAsFloatWithImplicitConversion_HasCoerciveRelation() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(boolType, asList(intType)));

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(typeHelper);

        //arrange
        ITypeSymbol actual = boolType;
        ITypeSymbol formal = createConvertibleTypeSymbol(floatType, symbolFactory, typeHelper);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
    }

    //see TINS-513 implicit conversions and num addition 0.4.0
    @Test
    public void isFirstSameOrSubTypeOfSecond_IntOrFloatToFloatAndIntToFloatImpl_HasNoRelation() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = createUnionTypeSymbol(typeHelper, intType, floatType);
        ITypeSymbol formal = floatType;

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_NO_RELATION));
    }

    //see TINS-513 implicit conversions and num addition 0.4.0
    @Test
    public void isFirstSameOrSubTypeOfSecond_IntInUnionToFloatAndIntToFloatImpl_HasCoerciveRelation() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = createUnionTypeSymbol(typeHelper, intType);
        ITypeSymbol formal = floatType;

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
    }

    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IntInIntersectionToFloatInIntersectionAndTypeVariableGiven_HasUpperBoundConstraint() {
        //pre-act necessary for arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)), pair(stringType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = createIntersectionTypeSymbol(typeHelper, intType);
        ITypeSymbol formal = createIntersectionTypeSymbol(typeHelper, floatType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal, "Ta");

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("int"))));
    }

    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IntInUnionToFloatInIntersectionAndTypeVariableGiven_HasUpperBoundConstraint() {
        //pre-act necessary for arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)), pair(stringType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = createUnionTypeSymbol(typeHelper, intType);
        ITypeSymbol formal = createIntersectionTypeSymbol(typeHelper, floatType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal, "Ta");

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("int"))));
    }

    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IntInIntersectionToFloatInUnionAndTypeVariableGiven_HasUpperBoundConstraint() {
        //pre-act necessary for arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)), pair(stringType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = createIntersectionTypeSymbol(typeHelper, intType);
        ITypeSymbol formal = createUnionTypeSymbol(typeHelper, floatType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal, "Ta");

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("int"))));
    }

    @Test
    public void
    isFirstSameOrSubTypeOfSecond_IntInUnionToFloatInUnionAndTypeVariableGiven_HasUpperBoundConstraint() {
        //pre-act necessary for arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)), pair(stringType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = createUnionTypeSymbol(typeHelper, intType);
        ITypeSymbol formal = createUnionTypeSymbol(typeHelper, floatType);

        //act
        TypeHelperDto result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal, "Ta");

        assertThat(result.relation, is(ERelation.HAS_COERCIVE_RELATION));
        assertThat(result.lowerConstraints.size(), is(0));
        assertThat(result.upperConstraints, isConstraints(pair("Ta", set("int"))));
    }

    private ISymbolFactory createSymbolFactory(ITypeHelper typeHelper) {
        return createSymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);
    }
}
