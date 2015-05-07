/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class AContainerTypeSymbol<TContainer extends IContainerTypeSymbol<? super TContainer>>
        implements IContainerTypeSymbol<TContainer>
{
    private static final String ERROR_MESSAGE = "You are dealing with an AContainerTypeSymbol.";

    protected static enum ETypeRelation
    {
        NO_RELATION,
        PARENT_TYPE,
        SUB_TYPE
    }

    protected final IOverloadResolver overloadResolver;
    protected final Map<String, ITypeSymbol> typeSymbols;

    protected boolean hasAbsoluteNameChanged = true;
    protected String ownAsboluteName;

    public AContainerTypeSymbol(IOverloadResolver theOverloadResolver) {
        super();
        overloadResolver = theOverloadResolver;
        typeSymbols = new HashMap<>();
    }

    public abstract String getTypeSeparator();

    /**
     * Returns the name of the type if no type is within the container
     */
    public abstract String getDefaultName();

    protected abstract boolean addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol);

    /**
     * Returns true if all types in the container can be used in an intersection.
     */
    @Override
    public boolean canBeUsedInIntersection() {
        boolean canBeUsed = true;
        for (ITypeSymbol typeSymbol : typeSymbols.values()) {
            if (!typeSymbol.canBeUsedInIntersection()) {
                canBeUsed = false;
                break;
            }
        }
        return canBeUsed;
    }

    @Override
    public ITypeSymbol evalSelf() {
        return this;
    }

    @Override
    public boolean addTypeSymbol(ITypeSymbol typeSymbol) {
        boolean hasChanged = false;

        String absoluteName = typeSymbol.getAbsoluteName();

        //no need to add it if it already exists in the container; ergo simplification = do not insert
        if (!typeSymbols.containsKey(absoluteName)) {
            hasChanged = addAndSimplify(absoluteName, typeSymbol);
        }

        hasAbsoluteNameChanged = hasAbsoluteNameChanged || hasChanged;

        return hasChanged;
    }

    protected boolean merge(IContainerTypeSymbol<TContainer> containerTypeSymbol) {
        boolean hasChanged = false;

        for (ITypeSymbol typeSymbol : containerTypeSymbol.getTypeSymbols().values()) {
            boolean hasUnionChanged = addTypeSymbol(typeSymbol);
            hasChanged = hasChanged || hasUnionChanged;
        }

        hasAbsoluteNameChanged = hasAbsoluteNameChanged || hasChanged;

        return hasChanged;
    }

    @Override
    public Map<String, ITypeSymbol> getTypeSymbols() {
        return typeSymbols;
    }

    @Override
    public IScope getDefinitionScope() {
        return null;
    }

    @Override
    public String getName() {
        return getAbsoluteName();
    }

    @Override
    public String getAbsoluteName() {
        if (hasAbsoluteNameChanged) {
            ownAsboluteName = calculateAbsoluteName();
            hasAbsoluteNameChanged = false;
        }
        return ownAsboluteName;
    }

    private String calculateAbsoluteName() {
        String absolutename;
        if (!typeSymbols.isEmpty()) {
            final String separator = getTypeSeparator();

            StringBuilder sb = new StringBuilder();
            SortedSet<String> sortedSet = new TreeSet<>(typeSymbols.keySet());
            Iterator<String> iterator = sortedSet.iterator();
            sb.append(iterator.next());
            while (iterator.hasNext()) {
                sb.append(separator).append(iterator.next());
            }
            if (typeSymbols.size() > 1) {
                sb.insert(0, "(");
                sb.append(")");
            }
            return sb.toString();
        } else {
            absolutename = getDefaultName();
        }
        return absolutename;
    }

    @Override
    public String toString() {
        return getAbsoluteName();
    }


    //--------------------------------------------------------------
    // Unsupported Methods

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void addModifier(Integer integer) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean removeModifier(Integer integer) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public IModifierSet getModifiers() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setModifiers(IModifierSet modifierSet) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public ITSPHPAst getDefinitionAst() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setDefinitionScope(IScope iScope) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public ITypeSymbol getType() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setType(ITypeSymbol iTypeSymbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean isFalseable() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean isNullable() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }
}
