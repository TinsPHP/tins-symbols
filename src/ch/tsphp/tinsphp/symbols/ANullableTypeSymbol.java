/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ANullableTypeSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */


package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;

import java.util.Set;

/**
 * Represents a type which can hold the value null.
 */
public abstract class ANullableTypeSymbol extends ATypeSymbol
{

    public ANullableTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(null, name, parentTypeSymbol);
        //make sure nullable is part of the modifiers
        TypeHelper.addNullableModifier(this);
    }

    public ANullableTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbols) {
        super(null, name, parentTypeSymbols);
        //make sure nullable is part of the modifiers
        TypeHelper.addNullableModifier(this);
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TokenTypes.Null, "null");
    }

    @Override
    public void setModifiers(IModifierSet newModifiers) {
        super.setModifiers(newModifiers);
        //make sure nullable is part of the modifiers
        TypeHelper.addNullableModifier(this);
    }

    @Override
    public boolean removeModifier(Integer modifier) {
        if (modifier != TokenTypes.QuestionMark) {
            return super.removeModifier(modifier);
        }
        //it's not allowed to remove the nullable modifier
        return false;
    }

}
