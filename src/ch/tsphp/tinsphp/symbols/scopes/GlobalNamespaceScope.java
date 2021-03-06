/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class GlobalNamespaceScope from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols.scopes;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.LowerCaseStringMap;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraintCollection;
import ch.tsphp.tinsphp.common.scopes.IGlobalNamespaceScope;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.utils.MapHelper;
import ch.tsphp.tinsphp.symbols.constraints.ConstraintCollection;

import java.util.List;


public class GlobalNamespaceScope extends AScope implements IGlobalNamespaceScope
{

    private final ILowerCaseStringMap<List<ISymbol>> symbolsCaseInsensitive = new LowerCaseStringMap<>();
    private final IConstraintCollection constraintCollection;

    public GlobalNamespaceScope(IScopeHelper scopeHelper, String scopeName) {
        super(scopeHelper, scopeName, null);
        constraintCollection = new ConstraintCollection(scopeName);
    }

    @Override
    public void define(ISymbol symbol) {
        scopeHelper.define(this, symbol);
        MapHelper.addToListInMap(symbolsCaseInsensitive, symbol.getName(), symbol);
    }

    @Override
    @Deprecated
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        throw new UnsupportedOperationException("Is deprecated and should no longer be used");
    }

    @Override
    public ISymbol resolve(ITSPHPAst identifier) {
        ISymbol symbol = null;
        String typeName = getTypeNameWithoutNamespacePrefix(identifier.getText());
        if (symbols.containsKey(typeName)) {
            symbol = symbols.get(typeName).get(0);
        }
        return symbol;
    }

    @Override
    public ISymbol resolveCaseInsensitive(ITSPHPAst identifier) {
        ISymbol symbol = null;
        String typeName = getTypeNameWithoutNamespacePrefix(identifier.getText());
        if (symbolsCaseInsensitive.containsKey(typeName)) {
            symbol = symbolsCaseInsensitive.get(typeName).get(0);
        }
        return symbol;
    }

    //Warning! start code duplication - same as in MethodSymbol
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
    //Warning! end code duplication - same as in MethodSymbol

    private String getTypeNameWithoutNamespacePrefix(String typeName) {
        String typeNameWithoutPrefix = typeName;
        int scopeNameLength = scopeName.length();
        if (typeName.length() > scopeNameLength && typeName.substring(0, scopeNameLength).equals(scopeName)) {
            typeNameWithoutPrefix = typeName.substring(scopeNameLength);
        }
        return typeNameWithoutPrefix;
    }

    @Override
    public String getAbsoluteName() {
        return constraintCollection.getAbsoluteName();
    }

    //Warning! start code duplication - same as in MethodSymbol
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
    //Warning! end code duplication - same as in MethodSymbol
}
