/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;


import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.symbols.IMinimalMethodSymbol;

import java.util.ArrayList;
import java.util.Collection;

public class MinimalMethodSymbol extends ASymbol implements IMinimalMethodSymbol
{
    private Collection<IFunctionType> overloads = new ArrayList<>();

    public MinimalMethodSymbol(String name) {
        super(null, name);
    }

    @Override
    public void setOverloads(Collection<IFunctionType> theOverloads) {
        overloads = theOverloads;
    }

    @Override
    public Collection<IFunctionType> getOverloads() {
        return overloads;
    }
}
