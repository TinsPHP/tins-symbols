/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;

public class LowerBoundException extends BoundException
{
    private IUnionTypeSymbol lowerTypeBound;
    private ITypeSymbol newUpperType;

    public LowerBoundException(
            String message, IUnionTypeSymbol theLowerTypeBound, ITypeSymbol theNewUpperType) {
        super(message);
        lowerTypeBound = theLowerTypeBound;
        newUpperType = theNewUpperType;
    }

    public IUnionTypeSymbol getLowerTypeBound() {

        return lowerTypeBound;
    }

    public ITypeSymbol getNewUpperType() {
        return newUpperType;
    }
}
