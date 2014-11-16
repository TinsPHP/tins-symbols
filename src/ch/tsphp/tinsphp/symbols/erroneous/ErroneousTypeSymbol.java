/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ErroneousTypeSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols.erroneous;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.Set;

public class ErroneousTypeSymbol extends AErroneousScopedSymbol implements IErroneousTypeSymbol
{

    public static final String ERROR_MESSAGE = "ErroneousTypeSymbol is not a real class.";
    private final IMethodSymbol construct;

    public ErroneousTypeSymbol(ITSPHPAst ast, String name, TSPHPException exception, IMethodSymbol theConstruct) {
        super(ast, name, exception);
        construct = theConstruct;
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
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
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TokenTypes.Null, "null");
    }
}
