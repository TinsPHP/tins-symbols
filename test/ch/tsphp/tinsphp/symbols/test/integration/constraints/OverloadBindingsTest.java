/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.ITypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Set;

import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OverloadBindingsTest extends ATypeTest
{
    private static ISymbolFactory symbolFactory;
    private static ITypeHelper typeHelper;

    @BeforeClass
    public static void init() {
        ATypeTest.init();

        typeHelper = new TypeHelper();
        symbolFactory = mock(ISymbolFactory.class);
        ITypeSymbol mixedTypeSymbol = mock(ITypeSymbol.class);
        when(mixedTypeSymbol.getAbsoluteName()).thenReturn("mixed");

        when(symbolFactory.getMixedTypeSymbol()).thenReturn(mixedTypeSymbol);
        when(symbolFactory.createUnionTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new UnionTypeSymbol(typeHelper);
            }
        });
        when(symbolFactory.createIntersectionTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new IntersectionTypeSymbol(typeHelper);
            }
        });
    }

    @Test
    public void copyConstructor_HasTwoVariables_CopyBoth() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));

        IOverloadBindings collection = createOverloadBindings(bindings1);

        assertThat(collection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(collection.getTypeVariableReference("$a").hasFixedType(), is(false));
        assertThat(collection.getTypeVariableReference("$b").hasFixedType(), is(false));
    }

    @Test
    public void copyConstructor_HasTwoVariablesFirstIsFixed_OnlyFirstIsFixed() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new FixedTypeVariableReference(new TypeVariableReference("T")));
        bindings1.addVariable("$b", new TypeVariableReference("T"));

        IOverloadBindings collection = createOverloadBindings(bindings1);

        assertThat(collection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(collection.getTypeVariableReference("$a").hasFixedType(), is(true));
        assertThat(collection.getTypeVariableReference("$b").hasFixedType(), is(false));
    }

    @Test
    public void copyConstructor_HasTwoVariablesSecondIsFixed_OnlySecondIsFixed() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T"));
        bindings1.addVariable("$b", new FixedTypeVariableReference(new TypeVariableReference("T")));

        IOverloadBindings collection = createOverloadBindings(bindings1);

        assertThat(collection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(collection.getTypeVariableReference("$a").hasFixedType(), is(false));
        assertThat(collection.getTypeVariableReference("$b").hasFixedType(), is(true));
    }

    @Test
    public void copyConstructor_HasLowerTypeBound_IsCopied() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T"));
        bindings1.addLowerTypeBound("T", intType);

        IOverloadBindings collection = createOverloadBindings(bindings1);
        bindings1.addLowerTypeBound("T", floatType);

        assertThat(collection, withVariableBindings(varBinding("$a", "T", asList("int"), null, false)));
    }

    @Test
    public void copyConstructor_HasUpperTypeBound_IsCopied() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T"));
        bindings1.addUpperTypeBound("T", interfaceAType);

        IOverloadBindings collection = createOverloadBindings(bindings1);
        bindings1.addUpperTypeBound("T", interfaceBType);
        bindings1.addLowerTypeBound("T", fooType);

        assertThat(collection, withVariableBindings(varBinding("$a", "T", null, asList("IA"), false)));
    }

    @Test
    public void copyConstructor_HasLowerRefBound_IsCopied() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        bindings1.addLowerRefBound("T1", new TypeVariableReference("T2"));

        IOverloadBindings collection = createOverloadBindings(bindings1);
        bindings1.addLowerRefBound("T2", new TypeVariableReference("T1"));

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", asList("@T2"), null, false),
                varBinding("$b", "T2", null, asList("@T1"), false)
        ));
    }

    @Test
    public void copyConstructor_HasVariablesAndRenameOneAfterCopying_DoesOnlyRenameOldBinding() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        bindings1.addVariable("$c", new TypeVariableReference("T3"));


        IOverloadBindings collection = createOverloadBindings(bindings1);
        bindings1.renameTypeVariable("T1", "T3");
        collection.renameTypeVariable("T3", "T2");

        assertThat(collection, withVariableBindings(
                varBinding("$a", "T1", null, null, false),
                varBinding("$b", "T2", null, null, false),
                varBinding("$c", "T2", null, null, false)

        ));
    }

    @Test
    public void copyConstructor_HasAppliedBinding_IsCopied() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        IFunctionType overload = mock(IFunctionType.class);
        bindings1.setAppliedOverload("$a", overload);

        IOverloadBindings collection = createOverloadBindings(bindings1);
        bindings1.addVariable("$b", new TypeVariableReference("T1"));
        bindings1.setAppliedOverload("$b", mock(IFunctionType.class));

        assertThat(collection.getAppliedOverload("$a"), is(overload));
        assertThat(collection.getAppliedOverload("$b"), is(nullValue()));
    }

    @Test
    public void getNextTypeVariable_FirstCall_ReturnsT1() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        TypeVariableReference result = collection.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("T1"));
    }

    @Test
    public void getNextTypeVariable_SecondCallAfterCopy_ReturnsT2() {
        OverloadBindings bindings1 = new OverloadBindings(symbolFactory, typeHelper);
        bindings1.getNextTypeVariable();

        IOverloadBindings collection = createOverloadBindings(bindings1);
        TypeVariableReference result = collection.getNextTypeVariable();

        assertThat(result.getTypeVariable(), is("T2"));
    }

    @Test
    public void addVariable_NotYetAdded_IsAdded() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        collection.addVariable("$a", new TypeVariableReference("T"));

        assertThat(collection.containsVariable("$a"), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addVariable_AlreadyAdded_ThrowsIllegalArgumentException() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        collection.addVariable("$a", new TypeVariableReference("T1"));

        //act
        collection.addVariable("$a", new TypeVariableReference("T2"));

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameTypeVariable_UnknownTypeVariable_ThrowsIllegalArgumentException() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        collection.addVariable("$lhs", new TypeVariableReference(lhs));

        //act
        collection.renameTypeVariable("T", lhs);

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameTypeVariable_UnknownNewName_ThrowsIllegalArgumentException() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        collection.addVariable("$lhs", new TypeVariableReference(lhs));

        //act
        collection.renameTypeVariable(lhs, "T");

        //assert in annotation
    }

    @Test
    public void renameTypeVariable_IsSelfReference_DoesNotCallSetTypeVariableOnConstraint() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        TypeVariableReference constraint = spy(new TypeVariableReference(lhs));
        collection.addVariable("$lhs", constraint);

        //act
        collection.renameTypeVariable(lhs, lhs);

        try {
            verify(constraint).setTypeVariable(anyString());
            Assert.fail("should not rename a self reference");
        } catch (MockitoAssertionError ex) {
            //should be thrown
        }
    }

    @Test
    public void renameTypeVariable_HasTypeBounds_TransferTypeBounds() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addLowerTypeBound(lhs, intType);
        collection.addUpperTypeBound(lhs, numType);

        //act
        collection.renameTypeVariable(lhs, rhs);

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", rhs, asList("int"), asList("num"), false),
                varBinding("$rhs", rhs, asList("int"), asList("num"), false)
        ));
    }

    //see TINS-466 rename type variable does not promote type bounds
    @Test
    public void renameTypeVariable_HasTypeBoundsAndOtherHasUpperRef_TransferTypeBoundsAndPropagate() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        String upperRhs = "Tupper";
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        collection.addVariable("$upper", new TypeVariableReference(upperRhs));
        collection.addLowerRefBound(upperRhs, new TypeVariableReference(rhs));
        collection.addLowerTypeBound(lhs, intType);
        collection.addUpperTypeBound(lhs, numType);

        //act
        collection.renameTypeVariable(lhs, rhs);

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", rhs, asList("int"), asList("num", "@" + upperRhs), false),
                varBinding("$rhs", rhs, asList("int"), asList("num", "@" + upperRhs), false),
                varBinding("$upper", upperRhs, asList("int", "@" + rhs), null, false)
        ));
    }

    @Test
    public void renameTypeVariable_HasRefBounds_TransfersBounds() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        collection.addVariable("$lhs", new TypeVariableReference(lhs));
        collection.addVariable("$rhs", new TypeVariableReference(rhs));
        String t1 = "T1";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        String t2 = "T2";
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addLowerRefBound(lhs, new TypeVariableReference(t1));
        collection.addLowerRefBound(t2, new TypeVariableReference(lhs));

        //act
        collection.renameTypeVariable(lhs, rhs);

        assertThat(collection, withVariableBindings(
                varBinding("$lhs", rhs, asList("@T1"), asList("@T2"), false),
                varBinding("$rhs", rhs, asList("@T1"), asList("@T2"), false),
                varBinding("$t1", t1, null, asList("@Trhs"), false),
                varBinding("$t2", t2, asList("@Trhs"), null, false)
        ));
    }

    @Test
    public void getLowerRefBounds_NothingDefined_ReturnsNull() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        Set<String> result = collection.getLowerRefBounds("T");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getLowerRefBounds_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        collection.addVariable("$a", new TypeVariableReference(t1));
        collection.addVariable("$b", new TypeVariableReference(t2));
        collection.addLowerRefBound(t1, new TypeVariableReference(t2));

        //act
        Set<String> result = collection.getLowerRefBounds(t1);

        assertThat(result, contains(t2));
    }

    @Test
    public void getUpperRefBounds_NothingDefined_ReturnsNull() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        Set<String> result = collection.getUpperRefBounds("T");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void getUpperRefBounds_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        collection.addVariable("$a", new TypeVariableReference(t1));
        collection.addVariable("$b", new TypeVariableReference(t2));
        collection.addLowerRefBound(t1, new TypeVariableReference(t2));

        //act
        Set<String> result = collection.getUpperRefBounds(t2);

        assertThat(result, contains(t1));
    }


    @Test(expected = IllegalArgumentException.class)
    public void fixType_VariableNotDefined_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IOverloadBindings collection = createOverloadBindings();
        collection.fixType("$a");

        //assert in annotation
    }

    @Test
    public void fixType_NotFixed_IsFixedAfterwards() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        collection.addVariable("$a", new TypeVariableReference(t1));

        //act
        collection.fixType("$a");
        boolean result = collection.getTypeVariableReference("$a").hasFixedType();

        assertThat(result, is(true));
    }

    @Test
    public void fixType_AlreadyFixed_DoesNotWrapItAgain() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        ITypeVariableReference constraint = new FixedTypeVariableReference(new TypeVariableReference(t1));
        collection.addVariable("$a", constraint);

        //act
        collection.fixType("$a");
        ITypeVariableReference result = collection.getTypeVariableReference("$a");

        assertThat(result, is(constraint));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAppliedOverload_NonExistingVariable_ThrowsIllegalArgumentException() {
        //no arrange necessary


        IOverloadBindings collection = createOverloadBindings();
        collection.setAppliedOverload("$nonExistingVariable", mock(IFunctionType.class));

        //assert in annotation
    }

    @Test
    public void setAndGetAppliedOverload_OneDefined_ReturnsTheOne() {
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        collection.addVariable("$a", new TypeVariableReference("T"));
        IFunctionType overload = mock(IFunctionType.class);

        //act
        collection.setAppliedOverload("$a", overload);
        IFunctionType result = collection.getAppliedOverload("$a");

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
