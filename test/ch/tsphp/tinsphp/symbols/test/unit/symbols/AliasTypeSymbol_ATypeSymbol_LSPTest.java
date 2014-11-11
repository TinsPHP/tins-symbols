/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ATypeSymbol;
import ch.tsphp.tinsphp.symbols.AliasTypeSymbol;
import org.junit.Test;

import java.util.Set;

public class AliasTypeSymbol_ATypeSymbol_LSPTest extends ATypeSymbolTest
{

    @Test
    public void getParentTypeSymbols_StandardWithSet_ReturnsOnePassedToConstructor() {
        //different behaviour, AliasTypeSymbol does not support multiple parents.
        //test is therefore not necessary
    }

    @Override
    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        return new AliasTypeSymbol(definitionAst, name, parentTypeSymbol);
    }

    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, Set<ITypeSymbol> parentTypeSymbol) {
        throw new RuntimeException("AliasSymbol does not support multiple parent types");
    }
}
