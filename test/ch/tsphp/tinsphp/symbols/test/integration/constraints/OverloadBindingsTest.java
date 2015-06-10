/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;

public class OverloadBindingsTest extends ATypeHelperTest
{

    @Test
    public void getNextTypeVariable_FirstCall_ReturnsV1() {
        //no arrange necessary

        IOverloadBindings overloadBindings = createOverloadBindings();
        ITypeVariableReference result = overloadBindings.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("V1"));
    }

    @Test
    public void getNextTypeVariable_SecondCallAfterCopy_ReturnsV2() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.getNextTypeVariable();

        IOverloadBindings overloadBindings = createOverloadBindings(bindings1);
        ITypeVariableReference result = overloadBindings.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("V2"));
    }

    @Test
    public void addVariable_NotYetAdded_IsAdded() {
        //no arrange necessary

        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));

        assertThat(overloadBindings.containsVariable("$a"), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addVariable_AlreadyAdded_ThrowsIllegalArgumentException() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        overloadBindings.addVariable("$a", new TypeVariableReference("T1"));

        //act
        overloadBindings.addVariable("$a", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test
    public void getLowerRefBounds_NothingDefined_ReturnsNull() {
        //no arrange necessary

        IOverloadBindings overloadBindings = createOverloadBindings();
        Set<String> result = overloadBindings.getLowerRefBounds("T");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getLowerRefBounds_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        overloadBindings.addVariable("$a", new TypeVariableReference(t1));
        overloadBindings.addVariable("$b", new TypeVariableReference(t2));
        overloadBindings.addLowerRefBound(t1, new TypeVariableReference(t2));

        //act
        Set<String> result = overloadBindings.getLowerRefBounds(t1);

        assertThat(result, contains(t2));
    }

    @Test
    public void getUpperRefBounds_NothingDefined_ReturnsNull() {
        //no arrange necessary

        IOverloadBindings overloadBindings = createOverloadBindings();
        Set<String> result = overloadBindings.getUpperRefBounds("T");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getUpperRefBounds_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        overloadBindings.addVariable("$a", new TypeVariableReference(t1));
        overloadBindings.addVariable("$b", new TypeVariableReference(t2));
        overloadBindings.addLowerRefBound(t1, new TypeVariableReference(t2));

        //act
        Set<String> result = overloadBindings.getUpperRefBounds(t2);

        assertThat(result, contains(t1));
    }


    @Test(expected = IllegalArgumentException.class)
    public void fixType_VariableNotDefined_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.fixType("$a");

        //assert in annotation
    }

    @Test
    public void fixType_NotFixed_IsFixedAfterwards() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        overloadBindings.addVariable("$a", new TypeVariableReference(t1));

        //act
        overloadBindings.fixType("$a");
        boolean result = overloadBindings.getTypeVariableReference("$a").hasFixedType();

        assertThat(result, is(true));
    }

    @Test
    public void fixType_AlreadyFixed_DoesNotWrapItAgain() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        ITypeVariableReference constraint = new FixedTypeVariableReference(new TypeVariableReference(t1));
        overloadBindings.addVariable("$a", constraint);

        //act
        overloadBindings.fixType("$a");
        ITypeVariableReference result = overloadBindings.getTypeVariableReference("$a");

        assertThat(result, is(constraint));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAppliedOverload_NonExistingVariable_ThrowsIllegalArgumentException() {
        //no arrange necessary


        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.setAppliedOverload("$nonExistingVariable", mock(IFunctionType.class));

        //assert in annotation
    }

    @Test
    public void setAndGetAppliedOverload_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        overloadBindings.addVariable("$a", new TypeVariableReference("T"));
        IFunctionType overload = mock(IFunctionType.class);

        //act
        overloadBindings.setAppliedOverload("$a", overload);
        IFunctionType result = overloadBindings.getAppliedOverload("$a");

        assertThat(result, is(overload));
    }

    private IOverloadBindings createOverloadBindings() {
        return createOverloadBindings(symbolFactory, typeHelper);
    }

    protected IOverloadBindings createOverloadBindings(
            ISymbolFactory symbolFactory, ITypeHelper typeHelper) {
        return new OverloadBindings(symbolFactory, typeHelper);
    }

    protected IOverloadBindings createOverloadBindings(OverloadBindings bindings) {
        return new OverloadBindings(bindings);
    }
}
