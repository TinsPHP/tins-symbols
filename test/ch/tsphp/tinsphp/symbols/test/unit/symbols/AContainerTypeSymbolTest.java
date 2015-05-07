/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.AContainerTypeSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;

public class AContainerTypeSymbolTest
{
    private interface IDummyContainerTypeSymbol extends IContainerTypeSymbol<IDummyContainerTypeSymbol>
    {
    }

    private class DummyAContainerTypeSymbol extends AContainerTypeSymbol<IDummyContainerTypeSymbol>
    {

        public DummyAContainerTypeSymbol(IOverloadResolver theOverloadResolver) {
            super(theOverloadResolver);
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
        protected boolean addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol) {
            return false;
        }

        @Override
        public IDummyContainerTypeSymbol copy() {
            return null;
        }
    }

    @Test
    public void evalSelf_Standard_ReturnsThis() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        ITypeSymbol result = typeSymbol.evalSelf();

        assertThat(result, is((ITypeSymbol) typeSymbol));
    }

    @Test
    public void getDefinitionScope_Standard_ReturnsNull() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        IScope result = typeSymbol.getDefinitionScope();

        assertThat(result, is(nullValue()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getParentTypeSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.getParentTypeSymbols();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefaultValue_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.getDefaultValue();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.addModifier(123);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.removeModifier(123);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.getModifiers();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.setModifiers(new ModifierSet());

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefinitionAst_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.getDefinitionAst();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setDefinitionScope_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.setDefinitionScope(mock(IScope.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.getType();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setType_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.setType(mock(ITypeSymbol.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isFalseable_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.isFalseable();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isNullable_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        AContainerTypeSymbol typeSymbol = createContainerTypeSymbol();
        typeSymbol.isNullable();

        //assert in annotation
    }

    private AContainerTypeSymbol createContainerTypeSymbol() {
        return createContainerTypeSymbol(mock(IOverloadResolver.class));
    }

    protected AContainerTypeSymbol createContainerTypeSymbol(IOverloadResolver overloadResolver) {
        return new DummyAContainerTypeSymbol(overloadResolver);
    }
}
