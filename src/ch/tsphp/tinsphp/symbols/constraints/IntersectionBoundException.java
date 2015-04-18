/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;

public class IntersectionBoundException extends UpperBoundException
{
    public IntersectionBoundException(
            String message, IIntersectionTypeSymbol theUpperTypeBound, ITypeSymbol theNewLowerType) {
        super(message, theUpperTypeBound, theNewLowerType);
    }
}
