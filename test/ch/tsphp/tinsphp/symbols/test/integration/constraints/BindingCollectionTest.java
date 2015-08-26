/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.OverloadApplicationDto;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.constraints.BindingCollection;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;

public class BindingCollectionTest extends ATypeHelperTest
{

    @Test
    public void getNextTypeVariable_FirstCall_ReturnsV1() {
        //no arrange necessary

        IBindingCollection bindingCollection = createBindingCollection();
        ITypeVariableReference result = bindingCollection.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("V1"));
    }

    @Test
    public void getNextTypeVariable_SecondCallAfterCopy_ReturnsV2() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.getNextTypeVariable();

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        ITypeVariableReference result = bindingCollection.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("V2"));
    }

    @Test
    public void addVariable_NotYetAdded_IsAdded() {
        //no arrange necessary

        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));

        assertThat(bindingCollection.containsVariable("$a"), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addVariable_AlreadyAdded_ThrowsIllegalArgumentException() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        bindingCollection.addVariable("$a", new TypeVariableReference("T1"));

        //act
        bindingCollection.addVariable("$a", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test
    public void getLowerRefBounds_NothingDefined_ReturnsNull() {
        //no arrange necessary

        IBindingCollection bindingCollection = createBindingCollection();
        Set<String> result = bindingCollection.getLowerRefBounds("T");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getLowerRefBounds_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$a", new TypeVariableReference(t1));
        bindingCollection.addVariable("$b", new TypeVariableReference(t2));
        bindingCollection.addLowerRefBound(t1, new TypeVariableReference(t2));

        //act
        Set<String> result = bindingCollection.getLowerRefBounds(t1);

        assertThat(result, contains(t2));
    }

    @Test
    public void getUpperRefBounds_NothingDefined_ReturnsNull() {
        //no arrange necessary

        IBindingCollection bindingCollection = createBindingCollection();
        Set<String> result = bindingCollection.getUpperRefBounds("T");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getUpperRefBounds_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$a", new TypeVariableReference(t1));
        bindingCollection.addVariable("$b", new TypeVariableReference(t2));
        bindingCollection.addLowerRefBound(t1, new TypeVariableReference(t2));

        //act
        Set<String> result = bindingCollection.getUpperRefBounds(t2);

        assertThat(result, contains(t1));
    }


    @Test(expected = IllegalArgumentException.class)
    public void fixType_VariableNotDefined_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.fixType("$a");

        //assert in annotation
    }

    @Test
    public void fixType_NotFixed_IsFixedAfterwards() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        bindingCollection.addVariable("$a", new TypeVariableReference(t1));

        //act
        bindingCollection.fixType("$a");
        boolean result = bindingCollection.getTypeVariableReference("$a").hasFixedType();

        assertThat(result, is(true));
    }

    @Test
    public void fixType_AlreadyFixed_DoesNotWrapItAgain() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        ITypeVariableReference constraint = new FixedTypeVariableReference(new TypeVariableReference(t1));
        bindingCollection.addVariable("$a", constraint);

        //act
        bindingCollection.fixType("$a");
        ITypeVariableReference result = bindingCollection.getTypeVariableReference("$a");

        assertThat(result, is(constraint));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAppliedOverload_NonExistingVariable_ThrowsIllegalArgumentException() {
        OverloadApplicationDto dto = new OverloadApplicationDto(mock(IFunctionType.class), null, null);

        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.setAppliedOverload("$nonExistingVariable", dto);

        //assert in annotation
    }

    @Test
    public void setAndGetAppliedOverload_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        OverloadApplicationDto dto = new OverloadApplicationDto(mock(IFunctionType.class), null, null);

        //act
        bindingCollection.setAppliedOverload("$a", dto);
        OverloadApplicationDto result = bindingCollection.getAppliedOverload("$a");

        assertThat(result, is(dto));
    }

    @Test
    public void fixTypeParameter_LowerInt_LowerNullAndUpperTypeBoundIsInt() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound("T", intType);

        //act
        bindingCollection.fixTypeParameter("T");

        assertThat(bindingCollection.getLowerTypeBounds("T"), is(nullValue()));
        assertThat(bindingCollection.getUpperTypeBounds("T").getAbsoluteName(), is("int"));
    }

    @Test
    public void fixTypeParameter_UpperInt_LowerNullAndUpperTypeBoundIsInt() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound("T", intType);

        //act
        bindingCollection.fixTypeParameter("T");

        assertThat(bindingCollection.getLowerTypeBounds("T"), is(nullValue()));
        assertThat(bindingCollection.getUpperTypeBounds("T").getAbsoluteName(), is("int"));
    }

    @Test
    public void fixTypeParameter_NoType_LowerNullAndUpperTypeBoundIsMixed() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));

        //act
        bindingCollection.fixTypeParameter("T");

        assertThat(bindingCollection.getLowerTypeBounds("T"), is(nullValue()));
        assertThat(bindingCollection.getUpperTypeBounds("T").getAbsoluteName(), is("mixed"));
    }

    @Test
    public void fixType_LowerInt_LowerIntAndUpperTypeBoundIsNull() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addLowerTypeBound("T", intType);

        //act
        bindingCollection.fixType("$a");


        assertThat(bindingCollection.getLowerTypeBounds("T").getAbsoluteName(), is("int"));
        assertThat(bindingCollection.getUpperTypeBounds("T"), is(Matchers.nullValue()));
    }

    @Test
    public void fixType_UpperInt_LowerIntAndUpperTypeBoundIsNull() {
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        bindingCollection.addVariable("$a", new TypeVariableReference("T"));
        bindingCollection.addUpperTypeBound("T", intType);

        //act
        bindingCollection.fixType("$a");


        assertThat(bindingCollection.getLowerTypeBounds("T").getAbsoluteName(), is("int"));
        assertThat(bindingCollection.getUpperTypeBounds("T"), is(Matchers.nullValue()));
    }

    private IBindingCollection createBindingCollection() {
        return createBindingCollection(symbolFactory, typeHelper);
    }

    protected IBindingCollection createBindingCollection(
            ISymbolFactory symbolFactory, ITypeHelper typeHelper) {
        return new BindingCollection(symbolFactory, typeHelper);
    }

    protected IBindingCollection createBindingCollection(BindingCollection bindings) {
        return new BindingCollection(bindings);
    }
}
