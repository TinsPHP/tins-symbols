/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.BoundException;
import ch.tsphp.tinsphp.symbols.constraints.IntersectionBoundException;
import ch.tsphp.tinsphp.symbols.constraints.LowerBoundException;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.constraints.UpperBoundException;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.Set;

import static ch.tsphp.tinsphp.symbols.TypeVariableNames.RETURN_VARIABLE_NAME;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OverloadBindingsTest extends ATypeTest
{
    private static ISymbolFactory symbolFactory;
    private static IOverloadResolver overloadResolver;

    @BeforeClass
    public static void init() {
        ATypeTest.init();

        overloadResolver = new OverloadResolver();
        symbolFactory = mock(ISymbolFactory.class);
        when(symbolFactory.createUnionTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new UnionTypeSymbol(overloadResolver);
            }
        });
        when(symbolFactory.createIntersectionTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new IntersectionTypeSymbol(overloadResolver);
            }
        });
    }

    @Test
    public void copyConstructor_HasTwoVariables_CopyBoth() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, overloadResolver);
        bindings1.addVariable("$a", new TypeVariableConstraint("T1"));
        bindings1.addVariable("$b", new TypeVariableConstraint("T2"));

        IOverloadBindings collection = createOverloadBindings(bindings1);

        assertThat(collection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(collection.getTypeVariableConstraint("$a").hasFixedType(), is(false));
        assertThat(collection.getTypeVariableConstraint("$b").hasFixedType(), is(false));
    }

    @Test
    public void copyConstructor_HasTwoVariablesFirstIsFixed_OnlyFirstIsFixed() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, overloadResolver);
        bindings1.addVariable("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
        bindings1.addVariable("$b", new TypeVariableConstraint("T"));

        IOverloadBindings collection = createOverloadBindings(bindings1);

        assertThat(collection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(collection.getTypeVariableConstraint("$a").hasFixedType(), is(true));
        assertThat(collection.getTypeVariableConstraint("$b").hasFixedType(), is(false));
    }

    @Test
    public void copyConstructor_HasTwoVariablesSecondIsFixed_OnlySecondIsFixed() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, overloadResolver);
        bindings1.addVariable("$a", new TypeVariableConstraint("T"));
        bindings1.addVariable("$b", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));

        IOverloadBindings collection = createOverloadBindings(bindings1);

        assertThat(collection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(collection.getTypeVariableConstraint("$a").hasFixedType(), is(false));
        assertThat(collection.getTypeVariableConstraint("$b").hasFixedType(), is(true));
    }

    @Test
    public void getNextTypeVariable_FirstCall_ReturnsT1() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        TypeVariableConstraint result = collection.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("T1"));
    }

    @Test
    public void getNextTypeVariable_SecondCallAfterCopy_ReturnsT2() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, overloadResolver);
        bindings1.getNextTypeVariable();

        IOverloadBindings collection = createOverloadBindings(bindings1);
        TypeVariableConstraint result = collection.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("T2"));
    }

    @Test
    public void addVariable_NotYetAdded_IsAdded() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        collection.addVariable("$a", new TypeVariableConstraint("T"));

        assertThat(collection.containsVariable("$a"), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addVariable_AlreadyAdded_ThrowsIllegalArgumentException() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        collection.addVariable("$a", new TypeVariableConstraint("T1"));

        //act
        collection.addVariable("$a", new TypeVariableConstraint("T2"));

        //assert in annotation
    }


