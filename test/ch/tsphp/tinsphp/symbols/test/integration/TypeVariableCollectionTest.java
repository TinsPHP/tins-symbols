/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration;

import ch.tsphp.tinsphp.common.inference.constraints.BoundException;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.common.inference.constraints.LowerBoundException;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.UpperBoundException;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.constraints.TypeVariableCollection;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class TypeVariableCollectionTest extends ATypeTest
{

    @Test
    public void addLowerBound_IsSubtypeOfExistingUpper_AddsLowerBound() {
        String typeVariable = "T";
        IConstraint constraint1 = new TypeConstraint(intType);
        IConstraint constraint2 = new TypeConstraint(numType);

        ITypeVariableCollection collection = createTypeVariableCollection();
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

        ITypeVariableCollection collection = createTypeVariableCollection();
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

        ITypeVariableCollection collection = createTypeVariableCollection();
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

        ITypeVariableCollection collection = createTypeVariableCollection();
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

        ITypeVariableCollection collection = createTypeVariableCollection();
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

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addUpperBound(lhs, lhsConstraint);
        collection.addUpperBound(rhs, rhsUpperConstraint);
        collection.addLowerBound(rhs, rhsLowerConstraint);
        collection.addLowerBound(lhs, rhsTypeVariableConstraint);

        //assert see annotation
    }

    @Test
    public void addUpperBound_LowerBoundIsSubType_AddsUpperBound() {
        String typeVariable = "T";
        IConstraint constraint1 = new TypeConstraint(intType);
        IConstraint constraint2 = new TypeConstraint(numType);

        ITypeVariableCollection collection = createTypeVariableCollection();
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

        ITypeVariableCollection collection = createTypeVariableCollection();
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

        ITypeVariableCollection collection = createTypeVariableCollection();
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


    private ITypeVariableCollection createTypeVariableCollection() {
        return createTypeVariableCollection(new OverloadResolver());
    }

    protected ITypeVariableCollection createTypeVariableCollection(IOverloadResolver overloadResolver) {
        return new TypeVariableCollection(overloadResolver);
    }
}
