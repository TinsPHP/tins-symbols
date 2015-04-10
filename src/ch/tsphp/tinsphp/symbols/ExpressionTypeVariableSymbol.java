/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

public class ExpressionTypeVariableSymbol extends ATypeVariableSymbol implements ITypeVariableSymbol
{
    private boolean hasFixedType;

    public ExpressionTypeVariableSymbol(ITSPHPAst theDefinitionAst) {
        super(theDefinitionAst,
                theDefinitionAst.getText() + "@"
                        + theDefinitionAst.getLine() + "|" + theDefinitionAst.getCharPositionInLine());
    }

    @Override
    public void setHasFixedType() {
        hasFixedType = true;
    }

    @Override
    public boolean hasFixedType() {
        return hasFixedType;
    }

    @Override
    public String getTypeVariable() {
        return null;
    }
}
