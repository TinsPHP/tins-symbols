/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.symbols.ATypeVariableSymbol;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.mock;

public class ATypeVariableSymbolTest
{
    private class DummyTypeVariableSymbol extends ATypeVariableSymbol
    {

        protected DummyTypeVariableSymbol(ITSPHPAst theDefinitionAst, String theName) {
            super(theDefinitionAst, theName);
        }
    }

    @Test
    public void getConstraints_NothingDefined_ReturnsEmptyList() {
        //no arrange necessary

        ITypeVariableSymbol symbol = createExpressionTypeVariableSymbol();
        List<IConstraint> result = symbol.getConstraints();

        assertThat(result, is(empty()));
    }

    @Test
    public void addAndGetConstraints_OneDefined_ReturnsListWithIt() {
        IConstraint constraint = mock(IConstraint.class);

        ITypeVariableSymbol symbol = createExpressionTypeVariableSymbol();
        symbol.addConstraint(constraint);
        List<IConstraint> result = symbol.getConstraints();

        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(constraint));
    }

    @Test
    public void isByValue_Standard_ReturnsTrue() {
        //no arrange necessary

        ITypeVariableSymbol symbol = createExpressionTypeVariableSymbol();
        boolean result = symbol.isByValue();

        assertThat(result, is(true));
    }

    @Test
    public void isByValue_SetByRefCalled_ReturnsFalse() {
        //no arrange necessary

        ITypeVariableSymbol symbol = createExpressionTypeVariableSymbol();
        symbol.setIsByRef();
        boolean result = symbol.isByValue();

        assertThat(result, is(false));
    }

    private ITypeVariableSymbol createExpressionTypeVariableSymbol() {
        return createExpressionTypeVariableSymbol(mock(ITSPHPAst.class), "foo");
    }

    protected ITypeVariableSymbol createExpressionTypeVariableSymbol(ITSPHPAst definitionAst, String name) {
        return new DummyTypeVariableSymbol(definitionAst, name);
    }
}
