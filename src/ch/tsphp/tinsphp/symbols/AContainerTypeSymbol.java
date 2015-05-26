/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class AContainerTypeSymbol<TContainer extends IContainerTypeSymbol<TContainer>>
        extends AIndirectTypeSymbol implements IContainerTypeSymbol<TContainer>
{
    protected static enum ETypeRelation
    {
        NO_RELATION,
        PARENT_TYPE,
        SUBTYPE
    }

    protected final ITypeHelper typeHelper;
    protected final Map<String, ITypeSymbol> typeSymbols;

    public AContainerTypeSymbol(ITypeHelper theTypeHelper) {
        super();
        typeHelper = theTypeHelper;
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
            boolean hasContainerChanged = addTypeSymbol(typeSymbol);
            hasChanged = hasChanged || hasContainerChanged;
        }

        hasAbsoluteNameChanged = hasAbsoluteNameChanged || hasChanged;

        return hasChanged;
    }

    @Override
    public Map<String, ITypeSymbol> getTypeSymbols() {
        return typeSymbols;
    }

    @Override
    protected String calculateAbsoluteName() {
        String absoluteName;
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
            absoluteName = getDefaultName();
        }
        return absoluteName;
    }

}
