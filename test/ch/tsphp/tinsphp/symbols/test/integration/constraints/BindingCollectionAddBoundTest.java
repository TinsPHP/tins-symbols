/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.inference.constraints.BoundException;
import ch.tsphp.tinsphp.common.inference.constraints.BoundResultDto;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IntersectionBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.LowerBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.UpperBoundException;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.constraints.BindingCollection;
import ch.tsphp.tinsphp.symbols.scopes.ScopeHelper;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.BindingCollectionMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.BindingCollectionMatcher.withVariableBindings;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.TypeParameterConstraintsMatcher.isConstraints;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BindingCollectionAddBoundTest extends ATypeHelperTest
{

    @Test(expected = IllegalArgumentException.class)
    public void addLowerTypeBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addLowerTypeBound("T", intType);

        //assert in annotation
    }

    @Test
    public void addLowerTypeBound_IsSubtypeOfExistingUpper_AddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = bindingCollection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerTypeBound_IsSameTypeAsExistingUpper_AddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = bindingCollection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test(expected = UpperBoundException.class)
    public void addLowerTypeBound_IsNotSameOrSubtypeOfExistingUpper_ThrowsLowerBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, intType);

        //act
        bindingCollection.addLowerTypeBound(typeVariable, numType);
        bindingCollection.getLowerTypeBounds(typeVariable);

        //assert in annotation
    }

    @Test
    public void addLowerTypeBound_IsSameAsExistingLower_ContainsTypeOnlyOnce() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = bindingCollection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerTypeBound_IsSubtypeOfExistingLower_RemainsExisting() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound(typeVariable, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = bindingCollection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerTypeBound_IsParentTypeOfExistingLower_IsNewLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(typeVariable, numType);
        IUnionTypeSymbol result = bindingCollection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerTypeBound_IsOutOfTypeHierarchyOfExistingLower_ContainsBoth() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(typeVariable, boolType);
        IUnionTypeSymbol result = bindingCollection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int", "bool"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-482 upper bound with convertible type which points to lower bound
    @Test
    public void addLowerTypeBound_ConvertibleTypeWhichPointsToItself_DoesNotAddTheConvertibleType() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        IConvertibleTypeSymbol asTlhs = symbolFactory.createConvertibleTypeSymbol();
        asTlhs.bindTo(bindingCollection, asList(tLhs));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tLhs, asTlhs);

        assertThat(bindingCollection.getLowerTypeBounds(tLhs), is(nullValue()));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerTypeBound_ConvertibleTypeWithRefWhichIsAlreadyUpper_DoesNotPropagateTheTypeUpwards() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(tRhs));
        IConvertibleTypeSymbol asTlhs = symbolFactory.createConvertibleTypeSymbol();
        asTlhs.bindTo(bindingCollection, asList(tLhs));
        bindingCollection.addLowerRefBound(tLhs, new TypeVariableReference(tRhs));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tRhs, asTlhs);

        assertThat(bindingCollection.getLowerTypeBounds(tLhs), is(nullValue()));
        assertThat(bindingCollection.getLowerTypeBounds(tRhs).getTypeSymbols().keySet(),
                containsInAnyOrder("{as Tlhs}"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerTypeBound_AddIntAndUpperIsFloatAndIntToFloatIsImplicit_UpperIsIntAndProviderIsInt() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addUpperTypeBound(tLhs, floatType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tLhs, intType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(intType));
    }

    @Test(expected = LowerBoundException.class)
    public void addLowerTypeBound_AddIntAndLowerIsFloatAndUpperIsFloatAndIntToFloatIsImplicit_ThrowsBoundException() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, floatType);
        bindingCollection.addUpperTypeBound(tLhs, floatType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tLhs, intType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(intType));
    }

    @Test
    public void addLowerTypeBound_AddIntAndUpperIsFloatAndIntToFloatIsImplAndHasLowerRef_UpperOfLowerIsUpdatedAsWell() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, floatType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tx, intType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("int", "@" + ty), asList("int"), false),
                varBinding("$y", ty, null, asList("int", "@" + tx), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(intType));
    }

    @Test
    public void
    addLowerTypeBound_AddIntAndUpperIsFloatAndIntToFloatIsImplAndHasLowerRefWithDiffUpper_DiffUpperIsNotLost() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, floatType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(ty, interfaceAType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tx, intType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("int", "@" + ty), asList("int"), false),
                varBinding("$y", ty, null, asList("int", "IA", "@" + tx), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(intType));
    }

    @Test(expected = LowerBoundException.class)
    public void
    addLowerTypeBound_AddBoolAndUpperIsNumAndBoolToNumIsImplAndHasLowerRefWithLowerInt_ThrowsLowerBoundException() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(boolType, asList(numType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(ty, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tx, boolType);

        //assert in annotation
    }

    @Test
    public void addLowerTypeBound_AddIntAndUpperIsIBAndNumToIBIsImpl_UpperIsNumAndProviderIsNum() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(numType, asList(interfaceBType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addUpperTypeBound(tLhs, interfaceBType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tLhs, intType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(numType));
    }

    @Test
    public void
    addLowerTypeBound_AddIntAndUpperIsAsIBAndIAAndIntToIBIsExplAndNumToIAIsImpl_UpperIsNumAndAsIBProviderIsNum() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(numType, asList(interfaceAType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(intType, asList(interfaceBType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);
        IConvertibleTypeSymbol asIB = createConvertibleTypeSymbol(interfaceBType, symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addUpperTypeBound(tLhs, interfaceAType);
        bindingCollection.addUpperTypeBound(tLhs, asIB);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tLhs, intType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("num", "{as IB}"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(numType));
    }

    @Test
    public void
    addLowerTypeBound_AddBoolAndUpperIsAsFloatAndBoolToIntIsExplAndIntToFloatImpl_LowerIsBoolAndImplicitWasUsed() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(boolType, asList(intType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol(
                floatType, symbolFactory, typeHelper);
        bindingCollection.addUpperTypeBound(tLhs, convertibleTypeSymbol);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tLhs, boolType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("bool"), asList("{as float}"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(nullValue()));
    }

    @Test
    public void
    addLowerTypeBound_AddBoolAndUpperIsAsIBAndBoolToIntIsExplAndIntToFooIsImpl_LowerIsBoolAndImplicitWasUsed() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(fooType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(boolType, asList(intType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol(
                interfaceBType, symbolFactory, typeHelper);
        bindingCollection.addUpperTypeBound(tLhs, convertibleTypeSymbol);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerTypeBound(tLhs, boolType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("bool"), asList("{as IB}"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addUpperTypeBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addUpperTypeBound("T", intType);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsParentTypeOfExistingLower_AddsUpperBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, numType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addUpperTypeBound_IsSameTypeAsExistingLower_AddsUpperBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test(expected = LowerBoundException.class)
    public void addUpperTypeBound_IsNotSameOrParentTypeOfExistingLower_ThrowsLowerBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound(typeVariable, numType);

        //act
        bindingCollection.addUpperTypeBound(typeVariable, intType);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsSameTypeAsExistingUpper_ContainsTypeOnlyOnce() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addUpperTypeBound_IsSubtypeOfExistingUpper_IsNewUpperBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addUpperTypeBound_IsParentTypeOfExistingUpper_RemainsExistingBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, numType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addUpperTypeBound_IsOutOfTypeHierarchyAndBothCanBeUsed_ContainsBoth() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, interfaceAType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, interfaceBType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("IA", "IB"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addUpperTypeBound_IsOutOfHierarchyAndCannotBeUsedAndExistingCanBeUsed_ContainsBoth() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, interfaceAType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, boolType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("bool", "IA"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addUpperTypeBound_IsOutOfTypeHierarchyAndCanBeUsedAndExistingCannotBeUsed_ContainsBoth() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, interfaceAType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int", "IA"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test(expected = IntersectionBoundException.class)
    public void addUpperTypeBound_IsOutOfTypeHierarchyAndBothCannotBeUsed_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, intType);

        //act
        bindingCollection.addUpperTypeBound(typeVariable, floatType);
        bindingCollection.getUpperTypeBounds(typeVariable);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsSubtypeOfOneOfTheTypesInTheUpperBound_NarrowsUpperBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String typeVariable = "T";
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound(typeVariable, interfaceAType);
        bindingCollection.addUpperTypeBound(typeVariable, interfaceBType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(typeVariable, interfaceSubAType);
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("ISubA", "IB"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-482 upper bound with convertible type which points to lower bound
    @Test
    public void addUpperTypeBound_ConvertibleTypeWithRefWhichIsAlreadyLower_DoesNotPropagateTheTypeDownwards() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(tRhs));
        IConvertibleTypeSymbol asTrhs = symbolFactory.createConvertibleTypeSymbol();
        asTrhs.bindTo(bindingCollection, asList(tRhs));
        bindingCollection.addLowerRefBound(tLhs, new TypeVariableReference(tRhs));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, asTrhs);

        assertThat(bindingCollection.getUpperTypeBounds(tRhs), is(nullValue()));
        assertThat(bindingCollection.getUpperTypeBounds(tLhs).getTypeSymbols().keySet(),
                containsInAnyOrder("{as Trhs}"));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-482 upper bound with convertible type which points to lower bound
    @Test
    public void addUpperTypeBound_ConvertibleTypeWhichPointsToItself_DoesNotAddTheConvertibleType() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        IConvertibleTypeSymbol asTlhs = symbolFactory.createConvertibleTypeSymbol();
        asTlhs.bindTo(bindingCollection, asList(tLhs));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, asTlhs);

        assertThat(bindingCollection.getUpperTypeBounds(tLhs), is(nullValue()));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-525 using implicit needs to restrict upper bound as well
    @Test
    public void addUpperTypeBound_AddFloatAndLowerIsIntAndIntToFloatIsImplicit_UpperIsIntAndProviderIsInt() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, floatType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(intType));
    }

    //see TINS-525 using implicit needs to restrict upper bound as well
    @Test
    public void
    addUpperTypeBound_AddFloatAndLowerAsWellAsUpperIsIntAndIntToFloatIsImplicit_UpperIsIntAndProviderIsInt() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, intType);
        bindingCollection.addUpperTypeBound(tLhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, floatType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(intType));
    }

    //see TINS-525 using implicit needs to restrict upper bound as well
    @Test
    public void addUpperTypeBound_AddFloatAndLowerIsIntAndUpperIsNumAndIntToFloatIsImpl_UpperIsIntAndProviderIsInt() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, intType);
        bindingCollection.addUpperTypeBound(tLhs, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, floatType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(intType));
    }

    //see TINS-525 using implicit needs to restrict upper bound as well
    @Test
    public void addUpperTypeBound_AddIBAndLowerIsIntAndNumToIBIsImpl_UpperIsNumAndNumIsProvider() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(numType, asList(interfaceBType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, interfaceBType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(numType));
    }

    //see TINS-525 using implicit needs to restrict upper bound as well
    @Test
    public void
    addUpperTypeBound_AddIBAndLowerIsIntAndUpperIsIntAndNumToIBIsImplicit_UpperIsIntAndProviderIsNum() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(numType, asList(interfaceBType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, intType);
        bindingCollection.addUpperTypeBound(tLhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, interfaceBType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(numType));
    }

    //see TINS-525 using implicit needs to restrict upper bound as well
    @Test
    public void
    addUpperTypeBound_AddIBAndLowerIsIntAndUpperIsScalarAndNumToIBIsImplicit_UpperIsNumAndProviderIsNum() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(numType, asList(interfaceBType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, intType);
        bindingCollection.addUpperTypeBound(tLhs, scalarType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, interfaceBType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("int"), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
        assertThat(resultDto.implicitConversionProvider, is(numType));
    }

    @Test
    public void
    addUpperTypeBound_AddAsFloatAndLowerIsBoolAndBoolToIntIsExplIntToFloatIsImpl_UpperIsAsFloatAndImplicitWasUsed() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(boolType, asList(intType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, boolType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol(floatType, symbolFactory,
                typeHelper);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, convertibleTypeSymbol);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("bool"), asList("{as float}"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
    }

    @Test
    public void
    addUpperTypeBound_AddAsIBAndLowerIsBoolAndBoolToIntIsExplAndIntToFooIsImpl_UpperIsAsIBAndImplicitWasUsed() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(fooType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(boolType, asList(intType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addLowerTypeBound(tLhs, boolType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol(
                interfaceBType, symbolFactory, typeHelper);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, convertibleTypeSymbol);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, asList("bool"), asList("{as IB}"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
    }

    //TINS-526 bool and arithmetic operators
    @Test
    public void
    addUpperTypeBound_AddAsTWhereTLowerStringAndLowerIsIntOrFloatAndIntAsWellAsFloatHaveExplToString_LowerConstraintContainsString() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(intType, asList(stringType)), pair(floatType, asList(stringType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tx, createUnionTypeSymbol(intType, floatType));
        bindingCollection.addUpperTypeBound(ty, stringType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(convertibleTypeSymbol, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, convertibleTypeSymbol);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("int", "float"), asList("{as " + ty + "}"), false),
                varBinding("$y", ty, null, asList("string"), false)
        ));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(ty, set("string"))));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void
    addUpperTypeBound_AddAsTWhereTLowerIsIntAndTUpperIsNumAndLowerIsFloat_LowerConstraintIsFloat() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tx, floatType);
        bindingCollection.addLowerTypeBound(ty, intType);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("float"), asList("{as " + ty + "}"), false),
                varBinding("$y", ty, asList("int"), asList("num"), false)
        ));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(ty, set("float"))));
        assertThat(resultDto.hasChanged, is(true));
    }

    @Test
    public void addUpperTypeBound_AddAsTyWhereTyLowerIsFloatAndTyUpperIsNumAndLowerIsInt_LowerConstraintIsInt() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();

        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addLowerTypeBound(ty, floatType);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("int"), asList("{as " + ty + "}"), false),
                varBinding("$y", ty, asList("float"), asList("num"), false)
        ));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(ty, set("int"))));
        assertThat(resultDto.hasChanged, is(true));
    }

    @Test
    public void
    addUpperTypeBound_AddAsTWhereTLowerIsFloatAndTUpperIsNumAndLowerIsString_LowerConstraintsContainIntAndFloat() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(stringType, asList(intType, floatType)));

        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tx, stringType);
        bindingCollection.addLowerTypeBound(ty, floatType);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("string"), asList("{as " + ty + "}"), false),
                varBinding("$y", ty, asList("float"), asList("num"), false)
        ));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(ty, set("float", "int"))));
        assertThat(resultDto.hasChanged, is(true));
    }

    @Test
    public void
    addUpperTypeBound_AddAsTWhereTLowerIsIntAndTUpperIsNumAndLowerIsString_LowerConstraintsContainIntAndFloat() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(stringType, asList(intType, floatType)));

        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tx, stringType);
        bindingCollection.addLowerTypeBound(ty, intType);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("string"), asList("{as " + ty + "}"), false),
                varBinding("$y", ty, asList("int"), asList("num"), false)
        ));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(ty, set("int", "float"))));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-526 bool and arithmetic operators
    @Test
    public void
    addUpperTypeBound_AddTwiceAsTWhereTLowerStringAndLowerIsIntOrFloatAndIntAsWellAsFloatHaveExplToString_LowerConstraintsContainString() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(intType, asList(stringType)), pair(floatType, asList(stringType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        String tz = "Tz";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addVariable("$z", new TypeVariableReference(tz));
        bindingCollection.addLowerTypeBound(tx, createUnionTypeSymbol(intType, floatType));
        bindingCollection.addUpperTypeBound(ty, stringType);
        bindingCollection.addUpperTypeBound(tz, stringType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));
        IConvertibleTypeSymbol asTz = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTz, asList(tz));

        //act
        bindingCollection.addUpperTypeBound(tx, asTy);
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTz);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("int", "float"), asList("{as " + tz + "}"), false),
                varBinding("$y", ty, null, asList("string", "@Tz"), false),
                varBinding("$z", tz, asList("@Ty"), asList("string"), false)
        ));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(tz, set("string"))));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }


    //see TINS-590 create lower ref for convertible is erroneous
    @Test
    public void addUpperTypeBound_AddAsTyAndTyIsLowerNumAndCurrentUpperIsNum_AddsLowerRef() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("num", "@" + ty), false),
                varBinding("$y", ty, asList("@" + tx), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-590 create lower ref for convertible is erroneous
    @Test
    public void addUpperTypeBound_AddAsTyAndTyIsLowerNumAndCurrentUpperIsInt_AddsLowerRef() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("int", "@" + ty), false),
                varBinding("$y", ty, asList("@" + tx), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-590 create lower ref for convertible is erroneous
    @Test
    public void addUpperTypeBound_AddAsTyAndTyIsLowerNumAndCurrentUpperIsFloat_AddsLowerRef() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, floatType);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("float", "@" + ty), false),
                varBinding("$y", ty, asList("@" + tx), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-590 create lower ref for convertible is erroneous
    @Test
    public void addUpperTypeBound_AddAsTyAndTyIsLowerIntOrFloatAndCurrentUpperIsString_TyHasLowerConstraints() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(stringType, asList(intType, floatType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(ty, createUnionTypeSymbol(intType, floatType));
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("string"), false),
                varBinding("$y", ty, null, asList("(float | int)"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
        assertThat(resultDto.lowerConstraints, is(isConstraints(pair("Ty", set("int", "float")))));
        assertThat(resultDto.upperConstraints, is(nullValue()));
    }

    //see TINS-590 create lower ref for convertible is erroneous
    @Test
    public void addUpperTypeBound_AddAsTyAndTyIsLowerIntOrFloatAndCurrentLowerIsString_TyHasLowerConstraints() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions = new HashMap<>();
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions
                = createConversions(pair(stringType, asList(intType, floatType)));
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);
        ISymbolFactory symbolFactory = createSymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(ty, createUnionTypeSymbol(intType, floatType));
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("string"), asList("{as " + ty + "}"), false),
                varBinding("$y", ty, null, asList("(float | int)"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
        assertThat(resultDto.lowerConstraints, is(isConstraints(pair("Ty", set("int", "float")))));
        assertThat(resultDto.upperConstraints, is(nullValue()));
    }

    //see TINS-627 multiple convertible types for same parameter
    @Test
    public void addUpperTypeBound_AddAsT2AndT2IsLowerNumAndCurrentUpperIsAsT1AndT1IsLowerNum_T1IsLowerT2LowerNum() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addVariable("!help0", new TypeVariableReference(t2));
        bindingCollection.addUpperTypeBound(t1, numType);
        bindingCollection.addUpperTypeBound(t2, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        IConvertibleTypeSymbol asT2 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT2, asList(t2));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asT2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T2}"), false),
                varBinding("+@1|2", t1, null, asList("num", "@T2"), false),
                varBinding("!help0", t2, asList("@T1"), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-627 multiple convertible types for same parameter
    @Test
    public void addUpperTypeBound_AddAsT2AndT2IsLowerIntAndCurrentUpperIsAsT1AndT1IsLowerNum_T2IsLowerT1LowerInt() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addVariable("!help0", new TypeVariableReference(t2));
        bindingCollection.addUpperTypeBound(t1, numType);
        bindingCollection.addUpperTypeBound(t2, intType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        IConvertibleTypeSymbol asT2 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT2, asList(t2));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asT2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T2}"), false),
                varBinding("+@1|2", t1, null, asList("int", "@T2"), false),
                varBinding("!help0", t2, asList("@T1"), asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-627 multiple convertible types for same parameter
    @Test
    public void addUpperTypeBound_AddAsT2AndT2IsLowerNumAndCurrentUpperIsAsT1AndT1IsLowerInt_T1IsLowerT2AndLowerInt() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addVariable("!help0", new TypeVariableReference(t2));
        bindingCollection.addUpperTypeBound(t1, intType);
        bindingCollection.addUpperTypeBound(t2, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        IConvertibleTypeSymbol asT2 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT2, asList(t2));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asT2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T1}"), false),
                varBinding("+@1|2", t1, null, asList("int", "@T2"), false),
                varBinding("!help0", t2, asList("@T1"), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-627 multiple convertible types for same parameter
    @Test
    public void addUpperTypeBound_AddAsNumAndCurrentUpperIsAsT1AndT1IsLowerNum_RemainsAsT1() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t1, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        IConvertibleTypeSymbol asNum = createConvertibleTypeSymbol(numType, symbolFactory, typeHelper);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asNum);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T1}"), false),
                varBinding("+@1|2", t1, null, asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
    }

    //see TINS-627 multiple convertible types for same parameter
    @Test
    public void addUpperTypeBound_AddAsIntAndCurrentUpperIsAsT1AndT1IsLowerNum_RemainsAsT1AndT1IsLowerInt() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t1, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        IConvertibleTypeSymbol asInt = createConvertibleTypeSymbol(intType, symbolFactory, typeHelper);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asInt);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T1}"), false),
                varBinding("+@1|2", t1, null, asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-630 multiple convertible types in intersection
    @Test
    public void addUpperTypeBound_AddAsNumAndCurrentUpperIsAsT1AndAsStringWhereT1IsLowerNum_RemainsAsT1() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t1, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        bindingCollection.addUpperTypeBound(tx, createConvertibleTypeSymbol(stringType, symbolFactory, typeHelper));
        IConvertibleTypeSymbol asNum = createConvertibleTypeSymbol(numType, symbolFactory, typeHelper);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asNum);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T1}", "{as string}"), false),
                varBinding("+@1|2", t1, null, asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
    }

    //see TINS-630 multiple convertible types in intersection
    @Test
    public void
    addUpperTypeBound_AddAsIntAndCurrentUpperIsAsT1AndAsStringWhereT1IsLowerNum_RemainsAsT1AndT1IsLowerInt() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t1, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        bindingCollection.addUpperTypeBound(tx, createConvertibleTypeSymbol(stringType, symbolFactory, typeHelper));
        IConvertibleTypeSymbol asInt = createConvertibleTypeSymbol(intType, symbolFactory, typeHelper);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asInt);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T1}", "{as string}"), false),
                varBinding("+@1|2", t1, null, asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    @Test
    public void
    addUpperTypeBound_AddAsNumAndCurrentUpperIsAsT1AndAsT2WhereT1IsLowerNumAndT2LowerString_RemainsAsT1AsT2() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addVariable(".@1|2", new TypeVariableReference(t2));
        bindingCollection.addUpperTypeBound(t1, numType);
        bindingCollection.addUpperTypeBound(t2, stringType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        IConvertibleTypeSymbol asT2 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT2, asList(t2));
        bindingCollection.addUpperTypeBound(tx, asT2);
        IConvertibleTypeSymbol asNum = createConvertibleTypeSymbol(numType, symbolFactory, typeHelper);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asNum);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T1}", "{as T2}"), false),
                varBinding("+@1|2", t1, null, asList("num"), false),
                varBinding(".@1|2", t2, null, asList("string"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
    }

    @Test
    public void
    addUpperTypeBound_AddAsIntAndCurrentUpperIsAsT1AndAsT2WhereT1IsLowerNumAndT2LowerString_RemainsAsT1AsT2() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addVariable(".@1|2", new TypeVariableReference(t2));
        bindingCollection.addUpperTypeBound(t1, numType);
        bindingCollection.addUpperTypeBound(t2, stringType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        IConvertibleTypeSymbol asT2 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT2, asList(t2));
        bindingCollection.addUpperTypeBound(tx, asT2);
        IConvertibleTypeSymbol asInt = createConvertibleTypeSymbol(intType, symbolFactory, typeHelper);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asInt);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T1}", "{as T2}"), false),
                varBinding("+@1|2", t1, null, asList("int"), false),
                varBinding(".@1|2", t2, null, asList("string"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    @Test
    public void
    addUpperTypeBound_AddAsT3AndAsStringWhereCurrentUpperIsAsT1AndAsT2WhereT1LowNumT2LowStringT3LowNum_EstablishRelation() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addVariable(".@1|2", new TypeVariableReference(t2));
        bindingCollection.addVariable("+@3|0", new TypeVariableReference(t3));
        bindingCollection.addUpperTypeBound(t1, numType);
        bindingCollection.addUpperTypeBound(t2, stringType);
        bindingCollection.addUpperTypeBound(t3, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, asT1);
        IConvertibleTypeSymbol asT2 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT2, asList(t2));
        bindingCollection.addUpperTypeBound(tx, asT2);
        IConvertibleTypeSymbol asT3 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT3, asList(t3));
        IConvertibleTypeSymbol asString = createConvertibleTypeSymbol(stringType, symbolFactory, typeHelper);
        IIntersectionTypeSymbol asT3AndAsString = createIntersectionTypeSymbol(asT3, asString);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asT3AndAsString);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("{as T3}", "{as T2}"), false),
                varBinding("+@1|2", t1, null, asList("num"), false),
                varBinding(".@1|2", t2, null, asList("string"), false),
                varBinding("+@3|0", t3, null, asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //TODO TINS-632 - handle convertible type in upper bound
//    @Test
//    public void
//    addUpperTypeBound_AddAsT3AndAsISubAWhereCurrentUpperIsAsT1AndAsT2WhereT1LowNumT2LowIAT3LowInt_EstablishRelation
// () {
//        //pre-act necessary for arrange
//        IBindingCollection bindingCollection = createBindingCollection();
//
//        //arrange
//        String tx = "Tx";
//        String t1 = "T1";
//        String t2 = "T2";
//        String t3 = "T3";
//        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
//        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
//        bindingCollection.addVariable(".@1|2", new TypeVariableReference(t2));
//        bindingCollection.addVariable("+@3|0", new TypeVariableReference(t3));
//        bindingCollection.addUpperTypeBound(t1, numType);
//        bindingCollection.addUpperTypeBound(t2, interfaceAType);
//        bindingCollection.addUpperTypeBound(t3, intType);
//        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
//        bindingCollection.bind(asT1, asList(t1));
//        bindingCollection.addUpperTypeBound(tx, asT1);
//        IConvertibleTypeSymbol asT2 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
//        bindingCollection.bind(asT2, asList(t2));
//        bindingCollection.addUpperTypeBound(tx, asT2);
//        IConvertibleTypeSymbol asT3 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
//        bindingCollection.bind(asT3, asList(t3));
//        IConvertibleTypeSymbol asISubA = createConvertibleTypeSymbol(interfaceSubAType, symbolFactory, typeHelper);
//        IIntersectionTypeSymbol asT3AndAsISubA = createIntersectionTypeSymbol(asT3, asISubA);
//
//        //act
//        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asT3AndAsISubA);
//
//        assertThat(bindingCollection, withVariableBindings(
//                varBinding("$x", tx, null, asList("{as T3}", "{as T2}"), false),
//                varBinding("+@1|2", t1, null, asList("num"), false),
//                varBinding(".@1|2", t2, null, asList("ISubA"), false),
//                varBinding("+@3|0", t3, null, asList("int"), false)
//        ));
//        assertThat(resultDto.hasChanged, is(true));
//    }

    @Test
    public void
    addUpperTypeBound_AddAsT1WhereT1IsLowerNumAndCurrentUpperIsNum_EstablishRelation() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t1, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(tx, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asT1);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("num", "@T1"), false),
                varBinding("+@1|2", t1, asList("@Tx"), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    @Test
    public void
    addUpperTypeBound_AddAsT1WhereIntIsLowerT1IsLowerNumAndCurrentUpperIsNum_EstablishRelation() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tx = "Tx";
        String t1 = "T1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t1, numType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(tx, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asT1);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("int"), asList("num", "@T1"), false),
                varBinding("+@1|2", t1, asList("int", "@Tx"), asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    @Test(expected = IntersectionBoundException.class)
    public void addUpperTypeBound_ContainsFinalIsNotInSameTypeHierarchy_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.isFinal()).thenReturn(true);
        when(typeSymbol.getAbsoluteName()).thenReturn("A");
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addUpperTypeBound(tLhs, typeSymbol);


        //act
        bindingCollection.addUpperTypeBound(tLhs, intType);

        //assert in annotation
    }

    @Test(expected = IntersectionBoundException.class)
    public void addUpperTypeBound_ContainsIBAndFinalAddedNotInSameTypeHierarchy_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addUpperTypeBound(tLhs, interfaceBType);
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.isFinal()).thenReturn(true);
        when(typeSymbol.getAbsoluteName()).thenReturn("A");


        //act
        bindingCollection.addUpperTypeBound(tLhs, typeSymbol);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_ContainsIBAndFinalAddedWhichIsSubtype_UpperBoundContainsFinal() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addUpperTypeBound(tLhs, interfaceBType);
        Set<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(interfaceBType);
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.isFinal()).thenReturn(true);
        when(typeSymbol.getAbsoluteName()).thenReturn("A");
        when(typeSymbol.getParentTypeSymbols()).thenReturn(parentTypes);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, typeSymbol);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, null, asList("A"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addUpperTypeBound_ContainsFinalAndParentTypeAdded_UpperBoundContainsFinal() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.isFinal()).thenReturn(true);
        when(typeSymbol.getAbsoluteName()).thenReturn("A");
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        ITypeSymbol typeSymbol2 = mock(ITypeSymbol.class);
        when(typeSymbol2.isFinal()).thenReturn(false);
        when(typeSymbol2.getAbsoluteName()).thenReturn("B");

        Set<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(typeSymbol2);
        when(typeSymbol.getParentTypeSymbols()).thenReturn(parentTypes);
        bindingCollection.addUpperTypeBound(tLhs, typeSymbol);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, typeSymbol2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, null, asList("A"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //See TINS-561 filter overloads with implicit only if others exist
    @Test
    public void addUpperTypeBound_ContainsIntAndFloatAdded_UpperRemainsInt() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addUpperTypeBound(tLhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, floatType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, null, asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(true));
    }

    //See TINS-586 coercive subtyping and upper bound constraints
    @Test
    public void addUpperTypeBound_ContainsFloatAndIntAdded_UpperIsIntAndHasChanged() {
        //pre-arrange
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(intType, asList(floatType)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tLhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addUpperTypeBound(tLhs, floatType);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tLhs, intType);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", tLhs, null, asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(true));
    }


    //see TINS-553 isFinal, container types and upper type bounds
    @Test(expected = IntersectionBoundException.class)
    public void addUpperTypeBound_AddFinalTypeAndCurrentIsFinalAlready_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType1 = mock(ITypeSymbol.class);
        when(finalType1.isFinal()).thenReturn(true);
        when(finalType1.getAbsoluteName()).thenReturn("dummy1");
        when(finalType1.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());
        ITypeSymbol finalType2 = mock(ITypeSymbol.class);
        when(finalType2.isFinal()).thenReturn(true);
        when(finalType2.getAbsoluteName()).thenReturn("dummy2");
        when(finalType2.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, createUnionTypeSymbol(finalType1, finalType1));


        //act
        bindingCollection.addUpperTypeBound(tx, finalType2);


        //assert in annotation
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test
    public void addUpperTypeBound_AddFinalTypeAndCurrentIsTheSameType_DoesNothingHasNotChanged() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType1 = mock(ITypeSymbol.class);
        when(finalType1.isFinal()).thenReturn(true);
        when(finalType1.getAbsoluteName()).thenReturn("dummy1");
        when(finalType1.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, createUnionTypeSymbol(finalType1, finalType1));


        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, finalType1);


        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("dummy1"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test(expected = IntersectionBoundException.class)
    public void addUpperTypeBound_AddIBAndCurrentAreTwoFinalTypesInUnion_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType1 = mock(ITypeSymbol.class);
        when(finalType1.isFinal()).thenReturn(true);
        when(finalType1.getAbsoluteName()).thenReturn("dummy1");
        when(finalType1.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());
        ITypeSymbol finalType2 = mock(ITypeSymbol.class);
        when(finalType2.isFinal()).thenReturn(true);
        when(finalType2.getAbsoluteName()).thenReturn("dummy2");
        when(finalType2.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, createUnionTypeSymbol(finalType1, finalType2));


        //act
        bindingCollection.addUpperTypeBound(tx, interfaceBType);


        //assert in annotation
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test(expected = IntersectionBoundException.class)
    public void addUpperTypeBound_AddTwoFinalTypesInUnionAndCurrentIsIB_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType1 = mock(ITypeSymbol.class);
        when(finalType1.isFinal()).thenReturn(true);
        when(finalType1.getAbsoluteName()).thenReturn("dummy1");
        when(finalType1.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());
        ITypeSymbol finalType2 = mock(ITypeSymbol.class);
        when(finalType2.isFinal()).thenReturn(true);
        when(finalType2.getAbsoluteName()).thenReturn("dummy2");
        when(finalType2.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, interfaceBType);


        //act
        bindingCollection.addUpperTypeBound(tx, createUnionTypeSymbol(finalType1, finalType2));


        //assert in annotation
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test(expected = IntersectionBoundException.class)
    public void
    addUpperTypeBound_AddTwoFinalTypesInUnionInIntersectionAndCurrentIsIB_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType1 = mock(ITypeSymbol.class);
        when(finalType1.isFinal()).thenReturn(true);
        when(finalType1.getAbsoluteName()).thenReturn("dummy1");
        when(finalType1.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());
        ITypeSymbol finalType2 = mock(ITypeSymbol.class);
        when(finalType2.isFinal()).thenReturn(true);
        when(finalType2.getAbsoluteName()).thenReturn("dummy2");
        when(finalType2.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, interfaceBType);
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(finalType1, finalType2);
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol(unionTypeSymbol);


        //act
        bindingCollection.addUpperTypeBound(tx, intersectionTypeSymbol);


        //assert in annotation
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test
    public void addUpperTypeBound_AddIBAndCurrentAreTwoFinalTypesInUnionOfWhichOneIsSubTypeOfIB_AddsIt() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType1 = mock(ITypeSymbol.class);
        when(finalType1.isFinal()).thenReturn(true);
        when(finalType1.getAbsoluteName()).thenReturn("dummy1");
        when(finalType1.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());
        ITypeSymbol finalType2 = mock(ITypeSymbol.class);
        when(finalType2.isFinal()).thenReturn(true);
        when(finalType2.getAbsoluteName()).thenReturn("dummy2");
        HashSet<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(interfaceBType);
        when(finalType2.getParentTypeSymbols()).thenReturn(parentTypes);

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, createUnionTypeSymbol(finalType1, finalType2));


        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, interfaceBType);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("(dummy1 | dummy2)", "IB"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test
    public void addUpperTypeBound_AddTwoFinalTypesInUnionOfWhichOneIsSubTypeOfIBAndCurrentIsIB_AddsThem() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType1 = mock(ITypeSymbol.class);
        when(finalType1.isFinal()).thenReturn(true);
        when(finalType1.getAbsoluteName()).thenReturn("dummy1");
        when(finalType1.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());
        ITypeSymbol finalType2 = mock(ITypeSymbol.class);
        when(finalType2.isFinal()).thenReturn(true);
        when(finalType2.getAbsoluteName()).thenReturn("dummy2");
        HashSet<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(interfaceBType);
        when(finalType2.getParentTypeSymbols()).thenReturn(parentTypes);

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, interfaceBType);
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(finalType1, finalType2);


        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, unionTypeSymbol);


        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("(dummy1 | dummy2)", "IB"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test
    public void
    addUpperTypeBound_AddTwoFinalTypesInUnionInIntersectionOfWhichOneIsSubTypeOfIBAndCurrentIsIB_AddsThem() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType1 = mock(ITypeSymbol.class);
        when(finalType1.isFinal()).thenReturn(true);
        when(finalType1.getAbsoluteName()).thenReturn("dummy1");
        when(finalType1.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());
        ITypeSymbol finalType2 = mock(ITypeSymbol.class);
        when(finalType2.isFinal()).thenReturn(true);
        when(finalType2.getAbsoluteName()).thenReturn("dummy2");
        HashSet<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(interfaceBType);
        when(finalType2.getParentTypeSymbols()).thenReturn(parentTypes);

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, interfaceBType);
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(finalType1, finalType2);
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol(unionTypeSymbol);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, intersectionTypeSymbol);


        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("(dummy1 | dummy2)", "IB"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test
    public void addUpperTypeBound_AddIBAndCurrentIsFinalOrFoo_UpperIsIntOrFooAndIB() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType = mock(ITypeSymbol.class);
        when(finalType.isFinal()).thenReturn(true);
        when(finalType.getAbsoluteName()).thenReturn("dummy");
        when(finalType.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, createUnionTypeSymbol(finalType, fooType));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, interfaceBType);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("(Foo | dummy)", "IB"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test
    public void addUpperTypeBound_AddFooOrFinalTypeAndCurrentIsIB_UpperIsIntOrFooAndIB() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType = mock(ITypeSymbol.class);
        when(finalType.isFinal()).thenReturn(true);
        when(finalType.getAbsoluteName()).thenReturn("dummy");
        when(finalType.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(finalType, fooType);
        bindingCollection.addUpperTypeBound(tx, interfaceBType);


        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, unionTypeSymbol);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("(Foo | dummy)", "IB"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-553 isFinal, container types and upper type bounds
    @Test
    public void addUpperTypeBound_AddFooOrFinalTypeInIntersectionAndCurrentIsIB_UpperIsIntOrFooAndIB() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        ITypeSymbol finalType = mock(ITypeSymbol.class);
        when(finalType.isFinal()).thenReturn(true);
        when(finalType.getAbsoluteName()).thenReturn("dummy");
        when(finalType.getParentTypeSymbols()).thenReturn(new HashSet<ITypeSymbol>());

        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, interfaceBType);
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(finalType, fooType);
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol(unionTypeSymbol);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, intersectionTypeSymbol);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("(Foo | dummy)", "IB"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-656 bound exception adding float as upper and current is int
    @Test
    public void addUpperTypeBound_AddFloatAndCurrentLowerAndUpperIsInt_DoesNotThrowABoundException() {
        //pre-arrange
        //intType from ATypeTest is not final, hence the own type
        ITypeSymbol finalInt = mock(ITypeSymbol.class);
        when(finalInt.isFinal()).thenReturn(true);
        when(finalInt.getAbsoluteName()).thenReturn("int");
        when(finalInt.getParentTypeSymbols()).thenReturn(set(mixedType));
        ITypeSymbol finalFloat = mock(ITypeSymbol.class);
        when(finalFloat.isFinal()).thenReturn(true);
        when(finalFloat.getAbsoluteName()).thenReturn("float");
        when(finalFloat.getParentTypeSymbols()).thenReturn(set(mixedType));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(finalInt, asList(finalFloat)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addLowerTypeBound(tx, finalInt);
        bindingCollection.addUpperTypeBound(tx, finalInt);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, finalFloat);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("int"), asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
    }

    //see TINS-656 bound exception adding float as upper and current is int
    @Test
    public void addUpperTypeBound_AddFloatAndCurrentUpperIsInt_DoesNotThrowABoundException() {
        //pre-arrange
        //intType from ATypeTest is not final, hence the own type
        ITypeSymbol finalInt = mock(ITypeSymbol.class);
        when(finalInt.isFinal()).thenReturn(true);
        when(finalInt.getAbsoluteName()).thenReturn("int");
        when(finalInt.getParentTypeSymbols()).thenReturn(set(mixedType));
        ITypeSymbol finalFloat = mock(ITypeSymbol.class);
        when(finalFloat.isFinal()).thenReturn(true);
        when(finalFloat.getAbsoluteName()).thenReturn("float");
        when(finalFloat.getParentTypeSymbols()).thenReturn(set(mixedType));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(finalInt, asList(finalFloat)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, finalInt);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, finalFloat);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
    }

    //see TINS-656 bound exception adding float as upper and current is int
    @Test
    public void addUpperTypeBound_AddIntAndCurrentUpperIsFloat_DoesNotThrowABoundException() {
        //pre-arrange
        //intType from ATypeTest is not final, hence the own type
        ITypeSymbol finalInt = mock(ITypeSymbol.class);
        when(finalInt.isFinal()).thenReturn(true);
        when(finalInt.getAbsoluteName()).thenReturn("int");
        when(finalInt.getParentTypeSymbols()).thenReturn(set(mixedType));
        ITypeSymbol finalFloat = mock(ITypeSymbol.class);
        when(finalFloat.isFinal()).thenReturn(true);
        when(finalFloat.getAbsoluteName()).thenReturn("float");
        when(finalFloat.getParentTypeSymbols()).thenReturn(set(mixedType));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions
                = createConversions(pair(finalInt, asList(finalFloat)));
        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions = new HashMap<>();
        ITypeHelper typeHelper = createTypeHelperAndInit(implicitConversions, explicitConversions);

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection(symbolFactory, typeHelper);

        //arrange
        String tx = "Tx";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, finalFloat);

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, finalInt);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, null, asList("int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
    }

    //see TINS-652 soft typing and convertible types
    @Test
    public void addUpperTypeBound_AddAsTWhereTLowerNumAndLowerIsAsNum_LowerConstraintIsNum() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();
        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        IConvertibleTypeSymbol asNum = createConvertibleTypeSymbol(numType, symbolFactory, typeHelper);
        bindingCollection.addLowerTypeBound(tx, asNum);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol();
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("{as num}"), asList("{as Ty}"), false),
                varBinding("$y", ty, null, asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(ty, set("num"))));
    }

    //see TINS-652 soft typing and convertible types
    @Test
    public void addUpperTypeBound_AddAsTWhereTLowerIntOrFloatAndLowerIsAsIntOrFloat_LowerConstraintIsIntOrFloat() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();
        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        IUnionTypeSymbol intOrFloat = createUnionTypeSymbol(intType, floatType);
        IConvertibleTypeSymbol asIntOrFloat = createConvertibleTypeSymbol(intOrFloat, symbolFactory, typeHelper);
        bindingCollection.addLowerTypeBound(tx, asIntOrFloat);
        bindingCollection.addUpperTypeBound(ty, intOrFloat);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol();
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("{as (float | int)}"), asList("{as Ty}"), false),
                varBinding("$y", ty, null, asList("(float | int)"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(ty, set("(float | int)"))));
    }

    //see TINS-652 soft typing and convertible types
    @Test
    public void addUpperTypeBound_AddAsTWhereTLowerNumAndLowerIsAsInt_LowerConstraintIsInt() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();
        //arrange
        String tx = "Tx";
        String ty = "Ty";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        IConvertibleTypeSymbol asInt = createConvertibleTypeSymbol(intType, symbolFactory, typeHelper);
        bindingCollection.addLowerTypeBound(tx, asInt);
        bindingCollection.addUpperTypeBound(ty, numType);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol();
        bindingCollection.bind(asTy, asList(ty));

        //act
        BoundResultDto resultDto = bindingCollection.addUpperTypeBound(tx, asTy);

        //assert
        assertThat(bindingCollection, withVariableBindings(
                varBinding("$x", tx, asList("{as int}"), asList("{as Ty}"), false),
                varBinding("$y", ty, null, asList("num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.lowerConstraints, isConstraints(pair(ty, set("int"))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLowerRefBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addLowerRefBound("T", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLowerRefBound_NonExistingRef_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerRefBound("T", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test
    public void addLowerRefBound_HasNoBounds_AddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), null, false),
                varBinding("$rhs", rhs, null, asList("@Tlhs"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefHasNoBounds_NarrowsRefAndAddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addUpperTypeBound(lhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsSame_AddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addUpperTypeBound(lhs, intType);
        bindingCollection.addUpperTypeBound(rhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsSubtype_RefsUpperStaysTheSameAndAddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addUpperTypeBound(lhs, numType);
        bindingCollection.addUpperTypeBound(rhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("num"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsParentType_NarrowRefAndAddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addUpperTypeBound(lhs, intType);
        bindingCollection.addUpperTypeBound(rhs, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsSame_NarrowRefAndLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addUpperTypeBound(lhs, intType);
        bindingCollection.addLowerTypeBound(rhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs", "int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsSubtype_NarrowRefAndLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addUpperTypeBound(lhs, numType);
        bindingCollection.addLowerTypeBound(rhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), asList("num"), false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs", "num"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test(expected = BoundException.class)
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsParentType_ThrowsBoundException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addUpperTypeBound(lhs, intType);
        bindingCollection.addLowerTypeBound(rhs, numType);

        //act
        bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        //assert in annotation
    }

    @Test
    public void addLowerRefBound_LhsHasNoBoundAndRefHasLower_NarrowsLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addLowerTypeBound(rhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsSameType_AddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addLowerTypeBound(lhs, intType);
        bindingCollection.addLowerTypeBound(rhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));


        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsSubtype_DoesNotChangeLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addLowerTypeBound(lhs, numType);
        bindingCollection.addLowerTypeBound(rhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("num", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsParentType_NarrowsLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addLowerTypeBound(lhs, intType);
        bindingCollection.addLowerTypeBound(rhs, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("num", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("num"), asList("@Tlhs"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_HasFixedTypeAndLowerBound_TransfersLowerTypeBoundButDoesNotAddLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addLowerTypeBound(rhs, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new FixedTypeVariableReference(new
                TypeVariableReference
                (rhs)));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("num"), null, false),
                varBinding("$rhs", rhs, asList("num"), null, false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }


    @Test
    public void addLowerRefBound_SelfRefWithoutBounds_AddsLowerBound() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_SelfRefWithUpperBound_AddsLowerBoundAndAddUpperBoundWasNotCalled() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = spy(createBindingCollection());

        //arrange
        String lhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addUpperTypeBound(lhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs", "int"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
        try {
            verify(bindingCollection, times(2)).addUpperTypeBound(anyString(), any(ITypeSymbol.class));
            Assert.fail("addUpperTypeBound was called but should not have been.");
        } catch (MockitoAssertionError ex) {
            //that's fine
        }
    }

    @Test
    public void addLowerRefBound_SelfRefWithLowerBound_AddsLowerBoundAndAddLowerBoundWasNotCalled() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = spy(createBindingCollection());

        //arrange
        String lhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addLowerTypeBound(lhs, intType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs", "int"), asList("@Tlhs"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
        try {
            verify(bindingCollection, times(2)).addLowerTypeBound(anyString(), any(ITypeSymbol.class));
            Assert.fail("addUpperTypeBound was called but should not have been.");
        } catch (MockitoAssertionError ex) {
            //that's fine
        }
    }

    @Test
    public void addLowerRefBound_SelfRefWithUpperBoundAndAlreadySelfRef_NoEndlessLoopDependencyOnlyOnes() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = spy(createBindingCollection());

        //arrange
        String lhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addUpperTypeBound(lhs, intType);
        bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs", "int"), false)
        ));
        assertThat(resultDto.hasChanged, is(false));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_BidirectionalRefWithBoundsButAlsoOtherRefs_PropagatesBoundsAccordingly() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$a", new TypeVariableReference(t1));
        bindingCollection.addVariable("$b", new TypeVariableReference(t2));
        bindingCollection.addVariable("$c", new TypeVariableReference(t3));
        bindingCollection.addLowerRefBound(t2, new TypeVariableReference(t1));
        bindingCollection.addLowerTypeBound(t2, intType);
        bindingCollection.addUpperTypeBound(t3, numType);
        bindingCollection.addLowerRefBound(t2, new TypeVariableReference(t3));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(t1, new TypeVariableReference(t2));


        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2"), asList("@T2"), false),
                varBinding("$b", "T2", asList("int", "@T3", "@T1"), asList("@T1"), false),
                varBinding("$c", "T3", null, asList("num", "@T2"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_CircularRefWithoutBounds_AddRefsAccordingly() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$a", new TypeVariableReference(t1));
        bindingCollection.addVariable("$b", new TypeVariableReference(t2));
        bindingCollection.addVariable("$c", new TypeVariableReference(t3));
        bindingCollection.addLowerRefBound(t2, new TypeVariableReference(t3));
        bindingCollection.addLowerRefBound(t3, new TypeVariableReference(t1));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(t1, new TypeVariableReference(t2));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", "T1", asList("@T2"), asList("@T3"), false),
                varBinding("$b", "T2", asList("@T3"), asList("@T1"), false),
                varBinding("$c", "T3", asList("@T1"), asList("@T2"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }


    @Test
    public void addLowerRefBound_CircularRefWithBounds_PropagatesTypesAccordingly() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$a", new TypeVariableReference(t1));
        bindingCollection.addVariable("$b", new TypeVariableReference(t2));
        bindingCollection.addVariable("$c", new TypeVariableReference(t3));
        bindingCollection.addLowerRefBound(t2, new TypeVariableReference(t3));
        bindingCollection.addLowerTypeBound(t2, intType);
        bindingCollection.addLowerRefBound(t3, new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t2, numType);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(t1, new TypeVariableReference(t2));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2"), asList("num", "@T3"), false),
                varBinding("$b", "T2", asList("int", "@T3"), asList("num", "@T1"), false),
                varBinding("$c", "T3", asList("int", "@T1"), asList("num", "@T2"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    @Test
    public void addLowerRefBound_CircularRefWithBoundsButAlsoOtherRefs_PropagatesBoundsAccordingly() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        String t4 = "T4";
        String t5 = "T5";
        bindingCollection.addVariable("$a", new TypeVariableReference(t1));
        bindingCollection.addVariable("$b", new TypeVariableReference(t2));
        bindingCollection.addVariable("$c", new TypeVariableReference(t3));
        bindingCollection.addVariable("$d", new TypeVariableReference(t4));
        bindingCollection.addVariable("$e", new TypeVariableReference(t5));
        bindingCollection.addLowerRefBound(t2, new TypeVariableReference(t3));
        bindingCollection.addLowerTypeBound(t2, intType);
        bindingCollection.addLowerRefBound(t3, new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t3, numType);
        bindingCollection.addLowerRefBound(t1, new TypeVariableReference(t4));
        bindingCollection.addLowerRefBound(t3, new TypeVariableReference(t5));

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(t1, new TypeVariableReference(t2));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2", "@T4"), asList("num", "@T3"), false),
                varBinding("$b", "T2", asList("int", "@T3"), asList("num", "@T1"), false),
                varBinding("$c", "T3", asList("int", "@T1", "@T5"), asList("num", "@T2"), false),
                varBinding("$d", "T4", null, asList("num", "@T1"), false),
                varBinding("$e", "T5", null, asList("num", "@T3"), false)
        ));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    //see TINS-482 upper bound with convertible type which points to lower bound
    @Test
    public void addLowerRefBound_HasAlreadyConvertibleWithSameRefAsUpper_DoesNotPropagateConvertible() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(tRhs));
        IConvertibleTypeSymbol asTrhs = symbolFactory.createConvertibleTypeSymbol();
        asTrhs.bindTo(bindingCollection, asList(tRhs));
        bindingCollection.addUpperTypeBound(tLhs, asTrhs);

        //act
        BoundResultDto resultDto = bindingCollection.addLowerRefBound(tLhs, new TypeVariableReference(tRhs));
        IIntersectionTypeSymbol result = bindingCollection.getUpperTypeBounds(tRhs);

        assertThat(result, is(nullValue()));
        assertThat(bindingCollection.getLowerRefBounds(tLhs), containsInAnyOrder(tRhs));
        assertThat(resultDto.hasChanged, is(true));
        assertThat(resultDto.usedImplicitConversion, is(false));
    }

    private IBindingCollection createBindingCollection() {
        return createBindingCollection(symbolFactory, typeHelper);
    }

    protected IBindingCollection createBindingCollection(
            ISymbolFactory symbolFactory, ITypeHelper typeHelper) {
        return new BindingCollection(symbolFactory, typeHelper);
    }

}
