/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ATypeSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */


package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;

import java.util.HashSet;
import java.util.Set;

public abstract class ATypeSymbol extends ASymbolWithModifier implements ITypeSymbol
{

    private final Set<ITypeSymbol> parentTypeSymbols;

    @SuppressWarnings("checkstyle:parameternumber")
    public ATypeSymbol(final ITSPHPAst theDefinitionAst, final String theName, final ITypeSymbol theParentTypeSymbol) {
        super(theDefinitionAst, new ModifierSet(), theName);
        if (theParentTypeSymbol != null) {
            parentTypeSymbols = new HashSet<>(1);
            parentTypeSymbols.add(theParentTypeSymbol);
        } else {
            parentTypeSymbols = new HashSet<>();
        }
    }

    @SuppressWarnings("checkstyle:parameternumber")
    public ATypeSymbol(ITSPHPAst theDefinitionAst, String theName, Set<ITypeSymbol> theParentTypeSymbols) {
        super(theDefinitionAst, new ModifierSet(), theName);
        parentTypeSymbols = theParentTypeSymbols;
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        return parentTypeSymbols;
    }

    @Override
    public boolean isFalseable() {
        return modifiers.isFalseable();
    }

    @Override
    public boolean isNullable() {
        return modifiers.isNullable();
    }

    /**
     * Returns false, override in sub-classes for another behaviour.
     */
    @Override
    public boolean canBeUsedInIntersection() {
        return false;
    }

    /**
     * Returns false, override in sub-classes for another behaviour.
     */
    @Override
    public boolean isFinal() {
        return false;
    }
}
