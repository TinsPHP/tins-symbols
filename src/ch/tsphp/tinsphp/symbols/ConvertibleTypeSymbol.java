/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.BoundResultDto;
import ch.tsphp.tinsphp.common.inference.constraints.EBindingCollectionMode;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConvertibleTypeSymbol extends APolymorphicTypeSymbol implements IConvertibleTypeSymbol
{

    private List<String> typeParameters;
    private Set<String> nonFixedTypeParameters;
    private IBindingCollection bindingCollection;
    private boolean wasBound = false;

    public ConvertibleTypeSymbol(IBindingCollection theBindingCollection) {
        bindingCollection = theBindingCollection;
        String typeVariable = "T";
        typeParameters = new ArrayList<>(1);
        typeParameters.add(typeVariable);
        nonFixedTypeParameters = new HashSet<>(1);
        nonFixedTypeParameters.add(typeVariable);
        bindingCollection.addVariable(typeVariable, new TypeVariableReference(typeVariable));
    }

    private ConvertibleTypeSymbol(ConvertibleTypeSymbol convertibleTypeSymbol) {
        bindingCollection = convertibleTypeSymbol.bindingCollection;
        typeParameters = new ArrayList<>(convertibleTypeSymbol.typeParameters);
        nonFixedTypeParameters = new HashSet<>(convertibleTypeSymbol.nonFixedTypeParameters);
        wasBound = convertibleTypeSymbol.wasBound;
        hasAbsoluteNameChanged = true;
    }

    @Override
    public boolean wasBound() {
        return wasBound;
    }


    //Warning! start code duplication - very similar to the one in FunctionType
    @Override
    public void fix(String fixedTypeParameter) {
        if (!wasBound()) {
            throw new IllegalStateException("cannot fix a type parameter if this convertible type was not bound to a "
                    + "parametric type");
        }

        if (!nonFixedTypeParameters.remove(fixedTypeParameter)) {
            throw new IllegalArgumentException("the convertible type was bound to " + typeParameters + " and its "
                    + "non-fixed type parameters are " + nonFixedTypeParameters + " but it was indicated that "
                    + fixedTypeParameter + " was fixed");
        }

        notifyNameHasChanged();
        notifyWasFixed();
    }
    //Warning! end code duplication - very similar to the one in FunctionType


    @Override
    public boolean isFixed() {
        return !wasBound || nonFixedTypeParameters.isEmpty();
    }

    @Override
    public IConvertibleTypeSymbol copy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        return new ConvertibleTypeSymbol(this);
    }


    //Warning! start code duplication - very similar to the one in FunctionType
    @Override
    public void renameTypeParameter(String typeParameter, String newTypeParameter) {
        if (!wasBound) {
            throw new IllegalStateException("can only rename a type parameter if this convertible type was bound to"
                    + " another parametric polymorphic type");
        }

        String typeVariable = typeParameters.get(0);
        if (!typeVariable.equals(typeParameter)) {
            throw new IllegalArgumentException("the convertible type was bound to " + typeParameters
                    + " but " + typeParameter + " should be renamed.");
        }
        renameTypeVariableAfterContainsCheck(typeParameter, newTypeParameter);

        notifyNameHasChanged();
    }

    private void renameTypeVariableAfterContainsCheck(String typeParameter, String newTypeParameter) {
        typeParameters.set(0, newTypeParameter);
        if (nonFixedTypeParameters.remove(typeParameter)) {
            nonFixedTypeParameters.add(newTypeParameter);
        }
    }
    //Warning! end code duplication - very similar to the one in FunctionType


    //Warning! start code duplication - very similar to the one in FunctionType
    @Override
    public void bindTo(IBindingCollection newBindingCollection, List<String> bindingTypeParameters) {
        if (typeParameters.size() != bindingTypeParameters.size()) {
            throw new IllegalArgumentException("This parametric type requires " + typeParameters.size()
                    + " type parameter(s) but only " + bindingTypeParameters.size() + " provided");
        }

        transferBounds(newBindingCollection, bindingTypeParameters);

        bindingCollection = newBindingCollection;
        renameTypeVariableAfterContainsCheck(typeParameters.get(0), bindingTypeParameters.get(0));
        wasBound = true;

        notifyNameHasChanged();
    }
    //Warning! end code duplication - very similar to the one in FunctionType


    private void transferBounds(IBindingCollection newBindingCollection, List<String> bindingTypeParameters) {
        String newTypeVariable = bindingTypeParameters.get(0);
        if (hasLowerTypeBounds()) {
            newBindingCollection.addLowerTypeBound(newTypeVariable, getLowerTypeBounds());
        }
        if (hasUpperTypeBounds()) {
            if (newBindingCollection.getMode() != EBindingCollectionMode.SoftTyping) {
                newBindingCollection.addUpperTypeBound(newTypeVariable, getUpperTypeBounds());
            } else {
                newBindingCollection.addLowerTypeBound(newTypeVariable, getUpperTypeBounds());
            }
        }

        //a convertible type has only one type parameter and therefore cannot have lower ref bounds
    }


    //Warning! end code duplication - same as in FunctionType
    @Override
    public void rebind(IBindingCollection newBindingCollection) {
        if (!wasBound) {
            throw new IllegalStateException("can only rebind a convertible type if it was already bound before.");
        }

        bindingCollection = newBindingCollection;
    }
    //Warning! end code duplication - same as in FunctionType

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
    public String getTypeVariable() {
        return typeParameters.get(0);
    }

    @Override
    public BoundResultDto addLowerTypeBound(ITypeSymbol typeSymbol) {
        BoundResultDto result = bindingCollection.addLowerTypeBound(typeParameters.get(0), typeSymbol);
        if (result.hasChanged) {
            notifyNameHasChanged();
        }
        return result;
    }

    @Override
    public BoundResultDto addUpperTypeBound(ITypeSymbol typeSymbol) {
        BoundResultDto result = bindingCollection.addUpperTypeBound(typeParameters.get(0), typeSymbol);
        if (result.hasChanged) {
            notifyNameHasChanged();
        }
        return result;
    }

    @Override
    public boolean hasLowerTypeBounds() {
        return bindingCollection.hasLowerTypeBounds(typeParameters.get(0));
    }

    @Override
    public boolean hasUpperTypeBounds() {
        return bindingCollection.hasUpperTypeBounds(typeParameters.get(0));
    }

    @Override
    public IUnionTypeSymbol getLowerTypeBounds() {
        return bindingCollection.getLowerTypeBounds(typeParameters.get(0));
    }

    @Override
    public IIntersectionTypeSymbol getUpperTypeBounds() {
        return bindingCollection.getUpperTypeBounds(typeParameters.get(0));
    }

    @Override
    protected String calculateAbsoluteName() {
        IUnionTypeSymbol lowerTypeBounds = getLowerTypeBounds();
        IIntersectionTypeSymbol upperTypeBounds = getUpperTypeBounds();
        String absoluteName;
        String typeVariable = typeParameters.get(0);

        if (lowerTypeBounds == null && upperTypeBounds == null) {
            absoluteName = "{as " + typeVariable + "}";
        } else {
            String lowerAbsoluteName = lowerTypeBounds != null ? lowerTypeBounds.getAbsoluteName() : "";
            String upperAbsoluteName = upperTypeBounds != null ? upperTypeBounds.getAbsoluteName() : "";
            if (isFixed()) {
                if (!lowerAbsoluteName.isEmpty()) {
                    absoluteName = "{as " + lowerAbsoluteName + "}";
                } else {
                    absoluteName = "{as " + upperAbsoluteName + "}";
                }
            } else {
                absoluteName = "{as " + typeVariable + "}";
            }
        }
        return absoluteName;
    }

    @Override
    public boolean canBeUsedInIntersection() {
        return true;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

}
