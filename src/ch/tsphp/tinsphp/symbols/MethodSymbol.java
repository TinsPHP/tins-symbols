/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class MethodSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraintCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.symbols.constraints.ConstraintCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MethodSymbol extends AScopedSymbol implements IMethodSymbol
{

    private final List<IVariableSymbol> parameters = new ArrayList<>();
    private final IMinimalVariableSymbol returnVariable;
    private final IModifierSet returnTypeModifiers;
    private final IConstraintCollection constraintCollection;
    private Map<String, IFunctionType> overloads = new HashMap<>(0);

    @SuppressWarnings("checkstyle:parameternumber")
    public MethodSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet methodModifiers,
            IModifierSet theReturnTypeModifiers,
            IMinimalVariableSymbol theReturnVariable,
            String name,
            IScope enclosingScope) {
        super(scopeHelper, definitionAst, methodModifiers, name, enclosingScope);
        returnTypeModifiers = theReturnTypeModifiers;
        returnVariable = theReturnVariable;

        constraintCollection = new ConstraintCollection(getAbsoluteName());
    }

    @Override
    public void addParameter(IVariableSymbol typeSymbol) {
        parameters.add(typeSymbol);
    }

    @Override
    public List<IVariableSymbol> getParameters() {
        return parameters;
    }

    @Override
    public IMinimalVariableSymbol getReturnVariable() {
        return returnVariable;
    }

    @Override
    public boolean isStatic() {
        return modifiers.isStatic();
    }

    @Override
    public boolean isFinal() {
        return modifiers.isFinal();
    }

    @Override
    public boolean isAbstract() {
        return modifiers.isAbstract();
    }

    @Override
    public boolean isAlwaysCasting() {
        return returnTypeModifiers.isAlwaysCasting();
    }

    @Override
    public boolean isPublic() {
        return modifiers.isPublic();
    }

    @Override
    public boolean isProtected() {
        return modifiers.isProtected();
    }

    @Override
    public boolean isPrivate() {
        return modifiers.isPrivate();
    }

    @Override
    public boolean isFalseable() {
        return returnTypeModifiers.isFalseable();
    }

    @Override
    public boolean isNullable() {
        return returnTypeModifiers.isNullable();
    }

    @Override
    public String toString() {
        return super.toString() + ModifierHelper.getModifiersAsString(returnTypeModifiers);
    }

    //Warning! start code duplication - same as in GlobalNamespaceScope
    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && initialisedSymbols.get(symbolName);
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && !initialisedSymbols.get(symbolName);
    }
    //Warning! end code duplication - same as in GlobalNamespaceScope

    //Warning! start code duplication - same as in GlobalNamespaceScope
    @Override
    public List<IConstraint> getConstraints() {
        return constraintCollection.getConstraints();
    }

    @Override
    public void addConstraint(IConstraint constraint) {
        constraintCollection.addConstraint(constraint);
    }

    @Override
    public List<IBindingCollection> getBindings() {
        return constraintCollection.getBindings();
    }

    @Override
    public void addBindingCollection(IBindingCollection bindingCollection) {
        constraintCollection.addBindingCollection(bindingCollection);
    }

    @Override
    public void setBindings(List<IBindingCollection> theBindings) {
        constraintCollection.setBindings(theBindings);
    }
    //Warning! end code duplication - same as in GlobalNamespaceScope


    @Override
    public void setOverloads(Collection<IFunctionType> theOverloads) {
        Iterator<IFunctionType> iterator = theOverloads.iterator();
        if (iterator.hasNext()) {
            IFunctionType firstOverload = iterator.next();
            if (iterator.hasNext()) {
                overloads = filterOverloads(iterator, firstOverload);
            } else {
                overloads.put(firstOverload.getSignature(), firstOverload);
            }
        }
    }

    private Map<String, IFunctionType> filterOverloads(Iterator<IFunctionType> iterator, IFunctionType firstOverload) {
        Map<String, IFunctionType> newOverloads = new HashMap<>();
        newOverloads.put(firstOverload.getSignature(), firstOverload);
        while (iterator.hasNext()) {
            IFunctionType overload = iterator.next();
            String signature = overload.getSignature();
            if (!newOverloads.containsKey(signature)) {
                newOverloads.put(signature, overload);
            } else {
                IFunctionType currentOverload = newOverloads.get(signature);
                if (overload.getNumberOfConvertibleApplications()
                        < currentOverload.getNumberOfConvertibleApplications()) {
                    newOverloads.put(signature, overload);
                }
            }
        }
        return newOverloads;
    }

    @Override
    public Collection<IFunctionType> getOverloads() {
        return overloads.values();
    }

}
