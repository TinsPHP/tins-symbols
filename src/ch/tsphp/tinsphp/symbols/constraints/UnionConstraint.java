/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

public class UnionConstraint implements IConstraint
{
    private ITypeVariableSymbol[] typeVariables;

    public UnionConstraint(ITypeVariableSymbol... theTypeVariables) {
        typeVariables = theTypeVariables;
    }

    public ITypeVariableSymbol[] getTypeVariables() {
        return typeVariables;
    }
}
