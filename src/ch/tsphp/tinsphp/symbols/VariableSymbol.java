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
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbolWithRef;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.TypeWithModifiersDto;

import java.util.Stack;

public class VariableSymbol extends ASymbolWithAccessModifier implements IVariableSymbol
{

    //Warning! start code duplication - same as in MinimalTypeVariableSymbolWithRef
    private ITypeVariableSymbolWithRef definition;
    private final Stack<ITypeVariableSymbol> referenceTypeVariables = new Stack<>();
    //Warning! end code duplication - same as in MinimalTypeVariableSymbolWithRef

    private boolean hasFixedType = false;

    public VariableSymbol(ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        super(definitionAst, modifiers, name);
    }

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

    //Warning! start code duplication - same as in MinimalTypeVariableSymbolWithRef
    @Override
    public void setOriginal(ITypeVariableSymbolWithRef theVariableDeclaration) {
        definition = theVariableDeclaration;
    }

    @Override
    public void addRefVariable(ITypeVariableSymbol variableSymbol) {
        referenceTypeVariables.push(variableSymbol);
    }

    @Override
    public ITypeVariableSymbol getCurrentTypeVariable() {
        if (referenceTypeVariables.size() > 0) {
            return referenceTypeVariables.peek();
        }
        return this;
    }
    //Warning! end code duplication - same as in MinimalTypeVariableSymbolWithRef


    @Override
    public void setHasFixedType() {
        hasFixedType = true;
    }

    @Override
    public boolean hasFixedType() {
        return hasFixedType;
    }

    @Override
    public String getTypeVariable() {
        return null;
    }
}
