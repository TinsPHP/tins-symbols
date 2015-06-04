/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
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

public class TypeHelperWithImplicitConversionsTest extends ATypeHelperTest
{

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToFloatWithoutImplicitConversion_ReturnsFalse() {
        //pre-act arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        //pre-act necessary for arrange
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //arrange
        ITypeSymbol actual = intType;
        ITypeSymbol formal = floatType;

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_NO_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_IntToFloatWithImplicitConversion_ReturnsTrue() {
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
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_COERCIVE_RELATION));
    }

    @Test
    public void isFirstSameOrSubTypeOfSecond_BoolToAsFloatWithImplicitConversion_ReturnsTrue() {
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
        ITypeSymbol formal = createConvertibleType(floatType, symbolFactory, typeHelper);

        //act
        ETypeHelperResult result = typeHelper.isFirstSameOrSubTypeOfSecond(actual, formal);

        assertThat(result, is(ETypeHelperResult.HAS_COERCIVE_RELATION));
    }

    private ISymbolFactory createSymbolFactory(ITypeHelper typeHelper) {
        return createSymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);
    }
}
