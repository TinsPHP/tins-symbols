/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousSymbolWithAccessModifier;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous.AErroneousSymbolTest;

public class AErroneousSymbolWithAccessModifier_AErroneousSymbol_LSPTest extends AErroneousSymbolTest
{
    class DummyErroneousSymbolWithAccessModifier extends AErroneousSymbolWithAccessModifier
    {
        public DummyErroneousSymbolWithAccessModifier(ITSPHPAst ast, String name, TSPHPException theException) {
            super(ast, name, theException);
        }
    }

    @Override
    protected AErroneousSymbol createSymbol(ITSPHPAst definitionAst, String name, TSPHPException exception) {
        return new DummyErroneousSymbolWithAccessModifier(definitionAst, name, exception);
    }
}
