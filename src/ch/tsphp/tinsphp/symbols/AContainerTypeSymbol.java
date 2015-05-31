/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IContainerTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IObservableTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IParametricTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class AContainerTypeSymbol extends APolymorphicTypeSymbol implements IContainerTypeSymbol
{
    protected static enum ETypeRelation
    {
        NO_RELATION,
        PARENT_TYPE,
        SUBTYPE
    }

    protected final ITypeHelper typeHelper;
    protected final Map<String, ITypeSymbol> typeSymbols;
    protected boolean isFixed = true;

    public AContainerTypeSymbol(ITypeHelper theTypeHelper) {
        super();
        typeHelper = theTypeHelper;
        typeSymbols = new HashMap<>();
    }

    protected AContainerTypeSymbol(
            ITypeHelper theTypeHelper,
            AContainerTypeSymbol containerTypeSymbol,
            Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        typeHelper = theTypeHelper;
        typeSymbols = new HashMap<>(containerTypeSymbol.typeSymbols);
        isFixed = containerTypeSymbol.isFixed;
        if (!isFixed) {
            deepenCopy(parametricTypeSymbols);
        }
    }

    public abstract String getTypeSeparator();

    /**
     * Returns the name of the type if no type is within the container
     */
    public abstract String getDefaultName();

    protected abstract boolean addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol);

    protected void deepenCopy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        Deque<IParametricTypeSymbol> tmpParametricTypeSymbols = new ArrayDeque<>();
        Deque<IContainerTypeSymbol> containerTypeSymbols = new ArrayDeque<>();

        Iterator<Map.Entry<String, ITypeSymbol>> iterator = getTypeSymbols().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ITypeSymbol> copyEntry = iterator.next();
            ITypeSymbol typeSymbol = copyEntry.getValue();
            if (isContainerTypeAndNotFixed(typeSymbol)) {
                iterator.remove();
                containerTypeSymbols.add((IContainerTypeSymbol) typeSymbol);
            } else if (isParametricTypeAndNotFixed(typeSymbol)) {
                iterator.remove();
                tmpParametricTypeSymbols.add((IParametricTypeSymbol) typeSymbol);
            }
        }

        for (IContainerTypeSymbol containerTypeSymbol : containerTypeSymbols) {
            IContainerTypeSymbol copy = containerTypeSymbol.copy(parametricTypeSymbols);
            addTypeSymbol(copy);
        }

        for (IParametricTypeSymbol parametricTypeSymbol : tmpParametricTypeSymbols) {
            IParametricTypeSymbol copy = parametricTypeSymbol.copy(parametricTypeSymbols);
            addTypeSymbol(copy);
            parametricTypeSymbols.add(copy);
        }
    }

    @Override
    public void remove(String absoluteName) {
        typeSymbols.remove(absoluteName);
        hasAbsoluteNameChanged = true;
    }

    private boolean isContainerTypeAndNotFixed(ITypeSymbol typeSymbol) {
        return typeSymbol instanceof IContainerTypeSymbol && !((IContainerTypeSymbol) typeSymbol).isFixed();
    }

    private boolean isParametricTypeAndNotFixed(ITypeSymbol typeSymbol) {
        return typeSymbol instanceof IParametricTypeSymbol && !((IParametricTypeSymbol) typeSymbol).isFixed();
    }

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

    protected boolean merge(IContainerTypeSymbol containerTypeSymbol) {
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
    public boolean isFixed() {
        return isFixed;
    }

    @Override
    public void nameOfObservableHasChanged(IObservableTypeSymbol type, String oldAbsoluteName) {
        typeSymbols.remove(oldAbsoluteName);
        typeSymbols.put(type.getAbsoluteName(), type);
        notifyHasChanged();
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
