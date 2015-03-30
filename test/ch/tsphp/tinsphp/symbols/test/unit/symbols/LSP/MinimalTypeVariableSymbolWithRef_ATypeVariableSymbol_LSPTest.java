/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.symbols.MinimalTypeVariableSymbolWithRef;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ATypeVariableSymbolTest;

public class MinimalTypeVariableSymbolWithRef_ATypeVariableSymbol_LSPTest extends ATypeVariableSymbolTest
{
    @Override
    protected ITypeVariableSymbol createExpressionTypeVariableSymbol(ITSPHPAst definitionAst, String name) {
        return new MinimalTypeVariableSymbolWithRef(name);
    }
}
