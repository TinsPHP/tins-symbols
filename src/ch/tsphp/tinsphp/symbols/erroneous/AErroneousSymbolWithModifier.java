/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ISymbolWithModifier;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousSymbol;

public abstract class AErroneousSymbolWithModifier extends AErroneousSymbol
        implements IErroneousSymbol, ISymbolWithModifier
{
    public static final String ERROR_MESSAGE_MODIFIER = "AErroneousScopedSymbol is not a real symbol with modifier.";

    public AErroneousSymbolWithModifier(ITSPHPAst ast, String name, TSPHPException theException) {
        super(ast, name, theException);
    }

    @Override
    public void addModifier(Integer modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public boolean removeModifier(Integer modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public IModifierSet getModifiers() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public void setModifiers(IModifierSet modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

}
