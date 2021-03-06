/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;

public class Variable implements IVariable
{
    private final String name;

    public Variable(String theName) {
        name = theName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbsoluteName() {
        return name;
    }

    @Override
    public ITypeSymbol getType() {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
