/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverloadBindingsTest extends ATypeTest
{

    @Test
    public void copyConstructor_Standard_TransfersLowerAndUpperBounds() {
        String typeVariable = "T";
        IConstraint lowerConstraint = new TypeConstraint(intType);
        IConstraint upperConstraint = new TypeConstraint(intType);

        ISymbolFactory symbolFactory = mock(ISymbolFactory.class);
        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        when(overloadResolver.isFirstSameOrParentTypeOfSecond(intType, intType)).thenReturn(true);

        OverloadBindings collection1 = new OverloadBindings(symbolFactory, overloadResolver);
        collection1.addLowerBound(typeVariable, lowerConstraint);
        collection1.addUpperBound(typeVariable, upperConstraint);

        IOverloadBindings collection = createTypeVariableCollection(symbolFactory, overloadResolver, collection1);
        Collection<IConstraint> lowerResult = collection.getLowerBounds(typeVariable);
        Collection<IConstraint> upperResult = collection.getUpperBounds(typeVariable);

        assertThat(lowerResult, hasItems(lowerConstraint));
        assertThat(upperResult, hasItems(upperConstraint));
    }

    @Test(expected = NullPointerException.class)
    public void getUpperBounds_NothingDefined_ReturnsNull() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        collection.getUpperBounds(typeVariable);

        //assert in annotation
    }

    @Test
    public void getUpperBounds_OneDefined_ReturnsSetWithId() {
        String typeVariable = "T";
        IConstraint constraint = new TypeConstraint(intType);
        when(constraint.getId()).thenReturn("id");

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint);
        Collection<IConstraint> result = collection.getUpperBounds(typeVariable);

        assertThat(result, hasItem(constraint));
        assertThat(result, hasSize(1));
    }

    @Test(expected = NullPointerException.class)
    public void getLowerBounds_NothingDefined_ReturnsNull() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        collection.getLowerBounds(typeVariable);

        //assert in annotation
    }

    @Test
    public void getLowerBounds_OneDefined_ReturnsSetWithId() {
        String typeVariable = "T";
        IConstraint constraint = new TypeConstraint(intType);
        when(constraint.getId()).thenReturn("id");

        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(typeVariable, constraint);
        Collection<IConstraint> result = collection.getLowerBounds(typeVariable);

        assertThat(result, hasItem(constraint));
        assertThat(result, hasSize(1));
    }

    @Test
    public void hasLowerBounds_NothingDefined_ReturnsFalse() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        boolean result = collection.hasLowerBounds(typeVariable);

        assertThat(result, is(false));
    }

    @Test
    public void hasLowerBounds_OneDefined_ReturnsTrue() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(typeVariable, new TypeConstraint(intType));
        boolean result = collection.hasLowerBounds(typeVariable);

        assertThat(result, is(true));
    }

    @Test
    public void hasUpperBounds_NothingDefined_ReturnsFalse() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        boolean result = collection.hasUpperBounds(typeVariable);

        assertThat(result, is(false));
    }


    @Test
    public void hasUpperBounds_OneDefined_ReturnsTrue() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, new TypeConstraint(intType));
        boolean result = collection.hasUpperBounds(typeVariable);

        assertThat(result, is(true));
    }

    @Test
    public void getTypeVariablesWithLowerBounds_NothingDefined_ReturnsEmptySet() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        Set<String> result = collection.getTypeVariablesWithLowerBounds();

        assertThat(result, is(empty()));
    }

    @Test
    public void getTypeVariablesWithLowerBounds_OneDefined_ReturnsTypeVariable() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(typeVariable, new TypeConstraint(intType));
        Set<String> result = collection.getTypeVariablesWithLowerBounds();

        assertThat(result, hasItem(typeVariable));
        assertThat(result, hasSize(1));
    }

    @Test
    public void getTypeVariablesWithUpperBounds_NothingDefined_ReturnsEmptySet() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        Set<String> result = collection.getTypeVariablesWithUpperBounds();

        assertThat(result, is(empty()));
    }

    @Test
    public void getTypeVariablesWithUpperBounds_OneDefined_ReturnsTypeVariable() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, new TypeConstraint(intType));
        Set<String> result = collection.getTypeVariablesWithUpperBounds();

        assertThat(result, hasItem(typeVariable));
        assertThat(result, hasSize(1));
    }

    @Test(expected = NullPointerException.class)
    public void getLowerBoundConstraintIds_NothingDefined_ReturnsNull() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        collection.getLowerBoundConstraintIds(typeVariable);

        //assert in annotation
    }

    @Test
    public void getLowerBoundConstraintIds_OneDefined_ReturnsSetWithId() {
        String typeVariable = "T";
        IConstraint constraint = new TypeConstraint(intType);
        when(constraint.getId()).thenReturn("id");

        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(typeVariable, constraint);
        Set<String> result = collection.getLowerBoundConstraintIds(typeVariable);

        assertThat(result, hasItem("id"));
        assertThat(result, hasSize(1));
    }

    @Test(expected = NullPointerException.class)
    public void getUpperBoundConstraintIds_NothingDefined_ReturnsNull() {
        String typeVariable = "T";

        IOverloadBindings collection = createOverloadBindings();
        collection.getUpperBoundConstraintIds(typeVariable);

        //assert in annotation
    }

    @Test
    public void getUpperBoundConstraintIds_OneDefined_ReturnsSetWithId() {
        String typeVariable = "T";
        IConstraint constraint = new TypeConstraint(intType);
        when(constraint.getId()).thenReturn("id");

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint);
        Set<String> result = collection.getUpperBoundConstraintIds(typeVariable);

        assertThat(result, hasItem("id"));
        assertThat(result, hasSize(1));
    }

    @Test
    public void addLowerBound_DoesAlreadyExists_NotAdded() {
        String typeVariable = "T";
        IConstraint constraint = new TypeConstraint(intType);

        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(typeVariable, constraint);
        collection.addLowerBound(typeVariable, constraint);
        Collection<IConstraint> result = collection.getLowerBounds(typeVariable);

        assertThat(result, hasItem(constraint));
        assertThat(result, hasSize(1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addLowerBound_notTypeNorTypeVariableConstraint_ThrowsClassCastException() {
        String typeVariable = "T";
        IConstraint constraint = mock(IConstraint.class);

        IOverloadBindings collection = createOverloadBindings();
        collection.addLowerBound(typeVariable, constraint);

        //assert in annotation
    }

    @Test
    public void addUpperBound_DoesAlreadyExists_NotAdded() {
        String typeVariable = "T";
        IConstraint constraint = new TypeConstraint(intType);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint);
        collection.addUpperBound(typeVariable, constraint);
        Collection<IConstraint> result = collection.getUpperBounds(typeVariable);

        assertThat(result, hasItem(constraint));
        assertThat(result, hasSize(1));
    }

    @Test(expected = ClassCastException.class)
    public void addUpperBound_notTypeConstraint_ThrowsClassCastException() {
        String typeVariable = "T";
        IConstraint constraint = mock(IConstraint.class);

        IOverloadBindings collection = createOverloadBindings();
        collection.addUpperBound(typeVariable, constraint);

        //assert in annotation
    }

    private IOverloadBindings createOverloadBindings() {
        return createTypeVariableCollection(mock(ISymbolFactory.class), mock(IOverloadResolver.class));
    }

    protected IOverloadBindings createTypeVariableCollection(
            ISymbolFactory symbolFactory, IOverloadResolver overloadResolver) {
        return new OverloadBindings(symbolFactory, overloadResolver);
    }

    protected IOverloadBindings createTypeVariableCollection(
            ISymbolFactory symbolFactory,
            IOverloadResolver overloadResolver,
            OverloadBindings typeVariableCollection) {
        return new OverloadBindings(symbolFactory, overloadResolver, typeVariableCollection);
    }
}
