/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;


import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;

import java.util.List;

public class FunctionTypeSymbol extends ASymbol implements IFunctionTypeSymbol
{
    private ITypeVariableCollection typeVariableCollection;
    private List<String> parameterTypeVariables;
    private String returnTypeVariable;

    public FunctionTypeSymbol(String theName,
            ITypeVariableCollection theTypeVariableCollection,
            List<String> theParameterTypeVariables,
            String theReturnTypeVariable) {
        super(null, theName);
        typeVariableCollection = theTypeVariableCollection;
        parameterTypeVariables = theParameterTypeVariables;
        returnTypeVariable = theReturnTypeVariable;
    }

    @Override
    public int getNumberOfNonOptionalParameters() {
        return parameterTypeVariables.size();
    }

    @Override
    public List<String> getParameterTypeVariables() {
        return parameterTypeVariables;
    }

    @Override
    public String getReturnTypeVariable() {
        return returnTypeVariable;
    }

    @Override
    public ITypeVariableCollection getTypeVariables() {
        return typeVariableCollection;
    }
}
