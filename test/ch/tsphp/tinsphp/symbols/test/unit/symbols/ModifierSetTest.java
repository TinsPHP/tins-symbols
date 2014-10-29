/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ModifierSetTest
{
    private String methodName;
    private int modifierType;

    public ModifierSetTest(String theMethodName, int theModifierType) {
        methodName = theMethodName;
        modifierType = theModifierType;
    }

    @Test
    public void is_ReturnsTrue() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        //no arrange necessary

        IModifierSet set = createModifierSet();
        set.add(modifierType);
        boolean result = (boolean) set.getClass().getMethod(methodName).invoke(set);

        assertTrue(methodName + " failed.", result);
    }

    @Test
    public void isNot_ReturnsFalse() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        //no arrange necessary

        IModifierSet set = createModifierSet();
        boolean result = (boolean) set.getClass().getMethod(methodName).invoke(set);

        assertFalse(methodName + " failed.", result);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"isAbstract", TokenTypes.Abstract},
                {"isFinal", TokenTypes.Final},
                {"isStatic", TokenTypes.Static},
                {"isPublic", TokenTypes.Public},
                {"isProtected", TokenTypes.Protected},
                {"isPrivate", TokenTypes.Private},
                {"isAlwaysCasting", TokenTypes.Cast},
                {"isFalseable", TokenTypes.LogicNot},
                {"isNullable", TokenTypes.QuestionMark},
        });
    }

    protected IModifierSet createModifierSet() {
        return new ModifierSet();
    }
}
