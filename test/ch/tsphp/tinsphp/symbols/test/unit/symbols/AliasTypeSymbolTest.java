/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IAliasTypeSymbol;
import ch.tsphp.tinsphp.symbols.AliasTypeSymbol;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class AliasTypeSymbolTest
{

    @Test(expected = UnsupportedOperationException.class)
    public void getDefaultValue_Standard_ThrowsException() {
        //no arrange necessary

        IAliasTypeSymbol symbol = this.createAliasTypeSymbol();
        symbol.getDefaultValue();

        //assert in annotation
    }


    private IAliasTypeSymbol createAliasTypeSymbol() {
        return createAliasTypeSymbol(mock(ITSPHPAst.class), "foo", mock(ITypeSymbol.class));
    }

    protected IAliasTypeSymbol createAliasTypeSymbol(
            ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        return new AliasTypeSymbol(definitionAst, name, parentTypeSymbol);
    }


}
