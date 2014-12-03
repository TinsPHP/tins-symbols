/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ASymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public abstract class ASymbol implements ISymbol
{

    protected final ITSPHPAst definitionAst;
    protected final String name;
    protected ITypeSymbol type;
    protected IScope definitionScope;

    protected ASymbol(ITSPHPAst theDefinitionAst, String theName) {
        definitionAst = theDefinitionAst;
        name = theName;
    }

    @Override
    public ITSPHPAst getDefinitionAst() {
        return definitionAst;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IScope getDefinitionScope() {
        return definitionScope;
    }

    @Override
    public void setDefinitionScope(IScope newScope) {
        definitionScope = newScope;
    }

    @Override
    public ITypeSymbol getType() {
        return type;
    }

    @Override
    public void setType(ITypeSymbol newType) {
        type = newType;
    }

    @Override
    public String toString() {
        return getName() + (type != null ? ":" + type : "");
    }
}
