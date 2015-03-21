/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AFunctionTypeSymbol extends ATypeSymbol implements IFunctionTypeSymbol
{
    private final List<List<IConstraint>> parameterConstraints = new ArrayList<>();
    private final Map<String, Integer> nameToIndexMap = new HashMap<>();

    public AFunctionTypeSymbol(String theName, List<String> theParameterIds, ITypeSymbol theParentTypeSymbol) {
        super(null, theName, theParentTypeSymbol);

        int size = theParameterIds != null ? theParameterIds.size() : 0;
        for (int i = 0; i < size; ++i) {
            nameToIndexMap.put(theParameterIds.get(i), i);
            parameterConstraints.add(new ArrayList<IConstraint>());
        }
    }

    @Override
    public void addParameterConstraint(String parameterId, IConstraint constraint) {
        if (!nameToIndexMap.containsKey(parameterId)) {
            throw new IllegalArgumentException("parameterId " + parameterId + " not found for this function.");
        }

        int index = nameToIndexMap.get(parameterId);
        parameterConstraints.get(index).add(constraint);
    }

    @Override
    public List<List<IConstraint>> getParametersConstraints() {
        return parameterConstraints;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException("functions do not have default values");
    }

}
