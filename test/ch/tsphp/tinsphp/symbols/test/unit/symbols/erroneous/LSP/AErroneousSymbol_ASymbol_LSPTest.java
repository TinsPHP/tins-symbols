/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.symbols.ASymbol;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.ASymbolTest;

public class AErroneousSymbol_ASymbol_LSPTest extends ASymbolTest
{
    class DummyErroneousSymbol extends AErroneousSymbol
    {

        public DummyErroneousSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
            super(ast, name, exception);
        }
    }

    @Override
    protected ASymbol createSymbol(ITSPHPAst definitionAst, String name) {
        return new DummyErroneousSymbol(definitionAst, name, new TSPHPException());
    }
}
