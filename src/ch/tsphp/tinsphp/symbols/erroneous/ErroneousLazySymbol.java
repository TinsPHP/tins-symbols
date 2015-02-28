/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.erroneous;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.TypeWithModifiersDto;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousLazySymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.ILazySymbolResolver;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

import java.util.Set;

public class ErroneousLazySymbol extends AErroneousScopedSymbol implements IErroneousLazySymbol
{
    public static final String ERROR_MESSAGE = "ErroneousLazySymbol is not a real class.";

    private final ILazySymbolResolver lazySymbolResolver;

    public ErroneousLazySymbol(ITSPHPAst ast, String name, TSPHPException exception,
            ILazySymbolResolver theLazySymbolResolver) {
        super(ast, name, exception);
        lazySymbolResolver = theLazySymbolResolver;
    }

    @Override
    public ISymbol resolveSymbolLazily() {
        return lazySymbolResolver.resolve();
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
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean isAlwaysCasting() {
        return true;
    }

//    @Override
//    public boolean isFinal() {
//        return false;
//    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TokenTypes.Null, "null");
    }

    @Override
    public ITypeSymbol evalSelf() {
        return this;
    }

    @Override
    public TypeWithModifiersDto toTypeWithModifiersDto() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

}
