/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.BoundException;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IntersectionBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.LowerBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.UpperBoundException;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OverloadBindingsAddBoundTest extends ATypeTest
{
    private static ISymbolFactory symbolFactory;
    private static ITypeHelper typeHelper;

    @BeforeClass
    public static void init() {
        ATypeTest.init();

        typeHelper = new TypeHelper();
        symbolFactory = mock(ISymbolFactory.class);
        ITypeSymbol mixedTypeSymbol = mock(ITypeSymbol.class);
        when(mixedTypeSymbol.getAbsoluteName()).thenReturn("mixed");

        when(symbolFactory.getMixedTypeSymbol()).thenReturn(mixedTypeSymbol);
        when(symbolFactory.createUnionTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new UnionTypeSymbol(typeHelper);
            }
        });
        when(symbolFactory.createIntersectionTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new IntersectionTypeSymbol(typeHelper);
            }
        });
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addVariable("$a", new TypeVariableReference("T"));
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
        collection.addLowerRefBound("T", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLowerRefBound_NonExistingRef_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings collection = createOverloadBindings();
        collection.addVariable("$a", new TypeVariableReference("T"));
        collection.addLowerRefBound("T", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test
    public void addLowerRefBound_HasNoBounds_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addUpperTypeBound(lhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addUpperTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addUpperTypeBound(lhs, numType);
        collection.addUpperTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addUpperTypeBound(rhs, numType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addUpperTypeBound(lhs, numType);
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addLowerTypeBound(rhs, numType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        //assert in annotation
    }

    @Test
    public void addLowerRefBound_LhsHasNoBoundAndRefHasLower_NarrowsLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addLowerTypeBound(lhs, intType);
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));


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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addLowerTypeBound(lhs, numType);
        collection.addLowerTypeBound(rhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));


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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addLowerTypeBound(lhs, intType);
        collection.addLowerTypeBound(rhs, numType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(rhs));


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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addLowerTypeBound(rhs, numType);

        //act
        collection.addLowerRefBound(lhs, new FixedTypeVariableReference(new TypeVariableReference(rhs)));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addUpperTypeBound(lhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addLowerTypeBound(lhs, intType);

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

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
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addUpperTypeBound(lhs, intType);
        collection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        //act
        collection.addLowerRefBound(lhs, new TypeVariableReference(lhs));

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
        collection.addVariable("$a", new TypeVariableReference(t1));
        collection.addVariable("$b", new TypeVariableReference(t2));
        collection.addVariable("$c", new TypeVariableReference(t3));
        collection.addLowerRefBound(t2, new TypeVariableReference(t1));
        collection.addLowerTypeBound(t2, intType);
        collection.addUpperTypeBound(t3, numType);
        collection.addLowerRefBound(t2, new TypeVariableReference(t3));

        //act
        collection.addLowerRefBound(t1, new TypeVariableReference(t2));


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
        collection.addVariable("$a", new TypeVariableReference(t1));
        collection.addVariable("$b", new TypeVariableReference(t2));
        collection.addVariable("$c", new TypeVariableReference(t3));
        collection.addLowerRefBound(t2, new TypeVariableReference(t3));
        collection.addLowerRefBound(t3, new TypeVariableReference(t1));

        //act
        collection.addLowerRefBound(t1, new TypeVariableReference(t2));

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
        collection.addVariable("$a", new TypeVariableReference(t1));
        collection.addVariable("$b", new TypeVariableReference(t2));
        collection.addVariable("$c", new TypeVariableReference(t3));
        collection.addLowerRefBound(t2, new TypeVariableReference(t3));
        collection.addLowerTypeBound(t2, intType);
        collection.addLowerRefBound(t3, new TypeVariableReference(t1));
        collection.addUpperTypeBound(t2, numType);

        //act
        collection.addLowerRefBound(t1, new TypeVariableReference(t2));

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
        collection.addVariable("$a", new TypeVariableReference(t1));
        collection.addVariable("$b", new TypeVariableReference(t2));
        collection.addVariable("$c", new TypeVariableReference(t3));
        collection.addVariable("$d", new TypeVariableReference(t4));
        collection.addVariable("$e", new TypeVariableReference(t5));
        collection.addLowerRefBound(t2, new TypeVariableReference(t3));
        collection.addLowerTypeBound(t2, intType);
        collection.addLowerRefBound(t3, new TypeVariableReference(t1));
        collection.addUpperTypeBound(t3, numType);
        collection.addLowerRefBound(t1, new TypeVariableReference(t4));
        collection.addLowerRefBound(t3, new TypeVariableReference(t5));

        //act
        collection.addLowerRefBound(t1, new TypeVariableReference(t2));

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2", "@T4"), asList("num", "@T3"), false),
                varBinding("$b", "T2", asList("int", "@T3"), asList("num", "@T1"), false),
                varBinding("$c", "T3", asList("int", "@T1", "@T5"), asList("num", "@T2"), false),
                varBinding("$d", "T4", null, asList("num", "@T1"), false),
                varBinding("$e", "T5", null, asList("num", "@T3"), false)
        ));
    }

    private IOverloadBindings createOverloadBindings() {
        return createOverloadBindings(symbolFactory, typeHelper);
    }

    protected IOverloadBindings createOverloadBindings(
            ISymbolFactory symbolFactory, ITypeHelper typeHelper) {
        return new OverloadBindings(symbolFactory, typeHelper);
    }

}
