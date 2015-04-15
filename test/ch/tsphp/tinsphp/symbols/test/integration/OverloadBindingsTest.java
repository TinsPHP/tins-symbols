/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration;

import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.symbols.constraints.BoundException;
import ch.tsphp.tinsphp.symbols.constraints.LowerBoundException;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.constraints.UpperBoundException;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;

public class OverloadBindingsTest extends ATypeTest
{

    @Test
    public void copyConstructor_HasTwoVariablesW_CopyBoth() {
        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        OverloadBindings bindings1 = new OverloadBindings(overloadResolver);
        Map<String, ITypeVariableConstraint> variable2TypeVariable = bindings1.getVariable2TypeVariable();
        ITypeVariableConstraint t1 = new TypeVariableConstraint("T1");
        variable2TypeVariable.put("$a", t1);
        ITypeVariableConstraint t2 = new TypeVariableConstraint("T2");
        variable2TypeVariable.put("$b", t2);

        IOverloadBindings collection = createOverloadBindings(overloadResolver, bindings1);
        Map<String, ITypeVariableConstraint> result = collection.getVariable2TypeVariable();

        assertThat(result, hasKey("$a"));
        assertThat(result, hasKey("$b"));
        assertThat(result.get("$a"), is(not(t1)));
        assertThat(result.get("$a"), is(not(t2)));
    }

    @Test
    public void copyConstructor_HasTwoVariablesWithSameTypeVariable_ReferToSameTypeVariableConstraintAfterwards() {
        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        OverloadBindings bindings1 = new OverloadBindings(overloadResolver);
        Map<String, ITypeVariableConstraint> variable2TypeVariable = bindings1.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
        variable2TypeVariable.put("$b", new TypeVariableConstraint("T"));

        IOverloadBindings collection = createOverloadBindings(overloadResolver, bindings1);
        Map<String, ITypeVariableConstraint> result = collection.getVariable2TypeVariable();

        assertThat(result, hasKey("$a"));
        assertThat(result, hasKey("$b"));
        assertThat(result.get("$a"), is(result.get("$b")));
    }

    @Test
    public void copyConstructor_HasTwoVariablesOneIsFixed_TheOneIsStillFixedAfterwards() {
        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        OverloadBindings bindings1 = new OverloadBindings(overloadResolver);
        Map<String, ITypeVariableConstraint> variable2TypeVariable = bindings1.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
        variable2TypeVariable.put("$b", new TypeVariableConstraint("T"));

        IOverloadBindings collection = createOverloadBindings(overloadResolver, bindings1);
        Map<String, ITypeVariableConstraint> result = collection.getVariable2TypeVariable();

        assertThat(result, hasKey("$a"));
        assertThat(result.get("$a").getTypeVariable(), is("T"));
        assertThat(result.get("$a").hasFixedType(), is(true));
        assertThat(result, hasKey("$b"));
        assertThat(result.get("$b").getTypeVariable(), is("T"));
    }

    @Test
    public void copyConstructor_HasTwoVariablesFirstIsFixed_ReferToSameAndOnlyFirstIsFixed() {
        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        OverloadBindings bindings1 = new OverloadBindings(overloadResolver);
        Map<String, ITypeVariableConstraint> variable2TypeVariable = bindings1.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
        variable2TypeVariable.put("$b", new TypeVariableConstraint("T"));

        IOverloadBindings collection = createOverloadBindings(overloadResolver, bindings1);
        Map<String, ITypeVariableConstraint> result = collection.getVariable2TypeVariable();

        assertThat(result, hasKey("$a"));
        assertThat(result.get("$a").hasFixedType(), is(true));
        assertThat(result, hasKey("$b"));
        assertThat(result.get("$b").hasFixedType(), is(false));
        assertThat(result.get("$b").getTypeVariable(), is("T"));
        result.get("$a").setTypeVariable("T2");
        assertThat(result.get("$b").getTypeVariable(), is("T2"));
    }

    @Test
    public void copyConstructor_HasTwoVariablesFirstIsSecond_ReferToSameAndOnlySecondIsFixed() {
        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        OverloadBindings bindings1 = new OverloadBindings(overloadResolver);
        Map<String, ITypeVariableConstraint> variable2TypeVariable = bindings1.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
        variable2TypeVariable.put("$b", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));

