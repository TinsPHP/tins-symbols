/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IMethodSymbol;
import ch.tsphp.tinsphp.symbols.MethodSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.gen.TokenTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class MethodSymbolReturnTypeModifierTest
{
    private String methodName;
    private int modifierType;

    public MethodSymbolReturnTypeModifierTest(String theMethodName, int theModifierType) {
        methodName = theMethodName;
        modifierType = theModifierType;
    }

    @Test
    public void is_ReturnsTrue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        IModifierSet set = createModifierSet();
        set.add(modifierType);

        IMethodSymbol methodSymbol = createMethodSymbol(set);
        boolean result = (boolean) methodSymbol.getClass().getMethod(methodName).invoke(methodSymbol);

        //the following three modifiers are return type modifiers and thus the expected result should be true
        //all other modifier are method modifiers the expected result will be false even though the return type modifier
        //set comprises those modifiers.
        boolean is = methodName.equals("isAlwaysCasting")
                || methodName.equals("isFalseable")
                || methodName.equals("isNullable");

        assertEquals(methodName + " failed.", is, result);
    }

    @Test
    public void isNot_ReturnsFalse() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        IModifierSet set = createModifierSet();

        IMethodSymbol methodSymbol = createMethodSymbol(set);
        boolean result = (boolean) methodSymbol.getClass().getMethod(methodName).invoke(methodSymbol);

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

    private IMethodSymbol createMethodSymbol(IModifierSet set) {
        return createMethodSymbol(
                mock(IScopeHelper.class),
                mock(ITSPHPAst.class),
                mock(IModifierSet.class),
                set,
                "foo",
                mock(IScope.class));
    }

    protected IMethodSymbol createMethodSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet methodModifiers,
            IModifierSet returnTypeModifiers,
            String name,
            IScope enclosingScope) {
        return new MethodSymbol(scopeHelper, definitionAst, methodModifiers, returnTypeModifiers, name, enclosingScope);
    }
}
