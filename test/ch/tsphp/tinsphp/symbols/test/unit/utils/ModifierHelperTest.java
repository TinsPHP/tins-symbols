/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.utils;

import ch.tsphp.common.symbols.ISymbolWithModifier;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.utils.ModifierHelper;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class ModifierHelperTest
{
    @Test
    public void addNullableModifier_Standard_IsNullableAfterwards() {
        ISymbolWithModifier symbol = mock(ISymbolWithModifier.class);
        final IModifierSet modifierSet = new ModifierSet();
        doAnswer(new Answer<Object>()
                 {
                     @Override
                     public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                         modifierSet.add((Integer) invocationOnMock.getArguments()[0]);
                         return null;
                     }
                 }
        ).when(symbol).addModifier(anyInt());

        ModifierHelper.addNullableModifier(symbol);

        assertThat(modifierSet.isNullable(), is(true));
    }

    @Test
    public void addFalseableModifier_Standard_IsFalseableAfterwards() {
        ISymbolWithModifier symbol = mock(ISymbolWithModifier.class);
        final IModifierSet modifierSet = new ModifierSet();
        doAnswer(new Answer<Object>()
                 {
                     @Override
                     public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                         modifierSet.add((Integer) invocationOnMock.getArguments()[0]);
                         return null;
                     }
                 }
        ).when(symbol).addModifier(anyInt());

        ModifierHelper.addFalseableModifier(symbol);

        assertThat(modifierSet.isFalseable(), is(true));
    }

    @Test
    public void addAlwaysCastingModifier_Standard_IsAlwaysCastingAfterwards() {
        ISymbolWithModifier symbol = mock(ISymbolWithModifier.class);
        final IModifierSet modifierSet = new ModifierSet();
        doAnswer(new Answer<Object>()
                 {
                     @Override
                     public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                         modifierSet.add((Integer) invocationOnMock.getArguments()[0]);
                         return null;
                     }
                 }
        ).when(symbol).addModifier(anyInt());

        ModifierHelper.addAlwaysCastingModifier(symbol);

        assertThat(modifierSet.isAlwaysCasting(), is(true));
    }
}
