/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.symbols.ExpressionTypeVariableSymbol;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.mock;

public class ExpressionTypeVariableSymbolTest
{
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

    private ITypeVariableSymbol createExpressionTypeVariableSymbol() {
        return createExpressionTypeVariableSymbol(mock(ITSPHPAst.class));
    }

    protected ITypeVariableSymbol createExpressionTypeVariableSymbol(ITSPHPAst definitionAst) {
        return new ExpressionTypeVariableSymbol(definitionAst);
    }
}