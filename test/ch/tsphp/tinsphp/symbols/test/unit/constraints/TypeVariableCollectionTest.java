/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import ch.tsphp.tinsphp.symbols.constraints.TypeVariableCollection;
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

public class TypeVariableCollectionTest extends ATypeTest
{

    @Test
    public void copyConstructor_Standard_TransfersLowerAndUpperBounds() {
        String typeVariable = "T";
        IConstraint lowerConstraint = new TypeConstraint(intType);
        IConstraint upperConstraint = new TypeConstraint(intType);

        IOverloadResolver overloadResolver = mock(IOverloadResolver.class);
        when(overloadResolver.isFirstSameOrParentTypeOfSecond(intType, intType)).thenReturn(true);

        TypeVariableCollection collection1 = new TypeVariableCollection(overloadResolver);
        collection1.addLowerBound(typeVariable, lowerConstraint);
        collection1.addUpperBound(typeVariable, upperConstraint);

        ITypeVariableCollection collection = createTypeVariableCollection(overloadResolver, collection1);
        Collection<IConstraint> lowerResult = collection.getLowerBounds(typeVariable);
        Collection<IConstraint> upperResult = collection.getUpperBounds(typeVariable);

        assertThat(lowerResult, hasItems(lowerConstraint));
        assertThat(upperResult, hasItems(upperConstraint));
    }

    @Test(expected = NullPointerException.class)
    public void getUpperBounds_NothingDefined_ReturnsNull() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.getUpperBounds(typeVariable);

        //assert in annotation
    }

    @Test
    public void getUpperBounds_OneDefined_ReturnsSetWithId() {
        String typeVariable = "T";
        IConstraint constraint = mock(IConstraint.class);
        when(constraint.getId()).thenReturn("id");

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addUpperBound(typeVariable, constraint);
        Collection<IConstraint> result = collection.getUpperBounds(typeVariable);

        assertThat(result, hasItem(constraint));
        assertThat(result, hasSize(1));
    }

    @Test(expected = NullPointerException.class)
    public void getLowerBounds_NothingDefined_ReturnsNull() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.getLowerBounds(typeVariable);

        //assert in annotation
    }

    @Test
    public void getLowerBounds_OneDefined_ReturnsSetWithId() {
        String typeVariable = "T";
        IConstraint constraint = mock(IConstraint.class);
        when(constraint.getId()).thenReturn("id");

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addLowerBound(typeVariable, constraint);
        Collection<IConstraint> result = collection.getLowerBounds(typeVariable);

        assertThat(result, hasItem(constraint));
        assertThat(result, hasSize(1));
    }

    @Test
    public void hasLowerBounds_NothingDefined_ReturnsFalse() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        boolean result = collection.hasLowerBounds(typeVariable);

        assertThat(result, is(false));
    }

    @Test
    public void hasLowerBounds_OneDefined_ReturnsTrue() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addLowerBound(typeVariable, mock(IConstraint.class));
        boolean result = collection.hasLowerBounds(typeVariable);

        assertThat(result, is(true));
    }

    @Test
    public void hasUpperBounds_NothingDefined_ReturnsFalse() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        boolean result = collection.hasUpperBounds(typeVariable);

        assertThat(result, is(false));
    }


    @Test
    public void hasUpperBounds_OneDefined_ReturnsTrue() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addUpperBound(typeVariable, mock(IConstraint.class));
        boolean result = collection.hasUpperBounds(typeVariable);

        assertThat(result, is(true));
    }

    @Test
    public void getTypeVariablesWithLowerBounds_NothingDefined_ReturnsEmptySet() {
        //no arrange necessary

        ITypeVariableCollection collection = createTypeVariableCollection();
        Set<String> result = collection.getTypeVariablesWithLowerBounds();

        assertThat(result, is(empty()));
    }

    @Test
    public void getTypeVariablesWithLowerBounds_OneDefined_ReturnsTypeVariable() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addLowerBound(typeVariable, mock(IConstraint.class));
        Set<String> result = collection.getTypeVariablesWithLowerBounds();

        assertThat(result, hasItem(typeVariable));
        assertThat(result, hasSize(1));
    }

    @Test
    public void getTypeVariablesWithUpperBounds_NothingDefined_ReturnsEmptySet() {
        //no arrange necessary

        ITypeVariableCollection collection = createTypeVariableCollection();
        Set<String> result = collection.getTypeVariablesWithUpperBounds();

        assertThat(result, is(empty()));
    }

    @Test
    public void getTypeVariablesWithUpperBounds_OneDefined_ReturnsTypeVariable() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addUpperBound(typeVariable, mock(IConstraint.class));
        Set<String> result = collection.getTypeVariablesWithUpperBounds();

        assertThat(result, hasItem(typeVariable));
        assertThat(result, hasSize(1));
    }

    @Test(expected = NullPointerException.class)
    public void getLowerBoundConstraintIds_NothingDefined_ReturnsNull() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.getLowerBoundConstraintIds(typeVariable);

        //assert in annotation
    }

    @Test
    public void getLowerBoundConstraintIds_OneDefined_ReturnsSetWithId() {
        String typeVariable = "T";
        IConstraint constraint = mock(IConstraint.class);
        when(constraint.getId()).thenReturn("id");

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addLowerBound(typeVariable, constraint);
        Set<String> result = collection.getLowerBoundConstraintIds(typeVariable);

        assertThat(result, hasItem("id"));
        assertThat(result, hasSize(1));
    }

    @Test(expected = NullPointerException.class)
    public void getUpperBoundConstraintIds_NothingDefined_ReturnsNull() {
        String typeVariable = "T";

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.getUpperBoundConstraintIds(typeVariable);

        //assert in annotation
    }

    @Test
    public void getUpperBoundConstraintIds_OneDefined_ReturnsSetWithId() {
        String typeVariable = "T";
        IConstraint constraint = mock(IConstraint.class);
        when(constraint.getId()).thenReturn("id");

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addUpperBound(typeVariable, constraint);
        Set<String> result = collection.getUpperBoundConstraintIds(typeVariable);

        assertThat(result, hasItem("id"));
        assertThat(result, hasSize(1));
    }

    @Test
    public void addLowerBound_DoesAlreadyExists_NotAdded() {
        String typeVariable = "T";
        IConstraint constraint = mock(IConstraint.class);

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addLowerBound(typeVariable, constraint);
        collection.addLowerBound(typeVariable, constraint);
        Collection<IConstraint> result = collection.getLowerBounds(typeVariable);

        assertThat(result, hasItem(constraint));
        assertThat(result, hasSize(1));
    }

    @Test
    public void addUpperBound_DoesAlreadyExists_NotAdded() {
        String typeVariable = "T";
        IConstraint constraint = mock(IConstraint.class);

        ITypeVariableCollection collection = createTypeVariableCollection();
        collection.addUpperBound(typeVariable, constraint);
        collection.addUpperBound(typeVariable, constraint);
        Collection<IConstraint> result = collection.getUpperBounds(typeVariable);

        assertThat(result, hasItem(constraint));
        assertThat(result, hasSize(1));
    }

    private ITypeVariableCollection createTypeVariableCollection() {
        return createTypeVariableCollection(mock(IOverloadResolver.class));
    }

    protected ITypeVariableCollection createTypeVariableCollection(IOverloadResolver overloadResolver) {
        return new TypeVariableCollection(overloadResolver);
    }

    protected ITypeVariableCollection createTypeVariableCollection(
            IOverloadResolver overloadResolver, TypeVariableCollection typeVariableCollection) {
        return new TypeVariableCollection(overloadResolver, typeVariableCollection);
    }
}
