/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class PseudoTypeSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;

public class PseudoTypeSymbol extends ATypeSymbol implements IPseudoTypeSymbol
{

    public PseudoTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(null, name, parentTypeSymbol);
    }

    @Override
    public String getAbsoluteName() {
        return name;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException("pseudo types do not have a default value");
    }
}