        IOverloadBindings collection = createOverloadBindings(overloadResolver, bindings1);
        Map<String, ITypeVariableConstraint> result = collection.getVariable2TypeVariable();

        assertThat(result, hasKey("$a"));
        assertThat(result.get("$a").hasFixedType(), is(false));
        assertThat(result, hasKey("$b"));
        assertThat(result.get("$b").hasFixedType(), is(true));
        assertThat(result.get("$b").getTypeVariable(), is("T"));
        result.get("$a").setTypeVariable("T2");
        assertThat(result.get("$b").getTypeVariable(), is("T2"));
    }

    @Test
    public void copyConstructor_HasTwoLowerBoundsWithSameTypeVariableRef_ReferToSameTypeVariableConstraintAfterwards() {
        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        OverloadBindings bindings1 = new OverloadBindings(overloadResolver);
        Map<String, ITypeVariableConstraint> variable2TypeVariable = bindings1.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T1"));
        variable2TypeVariable.put("$b", new TypeVariableConstraint("T2"));
        variable2TypeVariable.put("$c", new TypeVariableConstraint("T3"));
        bindings1.addLowerBound("T1", new TypeVariableConstraint("T3"));
        bindings1.addLowerBound("T2", new TypeVariableConstraint("T3"));

        IOverloadBindings collection = createOverloadBindings(overloadResolver, bindings1);
        Map<String, ITypeVariableConstraint> result = collection.getVariable2TypeVariable();

        assertThat(result, hasKey("$a"));
        assertThat(result, hasKey("$b"));
        assertThat(result, hasKey("$c"));
        assertThat(collection.hasLowerBounds("T1"), is(true));
        assertThat(collection.hasLowerBounds("T2"), is(true));
        IConstraint t1 = collection.getLowerBounds("T1").iterator().next();
        IConstraint t2 = collection.getLowerBounds("T2").iterator().next();
        assertThat(t1.getId(), is("@T3"));
        assertThat(t1, is(t2));
    }

    @Test
    public void tryToFixateType_IsAlreadyFixedNoLowerBounds_ReturnsTrue() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));

        //act
        boolean result = collection.tryToFixateType("$a");

        assertThat(result, is(true));
        assertThat(collection.hasLowerBounds("T"), is(false));
    }

    @Test
    public void tryToFixateType_IsAlreadyFixedSomeLowerBounds_DoesNotRemoveLowerBounds() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
        collection.addLowerBound("T", new TypeConstraint(intType));

        //act
        boolean result = collection.tryToFixateType("$a");

        assertThat(result, is(true));
        assertThat(collection.hasLowerBounds("T"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T"), contains("int"));
    }

    @Test
    public void tryToFixateType_IsAlreadyFixedOnlySelfAsLowerBound_RemovesAllLowerBounds() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
        collection.addLowerBound("T", new TypeVariableConstraint("T"));

        //act
        boolean result = collection.tryToFixateType("$a");

        assertThat(result, is(true));
        assertThat(collection.hasLowerBounds("T"), is(false));
    }

    @Test
    public void tryToFixateType_IsAlreadyFixedSelfAndTypeAsLowerBound_RemoveSelfRefButNotType() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new FixedTypeVariableConstraint(new TypeVariableConstraint("T")));
        collection.addLowerBound("T", new TypeVariableConstraint("T"));
        collection.addLowerBound("T", new TypeConstraint(intType));
        collection.addLowerBound("T", new TypeConstraint(floatType));

        //act
        boolean result = collection.tryToFixateType("$a");

        assertThat(result, is(true));
        assertThat(collection.hasLowerBounds("T"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T"), contains("int", "float"));
    }

    @Test
    public void resolveDependencies_HasNoLowerBounds_HasStillNoLowerBounds() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));

        //act
        collection.resolveDependencies("$a", new HashSet<String>());

        //assert
        assertThat(collection.hasLowerBounds("T"), is(false));
    }

    @Test
    public void resolveDependencies_HasOnlyTypeConstraintAsLowerBounds_NothingNewAdded() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
        collection.addLowerBound("T", new TypeConstraint(intType));

        //act
        collection.resolveDependencies("$a", new HashSet<String>());

        //assert
        assertThat(collection.hasLowerBounds("T"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T"), contains("int"));
    }


    @Test
    public void resolveDependencies_HasSelfRefAsLowerBounds_SelfRefIsNotRemoved() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
        collection.addLowerBound("T", new TypeVariableConstraint("T"));

        //act
        collection.resolveDependencies("$a", new HashSet<String>());

        //assert
        assertThat(collection.hasLowerBounds("T"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T"), contains("@T"));
    }

    @Test
    public void resolveDependencies_HasSelfARefAndTypeAsLowerBounds_NothingRemoved() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
        collection.addLowerBound("T", new TypeVariableConstraint("T"));
        collection.addLowerBound("T", new TypeConstraint(intType));

        //act
        collection.resolveDependencies("$a", new HashSet<String>());

        //assert
        assertThat(collection.hasLowerBounds("T"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T"), contains("@T", "int"));
    }

    @Test
    public void resolveDependencies_HasRefToOtherParam_NothingRemoved() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
        collection.addLowerBound("T", new TypeVariableConstraint("T2"));
        Set<String> parameterConstraintIds = new HashSet<>();
        parameterConstraintIds.add("@T2");

        //act
        collection.resolveDependencies("$a", parameterConstraintIds);

        //assert
        assertThat(collection.hasLowerBounds("T"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T"), contains("@T2"));
    }

    @Test
    public void resolveDependencies_HasRefWithInt_AddIntToLowerBound() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
        collection.addLowerBound("T", new TypeVariableConstraint("T2"));
        collection.addLowerBound("T2", new TypeConstraint(intType));

        //act
        collection.resolveDependencies("$a", new HashSet<String>());

        //assert
        assertThat(collection.hasLowerBounds("T"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T"), contains("int"));
    }

    @Test
    public void resolveDependencies_HasRefWithInt_NothingRemovedFromRef() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        Map<String, ITypeVariableConstraint> variable2TypeVariable = collection.getVariable2TypeVariable();
        variable2TypeVariable.put("$a", new TypeVariableConstraint("T"));
        collection.addLowerBound("T", new TypeVariableConstraint("T2"));
        collection.addLowerBound("T2", new TypeConstraint(intType));

        //act
        collection.resolveDependencies("$a", new HashSet<String>());

        //assert
        assertThat(collection.hasLowerBounds("T2"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T2"), contains("int"));
    }

    @Test
    public void renameTypeVariable_Standard_TransferConstraints() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        collection.addLowerBound("T", new TypeConstraint(intType));
        collection.addUpperBound("T", new TypeConstraint(numType));

        //act
        collection.renameTypeVariable(new TypeVariableConstraint("T"), "T2");

        //assert
        assertThat(collection.hasLowerBounds("T2"), is(true));
        assertThat(collection.hasUpperBounds("T2"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T2"), contains("int"));
        assertThat(collection.getUpperBoundConstraintIds("T2"), contains("num"));
    }

    @Test
    public void renameTypeVariable_Standard_RemovesOldLowerAndUpperBounds() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        collection.addLowerBound("T", new TypeConstraint(intType));
        collection.addUpperBound("T", new TypeConstraint(numType));

        //act
        collection.renameTypeVariable(new TypeVariableConstraint("T"), "T2");

        //assert
        assertThat(collection.hasLowerBounds("T"), is(false));
        assertThat(collection.hasUpperBounds("T"), is(false));
    }

    @Test
    public void renameTypeVariable_HasUpperBoundDependencies_RenamesDependency() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        collection.addLowerBound("T1", new TypeVariableConstraint("T"));

        //act
        collection.renameTypeVariable(new TypeVariableConstraint("T"), "T2");

        //assert
        assertThat(collection.hasLowerBounds("T1"), is(true));
        assertThat(collection.getLowerBoundConstraintIds("T1"), contains("@T2"));
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
        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        OverloadBindings bindings1 = new OverloadBindings(overloadResolver);
        bindings1.getNextTypeVariable();

        IOverloadBindings collection = createOverloadBindings(overloadResolver, bindings1);
        TypeVariableConstraint result = collection.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("T2"));
    }

    @Test
    public void addLowerBound_IsSubtypeOfExistingUpper_AddsLowerBound() {
        String typeVariable = "T";
        IConstraint constraint1 = new TypeConstraint(intType);
        IConstraint constraint2 = new TypeConstraint(numType);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint2);
        collection.addLowerBound(typeVariable, constraint1);
        Collection<IConstraint> result = collection.getLowerBounds(typeVariable);

        assertThat(result, hasItem(constraint1));
        assertThat(result, hasSize(1));
    }

    @Test(expected = LowerBoundException.class)
    public void addLowerBound_IsNotSubtypeOfExistingUpper_ThrowsLowerBoundException() {
        String typeVariable = "T";
        IConstraint intConstraint = new TypeConstraint(intType);
        IConstraint numConstraint = new TypeConstraint(numType);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, intConstraint);
        collection.addLowerBound(typeVariable, numConstraint);

        //assert in annotation
    }

    @Test
    public void addLowerBound_RefWhichIsInBound_AddsLowerBound() {
        String lhs = "Tlhs";
        IConstraint lhsConstraint = new TypeConstraint(numType);
        String rhs = "Trhs";
        IConstraint rhsConstraint = new TypeConstraint(intType);
        TypeVariableConstraint rhsTypeVariableConstraint = new TypeVariableConstraint(rhs);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(lhs, lhsConstraint);
        collection.addUpperBound(rhs, rhsConstraint);
        collection.addLowerBound(lhs, rhsTypeVariableConstraint);
        Collection<IConstraint> resultLhsLower = collection.getLowerBounds(lhs);
        Collection<IConstraint> resultRhsUpper = collection.getUpperBounds(rhs);

        assertThat(resultLhsLower, hasItem(rhsTypeVariableConstraint));
        assertThat(resultLhsLower, hasSize(1));
        assertThat(resultRhsUpper, hasItem(rhsConstraint));
        assertThat(resultRhsUpper, hasItem(lhsConstraint));
        assertThat(resultRhsUpper, hasSize(2));
    }

    @Test
    public void addLowerBound_RefWithoutLowerWhoseUpperIsNotSubtypeOfExistingUpper_AddsLowerBoundAndNarrowsRef() {
        String lhs = "Tlhs";
        IConstraint lhsConstraint = new TypeConstraint(intType);
        String rhs = "Trhs";
        IConstraint rhsConstraint = new TypeConstraint(numType);
        TypeVariableConstraint rhsTypeVariableConstraint = new TypeVariableConstraint(rhs);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(lhs, lhsConstraint);
        collection.addUpperBound(rhs, rhsConstraint);
        collection.addLowerBound(lhs, rhsTypeVariableConstraint);
        Collection<IConstraint> resultLhsLower = collection.getLowerBounds(lhs);
        Collection<IConstraint> resultRhsUpper = collection.getUpperBounds(rhs);

        assertThat(resultLhsLower, hasItem(rhsTypeVariableConstraint));
        assertThat(resultLhsLower, hasSize(1));
        //have been narrowed down to lhs upper bound
        assertThat(resultRhsUpper, hasItem(rhsConstraint));
        assertThat(resultRhsUpper, hasItem(lhsConstraint));
        assertThat(resultRhsUpper, hasSize(2));
    }

    @Test
    public void addLowerBound_RefLowerIsSubtypeAndUpperIsNotSubtypeOfExistingUpper_AddsLowerBoundAndNarrowRef() {
        String lhs = "Tlhs";
        IConstraint lhsConstraint = new TypeConstraint(intType);
        String rhs = "Trhs";
        IConstraint rhsConstraint = new TypeConstraint(numType);
        TypeVariableConstraint rhsTypeVariableConstraint = new TypeVariableConstraint(rhs);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(lhs, lhsConstraint);
        collection.addUpperBound(rhs, rhsConstraint);
        collection.addLowerBound(rhs, lhsConstraint);
        collection.addLowerBound(lhs, rhsTypeVariableConstraint);
        Collection<IConstraint> resultLhsLower = collection.getLowerBounds(lhs);
        Collection<IConstraint> resultRhsUpper = collection.getUpperBounds(rhs);

        assertThat(resultLhsLower, hasItem(rhsTypeVariableConstraint));
        assertThat(resultLhsLower, hasSize(1));
        //has been narrowed down to lhs upper bound
        assertThat(resultRhsUpper, hasItem(rhsConstraint));
        assertThat(resultRhsUpper, hasItem(lhsConstraint));
        assertThat(resultRhsUpper, hasSize(2));
    }

    @Test(expected = BoundException.class)
    public void addLowerBound_RefNeitherLowerNorUpperAreSubtypeOfExistingUpper_ThrowsException() {
        String lhs = "Tlhs";
        IConstraint lhsConstraint = new TypeConstraint(intType);
        String rhs = "Trhs";
        IConstraint rhsUpperConstraint = new TypeConstraint(numType);
        IConstraint rhsLowerConstraint = new TypeConstraint(floatType);
        TypeVariableConstraint rhsTypeVariableConstraint = new TypeVariableConstraint(rhs);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(lhs, lhsConstraint);
        collection.addUpperBound(rhs, rhsUpperConstraint);
        collection.addLowerBound(rhs, rhsLowerConstraint);
        collection.addLowerBound(lhs, rhsTypeVariableConstraint);

        //assert see annotation
    }

    @Test
    public void addLowerBound_SelfRef_AddsLowerBound() {
        String typeVariable = "T";
        IConstraint constraint1 = new TypeConstraint(numType);
        TypeVariableConstraint selfRef = new TypeVariableConstraint(typeVariable);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint1);
        collection.addLowerBound(typeVariable, selfRef);

        Collection<IConstraint> resultLhsLower = collection.getLowerBounds(typeVariable);
        assertThat(resultLhsLower, hasItem(selfRef));
    }

    @Test
    public void addUpperBound_LowerBoundIsSubType_AddsUpperBound() {
        String typeVariable = "T";
        IConstraint constraint1 = new TypeConstraint(intType);
        IConstraint constraint2 = new TypeConstraint(numType);

        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(typeVariable, constraint1);
        collection.addUpperBound(typeVariable, constraint2);
        Collection<IConstraint> result = collection.getUpperBounds(typeVariable);

        assertThat(result, hasItem(constraint2));
        assertThat(result, hasSize(1));
    }

    @Test(expected = UpperBoundException.class)
    public void addUpperBound_LowerBoundIsParentType_ThrowsUpperBoundException() {
        String typeVariable = "T";
        IConstraint numConstraint = new TypeConstraint(numType);
        IConstraint intConstraint = new TypeConstraint(intType);

        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(typeVariable, numConstraint);
        collection.addUpperBound(typeVariable, intConstraint);

        //assert in annotation
    }

    @Test
    public void addUpperBound_HasOtherTypeVariableAsLowerBound_AddsUpperBoundAndUpdatesOtherTypeVariable() {
        String lhs = "Tlhs";
        IConstraint scalarTypeConstraint = new TypeConstraint(scalarType);
        IConstraint numTypeConstraint = new TypeConstraint(numType);
        String rhs = "Trhs";
        IConstraint rhsConstraint = new TypeConstraint(intType);
        TypeVariableConstraint rhsTypeVariableConstraint = new TypeVariableConstraint(rhs);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(lhs, scalarTypeConstraint);
        collection.addUpperBound(rhs, rhsConstraint);
        collection.addLowerBound(lhs, rhsTypeVariableConstraint);
        collection.addUpperBound(lhs, numTypeConstraint);
        Collection<IConstraint> resultLhsLower = collection.getLowerBounds(lhs);
        Collection<IConstraint> resultRhsUpper = collection.getUpperBounds(rhs);

        assertThat(resultLhsLower, hasItem(rhsTypeVariableConstraint));
        assertThat(resultLhsLower, hasSize(1));
        assertThat(resultRhsUpper, hasItem(rhsConstraint));
        assertThat(resultRhsUpper, hasItem(scalarTypeConstraint));
        assertThat(resultRhsUpper, hasItem(numTypeConstraint));
        assertThat(resultRhsUpper, hasSize(3));
    }

    @Test
    public void addUpperBound_hasSelfRefLowerAndIsSameAsCurrentUpper_AddsUpperBound() {
        String typeVariable = "T";
        IConstraint constraint1 = new TypeConstraint(numType);
        IConstraint constraint2 = new TypeConstraint(numType);
        TypeVariableConstraint selfRef = new TypeVariableConstraint(typeVariable);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint1);
        collection.addLowerBound(typeVariable, selfRef);
        collection.addUpperBound(typeVariable, constraint2);
        Collection<IConstraint> resultUpper = collection.getUpperBounds(typeVariable);

        assertThat(resultUpper, hasItem(constraint1));
        assertThat(resultUpper, hasSize(1));
    }

    @Test
    public void addUpperBound_hasSelfRefLowerAndIsSubtypeOfCurrentUpper_AddsUpperBound() {
        String typeVariable = "T";
        IConstraint constraint1 = new TypeConstraint(numType);
        IConstraint constraint2 = new TypeConstraint(intType);
        TypeVariableConstraint selfRef = new TypeVariableConstraint(typeVariable);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint1);
        collection.addLowerBound(typeVariable, selfRef);
        collection.addUpperBound(typeVariable, constraint2);
        Collection<IConstraint> resultUpper = collection.getUpperBounds(typeVariable);

        assertThat(resultUpper, hasItem(constraint1));
        assertThat(resultUpper, hasItem(constraint2));
        assertThat(resultUpper, hasSize(2));
    }

    @Test(expected = BoundException.class)
    public void addUpperBound_hasSelfRefLowerAndIsParentOfCurrentUpper_AddsUpperBound() {
        String typeVariable = "T";
        IConstraint constraint1 = new TypeConstraint(intType);
        IConstraint constraint2 = new TypeConstraint(numType);
        TypeVariableConstraint selfRef = new TypeVariableConstraint(typeVariable);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint1);
        collection.addLowerBound(typeVariable, selfRef);
        collection.addUpperBound(typeVariable, constraint2);

        //assert in annotation
    }

    @Test
    public void addUpperBound_RefWithCycle_AddUpperBoundAndNoEndlessLoop() {
        //Corresponds to $a = 1; $b = $a; $b = 1.2; $a = $b;
        String e1 = "e1";
        IConstraint constraintE1 = new TypeConstraint(intType);
        String e2 = "e2";
        IConstraint constraintE2 = new TypeConstraint(floatType);

        String $a = "Ta";
        String $b = "Tb";
        ITypeVariableConstraint assign1 = new FixedTypeVariableConstraint(new TypeVariableConstraint(e1));
        IConstraint assign2 = new TypeVariableConstraint($a);
        ITypeVariableConstraint assign3 = new FixedTypeVariableConstraint(new TypeVariableConstraint(e2));
        IConstraint assign4 = new TypeVariableConstraint($b);

        //act
        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(e1, constraintE1);
        collection.addLowerBound(e2, constraintE2);
        collection.addLowerBound($a, assign1);
        collection.addLowerBound($b, assign2);
        collection.addLowerBound($b, assign3);
        collection.addLowerBound($a, assign4);

        Collection<IConstraint> resultLowerA = collection.getLowerBounds($a);
        assertThat(resultLowerA, hasItems(constraintE1, assign4));
        Collection<IConstraint> resultLowerB = collection.getLowerBounds($b);
        assertThat(resultLowerB, hasItems(constraintE1, constraintE2));
    }

    private IOverloadBindings createOverloadBindings() {
        return createOverloadBindings(new OverloadResolver());
    }


    protected IOverloadBindings createOverloadBindings(IOverloadResolver overloadResolver) {
        return new OverloadBindings(overloadResolver);
    }

    protected IOverloadBindings createOverloadBindings(
            IOverloadResolver overloadResolver, OverloadBindings bindings) {
        return new OverloadBindings(overloadResolver, bindings);
    }
}
