/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ATypeSymbol;
import ch.tsphp.tinsphp.symbols.PseudoTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ATypeSymbolTest;

import java.util.Set;

public class PseudoTypeSymbol_ATypeSymbol_LSPTest extends ATypeSymbolTest
{

    @Override
    public void getParentTypeSymbols_StandardWithSet_ReturnsOnePassedToConstructor() {
        // different behaviour - PseudoTypeSymbol does not support multiple parent types
    }

    @Override
    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        return new PseudoTypeSymbol(name, parentTypeSymbol);
    }

    @Override
    protected ATypeSymbol createTypeSymbol(ITSPHPAst definitionAst, String name, Set<ITypeSymbol> parentTypeSymbol) {
        throw new UnsupportedOperationException("PseudoTypeSymbol does not support multiple parent type symbols");
    }
}
