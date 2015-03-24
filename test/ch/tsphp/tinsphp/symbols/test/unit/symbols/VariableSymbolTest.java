/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.VariableSymbol;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;

public class VariableSymbolTest
{
    @Test
    public void getConstraints_NothingDefined_ReturnsNull() {
        //no arrange necessary

        IVariableSymbol variableSymbol = createVariableSymbol();
        IConstraint result = variableSymbol.getConstraint();

        assertThat(result, is(nullValue()));
    }

    @Test
    public void setAndGetConstraints_OneSet_ReturnsIt() {
        IConstraint constraint = mock(IConstraint.class);

        IVariableSymbol variableSymbol = createVariableSymbol();
        variableSymbol.setConstraint(constraint);
        IConstraint result = variableSymbol.getConstraint();

        assertThat(result, is(constraint));
    }

    @Test
    public void isByValue_Standard_ReturnsTrue() {
        //no arrange necessary

        IVariableSymbol variableSymbol = createVariableSymbol();
        boolean result = variableSymbol.isByValue();

        assertThat(result, is(true));
    }

    @Test
    public void isByValue_SetByRefCalled_ReturnsFalse() {
        //no arrange necessary

        IVariableSymbol variableSymbol = createVariableSymbol();
        variableSymbol.setIsByRef();
        boolean result = variableSymbol.isByValue();

        assertThat(result, is(false));
    }

    private IVariableSymbol createVariableSymbol() {
        return createVariableSymbol(mock(ITSPHPAst.class), new ModifierSet(), "foo");
    }

    protected IVariableSymbol createVariableSymbol(ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        return new VariableSymbol(definitionAst, modifiers, name);
    }
}
