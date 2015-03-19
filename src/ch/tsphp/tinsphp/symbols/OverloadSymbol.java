/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IOverloadSymbol;

import java.util.ArrayList;
import java.util.List;

public class OverloadSymbol extends ASymbol implements IOverloadSymbol
{
    private final List<IFunctionTypeSymbol> overloads = new ArrayList<>();

    public OverloadSymbol(String name) {
        super(null, name);
    }

    @Override
    public void addOverload(IFunctionTypeSymbol overload) {
        overloads.add(overload);
    }

    @Override
    public List<IFunctionTypeSymbol> getOverloads() {
        return overloads;
    }
}
