/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.common.symbols.IExpressionVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;

public class ExpressionVariableSymbol extends ASymbol implements IExpressionVariableSymbol
{
    private IMinimalMethodSymbol methodSymbol;

    public ExpressionVariableSymbol(ITSPHPAst theDefinitionAst) {
        super(theDefinitionAst,
                theDefinitionAst.getText() + "@"
                        + theDefinitionAst.getLine() + "|" + theDefinitionAst.getCharPositionInLine()
        );
    }

    @Override
    public void setMethodSymbol(IMinimalMethodSymbol theMethodSymbol) {
        methodSymbol = theMethodSymbol;
    }

    @Override
    public IMinimalMethodSymbol getMethodSymbol() {
        return methodSymbol;
    }
}
