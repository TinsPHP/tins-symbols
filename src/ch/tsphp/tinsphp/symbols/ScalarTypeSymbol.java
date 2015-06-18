/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ScalarTypeSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IScalarTypeSymbol;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public class ScalarTypeSymbol extends ATypeSymbol implements IScalarTypeSymbol
{

    private final int defaultValueTokenType;
    private final String defaultValue;

    @SuppressWarnings("checkstyle:parameternumber")
    public ScalarTypeSymbol(
            String name,
            ITypeSymbol parentTypeSymbol,
            int theDefaultValueTokenType,
            String theDefaultValue) {

        super(null, name, parentTypeSymbol);
        defaultValueTokenType = theDefaultValueTokenType;
        defaultValue = theDefaultValue;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(defaultValueTokenType, defaultValue);
    }

    @Override
    public String getAbsoluteName() {
        return name;
    }

    @Override
    public boolean isFinal() {
        return true;
    }
}