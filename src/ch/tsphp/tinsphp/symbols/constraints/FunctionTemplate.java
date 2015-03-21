/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IReadOnlyTypeVariableCollection;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

import java.util.Map;

public class FunctionTemplate implements IReadOnlyTypeVariableCollection
{
    private final Map<String, ITypeVariableSymbol> typeVariables;

    public FunctionTemplate(Map<String, ITypeVariableSymbol> theTypeVariables) {
        typeVariables = theTypeVariables;
    }

    @Override
    public Map<String, ITypeVariableSymbol> getTypeVariables() {
        return typeVariables;
    }
}