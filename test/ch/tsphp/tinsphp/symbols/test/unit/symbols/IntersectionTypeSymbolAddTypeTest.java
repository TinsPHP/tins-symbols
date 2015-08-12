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

public class IntersectionTypeSymbolAddTypeTest extends ATypeHelperTest
{

    @Test
    public void addTypeSymbol_EmptyAddInt_ReturnsTrueAndIntersectionContainsInt() {
        //no arrange necessary

        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        boolean result = intersectionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addTypeSymbol_IntAddFloat_ReturnsTrueAndIntersectionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(floatType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAddAnotherInt_ReturnsFalseAndIntersectionContainsOneIntOnly() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addTypeSymbol_IntAddNum_ReturnsFalseAndIntersectionContainsIntOnly() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(numType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addTypeSymbol_InterfaceAAndInterfaceBAddFoo_ReturnsTrueAndIntersectionContainsFooOnly() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(fooType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("Foo"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatAndIAAndIBAddFoo_ReturnsTrueAndIntersectionContainsIntAndFloatAndFoo() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);
        intersectionTypeSymbol.addTypeSymbol(floatType);
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(fooType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float", "Foo"));
    }

    @Test
    public void addTypeSymbol_EmptyAndIsUnionContainingIntAndFloat_ReturnsTrueAndIntersectionContainsUnion() {
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(intType, floatType);

        //act
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        boolean result = intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("(float | int)"));
    }

    @Test
    public void addTypeSymbol_IntAndIsUnionContainingIntAndBool_ReturnsFalseAndIntersectionContainsIntOnly() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(intType, boolType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
    }

    //see TINS-406 add single type in intersection to union
    @Test
    public void addTypeSymbol_EmptyAndIsUnionContainingInt_ReturnsTrueAndIntersectionContainsIntAsSingleType() {
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(intType);

        //act
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        boolean result = intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
        assertThat(symbols.get("int"), is(intType));
    }

    //see TINS-406 add single type in intersection to union
    @Test
    public void addTypeSymbol_FloatAndIsUnionContainingInt_ReturnsTrueAndIntersectionContainsIntAndFloatAsSingleType() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(floatType);
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(intType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
        assertThat(symbols.get("int"), is(intType));
        assertThat(symbols.get("float"), is(floatType));
    }

    //--------- merging intersection types tests

    @Test
    public void addTypeSymbol_EmptyAndIsIntersectionContainingIntAndFloat_ReturnsTrueAndIntersectionContainsUnion() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(intType);
        intersectionTypeSymbol2.addTypeSymbol(floatType);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntMergeWithIntAndFloat_ReturnsTrueAndIntersectionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(intType);

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(intType);
        intersectionTypeSymbol2.addTypeSymbol(floatType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    @Test
    public void addTypeSymbol_IntAndFloatMergeWithFloatAndInt_ReturnsFalseAndIntersectionContainsIntAndFloat() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(intType);
        intersectionTypeSymbol.addTypeSymbol(floatType);

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(floatType);
        intersectionTypeSymbol2.addTypeSymbol(intType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(false));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "float"));
    }

    @Test
    public void addTypeSymbol_NumAndStringMergeWithFloat_ReturnsTrueAndIntersectionContainsFloatAndString() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(numType);
        intersectionTypeSymbol.addTypeSymbol(stringType);

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(floatType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("float", "string"));
    }

    @Test
    public void addTypeSymbol_IBAndIAAndBoolMergeWithFooAndInt_ReturnsTrueAndIntersectionContainsFooAndBoolAndInt() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        intersectionTypeSymbol.addTypeSymbol(interfaceAType);
        intersectionTypeSymbol.addTypeSymbol(interfaceBType);
        intersectionTypeSymbol.addTypeSymbol(boolType);

        //arrange
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol();
        intersectionTypeSymbol2.addTypeSymbol(fooType);
        intersectionTypeSymbol2.addTypeSymbol(intType);

