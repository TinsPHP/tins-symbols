/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.symbols.ConstantFunctionTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.AFunctionTypeSymbolTest;

import java.util.List;

public class ConstantFunctionTypeSymbol_AFunctionTypeSymbol_LSPTest extends AFunctionTypeSymbolTest
{
    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<String> parameterIds,
            ITypeSymbol parentTypeSymbol) {
        return new ConstantFunctionTypeSymbol(name, parameterIds, parentTypeSymbol, null);
    }
}
