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
    protected final Map<String, Integer> parameterNamesToIndexMap = new HashMap<>();

    private final List<List<IConstraint>> inputConstraints = new ArrayList<>();
    private final List<List<IConstraint>> outputConstraints = new ArrayList<>();

    public AFunctionTypeSymbol(String theName, List<String> theParameterIds, ITypeSymbol theParentTypeSymbol) {
        super(null, theName, theParentTypeSymbol);

        int size = theParameterIds != null ? theParameterIds.size() : 0;
        for (int i = 0; i < size; ++i) {
            parameterNamesToIndexMap.put(theParameterIds.get(i), i);
            inputConstraints.add(new ArrayList<IConstraint>());
            outputConstraints.add(new ArrayList<IConstraint>());
        }
    }

    @Override
    public void addInputConstraint(String parameterId, IConstraint constraint) {
        addConstraint(parameterId, constraint, inputConstraints);
    }

    private void addConstraint(String parameterId, IConstraint constraint, List<List<IConstraint>> constraints) {
        if (!parameterNamesToIndexMap.containsKey(parameterId)) {
            throw new IllegalArgumentException("parameterId " + parameterId + " not found for this function.");
        }

        int index = parameterNamesToIndexMap.get(parameterId);
        constraints.get(index).add(constraint);
    }

    @Override
    public void addOutputConstraint(String parameterId, IConstraint constraint) {
        addConstraint(parameterId, constraint, outputConstraints);
    }

    @Override
    public List<List<IConstraint>> getInputConstraints() {
        return inputConstraints;
    }

    @Override
    public List<List<IConstraint>> getOutputConstraints() {
        return outputConstraints;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException("functions do not have default values");
    }

}
