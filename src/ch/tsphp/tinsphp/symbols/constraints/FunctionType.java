/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.symbols.TypeVariableNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
        if (signature != null) {
            throw new IllegalStateException("function " + name + " was already fixed");
        }
        signature = calculateSignature();
    }

    private String calculateSignature() {
        int numberOfParameters = parameters.size();
        Set<String> typeVariablesAdded = new HashSet<>(numberOfParameters + 1);
        StringBuilder sbTypeParameters = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        if (numberOfParameters > 0) {
            appendParameters(sb, typeVariablesAdded, sbTypeParameters);
        } else {
            sb.append("()");
        }

        sb.append(" -> ");
        appendParameter(sb, TypeVariableNames.RETURN_VARIABLE_NAME, typeVariablesAdded, sbTypeParameters);

        if (sbTypeParameters.length() > 0) {
            sb.append(sbTypeParameters);
        }
        return sb.toString();
    }

    private void appendParameters(StringBuilder sb, Set<String> typeVariablesAdded, StringBuilder sbTypeParameters) {
        Iterator<IVariable> iterator = parameters.iterator();
        if (iterator.hasNext()) {
            IVariable parameter = iterator.next();
            appendParameter(sb, parameter.getAbsoluteName(), typeVariablesAdded, sbTypeParameters);
        }
        while (iterator.hasNext()) {
            sb.append(" x ");
            IVariable parameter = iterator.next();
            appendParameter(sb, parameter.getAbsoluteName(), typeVariablesAdded, sbTypeParameters);
        }
    }

    private void appendParameter(
            StringBuilder sb, String variableId, Set<String> typeVariablesAdded, StringBuilder sbTypeParameters) {

        ITypeVariableReference reference = bindings.getTypeVariableReference(variableId);
        String typeVariable = reference.getTypeVariable();
        if (reference.hasFixedType()) {
            ITypeSymbol typeSymbol;
            if (bindings.hasUpperTypeBounds(typeVariable)) {
                typeSymbol = bindings.getUpperTypeBounds(typeVariable);
            } else {
                typeSymbol = bindings.getLowerTypeBounds(typeVariable);
            }
            sb.append(typeSymbol.getAbsoluteName());
        } else {
            sb.append(typeVariable);
            if (!typeVariablesAdded.contains(typeVariable)) {
                typeVariablesAdded.add(typeVariable);
                appendTypeParameter(sbTypeParameters, typeVariable);
            }
        }
    }

    private void appendTypeParameter(StringBuilder sbTypeParameters, String typeVariable) {
        boolean hasLowerBounds = bindings.hasLowerBounds(typeVariable);
        boolean hasUpperTypeBounds = bindings.hasUpperTypeBounds(typeVariable);
        boolean hasLowerOrUpperBounds = hasLowerBounds || hasUpperTypeBounds;


        if (hasLowerOrUpperBounds) {
            if (sbTypeParameters.length() > 0) {
                sbTypeParameters.append(", ");
            } else {
                sbTypeParameters.append(" \\ ");
            }
        }

        if (hasLowerBounds) {
            List<String> lowerBounds = new ArrayList<>();
            if (bindings.hasLowerTypeBounds(typeVariable)) {
                SortedSet<String> sortedSet = new TreeSet<>(
                        bindings.getLowerTypeBounds(typeVariable).getTypeSymbols().keySet());
                lowerBounds.addAll(sortedSet);
            }
            if (bindings.hasLowerRefBounds(typeVariable)) {
                SortedSet<String> sortedSet = new TreeSet<>(bindings.getLowerRefBounds(typeVariable));
                lowerBounds.addAll(sortedSet);
            }
            if (lowerBounds.size() != 1) {
                sbTypeParameters.append("(");
                Iterator<String> iterator = lowerBounds.iterator();
                if (iterator.hasNext()) {
                    sbTypeParameters.append(iterator.next());
                }
                while (iterator.hasNext()) {
                    sbTypeParameters.append(" | ").append(iterator.next());
                }
                sbTypeParameters.append(")");
            } else {
                sbTypeParameters.append(lowerBounds.get(0));
            }
            sbTypeParameters.append(" < ");
        }

        if (hasLowerOrUpperBounds) {
            sbTypeParameters.append(typeVariable);
        }

        if (hasUpperTypeBounds) {
            sbTypeParameters.append(" < ");
            sbTypeParameters.append(bindings.getUpperTypeBounds(typeVariable).getAbsoluteName());
        }
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
