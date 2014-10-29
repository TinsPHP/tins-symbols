/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.VariableSymbol;

public class VariableSymbol_ASymbol_LSPTest extends ASymbolTest
{
    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new VariableSymbol(definitionAst, new ModifierSet(), name);
    }
}
