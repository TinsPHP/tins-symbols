/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbolWithRef;

import java.util.Stack;

public class MinimalTypeVariableSymbolWithRef extends ATypeVariableSymbol implements ITypeVariableSymbolWithRef
{
    //Warning! start code duplication - same as in VariableSymbol
    private ITypeVariableSymbolWithRef definition;
    private final Stack<ITypeVariableSymbol> referenceTypeVariables = new Stack<>();
    //Warning! end code duplication - same as in VariableSymbol

    public MinimalTypeVariableSymbolWithRef(String theName) {
        super(null, theName);
    }

    //Warning! start code duplication - same as in VariableSymbol
    @Override
    public void setOriginal(ITypeVariableSymbolWithRef theVariableDeclaration) {
        definition = theVariableDeclaration;
    }

    @Override
    public void addRefVariable(ITypeVariableSymbol variableSymbol) {
//        if (getType().isReadyForEval()) {
//            throw new IllegalStateException("cannot add further references if the type is already sealed");
//        }
        referenceTypeVariables.push(variableSymbol);
    }

    @Override
    public ITypeVariableSymbol getCurrentTypeVariable() {
        if (referenceTypeVariables.size() > 0) {
            return referenceTypeVariables.peek();
        }
        return this;
    }
    //Warning! end code duplication - same as in VariableSymbol
}
