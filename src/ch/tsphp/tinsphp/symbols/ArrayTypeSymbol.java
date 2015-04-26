/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ArrayTypeSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IArrayTypeSymbol;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

public class ArrayTypeSymbol extends ATypeSymbol implements IArrayTypeSymbol
{

    private final ITypeSymbol keyTypeSymbol;
    private final ITypeSymbol valueTypeSymbol;

    @SuppressWarnings("checkstyle:parameternumber")
    public ArrayTypeSymbol(
            String name,
            ITypeSymbol theKeyTypeSymbol,
            ITypeSymbol theValueTypeSymbol,
            ITypeSymbol parentTypeSymbol) {
        super(null, name, parentTypeSymbol);
        keyTypeSymbol = theKeyTypeSymbol;
        valueTypeSymbol = theValueTypeSymbol;
    }

    @Override
    public ITypeSymbol getKeyTypeSymbol() {
        return keyTypeSymbol;
    }

    @Override
    public ITypeSymbol getValueTypeSymbol() {
        return valueTypeSymbol;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TokenTypes.Null, "null");
    }
}
