/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.VariableSymbol;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class VariableSymbolModifierTest
{
    private String methodName;
    private int modifierType;

    public VariableSymbolModifierTest(String theMethodName, int theModifierType) {
        methodName = theMethodName;
        modifierType = theModifierType;
    }

    @Test
    public void is_ReturnsTrue() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        IModifierSet set = createModifierSet();
        set.add(modifierType);

        IVariableSymbol variableSymbol = createVariableSymbol(set);
        boolean result = (boolean) variableSymbol.getClass().getMethod(methodName).invoke(variableSymbol);

        assertTrue(methodName + " failed.", result);
    }

    @Test
    public void isNot_ReturnsFalse() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        IModifierSet set = createModifierSet();

        IVariableSymbol variableSymbol = createVariableSymbol(set);
        boolean result = (boolean) variableSymbol.getClass().getMethod(methodName).invoke(variableSymbol);

        assertFalse(methodName + " failed.", result);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                //not yet supported by PHP but constants are treated as final VariableSymbols as well
//                {"isFinal", TokenTypes.Final},
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

    private IVariableSymbol createVariableSymbol(IModifierSet set) {
        return createVariableSymbol(mock(ITSPHPAst.class), set, "foo");
    }

    protected IVariableSymbol createVariableSymbol(ITSPHPAst definitionAst, IModifierSet modifiers, String name) {
        return new VariableSymbol(definitionAst, modifiers, name);
    }
}
