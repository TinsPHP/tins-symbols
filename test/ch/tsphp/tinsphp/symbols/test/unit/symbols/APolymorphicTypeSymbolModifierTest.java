/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.gen.TokenTypes;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IRecordTypeSymbol;
import ch.tsphp.tinsphp.symbols.ARecordTypeSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class APolymorphicTypeSymbolModifierTest
{
    class DummyRecordTypeSymbol extends ARecordTypeSymbol
    {

        public DummyRecordTypeSymbol(IScopeHelper scopeHelper, ITSPHPAst definitionAst, IModifierSet modifiers,
                String name, IScope enclosingScope, ITypeSymbol theParentTypeSymbol) {
            super(scopeHelper, definitionAst, modifiers, name, enclosingScope, theParentTypeSymbol);
        }

        @Override
        public boolean canBeUsedInIntersection() {
            return false;
        }
    }

    private String methodName;
    private int modifierType;

    public APolymorphicTypeSymbolModifierTest(String theMethodName, int theModifierType) {
        methodName = theMethodName;
        modifierType = theModifierType;
    }

    @Test
    public void is_ReturnsTrue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        IModifierSet set = createModifierSet();
        set.add(modifierType);

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(set);
        boolean result = (boolean) typeSymbol.getClass().getMethod(methodName).invoke(typeSymbol);

        assertTrue(methodName + " failed.", result);
    }

    @Test
    public void isNot_ReturnsFalse() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        IModifierSet set = createModifierSet();

        IRecordTypeSymbol typeSymbol = createPolymorphicTypeSymbol(set);
        boolean result = (boolean) typeSymbol.getClass().getMethod(methodName).invoke(typeSymbol);

        assertFalse(methodName + " failed.", result);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"isAbstract", TokenTypes.Abstract},
                {"isFalseable", TokenTypes.LogicNot},
                {"isNullable", TokenTypes.QuestionMark},
        });
    }

    protected IModifierSet createModifierSet() {
        return new ModifierSet();
    }

    private IRecordTypeSymbol createPolymorphicTypeSymbol(IModifierSet set) {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getName()).thenReturn("dummy");
        return createMethodSymbol(
                mock(IScopeHelper.class),
                mock(ITSPHPAst.class),
                set,
                "foo",
                mock(IScope.class),
                typeSymbol
        );
    }

    protected IRecordTypeSymbol createMethodSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet modifiers,
            String name,
            IScope enclosingScope,
            ITypeSymbol parentTypeSymbol) {
        return new DummyRecordTypeSymbol(
                scopeHelper, definitionAst, modifiers, name, enclosingScope, parentTypeSymbol);
    }

}
