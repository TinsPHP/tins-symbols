/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ErroneousVariableSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbolWithRef;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousVariableSymbol;


public class ErroneousVariableSymbol extends AErroneousSymbolWithAccessModifier implements IErroneousVariableSymbol
{

    private static final String ERROR_MESSAGE = "ErroneousVariableSymbol is not a real class.";

    public ErroneousVariableSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
        super(ast, name, exception);
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean isAlwaysCasting() {
        return true;
    }

    @Override
    public boolean isFalseable() {
        return true;
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public void setOriginal(IMinimalVariableSymbolWithRef variableDeclaration) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void addRefVariable(IMinimalVariableSymbol variableSymbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public IMinimalVariableSymbol getCurrentTypeVariable() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }
}
