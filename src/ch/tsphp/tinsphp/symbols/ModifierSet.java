/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ModifierSet from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols;


import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.gen.TokenTypes;

import java.util.Collection;
import java.util.HashSet;

public class ModifierSet extends HashSet<Integer> implements IModifierSet
{
    static final long serialVersionUID = 1L;

    public ModifierSet() {
        //empty modifier set
    }

    public ModifierSet(Collection<Integer> collection) {
        super(collection);
    }

    @Override
    public boolean isAbstract() {
        return contains(TokenTypes.Abstract);
    }

    @Override
    public boolean isFinal() {
        return contains(TokenTypes.Final);
    }

    public boolean isStatic() {
        return contains(TokenTypes.Static);
    }

    @Override
    public boolean isPublic() {
        return contains(TokenTypes.Public);
    }

    @Override
    public boolean isProtected() {
        return contains(TokenTypes.Protected);
    }

    @Override
    public boolean isPrivate() {
        return contains(TokenTypes.Private);
    }

    public boolean isAlwaysCasting() {
        return contains(TokenTypes.Cast);
    }

    public boolean isFalseable() {
        return contains(TokenTypes.LogicNot);
    }

    public boolean isNullable() {
        return contains(TokenTypes.QuestionMark);
    }
}
