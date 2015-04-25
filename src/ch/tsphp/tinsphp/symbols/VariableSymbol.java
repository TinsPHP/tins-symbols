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
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbolWithRef;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;

import java.util.Stack;

public class VariableSymbol extends ASymbolWithAccessModifier implements IVariableSymbol
{

    private IMinimalVariableSymbolWithRef definition;
    private final Stack<IMinimalVariableSymbol> referenceTypeVariables = new Stack<>();

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
    public void setOriginal(IMinimalVariableSymbolWithRef theVariableDeclaration) {
        definition = theVariableDeclaration;
    }

    @Override
    public void addRefVariable(IMinimalVariableSymbol variableSymbol) {
        referenceTypeVariables.push(variableSymbol);
    }

    @Override
    public IMinimalVariableSymbol getCurrentTypeVariable() {
        if (referenceTypeVariables.size() > 0) {
            return referenceTypeVariables.peek();
        }
        return this;
    }
}
