/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class VariableSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.TypeWithModifiersDto;

import java.util.ArrayList;
import java.util.List;

public class VariableSymbol extends ASymbolWithAccessModifier implements IVariableSymbol
{

    //Warning! start code duplication - same as in ATypeVariableSymbol
    private final List<IConstraint> constraints = new ArrayList<>();
    private boolean isByValue = true;
    //Warning! end code duplication - same as in ATypeVariableSymbol

    public VariableSymbol(ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        super(definitionAst, modifiers, name);
    }

//    @Override
//    public boolean isFinal() {
//        return modifiers.isFinal();
//    }

    @Override
    public boolean isStatic() {
        return modifiers.isStatic();
    }

    @Override
    public boolean isAlwaysCasting() {
        return modifiers.isAlwaysCasting();
    }

    @Override
    public boolean isFalseable() {
        return modifiers.isFalseable();
    }

    @Override
    public boolean isNullable() {
        return modifiers.isNullable();
    }

    @Override
    public TypeWithModifiersDto toTypeWithModifiersDto() {
        return new TypeWithModifiersDto(getType(), modifiers);
    }


    //Warning! start code duplication - same as in ATypeVariableSymbol
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
    //Warning! end code duplication - same as in ATypeVariableSymbol

    //Warning! start code duplication - same as in ATypeVariableSymbol
    @Override
    public void setIsByRef() {
        isByValue = false;
    }

    @Override
    public boolean isByValue() {
        return isByValue;
    }
    //Warning! end code duplication - same as in ATypeVariableSymbol
}
