/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.core.IConversionsProvider;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.constraints.BindingCollection;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnionTypeSymbolAddTypeTest extends ATypeHelperTest
{

    @Test
    public void addTypeSymbol_EmptyAddInt_ReturnsTrueAndUnionContainsInt() {
        //no arrange necessary

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addTypeSymbol_IntAddFloat_ReturnsTrueAndUnionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(floatType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAddAnotherInt_ReturnsFalseAndUnionContainsOneIntOnly() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addTypeSymbol_NumAddInt_ReturnsFalseAndUnionContainsNumOnly() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(numType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("num"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatAddNum_ReturnsTrueAndUnionContainsNumOnly() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(numType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("num"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatAndStringAndBoolAddNum_ReturnsTrueAndUnionContainsNumAndStringAndBool() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);
        unionTypeSymbol.addTypeSymbol(stringType);
        unionTypeSymbol.addTypeSymbol(boolType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(numType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("num", "string", "bool"));
    }

    @Test
    public void addTypeSymbol_EmptyAndIntersectionContainingIBAndIA_ReturnsTrueAndUnionContainsIntersection() {
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);

        //act
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("(IA & IB)"));
    }

    @Test
    public void addTypeSymbol_IntAndIntersectionContainingIBAndIA_ReturnsTrueAndUnionContainsIntAndIntersection() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("(IA & IB)", "int"));
    }

    @Test
    public void addTypeSymbol_IntAndIAIntersectionContainingIBAndIA_ReturnsFalseAndUnionContainsIntAndIA() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(interfaceAType);

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("IA", "int"));
    }

    //see TINS-406 add single type in intersection to union
    @Test
    public void addTypeSymbol_EmptyAndIntersectionContainingFloat_ReturnsTrueAndUnionContainsIntAndFloatAsSingleTypes
    () {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(floatType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("float", "int"));
        assertThat(symbols.get("float"), is(floatType));
        assertThat(symbols.get("int"), is(intType));
    }

    //see TINS-406 add single type in intersection to union
    @Test
    public void addTypeSymbol_IntAndIntersectionContainingFloat_ReturnsTrueAndUnionContainsFloatAsSingleType() {
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(floatType);

        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        boolean result = unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("float"));
        assertThat(symbols.get("float"), is(floatType));
    }

    //--------- merging unions test

    @Test
    public void addTypeSymbol_EmptyAndIsUnionContainingIntAndFloat_ReturnsTrueAndUnionContainsUnion() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(intType);
        unionTypeSymbol2.addTypeSymbol(floatType);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAndMergeWithIntAndFloat_ReturnsTrueAndUnionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(intType);

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(intType);
        unionTypeSymbol2.addTypeSymbol(floatType);

        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatMergeWithFloatAndInt_ReturnsFalseAndUnionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(floatType);
        unionTypeSymbol2.addTypeSymbol(intType);

        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    @Test
    public void addTypeSymbol_NumAndStringMergeWithFloatAndInt_ReturnsFalseAndUnionContainsNumAndString() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(numType);
        unionTypeSymbol.addTypeSymbol(stringType);

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(intType);
        unionTypeSymbol2.addTypeSymbol(floatType);

        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("num", "string"));
    }

    @Test
    public void addTypeSymbol_BoolAndIntAndFloatAndFooMergeWithNumAndIA_ReturnsTrueAndUnionContainsBoolAndNumAndIA() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        unionTypeSymbol.addTypeSymbol(boolType);
        unionTypeSymbol.addTypeSymbol(intType);
        unionTypeSymbol.addTypeSymbol(floatType);

        //arrange
        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol();
        unionTypeSymbol2.addTypeSymbol(numType);
        unionTypeSymbol2.addTypeSymbol(interfaceAType);

        boolean result = unionTypeSymbol.addTypeSymbol(unionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("bool", "num", "IA"));
    }

    //---------------------- implicit conversions -----------------

    @Test
    public void addTypeSymbol_IntAddFloatImplicitConversionDefined_ReturnsTrueAndUnionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        unionTypeSymbol.addTypeSymbol(intType);
        IConversionsProvider provider = mock(IConversionsProvider.class);
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions =
                createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        when(provider.getImplicitConversions()).thenReturn(implicitConversions);
        when(provider.getExplicitConversions()).thenReturn(explicitConversions);
        typeHelper.setConversionsProvider(provider);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(floatType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    //-------------------- polymorphic types -----------------


    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsConvertibleAndMixedAdded_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(mixedType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("mixed"));
        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsConvertibleInIntersectionAndMixedIsAdded_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol(convertibleTypeSymbol, fooType);
        unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(mixedType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("mixed"));
        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsConvertibleAndParentConvertibleAdded_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol);
        IConvertibleTypeSymbol parentConvertible = createConvertibleType(numType, symbolFactory, typeHelper);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(parentConvertible);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("{as num}"));
        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsConvertibleInIntersectionAndParentConvertibleAdded_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol(convertibleTypeSymbol, fooType);
        unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        IConvertibleTypeSymbol parentConvertible = createConvertibleType(numType, symbolFactory, typeHelper);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(parentConvertible);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("{as num}"));
        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesAndParentConvertibleOfOneTypeAdded_UnionIsNotFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol1);
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol2);
        IConvertibleTypeSymbol parentConvertible = createConvertibleType(numType, symbolFactory, typeHelper);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(parentConvertible);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("{as num}", "{as T2}"));
        assertThat(unionTypeSymbol.isFixed(), is(false));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesInIntersectionAndParentConvertibleOfOneTypeAdded_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        IIntersectionTypeSymbol intersectionTypeSymbol
                = createIntersectionTypeSymbol(convertibleTypeSymbol1, convertibleTypeSymbol2);
        unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);
        IConvertibleTypeSymbol parentConvertible = createConvertibleType(numType, symbolFactory, typeHelper);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(parentConvertible);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("{as num}"));
        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesAndMixedAdded_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol1);
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol2);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(mixedType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("mixed"));
        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesInIntersectionAndMixedAdded_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        IIntersectionTypeSymbol intersectionTypeSymbol
                = createIntersectionTypeSymbol(convertibleTypeSymbol1, convertibleTypeSymbol2);
        unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);

        //act
        boolean result = unionTypeSymbol.addTypeSymbol(mixedType);
        Map<String, ITypeSymbol> symbols = unionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("mixed"));
        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsConvertibleAndConvertibleIsFixed_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol);

        //act
        convertibleTypeSymbol.fix("T1");

        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsConvertibleInIntersectionAndConvertibleIsFixed_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        IIntersectionTypeSymbol intersectionTypeSymbol
                = createIntersectionTypeSymbol(convertibleTypeSymbol, stringType);
        unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);

        //act
        convertibleTypeSymbol.fix("T1");

        assertThat(unionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesAndOneConvertibleIsFixed_UnionIsNotFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol1);
        unionTypeSymbol.addTypeSymbol(convertibleTypeSymbol2);

        //act
        convertibleTypeSymbol1.fix("T1");

        assertThat(unionTypeSymbol.isFixed(), is(false));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesInIntersectionAndOneConvertibleIsFixed_UnionIsNotFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        IIntersectionTypeSymbol intersectionTypeSymbol
                = createIntersectionTypeSymbol(convertibleTypeSymbol1, convertibleTypeSymbol2);
        unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);

        //act
        convertibleTypeSymbol1.fix("T1");

        assertThat(unionTypeSymbol.isFixed(), is(false));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesInIntersectionAndBothAreFixed_UnionIsFixed() {
        //pre-act necessary for arrange
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleType();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        IIntersectionTypeSymbol intersectionTypeSymbol
                = createIntersectionTypeSymbol(convertibleTypeSymbol1, convertibleTypeSymbol2);
        unionTypeSymbol.addTypeSymbol(intersectionTypeSymbol);

        //act
        convertibleTypeSymbol1.fix("T1");
        convertibleTypeSymbol2.fix("T2");

        assertThat(unionTypeSymbol.isFixed(), is(true));
    }
}
