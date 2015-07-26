/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;


import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.TinsPHPConstants;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;

import java.util.ArrayList;
import java.util.Collection;
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
    private final Map<String, String> suffices = new HashMap<>(2);
    private final String name;
    private String signature;
    private IBindingCollection bindingCollection;
    private List<IVariable> parameters;

    //used as soon as function is simplified
    private List<String> typeParameters;
    private Set<String> nonFixedTypeParameters;
    private List<Integer> parameterAndReturn2TypeParameterIndex;
    private Map<String, Integer> typeParameter2Index;
    private int numberOfConvertibleApplications;
    private boolean hasConvertibleParameterTypes;

    private boolean wasSimplified;
    private boolean wasBound;
    private boolean hasSignatureChanged = true;

    public FunctionType(String theName, IBindingCollection theBindingCollection,
            List<IVariable> theParameterVariables) {
        name = theName;
        bindingCollection = theBindingCollection;
        parameters = theParameterVariables;
    }

    private FunctionType(FunctionType functionType) {
        name = functionType.name;
        signature = functionType.signature;
        bindingCollection = functionType.bindingCollection;
        parameters = functionType.parameters;
        wasBound = functionType.wasBound;
        wasSimplified = functionType.wasSimplified;
        if (wasSimplified) {
            typeParameters = new ArrayList<>(functionType.typeParameters);
            nonFixedTypeParameters = new HashSet<>(functionType.nonFixedTypeParameters);
            parameterAndReturn2TypeParameterIndex = new ArrayList<>(functionType.parameterAndReturn2TypeParameterIndex);
            typeParameter2Index = new HashMap<>(functionType.typeParameter2Index);
        }
    }

    @Override
    public int getNumberOfConvertibleApplications() {
        if (!wasSimplified) {
            throw new IllegalStateException("function " + name + " was not yet simplified, "
                    + "cannot report the number of convertible types.");
        }
        return numberOfConvertibleApplications;
    }

    @Override
    public boolean hasConvertibleParameterTypes() {
        if (!wasSimplified) {
            throw new IllegalStateException("function " + name + " was not yet simplified, "
                    + "cannot report the number of convertible types.");
        }
        return hasConvertibleParameterTypes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean wasSimplified() {
        return wasSimplified;
    }

    @Override
    public void manuallySimplified(
            Set<String> theNonFixedTypeParameters,
            int theNumberOfConvertibleTypes,
            boolean hasItConvertibleParameterTypes) {
        if (wasSimplified) {
            throw new IllegalStateException("function " + name + " was already simplified before.");
        }
        wasSimplified = true;

        nonFixedTypeParameters = theNonFixedTypeParameters;
        numberOfConvertibleApplications = theNumberOfConvertibleTypes;
        hasConvertibleParameterTypes = hasItConvertibleParameterTypes;

        calculateTypeParameters(false);
    }

    @Override
    public void simplify() {
        if (wasSimplified) {
            throw new IllegalStateException("function " + name + " was already simplified before.");
        }
        wasSimplified = true;

        Set<String> parameterTypeVariables = new HashSet<>();
        for (IVariable parameter : parameters) {
            String parameterId = parameter.getAbsoluteName();
            parameterTypeVariables.add(bindingCollection.getTypeVariableReference(parameterId).getTypeVariable());
        }
        nonFixedTypeParameters = bindingCollection.tryToFix(parameterTypeVariables);
        numberOfConvertibleApplications = bindingCollection.getNumberOfConvertibleApplications();

        calculateTypeParameters(true);

        searchConvertibleTypeInTypeBounds();
    }

    private void searchConvertibleTypeInTypeBounds() {
        for (String typeParameter : typeParameters) {
            if (searchForConvertibleTypeInTypeBounds(typeParameter)) {
                hasConvertibleParameterTypes = true;
                break;
            }
        }
    }

    private boolean searchForConvertibleTypeInTypeBounds(String typeParameter) {
        boolean foundConvertibles = false;
        if (bindingCollection.hasUpperTypeBounds(typeParameter)) {
            IIntersectionTypeSymbol upperTypeBounds = bindingCollection.getUpperTypeBounds(typeParameter);
            foundConvertibles = containsConvertibleType(upperTypeBounds);
        }

        if (!foundConvertibles && bindingCollection.hasLowerTypeBounds(typeParameter)) {
            IUnionTypeSymbol lowerTypeBounds = bindingCollection.getLowerTypeBounds(typeParameter);
            foundConvertibles = containsConvertibleType(lowerTypeBounds);
        }
        return foundConvertibles;
    }

    private boolean containsConvertibleType(IContainerTypeSymbol typeSymbol) {
        boolean convertibleTypeFound = false;
        for (ITypeSymbol innerTypeSymbol : typeSymbol.getTypeSymbols().values()) {
            if (innerTypeSymbol instanceof IConvertibleTypeSymbol) {
                convertibleTypeFound = true;
                break;
            } else if (innerTypeSymbol instanceof IContainerTypeSymbol) {
                convertibleTypeFound = containsConvertibleType((IContainerTypeSymbol) innerTypeSymbol);
                if (convertibleTypeFound) {
                    break;
                }
            }
        }
        return convertibleTypeFound;
    }

    private void calculateTypeParameters(boolean shallRenameTypeVariables) {
        typeParameters = new ArrayList<>();
        Set<String> typeVariablesAdded = new HashSet<>();
        typeParameter2Index = new HashMap<>();
        int numberOfParameters = parameters.size();
        parameterAndReturn2TypeParameterIndex = new ArrayList<>(numberOfParameters + 1);

        AddToTypeParameterDto dto = new AddToTypeParameterDto(
                typeVariablesAdded,
                shallRenameTypeVariables,
                nonFixedTypeParameters.size() > 1);

        for (int i = 0; i < numberOfParameters; ++i) {
            IVariable parameter = parameters.get(i);
            String parameterId = parameter.getAbsoluteName();
            dto.typeVariableReference = bindingCollection.getTypeVariableReference(parameterId);
            addTypeVariableToTypeParameters(dto);
        }

        dto.typeVariableReference
                = bindingCollection.getTypeVariableReference(TinsPHPConstants.RETURN_VARIABLE_NAME);
        addTypeVariableToTypeParameters(dto);

        //need to make a copy since they might be renamed in addNonFixedTypeVariableToTypeParameters
        Set<String> copyNonFixedTypeParameter = new HashSet<>(nonFixedTypeParameters);
        for (String nonFixedTypeParameter : copyNonFixedTypeParameter) {
            //Warning ! start code duplication, very similar to addTypeVariableToTypeParameters
            if (!typeVariablesAdded.contains(nonFixedTypeParameter)) {
                addNonFixedTypeVariableToTypeParameters(dto, nonFixedTypeParameter);
            }
            //Warning ! end code duplication, very similar to addTypeVariableToTypeParameters
        }
    }

    private void addTypeVariableToTypeParameters(AddToTypeParameterDto dto) {
        String typeVariable = dto.typeVariableReference.getTypeVariable();
        if (!dto.typeVariablesAdded.contains(typeVariable)) {
            int index = addNonFixedTypeVariableToTypeParameters(dto, typeVariable);
            parameterAndReturn2TypeParameterIndex.add(index);
        } else {
            parameterAndReturn2TypeParameterIndex.add(typeParameter2Index.get(typeVariable));
        }
    }

    private int addNonFixedTypeVariableToTypeParameters(AddToTypeParameterDto dto, String typeVariable) {
        String typeParameter = typeVariable;
        if (dto.shallRenameTypeVariables && nonFixedTypeParameters.contains(typeParameter)) {
            if (dto.useSuffix) {
                typeParameter = "T" + dto.typeParameterSuffix++;
            } else {
                typeParameter = "T";
            }
            bindingCollection.renameTypeVariable(typeVariable, typeParameter);
            nonFixedTypeParameters.remove(typeVariable);
            nonFixedTypeParameters.add(typeParameter);
        }
        typeParameters.add(typeParameter);
        dto.typeVariablesAdded.add(typeParameter);
        int count = dto.count++;
        typeParameter2Index.put(typeParameter, count);
        return count;
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
    public IFunctionType copy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        return new FunctionType(this);
    }

    @Override
    public boolean isFixed() {
        return nonFixedTypeParameters.isEmpty();
    }

    @Override
    public boolean wasBound() {
        return wasBound;
    }

    //Warning! start code duplication - very similar to the one in ConvertibleTypeSymbol
    @Override
    public void fix(String fixedTypeParameter) {
        if (!wasBound()) {
            throw new IllegalStateException("cannot fix a type parameter if this function type was not bound to a "
                    + "parametric type");
        }

        if (!nonFixedTypeParameters.remove(fixedTypeParameter)) {
            throw new IllegalArgumentException("the function type was bound to " + typeParameters + " and its "
                    + "non-fixed type parameters are " + nonFixedTypeParameters + " but it was indicated that "
                    + fixedTypeParameter + " was fixed");
        }

        //TODO type variable might have been shared between multiple paramters/return variable, if so,
        // then we need to rename them in order that they do not have the same name anymore

        hasSignatureChanged = true;
    }
    //Warning! end code duplication - very similar to the one in ConvertibleTypeSymbol


    //Warning! start code duplication - very similar to the one in ConvertibleTypeSymbol
    @Override
    public void renameTypeParameter(String typeParameter, String newTypeParameter) {
        if (!wasBound) {
            throw new IllegalStateException("can only rename a type parameter if this function type was bound to"
                    + " another parametric polymorphic type");
        }

        if (!typeParameter2Index.containsKey(typeParameter)) {
            throw new IllegalArgumentException("the function type was bound to " + typeParameters
                    + " but " + typeParameter + " should be renamed.");
        }

        renameTypeVariableAfterContainsCheck(typeParameter, newTypeParameter);
    }
    //Warning! end code duplication - very similar to the one in ConvertibleTypeSymbol


    private void renameTypeVariableAfterContainsCheck(String typeParameter, String newTypeParameter) {
        if (!typeParameter.equals(newTypeParameter)) {
            if (!typeParameter2Index.containsKey(newTypeParameter)) {
                int index = typeParameter2Index.remove(typeParameter);
                typeParameters.set(index, newTypeParameter);
                typeParameter2Index.put(newTypeParameter, index);
                if (nonFixedTypeParameters.remove(typeParameter)) {
                    nonFixedTypeParameters.add(newTypeParameter);
                }
            } else {
                // merge two type parameters - we need to update the data structures, remove the type parameter from the
                // list, recreate typeParameter2Index etc.
                throw new UnsupportedOperationException("not yet implemented");
            }
        }
    }


    //Warning! start code duplication - very similar to the one in ConvertibleTypeSymbol
    @Override
    public void bindTo(IBindingCollection newBindingCollection, List<String> bindingTypeParameters) {
        if (!wasSimplified) {
            throw new IllegalStateException("function " + name + " was not yet simplified, cannot bind it yet.");
        }

        int size = typeParameters.size();
        if (size != bindingTypeParameters.size()) {
            throw new IllegalArgumentException("This parametric type requires " + size
                    + " type parameter(s) but only " + bindingTypeParameters.size() + " provided");
        }

        transferBounds(newBindingCollection, bindingTypeParameters);

        bindingCollection = newBindingCollection;
        for (int i = 0; i < size; ++i) {
            renameTypeVariableAfterContainsCheck(typeParameters.get(i), bindingTypeParameters.get(i));
        }
        wasBound = true;
    }
    //Warning! end code duplication - very similar to the one in ConvertibleTypeSymbol

    private void transferBounds(IBindingCollection newBindingCollection, List<String> bindingTypeParameters) {
        int size = typeParameters.size();
        Map<String, String> typeParameter2BindingTypeParameter = new HashMap<>(size);
        for (int i = 0; i < size; ++i) {
            String typeParameter = typeParameters.get(i);
            String bindingTypeParameter = bindingTypeParameters.get(i);
            typeParameter2BindingTypeParameter.put(typeParameter, bindingTypeParameter);
        }

        for (int i = 0; i < size; ++i) {
            String typeParameter = typeParameters.get(i);
            String bindingTypeParameter = bindingTypeParameters.get(i);

            //TODO rstoll TINS-512 - function binding
            // parametric types need to be bound to the newOverload bindings as well

            if (bindingCollection.hasLowerTypeBounds(typeParameter)) {
                IUnionTypeSymbol lowerTypeBounds = bindingCollection.getLowerTypeBounds(typeParameter);
                newBindingCollection.addLowerTypeBound(bindingTypeParameter, lowerTypeBounds);
            }

            if (bindingCollection.hasUpperTypeBounds(typeParameter)) {
                IIntersectionTypeSymbol upperTypeBounds = bindingCollection.getUpperTypeBounds(typeParameter);
                newBindingCollection.addUpperTypeBound(bindingTypeParameter, upperTypeBounds);
            }

            if (bindingCollection.hasLowerRefBounds(typeParameter)) {
                for (String refTypeParameter : bindingCollection.getLowerRefBounds(typeParameter)) {
                    String refBindingTypeParameter = typeParameter2BindingTypeParameter.get(refTypeParameter);
                    TypeVariableReference reference = new TypeVariableReference(refBindingTypeParameter);
                    newBindingCollection.addLowerRefBound(bindingTypeParameter, reference);
                }
            }
        }
    }

    //Warning! start code duplication - same as in ConvertibleTypeSymbol
    @Override
    public void rebind(IBindingCollection newBindingCollection) {
        if (!wasBound) {
            throw new IllegalStateException("can only rebind a convertible type if it was already bound before.");
        }

        bindingCollection = newBindingCollection;
    }
    //Warning! end code duplication - same as in ConvertibleTypeSymbol

    @Override
    public List<String> getTypeParameters() {
        return typeParameters;
    }

    @Override
    public Set<String> getNonFixedTypeParameters() {
        return nonFixedTypeParameters;
    }

    @Override
    public IBindingCollection getBindingCollection() {
        return bindingCollection;
    }

    @Override
    public String getSignature() {
        if (wasSimplified) {
            if (hasSignatureChanged) {
                signature = calculateSignature();
                hasSignatureChanged = false;
            }
            return signature;
        }
        throw new IllegalStateException("function " + name + " was not yet simplified.");
    }

    private String calculateSignature() {
        int numberOfParameters = parameters.size();
        Set<String> typeVariablesAdded = new HashSet<>();
        StringBuilder sbTypeParameters = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        if (numberOfParameters > 0) {
            appendParameters(sb, typeVariablesAdded, sbTypeParameters);
        } else {
            sb.append("()");
        }

        sb.append(" -> ");
        appendParameter(sb, numberOfParameters, typeVariablesAdded, sbTypeParameters);

        for (String nonFixedTypeParameter : nonFixedTypeParameters) {
            if (!typeVariablesAdded.contains(nonFixedTypeParameter)) {
                typeVariablesAdded.add(nonFixedTypeParameter);
                appendTypeParameter(sbTypeParameters, nonFixedTypeParameter);
            }
        }

        if (sbTypeParameters.length() > 0) {
            sb.append(sbTypeParameters);
        }
        return sb.toString();
    }

    private void appendParameters(StringBuilder sb, Set<String> typeVariablesAdded, StringBuilder sbTypeParameters) {
        int size = parameters.size();
        if (size > 0) {
            appendParameter(sb, 0, typeVariablesAdded, sbTypeParameters);
        }
        for (int i = 1; i < size; ++i) {
            sb.append(" x ");
            appendParameter(sb, i, typeVariablesAdded, sbTypeParameters);
        }
    }

    private void appendParameter(
            StringBuilder stringBuilder,
            int parameterAndReturnIndex,
            Set<String> typeVariablesAdded,
            StringBuilder sbTypeParameters) {
        int typeParameterIndex = parameterAndReturn2TypeParameterIndex.get(parameterAndReturnIndex);
        String typeVariable = typeParameters.get(typeParameterIndex);
        if (!nonFixedTypeParameters.contains(typeVariable)) {
            List<String> bounds = new ArrayList<>();
            String separator = " | ";
            if (bindingCollection.hasUpperTypeBounds(typeVariable)) {
                SortedSet<String> sortedSet = new TreeSet<>(
                        bindingCollection.getUpperTypeBounds(typeVariable).getTypeSymbols().keySet());
                bounds.addAll(sortedSet);
                separator = " & ";
            } else if (bindingCollection.hasLowerTypeBounds(typeVariable)) {
                SortedSet<String> sortedSet = new TreeSet<>(
                        bindingCollection.getLowerTypeBounds(typeVariable).getTypeSymbols().keySet());
                bounds.addAll(sortedSet);
            }
            if (bindingCollection.hasLowerRefBounds(typeVariable)) {
                SortedSet<String> sortedSet = new TreeSet<>(bindingCollection.getLowerRefBounds(typeVariable));
                bounds.addAll(sortedSet);
            }
            appendBound(stringBuilder, bounds, separator);
        } else {
            stringBuilder.append(typeVariable);
            if (!typeVariablesAdded.contains(typeVariable)) {
                typeVariablesAdded.add(typeVariable);
                appendTypeParameter(sbTypeParameters, typeVariable);
            }
        }
    }

    private void appendTypeParameter(StringBuilder sbTypeParameters, String typeVariable) {
        boolean hasLowerBounds = bindingCollection.hasLowerBounds(typeVariable);
        boolean hasUpperTypeBounds = bindingCollection.hasUpperTypeBounds(typeVariable);
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
            if (bindingCollection.hasLowerTypeBounds(typeVariable)) {
                SortedSet<String> sortedSet = new TreeSet<>(
                        bindingCollection.getLowerTypeBounds(typeVariable).getTypeSymbols().keySet());
                lowerBounds.addAll(sortedSet);
            }
            if (bindingCollection.hasLowerRefBounds(typeVariable)) {
                SortedSet<String> sortedSet = new TreeSet<>(bindingCollection.getLowerRefBounds(typeVariable));
                lowerBounds.addAll(sortedSet);
            }
            appendBound(sbTypeParameters, lowerBounds, " | ");
            sbTypeParameters.append(" <: ");
        }

        if (hasLowerOrUpperBounds) {
            sbTypeParameters.append(typeVariable);
        }

        if (hasUpperTypeBounds) {
            sbTypeParameters.append(" <: ");
            sbTypeParameters.append(bindingCollection.getUpperTypeBounds(typeVariable).getAbsoluteName());
        }
    }

    private void appendBound(StringBuilder stringBuilder, Collection<String> bounds, String separator) {
        if (bounds.size() != 1) {
            stringBuilder.append("(");
            Iterator<String> iterator = bounds.iterator();
            if (iterator.hasNext()) {
                stringBuilder.append(iterator.next());
            }
            while (iterator.hasNext()) {
                stringBuilder.append(separator).append(iterator.next());
            }
            stringBuilder.append(")");
        } else {
            stringBuilder.append(bounds.iterator().next());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("{").append(getNumberOfNonOptionalParameters()).append("}").append("[");
        if (!wasSimplified) {
            for (IVariable parameter : parameters) {
                appendVariable(sb, parameter.getAbsoluteName()).append(", ");
            }
            appendVariable(sb, TinsPHPConstants.RETURN_VARIABLE_NAME);
        } else {
            //-1 since last is the return type variable
            int numberOfParameters = parameterAndReturn2TypeParameterIndex.size() - 1;
            for (int index = 0; index < numberOfParameters; ++index) {
                String typeVariable = typeParameters.get(parameterAndReturn2TypeParameterIndex.get(index));
                sb.append(parameters.get(index).getAbsoluteName()).append(":");
                appendVariable(sb, typeVariable, !nonFixedTypeParameters.contains(typeVariable)).append(", ");
            }
            String typeVariable = typeParameters.get(parameterAndReturn2TypeParameterIndex.get(numberOfParameters));
            sb.append(TinsPHPConstants.RETURN_VARIABLE_NAME).append(":");
            appendVariable(sb, typeVariable, !nonFixedTypeParameters.contains(typeVariable)).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }


    private StringBuilder appendVariable(StringBuilder sb, String variableName) {
        sb.append(variableName).append(":");
        ITypeVariableReference reference = bindingCollection.getTypeVariableReference(variableName);
        boolean isFixed = reference.hasFixedType();
        String typeVariable = reference.getTypeVariable();
        return appendVariable(sb, typeVariable, isFixed);
    }

    private StringBuilder appendVariable(StringBuilder sb, String typeVariable, boolean isFixed) {
        sb.append(typeVariable)
                .append("<")
                .append(bindingCollection.getLowerBoundConstraintIds(typeVariable))
                .append(",")
                .append(bindingCollection.getUpperBoundConstraintIds(typeVariable))
                .append(">");
        if (isFixed) {
            sb.append("#");
        }
        return sb;
    }

    @SuppressWarnings("checkstyle:visibilitymodifier")
    private class AddToTypeParameterDto
    {
        public ITypeVariableReference typeVariableReference;
        public Set<String> typeVariablesAdded;
        public int count = 0;
        public boolean shallRenameTypeVariables;
        public int typeParameterSuffix = 1;
        public boolean useSuffix;

        private AddToTypeParameterDto(
                Set<String> theTypeVariablesAdded,
                boolean mustRenameTypeVariables,
                boolean shallUseSuffix) {
            typeVariablesAdded = theTypeVariablesAdded;
            shallRenameTypeVariables = mustRenameTypeVariables;
            useSuffix = shallUseSuffix;
        }
    }
}
