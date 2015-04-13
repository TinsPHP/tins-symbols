/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;

public abstract class AMinimalVariableSymbol extends ASymbol implements IMinimalVariableSymbol
{

    private final String typeVariable;

    //Warning! start code duplication - same as in VariableSymbol and Variable
    private boolean hasFixedType;
    //Warning! end code duplication - same as in VariableSymbol and Variable

    protected AMinimalVariableSymbol(ITSPHPAst theDefinitionAst, String theName, String theTypeVariable) {
        super(theDefinitionAst, theName);
        typeVariable = theTypeVariable;
    }

    //Warning! start code duplication - same as in VariableSymbol and Variable
    @Override
    public void setHasFixedType() {
        hasFixedType = true;
    }

    @Override
    public boolean hasFixedType() {
        return hasFixedType;
    }
    //Warning! end code duplication - same as in VariableSymbol and Variable

    @Override
    public String getTypeVariable() {
        return typeVariable;
    }

    @Override
    public String toString() {
        return super.toString()
                + (typeVariable != null ? ":" + typeVariable : "")
                + (hasFixedType ? "#" : "");
    }
}
