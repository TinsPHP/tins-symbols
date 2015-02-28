/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.symbols.IForEvalReadyListener;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.ALazyTypeSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ALazyTypeSymbolTest
{
    class DummyALazyTypeSymbol extends ALazyTypeSymbol
    {
        @Override
        public ITypeSymbol evalSelf() {
            return null;
        }

        public void callNotifyForEvalReadyListeners() {
            this.notifyForEvalReadyListeners();
        }
    }

    @Test
    public void isReadyForEval_NothingDone_ReturnsFalse() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        boolean result = typeSymbol.isReadyForEval();

        assertThat(result, is(false));
    }

    @Test
    public void isReadyForEval_NothingDoneCallingTwice_ReturnsFalseBothTimes() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        boolean result1 = typeSymbol.isReadyForEval();
        boolean result2 = typeSymbol.isReadyForEval();

        assertThat(result1, is(false));
        assertThat(result2, is(false));
    }

    @Test
    public void isReadyForEval_AfterNotifyForEvalReadyListenersWasCalled_ReturnsTrue() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        callNotifyForEvalReadyListeners(typeSymbol);
        boolean result = typeSymbol.isReadyForEval();

        assertThat(result, is(true));
    }

    @Test
    public void isReadyForEval_AfterNotifyForEvalReadyListenersWasCalledCheckingTwice_ReturnsTrueBothTimes() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        callNotifyForEvalReadyListeners(typeSymbol);
        boolean result1 = typeSymbol.isReadyForEval();
        boolean result2 = typeSymbol.isReadyForEval();

        assertThat(result1, is(true));
        assertThat(result2, is(true));
    }

    @Test
    public void addForEvalReadyListener_OneAdded_InformedWhenNotifyForEvalReadyIsCalled() {
        IForEvalReadyListener listener = mock(IForEvalReadyListener.class);

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.addForEvalReadyListener(listener);
        callNotifyForEvalReadyListeners(typeSymbol);

        verify(listener).notifyReadyForEval();
    }

    @Test
    public void addForEvalReadyListener_TwoAdded_BothInformedWhenNotifyForEvalReadyIsCalled() {
        IForEvalReadyListener listener1 = mock(IForEvalReadyListener.class);
        IForEvalReadyListener listener2 = mock(IForEvalReadyListener.class);

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.addForEvalReadyListener(listener1);
        typeSymbol.addForEvalReadyListener(listener2);
        callNotifyForEvalReadyListeners(typeSymbol);

        verify(listener1).notifyReadyForEval();
        verify(listener2).notifyReadyForEval();
    }

    @Test
    public void addForEvalReadyListener_OneAddedAfterNotifyForEvalReadyWasCalled_StillInformed() {
        IForEvalReadyListener listener = mock(IForEvalReadyListener.class);

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        callNotifyForEvalReadyListeners(typeSymbol);
        typeSymbol.addForEvalReadyListener(listener);

        verify(listener).notifyReadyForEval();
    }

    @Test(expected = IllegalStateException.class)
    public void notifyForEvalReadyListeners_CallItTwice_ThrowsIllegalStateException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        callNotifyForEvalReadyListeners(typeSymbol);
        callNotifyForEvalReadyListeners(typeSymbol);

        //assert in annotation
    }

    @Test
    public void getName_Standard_ReturnsQuestionMark() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        String result = typeSymbol.getName();

        assertThat(result, CoreMatchers.is("?"));
    }

    @Test
    public void getAbsoluteName_Standard_ReturnsQuestionMark() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        String result = typeSymbol.getAbsoluteName();

        assertThat(result, CoreMatchers.is("?"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getParentTypeSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.getParentTypeSymbols();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefaultValue_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.getDefaultValue();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.addModifier(123);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.removeModifier(123);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.getModifiers();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.setModifiers(new ModifierSet());

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefinitionAst_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.getDefinitionAst();

        //assert in annotation
    }


    @Test(expected = UnsupportedOperationException.class)
    public void getDefinitionScope_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.getDefinitionScope();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setDefinitionScope_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.setDefinitionScope(mock(IScope.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.getType();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setType_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.setType(mock(ITypeSymbol.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isFalseable_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.isFalseable();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isNullable_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        ALazyTypeSymbol typeSymbol = createLazyTypeSymbol();
        typeSymbol.isNullable();

        //assert in annotation
    }

    protected ALazyTypeSymbol createLazyTypeSymbol() {
        return new DummyALazyTypeSymbol();
    }

    protected void callNotifyForEvalReadyListeners(ALazyTypeSymbol typeSymbol) {
        ((DummyALazyTypeSymbol) typeSymbol).callNotifyForEvalReadyListeners();
    }
}
