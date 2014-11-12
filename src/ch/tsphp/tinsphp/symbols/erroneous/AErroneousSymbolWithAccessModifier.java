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

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.common.symbols.ISymbolWithAccessModifier;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousSymbol;

public abstract class AErroneousSymbolWithAccessModifier extends AErroneousSymbolWithModifier
        implements IErroneousSymbol, ISymbolWithAccessModifier
{

    public AErroneousSymbolWithAccessModifier(ITSPHPAst ast, String name, TSPHPException theException) {
        super(ast, name, theException);
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

}
