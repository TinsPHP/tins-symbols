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
import ch.tsphp.tinsphp.common.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static ch.tsphp.tinsphp.symbols.AContainerTypeSymbol.EAdditionStatus.CAN_BE_ADDED;
import static ch.tsphp.tinsphp.symbols.AContainerTypeSymbol.EAdditionStatus.DOES_NOT_ADD_NEW_INFORMATION;
import static ch.tsphp.tinsphp.symbols.AContainerTypeSymbol.EAdditionStatus.REPLACES_EXISTING;

public abstract class AContainerTypeSymbol extends APolymorphicTypeSymbol implements IContainerTypeSymbol
{

    protected static enum EAdditionStatus
    {
        CAN_BE_ADDED,
        DOES_NOT_ADD_NEW_INFORMATION,
        REPLACES_EXISTING
    }

    protected final ITypeHelper typeHelper;
    protected final Map<String, ITypeSymbol> typeSymbols;
    protected int nonFixedTypesCount = 0;

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
        nonFixedTypesCount = containerTypeSymbol.nonFixedTypesCount;
        if (nonFixedTypesCount > 0) {
            deepenCopy(parametricTypeSymbols);
        }
    }

    protected abstract boolean firstReplacesSecondType(ITypeSymbol newTypeSymbol, ITypeSymbol existingTypeSymbol);

    protected abstract boolean secondReplacesFirstType(ITypeSymbol newTypeSymbol, ITypeSymbol existingTypeSymbol);

    public abstract String getTypeSeparator();

    /**
     * Returns the name of the type if no type is within the container
     */
    public abstract String getDefaultName();

    protected void deepenCopy(Collection<IParametricTypeSymbol> parametricTypeSymbols) {
        Deque<IParametricTypeSymbol> tmpParametricTypeSymbols = new ArrayDeque<>();
        Deque<IContainerTypeSymbol> containerTypeSymbols = new ArrayDeque<>();

        Iterator<Map.Entry<String, ITypeSymbol>> iterator = getTypeSymbols().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ITypeSymbol> copyEntry = iterator.next();
            ITypeSymbol typeSymbol = copyEntry.getValue();
            if (isContainerTypeAndNotFixed(typeSymbol)) {
                iterator.remove();
                --nonFixedTypesCount;
                containerTypeSymbols.add((IContainerTypeSymbol) typeSymbol);
            } else if (isParametricTypeAndNotFixed(typeSymbol)) {
                iterator.remove();
                --nonFixedTypesCount;
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
        ITypeSymbol typeSymbol = typeSymbols.remove(absoluteName);
        unregisterAndDecreaseNonFixedCounter(typeSymbol);
    }

    protected void unregisterAndDecreaseNonFixedCounter(ITypeSymbol typeSymbol) {
        if (typeSymbol instanceof IPolymorphicTypeSymbol
                && !((IPolymorphicTypeSymbol) typeSymbol).isFixed()) {
            --nonFixedTypesCount;
            if (nonFixedTypesCount == 0) {
                notifyWasFixed();
            }
        }
        if (typeSymbol instanceof IObservableTypeSymbol) {
            ((IObservableTypeSymbol) typeSymbol).removeObservableListener(this);
        }
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

    /**
     * Returns true if all types in the container are final.
     */
    @Override
    public boolean isFinal() {
        boolean areFinal = true;
        for (ITypeSymbol typeSymbol : typeSymbols.values()) {
            if (!typeSymbol.isFinal()) {
                areFinal = false;
                break;
            }
        }
        return areFinal;
    }

    @Override
    public boolean addTypeSymbol(ITypeSymbol typeSymbol) {
        boolean hasChanged = false;

        String absoluteName = typeSymbol.getAbsoluteName();

        //no need to add it if it already exists in the container; ergo simplification = do not insert
        if (!typeSymbols.containsKey(absoluteName)) {
            hasChanged = addAndSimplify(absoluteName, typeSymbol);
        }

        if (hasChanged) {
            if (typeSymbol instanceof IPolymorphicTypeSymbol && !((IPolymorphicTypeSymbol) typeSymbol).isFixed()) {
                ++nonFixedTypesCount;
            }
            if (typeSymbol instanceof IObservableTypeSymbol) {
                ((IObservableTypeSymbol) typeSymbol).registerObservableListener(this);
            }
            hasAbsoluteNameChanged = true;
        }

        return hasChanged;
    }

    protected boolean addAndSimplify(String absoluteName, ITypeSymbol newTypeSymbol) {
        boolean hasChanged = false;

        EAdditionStatus status = CAN_BE_ADDED;
        Iterator<Map.Entry<String, ITypeSymbol>> iterator = typeSymbols.entrySet().iterator();
        while (iterator.hasNext()) {
            ITypeSymbol existingTypeSymbol = iterator.next().getValue();
            if (firstReplacesSecondType(newTypeSymbol, existingTypeSymbol)) {
                // new type is more specific for the container type; hence the existing type does no longer provide
                // useful information for this container type
                status = REPLACES_EXISTING;
                unregisterAndDecreaseNonFixedCounter(existingTypeSymbol);
                iterator.remove();
            } else if (status == CAN_BE_ADDED && secondReplacesFirstType(newTypeSymbol, existingTypeSymbol)) {
                status = DOES_NOT_ADD_NEW_INFORMATION;
                break;
            }
        }

        if (status != DOES_NOT_ADD_NEW_INFORMATION) {
            hasChanged = true;
            typeSymbols.put(absoluteName, newTypeSymbol);
        }

        //Warning! end code duplication - almost the same as in UnionTypeSymbol

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
        return nonFixedTypesCount == 0;
    }

    @Override
    public void nameOfObservableHasChanged(IObservableTypeSymbol type, String oldAbsoluteName) {
        typeSymbols.remove(oldAbsoluteName);
        typeSymbols.put(type.getAbsoluteName(), type);
        notifyNameHasChanged();
    }

    @Override
    public void observableWasFixed(IObservableTypeSymbol type) {
        String absoluteName = type.getAbsoluteName();
        if (typeSymbols.containsKey(absoluteName)) {
            // We trust the source/callee -- might be that the given type is not yet fixed or was already fixed.
            // In this case we will blame the source for being buggy.
            --nonFixedTypesCount;
            if (nonFixedTypesCount == 0) {
                notifyWasFixed();
            }
        }
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
