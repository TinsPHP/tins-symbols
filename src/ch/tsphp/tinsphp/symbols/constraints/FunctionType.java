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
import ch.tsphp.tinsphp.symbols.TypeVariableNames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionType implements IFunctionType
{
    protected final String name;
    private String signature;
    private IOverloadBindings bindings;
    private List<IVariable> parameters;
    private final Map<String, String> suffices = new HashMap<>(2);

    public FunctionType(String theName, IOverloadBindings theOverloadBindings, List<IVariable> theParameterVariables) {
        name = theName;
        bindings = theOverloadBindings;
        parameters = theParameterVariables;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void fix() {
        signature = calculateSignature();
    }

    private String calculateSignature() {
        //TODO rstoll TINS-277 operator helper
        return name;
    }

    @Override
    public String getSignature() {
        return signature;
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
    public IOverloadBindings getBindings() {
        return bindings;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("{").append(getNumberOfNonOptionalParameters()).append("}").append("[");
        for (IVariable parameter : parameters) {
            appendVariable(sb, parameter.getAbsoluteName()).append(", ");
        }
        appendVariable(sb, TypeVariableNames.RETURN_VARIABLE_NAME);
        sb.append("]");
        return sb.toString();
    }

    private StringBuilder appendVariable(StringBuilder sb, String variableName) {
        sb.append(variableName).append(":");
        ITypeVariableReference constraint = bindings.getTypeVariableReference(variableName);
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
