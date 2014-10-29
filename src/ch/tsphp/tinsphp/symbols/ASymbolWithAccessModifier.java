/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ASymbolWithAccessModifier from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.symbols.ISymbolWithAccessModifier;

public abstract class ASymbolWithAccessModifier extends ASymbolWithModifier implements ISymbolWithAccessModifier
{

    public ASymbolWithAccessModifier(ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        super(definitionAst, modifiers, name);
    }

    @Override
    public boolean isPublic() {
        return modifiers.isPublic();
    }

    @Override
    public boolean isProtected() {
        return modifiers.isProtected();
    }

    @Override
    public boolean isPrivate() {
        return modifiers.isPrivate();
    }
}
