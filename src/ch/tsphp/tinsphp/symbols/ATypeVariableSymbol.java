/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;

import java.util.ArrayList;
import java.util.List;

public abstract class ATypeVariableSymbol extends ASymbol implements ITypeVariableSymbol
{
    //Warning! start code duplication - same as in VariableSymbol
    private final List<IConstraint> constraints = new ArrayList<>();

    protected ATypeVariableSymbol(ITSPHPAst theDefinitionAst, String theName) {
        super(theDefinitionAst, theName);
    }
    //Warning! end code duplication - same as in VariableSymbol

    //Warning! start code duplication - same as in VariableSymbol
    @Override
    public IUnionTypeSymbol getType() {
        return (IUnionTypeSymbol) super.getType();
    }

    @Override
    public void addConstraint(IConstraint constraint) {
        constraints.add(constraint);
    }

    @Override
    public List<IConstraint> getConstraints() {
        return constraints;
    }
    //Warning! end code duplication - same as in VariableSymbol
}
