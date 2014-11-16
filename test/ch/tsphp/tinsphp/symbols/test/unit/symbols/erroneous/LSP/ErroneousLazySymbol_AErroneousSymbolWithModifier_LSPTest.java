/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous.LSP;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.common.symbols.erroneous.ILazySymbolResolver;
import ch.tsphp.tinsphp.symbols.erroneous.AErroneousSymbolWithModifier;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousLazySymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous.AErroneousSymbolWithModifierTest;

import static org.mockito.Mockito.mock;

public class ErroneousLazySymbol_AErroneousSymbolWithModifier_LSPTest extends AErroneousSymbolWithModifierTest
{

    @Override
    protected AErroneousSymbolWithModifier createSymbol(
            ITSPHPAst definitionAst, String name, TSPHPException exception) {
        return new ErroneousLazySymbol(definitionAst, name, exception, mock(ILazySymbolResolver.class));
    }
}
