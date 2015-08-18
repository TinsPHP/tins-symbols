/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.AContainerTypeSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AContainerTypeSymbolTest
{

    private class DummyAContainerTypeSymbol extends AContainerTypeSymbol
    {

        public DummyAContainerTypeSymbol(ITypeHelper theTypeHelper) {
            super(theTypeHelper);
        }

        @Override
        protected boolean firstTypeReplacesSecond(ITypeSymbol newTypeSymbol, ITypeSymbol existingTypeSymbol) {
            return false;
        }

        @Override
        protected boolean secondTypeReplacesFirst(ITypeSymbol newTypeSymbol, ITypeSymbol existingTypeSymbol) {
            return false;
        }

        @Override
        public String getTypeSeparator() {
            return null;
        }

        @Override
        public String getDefaultName() {
            return null;
        }

        @Override
        public IContainerTypeSymbol copy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
            return null;
        }
    }

    @Test
    public void isFixed_NothingAdded_ReturnsTrue() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, is(true));
    }

    @Test
    public void isFixed_AddingNonPolymorphicType_ReturnsTrue() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.addTypeSymbol(typeSymbol);
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, is(true));
    }

    @Test
    public void isFixed_AddingAFixedPolymorphicType_ReturnsTrue() {
        IPolymorphicTypeSymbol typeSymbol = mock(IPolymorphicTypeSymbol.class);
        when(typeSymbol.isFixed()).thenReturn(true);

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.addTypeSymbol(typeSymbol);
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, is(true));
    }

    @Test
    public void isFixed_AddingANonFixedPolymorphicType_ReturnsFalse() {
        IPolymorphicTypeSymbol typeSymbol = mock(IPolymorphicTypeSymbol.class);
        when(typeSymbol.isFixed()).thenReturn(false);

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.addTypeSymbol(typeSymbol);
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, is(false));
    }

    @Test
    public void getDefinitionScope_Standard_ReturnsNull() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        IScope result = containerTypeSymbol.getDefinitionScope();

        assertThat(result, is(nullValue()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getParentTypeSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.getParentTypeSymbols();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefaultValue_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.getDefaultValue();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.addModifier(123);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.removeModifier(123);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.getModifiers();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.setModifiers(new ModifierSet());

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefinitionAst_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.getDefinitionAst();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setDefinitionScope_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.setDefinitionScope(mock(IScope.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.getType();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setType_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.setType(mock(ITypeSymbol.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isFalseable_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.isFalseable();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isNullable_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol containerTypeSymbol = createContainerTypeSymbol();
        containerTypeSymbol.isNullable();

        //assert in annotation
    }

    private AContainerTypeSymbol createContainerTypeSymbol() {
        return createContainerTypeSymbol(mock(ITypeHelper.class));
    }

    protected AContainerTypeSymbol createContainerTypeSymbol(ITypeHelper typeHelper) {
        return new DummyAContainerTypeSymbol(typeHelper);
    }
}
