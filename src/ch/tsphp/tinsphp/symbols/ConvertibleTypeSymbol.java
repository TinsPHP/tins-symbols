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
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;

public class ConvertibleTypeSymbol extends AIndirectTypeSymbol implements IConvertibleTypeSymbol
{
    private static final String TYPE_VARIABLE = "T";
    private IOverloadBindings overloadBindings;

    public ConvertibleTypeSymbol(
            IOverloadBindings theOverloadBindings) {
        overloadBindings = theOverloadBindings;
        overloadBindings.addVariable(TYPE_VARIABLE, new TypeVariableReference(TYPE_VARIABLE));
    }

    @Override
    public boolean addLowerTypeBound(ITypeSymbol typeSymbol) {
        boolean hasChanged = overloadBindings.addLowerTypeBound(TYPE_VARIABLE, typeSymbol);
        hasAbsoluteNameChanged = hasAbsoluteNameChanged || hasChanged;
        return hasChanged;
    }

    @Override
    public boolean addUpperTypeBound(ITypeSymbol typeSymbol) {
        boolean hasChanged = overloadBindings.addUpperTypeBound(TYPE_VARIABLE, typeSymbol);
        hasAbsoluteNameChanged = hasAbsoluteNameChanged || hasChanged;
        return hasChanged;
    }

    @Override
    public boolean hasLowerTypeBounds() {
        return overloadBindings.hasLowerTypeBounds(TYPE_VARIABLE);
    }

    @Override
    public boolean hasUpperTypeBounds() {
        return overloadBindings.hasUpperTypeBounds(TYPE_VARIABLE);
    }

    @Override
    public IUnionTypeSymbol getLowerTypeBounds() {
        return overloadBindings.getLowerTypeBounds(TYPE_VARIABLE);
    }

    @Override
    public IIntersectionTypeSymbol getUpperTypeBounds() {
        return overloadBindings.getUpperTypeBounds(TYPE_VARIABLE);
    }

    @Override
    protected String calculateAbsoluteName() {
        IUnionTypeSymbol lowerTypeBounds = getLowerTypeBounds();
        IIntersectionTypeSymbol upperTypeBounds = getUpperTypeBounds();
        String absoluteName;

        if (lowerTypeBounds == null && upperTypeBounds == null) {
            absoluteName = "{as " + TYPE_VARIABLE + "}";
        } else {
            String lowerAbsoluteName = lowerTypeBounds != null ? lowerTypeBounds.getAbsoluteName() : "";
            String upperAbsoluteName = upperTypeBounds != null ? getUpperTypeBounds().getAbsoluteName() : "";
            if (lowerAbsoluteName.equals(upperAbsoluteName)) {
                absoluteName = "{as " + lowerAbsoluteName + "}";
            } else {
                StringBuilder stringBuilder = new StringBuilder("{as ");
                stringBuilder.append(TYPE_VARIABLE).append(" \\ ");
                if (!lowerAbsoluteName.isEmpty()) {
                    stringBuilder.append(lowerAbsoluteName).append(" < ");
                }
                if (!upperAbsoluteName.isEmpty()) {
                    stringBuilder.append(" < ").append(upperAbsoluteName);
                }
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
