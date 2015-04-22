/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.AContainerTypeSymbol;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.AContainerTypeSymbolTest;

public class IntersectionTypeSymbol_AContainerTypeSymbol_LSPTest extends AContainerTypeSymbolTest
{

    protected AContainerTypeSymbol createContainerTypeSymbol(IOverloadResolver overloadResolver) {
        return new IntersectionTypeSymbol(overloadResolver);
    }

}