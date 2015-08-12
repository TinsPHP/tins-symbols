/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.OverloadApplicationDto;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.BindingCollection;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import static ch.tsphp.tinsphp.symbols.test.integration.testutils.BindingCollectionMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.BindingCollectionMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;

public class BindingCollectionCopyTest extends ATypeHelperTest
{

    @Test
    public void copyConstructor_HasTwoVariables_CopyBoth() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));

        IBindingCollection bindingCollection = createBindingCollection(bindings1);

        assertThat(bindingCollection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(bindingCollection.getTypeVariableReference("$a").hasFixedType(), is(false));
        assertThat(bindingCollection.getTypeVariableReference("$b").hasFixedType(), is(false));
    }

    @Test
    public void copyConstructor_HasTwoVariablesFirstIsFixed_OnlyFirstIsFixed() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new FixedTypeVariableReference(new TypeVariableReference("T")));
        bindings1.addVariable("$b", new TypeVariableReference("T"));

        IBindingCollection bindingCollection = createBindingCollection(bindings1);

        assertThat(bindingCollection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(bindingCollection.getTypeVariableReference("$a").hasFixedType(), is(true));
        assertThat(bindingCollection.getTypeVariableReference("$b").hasFixedType(), is(false));
    }

    @Test
    public void copyConstructor_HasTwoVariablesSecondIsFixed_OnlySecondIsFixed() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T"));
        bindings1.addVariable("$b", new FixedTypeVariableReference(new TypeVariableReference("T")));

        IBindingCollection bindingCollection = createBindingCollection(bindings1);

        assertThat(bindingCollection.getVariableIds(), containsInAnyOrder("$a", "$b"));
        assertThat(bindingCollection.getTypeVariableReference("$a").hasFixedType(), is(false));
        assertThat(bindingCollection.getTypeVariableReference("$b").hasFixedType(), is(true));
    }

    @Test
    public void copyConstructor_HasLowerTypeBound_IsCopied() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T"));
        bindings1.addLowerTypeBound("T", intType);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        bindings1.addLowerTypeBound("T", floatType);

        assertThat(bindingCollection, withVariableBindings(varBinding("$a", "T", asList("int"), null, false)));
    }

    @Test
    public void copyConstructor_HasUpperTypeBound_IsCopied() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T"));
        bindings1.addUpperTypeBound("T", interfaceAType);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        bindings1.addUpperTypeBound("T", interfaceBType);
        bindings1.addLowerTypeBound("T", fooType);

        assertThat(bindingCollection, withVariableBindings(varBinding("$a", "T", null, asList("IA"), false)));
    }

    @Test
    public void copyConstructor_HasLowerRefBound_IsCopied() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        bindings1.addLowerRefBound("T1", new TypeVariableReference("T2"));

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        bindings1.addLowerRefBound("T2", new TypeVariableReference("T1"));

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", "T1", asList("@T2"), null, false),
                varBinding("$b", "T2", null, asList("@T1"), false)
        ));
    }

    @Test
    public void copyConstructor_HasVariablesAndRenameOneAfterCopying_DoesOnlyRenameOldBinding() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        bindings1.addVariable("$c", new TypeVariableReference("T3"));


        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        bindings1.mergeFirstIntoSecond("T1", "T3");
        bindingCollection.mergeFirstIntoSecond("T3", "T2");

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", "T1", null, null, false),
                varBinding("$b", "T2", null, null, false),
                varBinding("$c", "T2", null, null, false)

        ));
    }

    @Test
    public void copyConstructor_HasAppliedOverload_IsCopied() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        OverloadApplicationDto dto = new OverloadApplicationDto(mock(IFunctionType.class), null, null);
        bindings1.setAppliedOverload("$a", dto);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        bindings1.addVariable("$b", new TypeVariableReference("T1"));
        OverloadApplicationDto dto2 = new OverloadApplicationDto(mock(IFunctionType.class), null, null);
        bindings1.setAppliedOverload("$b", dto2);

        assertThat(bindingCollection.getAppliedOverload("$a"), is(dto));
        assertThat(bindingCollection.getAppliedOverload("$b"), is(nullValue()));
    }

    @Test
    public void copyConstructor_HasConvertibleInUpperBound_IsCopied() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings1.bind(convertibleTypeSymbol, asList("T2"));
        bindings1.addUpperTypeBound("T1", convertibleTypeSymbol);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        convertibleTypeSymbol.renameTypeParameter("T2", "T3");

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", "T1", null, asList("{as T2}"), false),
                varBinding("$b", "T2", null, null, false)
        ));
    }

    @Test
    public void copyConstructor_HasConvertibleInLowerBound_IsCopied() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings1.bind(convertibleTypeSymbol, asList("T2"));
        bindings1.addLowerTypeBound("T1", convertibleTypeSymbol);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        convertibleTypeSymbol.renameTypeParameter("T2", "T3");

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", "T1", asList("{as T2}"), null, false),
                varBinding("$b", "T2", null, null, false)
        ));
    }

    //see TINS-486 rebinding convertible types does not always work
    @Test
    public void copyConstructor_HasConvertibleInUpperBound_IsReboundToNewBindingCollection() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings1.bind(convertibleTypeSymbol, asList("T2"));
        bindings1.addUpperTypeBound("T1", convertibleTypeSymbol);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        IIntersectionTypeSymbol upperTypeBounds = bindingCollection.getUpperTypeBounds("T1");
        IConvertibleTypeSymbol result
                = (IConvertibleTypeSymbol) upperTypeBounds.getTypeSymbols().values().iterator().next();

        assertThat(result.getBindingCollection(), is(bindingCollection));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void copyConstructor_HasConvertibleInUpperBound_NewUpperBoundIsRegisteredToNewConvertible() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings1.bind(convertibleTypeSymbol, asList("T2"));
        bindings1.addUpperTypeBound("T1", convertibleTypeSymbol);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        IIntersectionTypeSymbol upperTypeBounds = bindingCollection.getUpperTypeBounds("T1");
        //should no non-fixed now
        assertThat(upperTypeBounds.isFixed(), is(false));
        bindingCollection.fixTypeParameter("T2");

        assertThat(upperTypeBounds.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void copyConstructor_HasConvertibleInUpperBoundInUnion_NewUpperBoundIsRegisteredToNewConvertible() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings1.bind(convertibleTypeSymbol, asList("T2"));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol, interfaceBType);
        bindings1.addUpperTypeBound("T1", unionTypeSymbol);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        IIntersectionTypeSymbol upperTypeBounds = bindingCollection.getUpperTypeBounds("T1");
        //should no non-fixed now
        assertThat(upperTypeBounds.isFixed(), is(false));
        bindingCollection.fixTypeParameter("T2");

        assertThat(upperTypeBounds.isFixed(), is(true));
    }

    //see TINS-486 rebinding convertible types does not always work
    @Test
    public void copyConstructor_HasConvertibleInLowerBound_IsReboundToNewBindingCollection() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings1.bind(convertibleTypeSymbol, asList("T2"));
        bindings1.addLowerTypeBound("T1", convertibleTypeSymbol);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        IUnionTypeSymbol lowerTypeBounds = bindingCollection.getLowerTypeBounds("T1");
        IConvertibleTypeSymbol result
                = (IConvertibleTypeSymbol) lowerTypeBounds.getTypeSymbols().values().iterator().next();

        assertThat(result.getBindingCollection(), is(bindingCollection));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void copyConstructor_HasConvertibleInLowerBound_NewLowerBoundIsRegisteredToNewConvertible() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings1.bind(convertibleTypeSymbol, asList("T2"));
        bindings1.addLowerTypeBound("T1", convertibleTypeSymbol);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        IUnionTypeSymbol lowerTypeBounds = bindingCollection.getLowerTypeBounds("T1");
        //should no non-fixed now
        assertThat(lowerTypeBounds.isFixed(), is(false));
        bindingCollection.fixTypeParameter("T2");

        assertThat(lowerTypeBounds.isFixed(), is(true));
    }

    //see TINS-488 container type not fixed but should be
    @Test
    public void copyConstructor_HasConvertibleInIntersectionInLowerBound_NewLowerBoundIsRegisteredToNewConvertible() {
        BindingCollection bindings1 = new BindingCollection(symbolFactory, typeHelper);
        bindings1.addVariable("$a", new TypeVariableReference("T1"));
        bindings1.addVariable("$b", new TypeVariableReference("T2"));
        bindings1.addUpperTypeBound("T2", numType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings1.bind(convertibleTypeSymbol, asList("T2"));
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol(
                convertibleTypeSymbol, interfaceBType);
        bindings1.addLowerTypeBound("T1", intersectionTypeSymbol);

        IBindingCollection bindingCollection = createBindingCollection(bindings1);
        IUnionTypeSymbol lowerTypeBounds = bindingCollection.getLowerTypeBounds("T1");
        //should no non-fixed now
        assertThat(lowerTypeBounds.isFixed(), is(false));
        bindingCollection.fixTypeParameter("T2");

        assertThat(lowerTypeBounds.isFixed(), is(true));
    }

    protected IBindingCollection createBindingCollection(BindingCollection bindings) {
        return new BindingCollection(bindings);
    }
}
