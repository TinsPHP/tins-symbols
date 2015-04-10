/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.Mockito.mock;

public class UnionTypeSymbolTest extends ATypeTest
{

    @Test
    public void getTypeSymbols_Standard_ReturnsSetPassedByConstructor() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.NULL, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.FALSE, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.TRUE, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.INT, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.FLOAT, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.NUM, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.STRING, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.SCALAR, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.ARRAY, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.RESOURCE, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.MIXED, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        Map<String, ITypeSymbol> result = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(types));
    }

    @Test
    public void addTypeSymbol_EmptyAddInt_ReturnsTrueAndUnionContainsInt() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int"));
    }

    @Test
    public void addTypeSymbol_IntAddFloat_ReturnsTrueAndUnionContainsIntAndFloat() {
        Map<String, ITypeSymbol> map = createMapWithTypes(intType);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        boolean result = unionTypeSymbol.addTypeSymbol(floatType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAddAnotherInt_ReturnsFalseAndUnionContainsOneIntOnly() {
        Map<String, ITypeSymbol> map = createMapWithTypes(intType);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("int"));
    }

    @Test
    public void addTypeSymbol_NumAddInt_ReturnsFalseAndUnionContainsNumOnly() {
        Map<String, ITypeSymbol> map = createMapWithTypes(numType);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("num"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatAddNum_ReturnsTrueAndUnionContainsNumOnly() {
        Map<String, ITypeSymbol> map = createMapWithTypes(intType, floatType);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        boolean result = unionTypeSymbol.addTypeSymbol(numType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("num"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatAndStringAndBoolAddNum_ReturnsTrueAndUnionContainsNumAndStringAndBool() {
        Map<String, ITypeSymbol> map = createMapWithTypes(intType, floatType, stringType, boolType);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        boolean result = unionTypeSymbol.addTypeSymbol(numType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("num", "string", "bool"));
    }

    @Test(expected = IllegalStateException.class)
    public void addTypeSymbol_IsAlreadySealed_ThrowsIllegalStateException() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.seal();
        unionTypeSymbol.addTypeSymbol(numType);

        //assert in annotation
    }

    @Test
    public void addTypeSymbol_EmptyAndIsUnionContainingIntAndFloat_ReturnsTrueAndUnionContainsIntAndFloat() {
        Map<String, ITypeSymbol> map2 = createMapWithTypes(intType, floatType);
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol(map2);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void merge_EmptyMergeWithIntAndFloat_ReturnsTrueAndUnionContainsIntAndFloat() {
        Map<String, ITypeSymbol> map2 = createMapWithTypes(intType, floatType);
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol(map2);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.merge(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void merge_IntAndFloatMergeWithFloatAndInt_ReturnsFalseAndUnionContainsIntAndFloat() {
        Map<String, ITypeSymbol> map = createMapWithTypes(intType, floatType);
        Map<String, ITypeSymbol> map2 = createMapWithTypes(intType, floatType);
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol(map2);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        boolean result = unionTypeSymbol.merge(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("int", "float"));
    }

    @Test
    public void merge_NumAndStringMergeWithFloatAndInt_ReturnsFalseAndUnionContainsNumAndString() {
        Map<String, ITypeSymbol> map = createMapWithTypes(numType, stringType);
        Map<String, ITypeSymbol> map2 = createMapWithTypes(intType, floatType);
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol(map2);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        boolean result = unionTypeSymbol.merge(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), hasItems("num", "string"));
    }

    @Test
    public void merge_BoolAndIntAndFloatAndFooMergeWithNumAndIA_ReturnsTrueAndUnionContainsBoolAndNumAndIA() {
        Map<String, ITypeSymbol> map = createMapWithTypes(boolType, intType, floatType);
        Map<String, ITypeSymbol> map2 = createMapWithTypes(numType, interfaceAType);
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol(map2);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        boolean result = unionTypeSymbol.merge(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), hasItems("num", "IA"));
    }

    @Test(expected = IllegalStateException.class)
    public void merge_IsAlreadySealed_ThrowsIllegalStateException() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.seal();
        unionTypeSymbol.merge(createUnionTypeSymbol());

        //assert in annotation
    }

    @Test
    public void evalSelf_IsNotSealed_ReturnsNull() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        ITypeSymbol result = unionTypeSymbol.evalSelf();

        assertThat(result, is(nullValue()));
    }

    @Test
    public void evalSelf_IsSealed_ReturnsItself() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.seal();
        ITypeSymbol result = unionTypeSymbol.evalSelf();

        assertThat(result, is((ITypeSymbol) unionTypeSymbol));
    }

    @Test
    public void seal_Standard_IsReadyForEvalAfterwards() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.seal();
        boolean result = unionTypeSymbol.isReadyForEval();

        assertThat(result, is(true));
    }


    @Test
    public void isFalseable_IsSealedAndContainsFalseType_ReturnsTrue() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.FALSE, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        unionTypeSymbol.seal();
        boolean result = unionTypeSymbol.isFalseable();

        assertThat(result, is(true));
    }

    @Test
    public void isFalseable_IsNotSealedAndContainsFalseType_ReturnsFalse() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.FALSE, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        boolean result = unionTypeSymbol.isFalseable();

        assertThat(result, is(false));
    }

    @Test
    public void isFalseable_IsSealedAndDoesNotContainFalseType_ReturnsFalse() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.BOOL, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        unionTypeSymbol.seal();
        boolean result = unionTypeSymbol.isFalseable();

        assertThat(result, is(false));
    }

    @Test
    public void isFalseable_IsNotSealedAndDoesNotContainFalseType_ReturnsFalse() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.BOOL, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        boolean result = unionTypeSymbol.isFalseable();

        assertThat(result, is(false));
    }

    @Test
    public void isNullable_IsSealedAndContainsNullType_ReturnsTrue() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.NULL, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        unionTypeSymbol.seal();
        boolean result = unionTypeSymbol.isNullable();

        assertThat(result, is(true));
    }

    @Test
    public void isNullable_IsNotSealedAndContainsNullType_ReturnsFalse() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.NULL, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        boolean result = unionTypeSymbol.isNullable();

        assertThat(result, is(false));
    }

    @Test
    public void isNullable_IsSealedAndDoesNotContainNullType_ReturnsFalse() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.BOOL, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        unionTypeSymbol.seal();
        boolean result = unionTypeSymbol.isNullable();

        assertThat(result, is(false));
    }

    @Test
    public void isNullable_IsNotSealedAndDoesNotContainNullType_ReturnsFalse() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.BOOL, mock(ITypeSymbol.class));

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(types);
        boolean result = unionTypeSymbol.isNullable();

        assertThat(result, is(false));
    }

    @Test
    public void getName_IsNotSealedAndEmpty_ReturnsQuestionMark() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        String result = unionTypeSymbol.getName();

        assertThat(result, is("?"));
    }

    @Test
    public void getName_IsSealedAndOneTypeOnly_ReturnsTypeWithoutBraces() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.seal();
        String result = unionTypeSymbol.getName();

        assertThat(result, is("int"));
    }

    @Test
    public void getName_IsSealedAndEmpty_ReturnsEmptyBraces() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.seal();
        String result = unionTypeSymbol.getName();

        assertThat(result, is("{}"));
    }

    @Test
    public void getName_IsSealedAndIntAndFloat_ReturnsEmptyBraces() {
        Map<String, ITypeSymbol> map = createMapWithTypes(intType, floatType);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(map);
        unionTypeSymbol.seal();
        String result = unionTypeSymbol.getName();

        assertThat(result, anyOf(is("{int | float}"), is("{float | int}")));
    }

    private IUnionTypeSymbol createUnionTypeSymbol() {
        return createUnionTypeSymbol(new OverloadResolver());
    }

    private IUnionTypeSymbol createUnionTypeSymbol(Map<String, ITypeSymbol> unionTypeSymbols) {
        return createUnionTypeSymbol(new OverloadResolver(), unionTypeSymbols);
    }

    protected IUnionTypeSymbol createUnionTypeSymbol(IOverloadResolver overloadResolver) {
        return new UnionTypeSymbol(overloadResolver);
    }

    protected IUnionTypeSymbol createUnionTypeSymbol(
            IOverloadResolver overloadResolver, Map<String, ITypeSymbol> unionTypeSymbols) {
        return new UnionTypeSymbol(overloadResolver, unionTypeSymbols);
    }

}
