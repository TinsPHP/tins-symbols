/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.ASymbolWithModifier;
import ch.tsphp.tinsphp.symbols.ScalarTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolWithModifierTest;

public class ScalarTypeSymbol_ASymbolWithModifier_LSPTest extends ASymbolWithModifierTest
{

    @Override
    protected ASymbolWithModifier createSymbolWithModifier(
            ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        ScalarTypeSymbol symbol = new ScalarTypeSymbol(name, null, 0, "");
        symbol.setModifiers(modifiers);
        return symbol;
    }
}
