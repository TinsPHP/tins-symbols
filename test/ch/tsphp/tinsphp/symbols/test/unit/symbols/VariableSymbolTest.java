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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.mock;

public class VariableSymbolTest
{
    @Test
    public void getConstraints_NothingDefined_ReturnsEmptyList() {
        //no arrange necessary

        IVariableSymbol variableSymbol = createVariableSymbol();
        List<IConstraint> result = variableSymbol.getConstraints();

        assertThat(result, is(empty()));
    }

    @Test
    public void addAndGetConstraints_OneDefined_ReturnsListWithIt() {
        IConstraint constraint = mock(IConstraint.class);

        IVariableSymbol variableSymbol = createVariableSymbol();
        variableSymbol.addConstraint(constraint);
        List<IConstraint> result = variableSymbol.getConstraints();

        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(constraint));
    }

    private IVariableSymbol createVariableSymbol() {
        return createVariableSymbol(mock(ITSPHPAst.class), new ModifierSet(), "foo");
    }

    protected IVariableSymbol createVariableSymbol(ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        return new VariableSymbol(definitionAst, modifiers, name);
    }
}
