/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ConvertibleTypeSymbol extends APolymorphicTypeSymbol implements IConvertibleTypeSymbol
{
    private String typeVariable = "T";
    private IOverloadBindings overloadBindings;
    private boolean wasBound = false;

    public ConvertibleTypeSymbol(IOverloadBindings theOverloadBindings) {
        overloadBindings = theOverloadBindings;
        overloadBindings.addVariable(typeVariable, new TypeVariableReference(typeVariable));
    }

    private ConvertibleTypeSymbol(ConvertibleTypeSymbol convertibleTypeSymbol) {
        overloadBindings = convertibleTypeSymbol.overloadBindings;
        typeVariable = convertibleTypeSymbol.typeVariable;
        wasBound = true;
        hasAbsoluteNameChanged = true;
    }

    @Override
    public boolean wasBound() {
        return wasBound;
    }

    @Override
    public IConvertibleTypeSymbol copy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        return new ConvertibleTypeSymbol(this);
    }

    @Override
    public boolean isFixed() {
        return !wasBound;
    }

    @Override
    public void renameTypeVariable(String theTypeVariable, String newTypeVariable) {
        if (!typeVariable.equals(theTypeVariable)) {
            throw new IllegalArgumentException("given type variable name \"" + theTypeVariable + "\" "
                    + "was not the current type variable name \"" + typeVariable + "\"");
        }

        if (!wasBound) {
            throw new IllegalArgumentException("can only rename the type variable if it is bound to another "
                    + "parametric polymorphic type");
        }

        typeVariable = newTypeVariable;

        notifyHasChanged();
    }

    @Override
    public void bindTo(IOverloadBindings bindings, List<String> typeParameters) {
        if (typeParameters.size() != 1) {
            throw new IllegalArgumentException("a convertible type expects exactly one type parameter");
        }

        String newTypeVariable = typeParameters.get(0);
        if (hasLowerTypeBounds()) {
            bindings.addLowerTypeBound(newTypeVariable, getLowerTypeBounds());
        }
        if (hasUpperTypeBounds()) {
            bindings.addUpperTypeBound(newTypeVariable, getUpperTypeBounds());
        }
        overloadBindings = bindings;
        typeVariable = newTypeVariable;
        notifyHasChanged();
        wasBound = true;
    }

    @Override
    public List<String> rebind(IOverloadBindings newOverloadBindings) {
        if (!wasBound) {
            throw new IllegalStateException("can only rebind a convertible type if it was already bound before.");
        }

        //ensures that the type variable exists in the new overload bindings
        newOverloadBindings.renameTypeVariable(typeVariable, typeVariable);

        overloadBindings = newOverloadBindings;

        return Arrays.asList(typeVariable);
    }

    @Override
    public List<String> getTypeVariables() {
        return Arrays.asList(typeVariable);
    }

    @Override
    public IOverloadBindings getOverloadBindings() {
        return overloadBindings;
    }

    @Override
    public String getTypeVariable() {
        return typeVariable;
    }

    @Override
    public boolean addLowerTypeBound(ITypeSymbol typeSymbol) {
        boolean hasChanged = overloadBindings.addLowerTypeBound(typeVariable, typeSymbol);
        if (hasChanged) {
            notifyHasChanged();
        }
        return hasChanged;
    }

    @Override
    public boolean addUpperTypeBound(ITypeSymbol typeSymbol) {
        boolean hasChanged = overloadBindings.addUpperTypeBound(typeVariable, typeSymbol);
        if (hasChanged) {
            notifyHasChanged();
        }
        return hasChanged;
    }

    @Override
    public boolean hasLowerTypeBounds() {
        return overloadBindings.hasLowerTypeBounds(typeVariable);
    }

    @Override
    public boolean hasUpperTypeBounds() {
        return overloadBindings.hasUpperTypeBounds(typeVariable);
    }

    @Override
    public IUnionTypeSymbol getLowerTypeBounds() {
        return overloadBindings.getLowerTypeBounds(typeVariable);
    }

    @Override
    public IIntersectionTypeSymbol getUpperTypeBounds() {
        return overloadBindings.getUpperTypeBounds(typeVariable);
    }

    @Override
    protected String calculateAbsoluteName() {
        IUnionTypeSymbol lowerTypeBounds = getLowerTypeBounds();
        IIntersectionTypeSymbol upperTypeBounds = getUpperTypeBounds();
        String absoluteName;

        if (lowerTypeBounds == null && upperTypeBounds == null) {
            absoluteName = "{as " + typeVariable + "}";
        } else {
            String lowerAbsoluteName = lowerTypeBounds != null ? lowerTypeBounds.getAbsoluteName() : "";
            String upperAbsoluteName = upperTypeBounds != null ? getUpperTypeBounds().getAbsoluteName() : "";
            if (lowerAbsoluteName.equals(upperAbsoluteName)) {
                absoluteName = "{as " + lowerAbsoluteName + "}";
            } else if (wasBound) {
                absoluteName = "{as " + typeVariable + "}";
            } else {
                StringBuilder stringBuilder = new StringBuilder("{as ");
                stringBuilder.append(typeVariable).append(" \\ ");
                if (!lowerAbsoluteName.isEmpty()) {
                    stringBuilder.append(lowerAbsoluteName).append(" < ");
                }
                stringBuilder.append(typeVariable);
                if (!upperAbsoluteName.isEmpty()) {
                    stringBuilder.append(" < ").append(upperAbsoluteName);
                }
                stringBuilder.append("}");
                absoluteName = stringBuilder.toString();
            }
        }
        return absoluteName;
    }

    @Override
    public ITypeSymbol evalSelf() {
        return this;
    }

    @Override
    public boolean canBeUsedInIntersection() {
        return true;
    }

}