//    @Test
//    public void tryToFixType_IsAlreadyFixedNoLowerBounds_ReturnsTrue() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
//
//        //act
//        boolean result = collection.tryToFixType("$a");
//
//        assertThat(result, is(true));
//        assertThat(collection.hasLowerBounds("T"), is(false));
//    }
//
//    @Test
//    public void tryToFixType_IsAlreadyFixedSomeLowerBounds_DoesNotRemoveLowerBounds() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
//        collection.addLowerBound("T", new TypeConstraint(intType));
//
//        //act
//        boolean result = collection.tryToFixType("$a");
//
//        assertThat(result, is(true));
//        assertThat(collection.hasLowerBounds("T"), is(true));
//        assertThat(collection.getLowerBoundConstraintIds("T"), containsInAnyOrder("int"));
//    }
//
//    @Test
//    public void tryToFixType_IsAlreadyFixedOnlySelfAsLowerBound_RemovesAllLowerBounds() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
//        collection.addLowerBound("T", new TypeVariableConstraint("T"));
//
//        //act
//        boolean result = collection.tryToFixType("$a");
//
//        assertThat(result, is(true));
//        assertThat(collection.hasLowerBounds("T"), is(false));
//    }
//
//    @Test
//    public void tryToFixType_IsAlreadyFixedSelfAndTypeAsLowerBound_RemoveSelfRefButNotType() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
//        collection.addLowerBound("T", new TypeVariableConstraint("T"));
//        collection.addLowerBound("T", new TypeConstraint(intType));
//        collection.addLowerBound("T", new TypeConstraint(floatType));
//
//        //act
//        boolean result = collection.tryToFixType("$a");
//
//        assertThat(result, is(true));
//        assertThat(collection.hasLowerBounds("T"), is(true));
//        assertThat(collection.getLowerBoundConstraintIds("T"), containsInAnyOrder("int", "float"));
//    }
//
//    @Test
//    public void resolveDependencies_HasNoLowerBounds_HasStillNoLowerBounds() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
//
//        //act
//        collection.resolveDependencies("$a", new HashSet<String>());
//
//        //assert
//        assertThat(collection.hasLowerBounds("T"), is(false));
//    }
//
//    @Test
//    public void resolveDependencies_HasOnlyTypeConstraintAsLowerBounds_NothingNewAdded() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
//        collection.addLowerBound("T", new TypeConstraint(intType));
//
//        //act
//        collection.resolveDependencies("$a", new HashSet<String>());
//
//        //assert
//        assertThat(collection.hasLowerBounds("T"), is(true));
//        assertThat(collection.getLowerBoundConstraintIds("T"), containsInAnyOrder("int"));
//    }
//
//
//    @Test
//    public void resolveDependencies_HasSelfRefAsLowerBounds_SelfRefIsNotRemoved() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
//        collection.addLowerBound("T", new TypeVariableConstraint("T"));
//
//        //act
//        collection.resolveDependencies("$a", new HashSet<String>());
//
//        //assert
//        assertThat(collection.hasLowerBounds("T"), is(true));
//        assertThat(collection.getLowerBoundConstraintIds("T"), containsInAnyOrder("@T"));
//    }
//
//    @Test
//    public void resolveDependencies_HasSelfARefAndTypeAsLowerBounds_NothingRemoved() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
//        collection.addLowerBound("T", new TypeVariableConstraint("T"));
//        collection.addLowerBound("T", new TypeConstraint(intType));
//
//        //act
//        collection.resolveDependencies("$a", new HashSet<String>());
//
//        //assert
//        assertThat(collection.hasLowerBounds("T"), is(true));
//        assertThat(collection.getLowerBoundConstraintIds("T"), containsInAnyOrder("@T", "int"));
//    }
//
//    @Test
//    public void resolveDependencies_HasRefToOtherParam_NothingRemoved() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
//        collection.addLowerBound("T", new TypeVariableConstraint("T2"));
//        Set<String> parameterConstraintIds = new HashSet<>();
//        parameterConstraintIds.add("@T2");
//
//        //act
//        collection.resolveDependencies("$a", parameterConstraintIds);
//
//        //assert
//        assertThat(collection.hasLowerBounds("T"), is(true));
//        assertThat(collection.getLowerBoundConstraintIds("T"), containsInAnyOrder("@T2"));
//    }
//
//    @Test
//    public void resolveDependencies_HasRefWithInt_AddIntToLowerBound() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
//        collection.addLowerBound("T", new TypeVariableConstraint("T2"));
//        collection.addLowerBound("T2", new TypeConstraint(intType));
//
//        //act
//        collection.resolveDependencies("$a", new HashSet<String>());
//
//        //assert
//        assertThat(collection.hasLowerBounds("T"), is(true));
//        assertThat(collection.getLowerBoundConstraintIds("T"), containsInAnyOrder("int"));
//    }
//
//    @Test
//    public void resolveDependencies_HasRefWithInt_NothingRemovedFromRef() {
//        //pre act - necessary for arrange
//        IOverloadBindings collection = createOverloadBindings();
//
//        //arrange
//        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
//        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
//        collection.addLowerBound("T", new TypeVariableConstraint("T2"));
//        collection.addLowerBound("T2", new TypeConstraint(intType));
//
//        //act
//        collection.resolveDependencies("$a", new HashSet<String>());
//
//        //assert
//        assertThat(collection.hasLowerBounds("T2"), is(true));
//        assertThat(collection.getLowerBoundConstraintIds("T2"), containsInAnyOrder("int"));
//    }

    @Test(expected = IllegalArgumentException.class)
    public void renameTypeVariable_UnknownTypeVariable_ThrowsIllegalArgumentException() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));

        //act
        collection.renameTypeVariable("T", lhs);

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameTypeVariable_UnknownNewName_ThrowsIllegalArgumentException() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));

        //act
        collection.renameTypeVariable(lhs, "T");

        //assert in annotation
    }

    @Test
    public void renameTypeVariable_HasTypeBounds_TransferTypeBounds() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addLowerTypeBound(lhs, intType);
        collection.addUpperTypeBound(lhs, numType);

        //act
        collection.renameTypeVariable(lhs, rhs);

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", rhs, asList("int"), asList("num"), false),
                varBinding("$rhs", rhs, asList("int"), asList("num"), false)
        ));
    }

    @Test
    public void renameTypeVariable_HasRefBounds_TransfersBounds() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        String t1 = "T1";
        collection.addVariable("$t1", new TypeVariableConstraint(t1));
        String t2 = "T2";
        collection.addVariable("$t2", new TypeVariableConstraint(t2));
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(t1));
        collection.addLowerRefBound(t2, new TypeVariableConstraint(lhs));

        //act
        collection.renameTypeVariable(lhs, rhs);

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", rhs, asList("@T1"), asList("@T2"), false),
                varBinding("$rhs", rhs, asList("@T1"), asList("@T2"), false),
                varBinding("$t1", t1, null, asList("@Trhs"), false),
                varBinding("$t2", t2, asList("@Trhs"), null, false)
        ));
    }


    @Test(expected = IllegalArgumentException.class)
    public void addLowerTypeBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerTypeBound("T", intType);

        //assert in annotation
    }

    @Test
    public void addLowerTypeBound_IsSubtypeOfExistingUpper_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, numType);

        //act
        collection.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = collection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addLowerTypeBound_IsSameTypeAsExistingUpper_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, intType);

        //act
        collection.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = collection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
    }

    @Test(expected = UpperBoundException.class)
    public void addLowerTypeBound_IsNotSameOrSubtypeOfExistingUpper_ThrowsLowerBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, intType);

        //act
        collection.addLowerTypeBound(typeVariable, numType);
        IUnionTypeSymbol result = collection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addLowerTypeBound_IsSameAsExistingLower_ContainsTypeOnlyOnce() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(typeVariable, intType);

        //act
        collection.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = collection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addLowerTypeBound_IsSubtypeOfExistingLower_RemainsExisting() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(typeVariable, numType);

        //act
        collection.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = collection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
    }

    @Test
    public void addLowerTypeBound_IsParentTypeOfExistingLower_IsNewLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(typeVariable, intType);

        //act
        collection.addLowerTypeBound(typeVariable, numType);
        IUnionTypeSymbol result = collection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
    }

    @Test
    public void addLowerTypeBound_IsOutOfTypeHierarchyOfExistingLower_ContainsBoth() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(typeVariable, intType);

        //act
        collection.addLowerTypeBound(typeVariable, boolType);
        IUnionTypeSymbol result = collection.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int", "bool"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addUpperTypeBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperTypeBound("T", intType);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsParentTypeOfExistingLower_AddsUpperBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(typeVariable, intType);

        //act
        collection.addUpperTypeBound(typeVariable, numType);
        IIntersectionTypeSymbol result = collection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
    }

    @Test
    public void addUpperTypeBound_IsSameTypeAsExistingLower_AddsUpperBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(typeVariable, intType);

        //act
        collection.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = collection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
    }

    @Test(expected = LowerBoundException.class)
    public void addUpperTypeBound_IsNotSameOrParentTypeOfExistingLower_ThrowsLowerBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addLowerTypeBound(typeVariable, numType);

        //act
        collection.addUpperTypeBound(typeVariable, intType);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsSameTypeAsExistingUpper_ContainsTypeOnlyOnce() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, intType);

        //act
        collection.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = collection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addUpperTypeBound_IsSubtypeOfExistingUpper_IsNewUpperBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, numType);

        //act
        collection.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = collection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
    }

    @Test
    public void addUpperTypeBound_IsParentTypeOfExistingUpper_RemainsExistingBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, intType);

        //act
        collection.addUpperTypeBound(typeVariable, numType);
        IIntersectionTypeSymbol result = collection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
    }

    @Test(expected = IntersectionBoundException.class)
    public void
    addUpperTypeBound_IsOutOfTypeHierarchyOfExistingUpperAndCannotBeUsedInIntersection_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, interfaceAType);

        //act
        collection.addUpperTypeBound(typeVariable, boolType);

        //assert in annotation
    }

    @Test(expected = IntersectionBoundException.class)
    public void
    addUpperTypeBound_IsOutOfTypeHierarchyOfExistingUpperAndExistingCannotBeUsedInIntersection_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, intType);

        //act
        collection.addUpperTypeBound(typeVariable, interfaceAType);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsOutOfTypeHierarchyOfExistingAndBothCanBeUsedInIntersection_ContainsBoth() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, interfaceAType);

        //act
        collection.addUpperTypeBound(typeVariable, interfaceBType);
        IIntersectionTypeSymbol result = collection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("IA", "IB"));
    }

    @Test
    public void addUpperTypeBound_IsSubtypeOfOneOfTheTypesInTheUpperBound_NarrowsUpperBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addUpperTypeBound(typeVariable, interfaceAType);
        collection.addUpperTypeBound(typeVariable, interfaceBType);

        //act
        collection.addUpperTypeBound(typeVariable, interfaceSubAType);
        IIntersectionTypeSymbol result = collection.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("ISubA", "IB"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLowerRefBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerRefBound("T", new TypeVariableConstraint("T2"));

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLowerRefBound_NonExistingRef_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings collection = createOverloadBindings();
        collection.addVariable("$a", new TypeVariableConstraint("T"));
        collection.addLowerRefBound("T", new TypeVariableConstraint("T2"));

        //assert in annotation
    }

    @Test
    public void addLowerRefBound_HasNoBounds_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), null, false),
                varBinding("$rhs", rhs, null, asList("@Tlhs"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefHasNoBounds_NarrowsRefAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addUpperTypeBound(lhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsSame_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addUpperTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsSubtype_RefsUpperStaysTheSameAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addUpperTypeBound(lhs, numType);
        collection.addUpperTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("num"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsParentType_NarrowRefAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addUpperTypeBound(rhs, numType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsSame_NarrowRefAndLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs", "int"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsSubtype_NarrowRefAndLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addUpperTypeBound(lhs, numType);
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), asList("num"), false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs", "num"), false)
        ));
    }

    @Test(expected = BoundException.class)
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsParentType_ThrowsBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addLowerTypeBound(rhs, numType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        //assert in annotation
    }

    @Test
    public void addLowerRefBound_LhsHasNoBoundAndRefHasLower_NarrowsLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsSameType_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addLowerTypeBound(lhs, intType);
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));


        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsSubtype_DoesNotChangeLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addLowerTypeBound(lhs, numType);
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));


        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("num", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsParentType_NarrowsLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addLowerTypeBound(lhs, intType);
        collection.addLowerTypeBound(rhs, numType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(rhs));


        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("num", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("num"), asList("@Tlhs"), false)
        ));
    }

    @Test
    public void addLowerRefBound_HasFixedTypeAndLowerBound_TransfersLowerBoundButDoesNotAddLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addVariable("$rhs", new TypeVariableConstraint(rhs));
        collection.addLowerTypeBound(rhs, numType);

        //act
        collection.addLowerRefBound(lhs, new FixedTypeVariableConstraint(new TypeVariableConstraint(rhs)));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("num"), null, false),
                varBinding("$rhs", rhs, asList("num"), null, false)
        ));
    }


    @Test
    public void addLowerRefBound_SelfRefWithoutBounds_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(lhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs"), false)
        ));
    }

    @Test
    public void addLowerRefBound_SelfRefWithUpperBound_AddsLowerBoundAndAddUpperBoundWasNotCalled() {
        //pre-act necessary for arrange
        IOverloadBindings collection = spy(createOverloadBindings());

        //arrange
        String lhs = "Tlhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addUpperTypeBound(lhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(lhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs", "int"), false)
        ));
        try {
            verify(collection, times(2)).addUpperTypeBound(anyString(), any(ITypeSymbol.class));
            Assert.fail("addUpperTypeBound was called but should not have been.");
        } catch (MockitoAssertionError ex) {
            //that's fine
        }
    }

    @Test
    public void addLowerRefBound_SelfRefWithLowerBound_AddsLowerBoundAndAddLowerBoundWasNotCalled() {
        //pre-act necessary for arrange
        IOverloadBindings collection = spy(createOverloadBindings());

        //arrange
        String lhs = "Tlhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addLowerTypeBound(lhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(lhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs", "int"), asList("@Tlhs"), false)
        ));
        try {
            verify(collection, times(2)).addLowerTypeBound(anyString(), any(ITypeSymbol.class));
            Assert.fail("addUpperTypeBound was called but should not have been.");
        } catch (MockitoAssertionError ex) {
            //that's fine
        }
    }

    @Test
    public void addLowerRefBound_SelfRefWithUpperBoundAndAlreadySelfRef_NoEndlessLoopDependencyOnlyOnes() {
        //pre-act necessary for arrange
        IOverloadBindings collection = spy(createOverloadBindings());

        //arrange
        String lhs = "Tlhs";
        collection.addVariable("$lhs", new TypeVariableConstraint(lhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(lhs));

        //act
        collection.addLowerRefBound(lhs, new TypeVariableConstraint(lhs));

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs", "int"), false)
        ));
    }

    @Test
    public void addLowerRefBound_BidirectionalRefWithBoundsButAlsoOtherRefs_PropagatesBoundsAccordingly() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$a", new TypeVariableConstraint(t1));
        collection.addVariable("$b", new TypeVariableConstraint(t2));
        collection.addVariable("$c", new TypeVariableConstraint(t3));
        collection.addLowerRefBound(t2, new TypeVariableConstraint(t1));
        collection.addLowerTypeBound(t2, intType);
        collection.addUpperTypeBound(t3, numType);
        collection.addLowerRefBound(t2, new TypeVariableConstraint(t3));

        //act
        collection.addLowerRefBound(t1, new TypeVariableConstraint(t2));


        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2"), asList("@T2"), false),
                varBinding("$b", "T2", asList("int", "@T3", "@T1"), asList("@T1"), false),
                varBinding("$c", "T3", null, asList("num", "@T2"), false)
        ));
    }

    @Test
    public void addLowerRefBound_CircularRefWithoutBounds_AddRefsAccordingly() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$a", new TypeVariableConstraint(t1));
        collection.addVariable("$b", new TypeVariableConstraint(t2));
        collection.addVariable("$c", new TypeVariableConstraint(t3));
        collection.addLowerRefBound(t2, new TypeVariableConstraint(t3));
        collection.addLowerRefBound(t3, new TypeVariableConstraint(t1));

        //act
        collection.addLowerRefBound(t1, new TypeVariableConstraint(t2));

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("@T2"), asList("@T3"), false),
                varBinding("$b", "T2", asList("@T3"), asList("@T1"), false),
                varBinding("$c", "T3", asList("@T1"), asList("@T2"), false)
        ));
    }


    @Test
    public void addLowerRefBound_CircularRefWithBounds_PropagatesTypesAccordingly() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$a", new TypeVariableConstraint(t1));
        collection.addVariable("$b", new TypeVariableConstraint(t2));
        collection.addVariable("$c", new TypeVariableConstraint(t3));
        collection.addLowerRefBound(t2, new TypeVariableConstraint(t3));
        collection.addLowerTypeBound(t2, intType);
        collection.addLowerRefBound(t3, new TypeVariableConstraint(t1));
        collection.addUpperTypeBound(t2, numType);

        //act
        collection.addLowerRefBound(t1, new TypeVariableConstraint(t2));

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2"), asList("num", "@T3"), false),
                varBinding("$b", "T2", asList("int", "@T3"), asList("num", "@T1"), false),
                varBinding("$c", "T3", asList("int", "@T1"), asList("num", "@T2"), false)
        ));
    }

    @Test
    public void addLowerRefBound_CircularRefWithBoundsButAlsoOtherRefs_PropagatesBoundsAccordingly() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        String t4 = "T4";
        String t5 = "T5";
        collection.addVariable("$a", new TypeVariableConstraint(t1));
        collection.addVariable("$b", new TypeVariableConstraint(t2));
        collection.addVariable("$c", new TypeVariableConstraint(t3));
        collection.addVariable("$d", new TypeVariableConstraint(t4));
        collection.addVariable("$e", new TypeVariableConstraint(t5));
        collection.addLowerRefBound(t2, new TypeVariableConstraint(t3));
        collection.addLowerTypeBound(t2, intType);
        collection.addLowerRefBound(t3, new TypeVariableConstraint(t1));
        collection.addUpperTypeBound(t3, numType);
        collection.addLowerRefBound(t1, new TypeVariableConstraint(t4));
        collection.addLowerRefBound(t3, new TypeVariableConstraint(t5));

        //act
        collection.addLowerRefBound(t1, new TypeVariableConstraint(t2));

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2", "@T4"), asList("num", "@T3"), false),
                varBinding("$b", "T2", asList("int", "@T3"), asList("num", "@T1"), false),
                varBinding("$c", "T3", asList("int", "@T1", "@T5"), asList("num", "@T2"), false),
                varBinding("$d", "T4", null, asList("num", "@T1"), false),
                varBinding("$e", "T5", null, asList("num", "@T3"), false)
        ));
    }

    @Test
    public void tryToFix_NoParams_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = 2.2; return true;}

        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String tReturn = "Treturn";

        collection.addVariable("$a", new TypeVariableConstraint(t1));
        collection.addVariable("$b", new TypeVariableConstraint(t2));
        collection.addVariable(
                RETURN_VARIABLE_NAME, new FixedTypeVariableConstraint(new TypeVariableConstraint(tReturn)));
        collection.addLowerTypeBound(t1, intType);
        collection.addLowerTypeBound(t2, floatType);
        collection.addLowerTypeBound(tReturn, boolType);

        //act
        collection.tryToFix(new HashSet<String>());

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("int"), null, true),
                varBinding("$b", "T2", asList("float"), null, true),
                varBinding(RETURN_VARIABLE_NAME, "Treturn", asList("bool"), null, true)
        ));
    }

    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaIndirection_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = 2.2; return $b;}

        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String tReturn = "Treturn";

        collection.addVariable("$a", new TypeVariableConstraint(t1));
        collection.addVariable("$b", new TypeVariableConstraint(t2));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableConstraint(tReturn));
        collection.addLowerTypeBound(t1, intType);
        collection.addLowerTypeBound(t2, floatType);
        collection.addLowerRefBound(tReturn, new TypeVariableConstraint(t2));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("int"), null, true),
                varBinding("$b", "T2", asList("float"), null, true),
                varBinding(RETURN_VARIABLE_NAME, "Treturn", asList("float"), null, true)
        ));
    }

    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaDoubleIndirection_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = $a; return $b;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String tReturn = "Treturn";

        collection.addVariable("$a", new TypeVariableConstraint(t1));
        collection.addVariable("$b", new TypeVariableConstraint(t2));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableConstraint(tReturn));
        collection.addLowerTypeBound(t1, intType);
        collection.addLowerRefBound(t2, new TypeVariableConstraint(t1));
        collection.addLowerRefBound(tReturn, new TypeVariableConstraint(t2));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("int"), null, true),
                varBinding("$b", "T2", asList("int"), null, true),
                varBinding(RETURN_VARIABLE_NAME, "Treturn", asList("int"), null, true)
        ));
    }

    private IOverloadBindings createOverloadBindings() {
        return createOverloadBindings(symbolFactory, overloadResolver);
    }

    private IOverloadBindings createOverloadBindings(OverloadBindings bindings) {
        return createOverloadBindings(symbolFactory, overloadResolver, bindings);
    }

    protected IOverloadBindings createOverloadBindings(
            ISymbolFactory symbolFactory, IOverloadResolver overloadResolver) {
        return new OverloadBindings(symbolFactory, overloadResolver);
    }

    protected IOverloadBindings createOverloadBindings(
            ISymbolFactory symbolFactory, IOverloadResolver overloadResolver, OverloadBindings bindings) {
        return new OverloadBindings(symbolFactory, overloadResolver, bindings);
    }
}
