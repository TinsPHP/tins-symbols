/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous.AErroneousSymbolTest;

import static org.mockito.Mockito.mock;

public class ErroneousTypeSymbol_AErroneousSymbol_LSPTest extends AErroneousSymbolTest
{

    @Override
    protected AErroneousSymbol createSymbol(ITSPHPAst definitionAst, String name, TSPHPException exception) {
        return new ErroneousTypeSymbol(definitionAst, name, exception, mock(IMethodSymbol.class));
    }
}
