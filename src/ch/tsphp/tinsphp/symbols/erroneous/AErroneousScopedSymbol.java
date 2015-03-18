/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class AErroneousScopedSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols.erroneous;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ISymbol;

import java.util.List;
import java.util.Map;

public abstract class AErroneousScopedSymbol extends AErroneousSymbolWithModifier implements IScope
{

    public static final String ERROR_MESSAGE_SCOPE = "AErroneousScopedSymbol is not a real scope.";

    public AErroneousScopedSymbol(ITSPHPAst ast, String name, TSPHPException theException) {
        super(ast, name, theException);
    }


    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        return true;
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        return true;
    }

    //--------------------------------------------------------------
    // Unsupported Methods

    @Override
    public String getScopeName() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public IScope getEnclosingScope() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public void define(ISymbol symbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }


    @Override
    public void addToInitialisedSymbols(ISymbol symbol, boolean isFullyInitialised) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public Map<String, Boolean> getInitialisedSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }
}
