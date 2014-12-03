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

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IPseudoTypeSymbol;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public class PseudoTypeSymbol extends ANullableTypeSymbol implements IPseudoTypeSymbol
{

    public PseudoTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(name, parentTypeSymbol);
    }
}