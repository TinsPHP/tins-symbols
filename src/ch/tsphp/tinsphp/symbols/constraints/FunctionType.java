/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionType implements IFunctionType
{
    protected final String name;
    private IOverloadBindings bindings;
    private List<IVariable> parameters;
    private IVariable returnVariable;
    private final Map<String, String> suffices = new HashMap<>(2);

    public FunctionType(String theName,
            IOverloadBindings theOverloadBindings,
            List<IVariable> theParameterVariables,
            IVariable theReturnVariable) {
        name = theName;
        bindings = theOverloadBindings;
        parameters = theParameterVariables;
        returnVariable = theReturnVariable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSuffix(String translatorId) {
        return suffices.get(translatorId);
    }

    @Override
    public void addSuffix(String translatorId, String newName) {
        suffices.put(translatorId, newName);
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
    public IOverloadBindings getBindings() {
        return bindings;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("{").append(getNumberOfNonOptionalParameters()).append("}").append("[");
        for (IVariable parameter : parameters) {
            toString(sb, parameter).append(", ");
        }
        toString(sb, returnVariable);
        sb.append("]");
        return sb.toString();
    }

    private StringBuilder toString(
            StringBuilder sb, IVariable parameter) {
        String absoluteName = parameter.getAbsoluteName();
        sb.append(absoluteName).append(":");
        ITypeVariableReference constraint = bindings.getTypeVariableReference(absoluteName);
        String typeVariable = constraint.getTypeVariable();
        sb.append(typeVariable)
                .append("<")
                .append(bindings.getLowerBoundConstraintIds(typeVariable))
                .append(",")
                .append(bindings.getUpperBoundConstraintIds(typeVariable))
                .append(">");
        if (constraint.hasFixedType()) {
            sb.append("#");
        }
        return sb;
    }
}
