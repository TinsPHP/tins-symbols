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
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;

import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OverloadBindingsAddBoundTest extends ATypeTest
{


    @Test(expected = IllegalArgumentException.class)
    public void addLowerTypeBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addLowerTypeBound("T", intType);

        //assert in annotation
    }

    @Test
    public void addLowerTypeBound_IsSubtypeOfExistingUpper_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, numType);

        //act
        boolean changed = overloadBindings.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = overloadBindings.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerTypeBound_IsSameTypeAsExistingUpper_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = overloadBindings.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(changed, is(true));
    }

    @Test(expected = UpperBoundException.class)
    public void addLowerTypeBound_IsNotSameOrSubtypeOfExistingUpper_ThrowsLowerBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, intType);

        //act
        overloadBindings.addLowerTypeBound(typeVariable, numType);
        overloadBindings.getLowerTypeBounds(typeVariable);

        //assert in annotation
    }

    @Test
    public void addLowerTypeBound_IsSameAsExistingLower_ContainsTypeOnlyOnce() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addLowerTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = overloadBindings.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(changed, is(false));
    }

    @Test
    public void addLowerTypeBound_IsSubtypeOfExistingLower_RemainsExisting() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addLowerTypeBound(typeVariable, numType);

        //act
        boolean changed = overloadBindings.addLowerTypeBound(typeVariable, intType);
        IUnionTypeSymbol result = overloadBindings.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
        assertThat(changed, is(false));
    }

    @Test
    public void addLowerTypeBound_IsParentTypeOfExistingLower_IsNewLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addLowerTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addLowerTypeBound(typeVariable, numType);
        IUnionTypeSymbol result = overloadBindings.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerTypeBound_IsOutOfTypeHierarchyOfExistingLower_ContainsBoth() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addLowerTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addLowerTypeBound(typeVariable, boolType);
        IUnionTypeSymbol result = overloadBindings.getLowerTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int", "bool"));
        assertThat(changed, is(true));
    }

    //see TINS-482 upper bound with convertible type which points to lower bound
    @Test
    public void addLowerTypeBound_ConvertibleTypeWhichPointsToItself_DoesNotAddTheConvertibleType() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String tLhs = "Tlhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(tLhs));
        IConvertibleTypeSymbol asTlhs = symbolFactory.createConvertibleTypeSymbol();
        asTlhs.bindTo(overloadBindings, asList(tLhs));

        //act
        boolean changed = overloadBindings.addLowerTypeBound(tLhs, asTlhs);

        assertThat(overloadBindings.getLowerTypeBounds(tLhs), is(nullValue()));
        assertThat(changed, is(false));
    }

    @Test
    public void addLowerTypeBound_ConvertibleTypeWithRefWhichIsAlreadyUpper_DoesNotPropagateTheTypeUpwards() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(tLhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(tRhs));
        IConvertibleTypeSymbol asTlhs = symbolFactory.createConvertibleTypeSymbol();
        asTlhs.bindTo(overloadBindings, asList(tLhs));
        overloadBindings.addLowerRefBound(tLhs, new TypeVariableReference(tRhs));

        //act
        boolean changed = overloadBindings.addLowerTypeBound(tRhs, asTlhs);

        assertThat(overloadBindings.getLowerTypeBounds(tLhs), is(nullValue()));
        assertThat(overloadBindings.getLowerTypeBounds(tRhs).getTypeSymbols().keySet(),
                containsInAnyOrder("{as Tlhs}"));
        assertThat(changed, is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addUpperTypeBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addUpperTypeBound("T", intType);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsParentTypeOfExistingLower_AddsUpperBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addLowerTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, numType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("num"));
        assertThat(changed, is(true));
    }

    @Test
    public void addUpperTypeBound_IsSameTypeAsExistingLower_AddsUpperBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addLowerTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(changed, is(true));
    }

    @Test(expected = LowerBoundException.class)
    public void addUpperTypeBound_IsNotSameOrParentTypeOfExistingLower_ThrowsLowerBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addLowerTypeBound(typeVariable, numType);

        //act
        overloadBindings.addUpperTypeBound(typeVariable, intType);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsSameTypeAsExistingUpper_ContainsTypeOnlyOnce() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(changed, is(false));
    }

    @Test
    public void addUpperTypeBound_IsSubtypeOfExistingUpper_IsNewUpperBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, numType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, intType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(changed, is(true));
    }

    @Test
    public void addUpperTypeBound_IsParentTypeOfExistingUpper_RemainsExistingBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, numType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int"));
        assertThat(changed, is(false));
    }

    @Test
    public void addUpperTypeBound_IsOutOfTypeHierarchyAndBothCanBeUsed_ContainsBoth() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, interfaceAType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, interfaceBType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("IA", "IB"));
        assertThat(changed, is(true));
    }

    @Test
    public void addUpperTypeBound_IsOutOfHierarchyAndCannotBeUsedAndExistingCanBeUsed_ContainsBoth() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, interfaceAType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, boolType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("bool", "IA"));
        assertThat(changed, is(true));
    }

    @Test
    public void addUpperTypeBound_IsOutOfTypeHierarchyAndCanBeUsedAndExistingCannotBeUsed_ContainsBoth() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, intType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, interfaceAType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("int", "IA"));
        assertThat(changed, is(true));
    }

    @Test(expected = IntersectionBoundException.class)
    public void addUpperTypeBound_IsOutOfTypeHierarchyAndBothCannotBeUsed_ThrowsIntersectionBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, intType);

        //act
        overloadBindings.addUpperTypeBound(typeVariable, floatType);
        overloadBindings.getUpperTypeBounds(typeVariable);

        //assert in annotation
    }

    @Test
    public void addUpperTypeBound_IsSubtypeOfOneOfTheTypesInTheUpperBound_NarrowsUpperBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String typeVariable = "T";
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addUpperTypeBound(typeVariable, interfaceAType);
        overloadBindings.addUpperTypeBound(typeVariable, interfaceBType);

        //act
        boolean changed = overloadBindings.addUpperTypeBound(typeVariable, interfaceSubAType);
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(typeVariable);

        assertThat(result.getTypeSymbols().keySet(), containsInAnyOrder("ISubA", "IB"));
        assertThat(changed, is(true));
    }

    //see TINS-482 upper bound with convertible type which points to lower bound
    @Test
    public void addUpperTypeBound_ConvertibleTypeWithRefWhichIsAlreadyLower_DoesNotPropagateTheTypeDownwards() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(tLhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(tRhs));
        IConvertibleTypeSymbol asTrhs = symbolFactory.createConvertibleTypeSymbol();
        asTrhs.bindTo(overloadBindings, asList(tRhs));
        overloadBindings.addLowerRefBound(tLhs, new TypeVariableReference(tRhs));

        //act
        boolean changed = overloadBindings.addUpperTypeBound(tLhs, asTrhs);

        assertThat(overloadBindings.getUpperTypeBounds(tRhs), is(nullValue()));
        assertThat(overloadBindings.getUpperTypeBounds(tLhs).getTypeSymbols().keySet(),
                containsInAnyOrder("{as Trhs}"));
        assertThat(changed, is(true));
    }

    //see TINS-482 upper bound with convertible type which points to lower bound
    @Test
    public void addUpperTypeBound_ConvertibleTypeWhichPointsToItself_DoesNotAddTheConvertibleType() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String tLhs = "Tlhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(tLhs));
        IConvertibleTypeSymbol asTlhs = symbolFactory.createConvertibleTypeSymbol();
        asTlhs.bindTo(overloadBindings, asList(tLhs));

        //act
        boolean changed = overloadBindings.addUpperTypeBound(tLhs, asTlhs);

        assertThat(overloadBindings.getUpperTypeBounds(tLhs), is(nullValue()));
        assertThat(changed, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLowerRefBound_ForNonExistingBinding_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addLowerRefBound("T", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void addLowerRefBound_NonExistingRef_ThrowsIllegalArgumentException() {
        //no arrange necessary

        //act
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        overloadBindings.addLowerRefBound("T", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test
    public void addLowerRefBound_HasNoBounds_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), null, false),
                varBinding("$rhs", rhs, null, asList("@Tlhs"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefHasNoBounds_NarrowsRefAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addUpperTypeBound(lhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsSame_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addUpperTypeBound(lhs, intType);
        overloadBindings.addUpperTypeBound(rhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsSubtype_RefsUpperStaysTheSameAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addUpperTypeBound(lhs, numType);
        overloadBindings.addUpperTypeBound(rhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("num"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsUpperIsParentType_NarrowRefAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addUpperTypeBound(lhs, intType);
        overloadBindings.addUpperTypeBound(rhs, numType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, null, asList("@Tlhs", "int"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsSame_NarrowRefAndLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addUpperTypeBound(lhs, intType);
        overloadBindings.addLowerTypeBound(rhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), asList("int"), false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs", "int"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsSubtype_NarrowRefAndLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addUpperTypeBound(lhs, numType);
        overloadBindings.addLowerTypeBound(rhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), asList("num"), false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs", "num"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test(expected = BoundException.class)
    public void addLowerRefBound_LhsHasUpperAndRefsLowerIsParentType_ThrowsBoundException() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addUpperTypeBound(lhs, intType);
        overloadBindings.addLowerTypeBound(rhs, numType);

        //act
        overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        //assert in annotation
    }

    @Test
    public void addLowerRefBound_LhsHasNoBoundAndRefHasLower_NarrowsLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addLowerTypeBound(rhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsSameType_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addLowerTypeBound(lhs, intType);
        overloadBindings.addLowerTypeBound(rhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));


        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("int", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsSubtype_DoesNotChangeLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addLowerTypeBound(lhs, numType);
        overloadBindings.addLowerTypeBound(rhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("num", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("int"), asList("@Tlhs"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_LhsHasLowerBoundAndRefsLowerIsParentType_NarrowsLhsAndAddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addLowerTypeBound(lhs, intType);
        overloadBindings.addLowerTypeBound(rhs, numType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(rhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("num", "@Trhs"), null, false),
                varBinding("$rhs", rhs, asList("num"), asList("@Tlhs"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_HasFixedTypeAndLowerBound_TransfersLowerTypeBoundButDoesNotAddLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(rhs));
        overloadBindings.addLowerTypeBound(rhs, numType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new FixedTypeVariableReference(new
                TypeVariableReference
                (rhs)));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("num"), null, false),
                varBinding("$rhs", rhs, asList("num"), null, false)
        ));
        assertThat(changed, is(true));
    }


    @Test
    public void addLowerRefBound_SelfRefWithoutBounds_AddsLowerBound() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_SelfRefWithUpperBound_AddsLowerBoundAndAddUpperBoundWasNotCalled() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = spy(createOverloadBindings());

        //arrange
        String lhs = "Tlhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addUpperTypeBound(lhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs", "int"), false)
        ));
        assertThat(changed, is(true));
        try {
            verify(overloadBindings, times(2)).addUpperTypeBound(anyString(), any(ITypeSymbol.class));
            Assert.fail("addUpperTypeBound was called but should not have been.");
        } catch (MockitoAssertionError ex) {
            //that's fine
        }
    }

    @Test
    public void addLowerRefBound_SelfRefWithLowerBound_AddsLowerBoundAndAddLowerBoundWasNotCalled() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = spy(createOverloadBindings());

        //arrange
        String lhs = "Tlhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addLowerTypeBound(lhs, intType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs", "int"), asList("@Tlhs"), false)
        ));
        assertThat(changed, is(true));
        try {
            verify(overloadBindings, times(2)).addLowerTypeBound(anyString(), any(ITypeSymbol.class));
            Assert.fail("addUpperTypeBound was called but should not have been.");
        } catch (MockitoAssertionError ex) {
            //that's fine
        }
    }

    @Test
    public void addLowerRefBound_SelfRefWithUpperBoundAndAlreadySelfRef_NoEndlessLoopDependencyOnlyOnes() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = spy(createOverloadBindings());

        //arrange
        String lhs = "Tlhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(lhs));
        overloadBindings.addUpperTypeBound(lhs, intType);
        overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        //act
        boolean changed = overloadBindings.addLowerRefBound(lhs, new TypeVariableReference(lhs));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$lhs", lhs, asList("@Tlhs"), asList("@Tlhs", "int"), false)
        ));
        assertThat(changed, is(false));
    }

    @Test
    public void addLowerRefBound_BidirectionalRefWithBoundsButAlsoOtherRefs_PropagatesBoundsAccordingly() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        overloadBindings.addVariable("$a", new TypeVariableReference(t1));
        overloadBindings.addVariable("$b", new TypeVariableReference(t2));
        overloadBindings.addVariable("$c", new TypeVariableReference(t3));
        overloadBindings.addLowerRefBound(t2, new TypeVariableReference(t1));
        overloadBindings.addLowerTypeBound(t2, intType);
        overloadBindings.addUpperTypeBound(t3, numType);
        overloadBindings.addLowerRefBound(t2, new TypeVariableReference(t3));

        //act
        boolean changed = overloadBindings.addLowerRefBound(t1, new TypeVariableReference(t2));


        assertThat(overloadBindings, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2"), asList("@T2"), false),
                varBinding("$b", "T2", asList("int", "@T3", "@T1"), asList("@T1"), false),
                varBinding("$c", "T3", null, asList("num", "@T2"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_CircularRefWithoutBounds_AddRefsAccordingly() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        overloadBindings.addVariable("$a", new TypeVariableReference(t1));
        overloadBindings.addVariable("$b", new TypeVariableReference(t2));
        overloadBindings.addVariable("$c", new TypeVariableReference(t3));
        overloadBindings.addLowerRefBound(t2, new TypeVariableReference(t3));
        overloadBindings.addLowerRefBound(t3, new TypeVariableReference(t1));

        //act
        boolean changed = overloadBindings.addLowerRefBound(t1, new TypeVariableReference(t2));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$a", "T1", asList("@T2"), asList("@T3"), false),
                varBinding("$b", "T2", asList("@T3"), asList("@T1"), false),
                varBinding("$c", "T3", asList("@T1"), asList("@T2"), false)
        ));
        assertThat(changed, is(true));
    }


    @Test
    public void addLowerRefBound_CircularRefWithBounds_PropagatesTypesAccordingly() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        overloadBindings.addVariable("$a", new TypeVariableReference(t1));
        overloadBindings.addVariable("$b", new TypeVariableReference(t2));
        overloadBindings.addVariable("$c", new TypeVariableReference(t3));
        overloadBindings.addLowerRefBound(t2, new TypeVariableReference(t3));
        overloadBindings.addLowerTypeBound(t2, intType);
        overloadBindings.addLowerRefBound(t3, new TypeVariableReference(t1));
        overloadBindings.addUpperTypeBound(t2, numType);

        //act
        boolean changed = overloadBindings.addLowerRefBound(t1, new TypeVariableReference(t2));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2"), asList("num", "@T3"), false),
                varBinding("$b", "T2", asList("int", "@T3"), asList("num", "@T1"), false),
                varBinding("$c", "T3", asList("int", "@T1"), asList("num", "@T2"), false)
        ));
        assertThat(changed, is(true));
    }

    @Test
    public void addLowerRefBound_CircularRefWithBoundsButAlsoOtherRefs_PropagatesBoundsAccordingly() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        String t4 = "T4";
        String t5 = "T5";
        overloadBindings.addVariable("$a", new TypeVariableReference(t1));
        overloadBindings.addVariable("$b", new TypeVariableReference(t2));
        overloadBindings.addVariable("$c", new TypeVariableReference(t3));
        overloadBindings.addVariable("$d", new TypeVariableReference(t4));
        overloadBindings.addVariable("$e", new TypeVariableReference(t5));
        overloadBindings.addLowerRefBound(t2, new TypeVariableReference(t3));
        overloadBindings.addLowerTypeBound(t2, intType);
        overloadBindings.addLowerRefBound(t3, new TypeVariableReference(t1));
        overloadBindings.addUpperTypeBound(t3, numType);
        overloadBindings.addLowerRefBound(t1, new TypeVariableReference(t4));
        overloadBindings.addLowerRefBound(t3, new TypeVariableReference(t5));

        //act
        boolean changed = overloadBindings.addLowerRefBound(t1, new TypeVariableReference(t2));

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$a", "T1", asList("int", "@T2", "@T4"), asList("num", "@T3"), false),
                varBinding("$b", "T2", asList("int", "@T3"), asList("num", "@T1"), false),
                varBinding("$c", "T3", asList("int", "@T1", "@T5"), asList("num", "@T2"), false),
                varBinding("$d", "T4", null, asList("num", "@T1"), false),
                varBinding("$e", "T5", null, asList("num", "@T3"), false)
        ));
        assertThat(changed, is(true));
    }

    //see TINS-482 upper bound with convertible type which points to lower bound
    @Test
    public void addLowerRefBound_HasAlreadyConvertibleWithSameRefAsUpper_DoesNotPropagateConvertible() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(tLhs));
        overloadBindings.addVariable("$rhs", new TypeVariableReference(tRhs));
        IConvertibleTypeSymbol asTrhs = symbolFactory.createConvertibleTypeSymbol();
        asTrhs.bindTo(overloadBindings, asList(tRhs));
        overloadBindings.addUpperTypeBound(tLhs, asTrhs);

        //act
        boolean changed = overloadBindings.addLowerRefBound(tLhs, new TypeVariableReference(tRhs));
        IIntersectionTypeSymbol result = overloadBindings.getUpperTypeBounds(tRhs);

        assertThat(result, is(nullValue()));
        assertThat(overloadBindings.getLowerRefBounds(tLhs), containsInAnyOrder(tRhs));
        assertThat(changed, is(true));
    }

    private IOverloadBindings createOverloadBindings() {
        return createOverloadBindings(symbolFactory, typeHelper);
    }

    protected IOverloadBindings createOverloadBindings(
            ISymbolFactory symbolFactory, ITypeHelper typeHelper) {
        return new OverloadBindings(symbolFactory, typeHelper);
    }

}