        boolean result = intersectionTypeSymbol.addTypeSymbol(intersectionTypeSymbol2);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("Foo", "bool", "int"));
    }

    //---------------------------------- implicit conversions

    @Test
    public void addTypeSymbol_IntAddIBAndIntHasImplicitConversionToIB_ReturnsTrueAndContainsIntAndIB() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        intersectionTypeSymbol.addTypeSymbol(intType);
        IConversionsProvider provider = mock(IConversionsProvider.class);
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions =
                createConversions(pair(intType, asList(interfaceBType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        when(provider.getImplicitConversions()).thenReturn(implicitConversions);
        when(provider.getExplicitConversions()).thenReturn(explicitConversions);
        typeHelper.setConversionsProvider(provider);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(interfaceBType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int", "IB"));
    }

    //-------------------- polymorphic types -----------------


    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsAsIntAndIntAdded_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsAsIntInUnionAndIntAdded_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol, stringType);
        intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsAsNumAndAsIntAdded_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addLowerTypeBound("T1", numType);
        bindingCollection.addUpperTypeBound("T1", numType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol);
        IConvertibleTypeSymbol subtypeConvertible = createConvertibleTypeSymbol(intType, symbolFactory, typeHelper);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(subtypeConvertible);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("{as int}"));
        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsAsNumInUnionAndAsIntAdded_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addLowerTypeBound("T1", numType);
        bindingCollection.addUpperTypeBound("T1", numType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol, stringType);
        intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);
        IConvertibleTypeSymbol subtypeConvertible = createConvertibleTypeSymbol(intType, symbolFactory, typeHelper);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(subtypeConvertible);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("{as int}"));
        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsAsNumAndAsStringAndAsIntAdded_IntersectionIsNotFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addUpperTypeBound("T1", numType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol1);
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol2);
        IConvertibleTypeSymbol subtypeConvertible = createConvertibleTypeSymbol(intType, symbolFactory, typeHelper);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(subtypeConvertible);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("{as int}", "{as T2}"));
        assertThat(intersectionTypeSymbol.isFixed(), is(false));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2AsNumAndAsStringInUnionAndAsIntAdded_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", numType);
        bindingCollection.addUpperTypeBound("T1", numType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol1, convertibleTypeSymbol2);
        intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);
        IConvertibleTypeSymbol subtypeConvertible = createConvertibleTypeSymbol(numType, symbolFactory, typeHelper);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(subtypeConvertible);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("{as num}"));
        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2AsNumAndAsStringAndNothingAdded_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol1);
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol2);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(createUnionTypeSymbol());
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("nothing"));
        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2AsIntAndAsStringInUnionAndIntAdded_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol1, convertibleTypeSymbol2);
        intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);

        //act
        boolean result = intersectionTypeSymbol.addTypeSymbol(intType);
        Map<String, ITypeSymbol> symbols = intersectionTypeSymbol.getTypeSymbols();

        assertThat(result, is(true));
        assertThat(symbols.keySet(), containsInAnyOrder("int"));
        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsConvertibleAndConvertibleIsFixed_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol);

        //act
        convertibleTypeSymbol.fix("T1");

        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_ContainsConvertibleInUnionAndConvertibleIsFixed_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList("T1"));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol, stringType);
        intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);

        //act
        convertibleTypeSymbol.fix("T1");

        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }


    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesAndOneConvertibleIsFixed_IntersectionIsNotFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol1);
        intersectionTypeSymbol.addTypeSymbol(convertibleTypeSymbol2);

        //act
        convertibleTypeSymbol1.fix("T1");

        assertThat(intersectionTypeSymbol.isFixed(), is(false));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesInUnionAndOneConvertibleIsFixed_IntersectionIsNotFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol1, convertibleTypeSymbol2);
        intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);

        //act
        convertibleTypeSymbol1.fix("T1");

        assertThat(intersectionTypeSymbol.isFixed(), is(false));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void addTypeSymbol_Contains2ConvertiblesInUnionAndBothAreFixed_IntersectionIsFixed() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        IBindingCollection bindingCollection = new BindingCollection(symbolFactory, typeHelper);
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$b", new TypeVariableReference("T2"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", stringType);
        bindingCollection.addUpperTypeBound("T2", stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol1 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol1, asList("T1"));
        IConvertibleTypeSymbol convertibleTypeSymbol2 = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol2, asList("T2"));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol1, convertibleTypeSymbol2);
        intersectionTypeSymbol.addTypeSymbol(unionTypeSymbol);

        //act
        convertibleTypeSymbol1.fix("T1");
        convertibleTypeSymbol2.fix("T2");

        assertThat(intersectionTypeSymbol.isFixed(), is(true));
    }
}

