/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;


import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;

import java.util.List;

public class FunctionTypeSymbol extends ASymbol implements IFunctionTypeSymbol
{
    private ITypeVariableCollection typeVariableCollection;
    private List<IVariable> parameters;
    private IVariable returnVariable;

    public FunctionTypeSymbol(String theName,
            ITypeVariableCollection theTypeVariableCollection,
            List<IVariable> theParameterVariables,
            IVariable theReturnVariable) {
        super(null, theName);
        typeVariableCollection = theTypeVariableCollection;
        parameters = theParameterVariables;
        returnVariable = theReturnVariable;
    }

    @Override
    public int getNumberOfNonOptionalParameters() {
        return parameters.size();
    }

    @Override
    public List<IVariable> getParameters() {
        return parameters;
    }

    @Override
    public IVariable getReturnVariable() {
        return returnVariable;
    }

    @Override
    public ITypeVariableCollection getTypeVariables() {
        return typeVariableCollection;
    }
}
