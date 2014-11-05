/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.utils;

import ch.tsphp.common.symbols.ISymbolWithModifier;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;

public final class TypeHelper
{
    private TypeHelper() {

    }

    public static final void addNullableModifier(ISymbolWithModifier symbol) {
        symbol.addModifier(TokenTypes.QuestionMark);
    }
}
