/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.constraints.BindingCollection;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;

import static ch.tsphp.tinsphp.symbols.test.integration.testutils.BindingCollectionMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.BindingCollectionMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BindingCollectionMergeTypeVariablesTest extends ATypeHelperTest
{
    @Test(expected = IllegalArgumentException.class)
    public void renameTypeVariable_UnknownTypeVariable_ThrowsIllegalArgumentException() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));

        //act
        bindingCollection.mergeFirstIntoSecond("T", lhs);

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameTypeVariable_UnknownNewName_ThrowsIllegalArgumentException() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));

        //act
        bindingCollection.mergeFirstIntoSecond(lhs, "T");

        //assert in annotation
    }

    @Test
    public void renameTypeVariable_IsSelfReference_DoesNotCallSetTypeVariableOnConstraint() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        TypeVariableReference constraint = spy(new TypeVariableReference(lhs));
        bindingCollection.addVariable("$lhs", constraint);

        //act
        bindingCollection.mergeFirstIntoSecond(lhs, lhs);

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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addLowerTypeBound(lhs, intType);
        bindingCollection.addUpperTypeBound(lhs, numType);

        //act
        bindingCollection.mergeFirstIntoSecond(lhs, rhs);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", rhs, asList("int"), asList("num"), false),
                varBinding("$rhs", rhs, asList("int"), asList("num"), false)
        ));
    }

    //see TINS-466 rename type variable does not promote type bounds
    @Test
    public void renameTypeVariable_HasTypeBoundsAndOtherHasUpperRef_TransferTypeBoundsAndPropagate() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        String upperRhs = "Tupper";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        bindingCollection.addVariable("$upper", new TypeVariableReference(upperRhs));
        bindingCollection.addLowerRefBound(upperRhs, new TypeVariableReference(rhs));
        bindingCollection.addLowerTypeBound(lhs, intType);
        bindingCollection.addUpperTypeBound(lhs, numType);

        //act
        bindingCollection.mergeFirstIntoSecond(lhs, rhs);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", rhs, asList("int"), asList("num", "@" + upperRhs), false),
                varBinding("$rhs", rhs, asList("int"), asList("num", "@" + upperRhs), false),
                varBinding("$upper", upperRhs, asList("int", "@" + rhs), null, false)
        ));
    }

    @Test
    public void renameTypeVariable_HasRefBounds_TransfersBounds() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String lhs = "Tlhs";
        String rhs = "Trhs";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(lhs));
        bindingCollection.addVariable("$rhs", new TypeVariableReference(rhs));
        String t1 = "T1";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        String t2 = "T2";
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addLowerRefBound(lhs, new TypeVariableReference(t1));
        bindingCollection.addLowerRefBound(t2, new TypeVariableReference(lhs));

        //act
        bindingCollection.mergeFirstIntoSecond(lhs, rhs);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$lhs", rhs, asList("@T1"), asList("@T2"), false),
                varBinding("$rhs", rhs, asList("@T1"), asList("@T2"), false),
                varBinding("$t1", t1, null, asList("@Trhs"), false),
                varBinding("$t2", t2, asList("@Trhs"), null, false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasUpperBoundToConvertibleType_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList(t1));
        bindingCollection.addUpperTypeBound(t3, convertibleTypeSymbol);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false),
                varBinding("$t3", t3, null, asList("{as T2}"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasLowerBoundToConvertibleType_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList(t1));
        bindingCollection.addLowerTypeBound(t3, convertibleTypeSymbol);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false),
                varBinding("$t3", t3, asList("{as T2}"), null, false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasUpperBoundToConvertibleTypeInUnion_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.addUpperTypeBound(t1, stringType);
        bindingCollection.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol);
        bindingCollection.addUpperTypeBound(t3, unionTypeSymbol);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("string"), false),
                varBinding("$t2", t2, null, asList("string"), false),
                varBinding("$t3", t3, null, asList("{as T2}"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasLowerBoundToConvertibleTypeInUnion_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.addUpperTypeBound(t1, stringType);
        bindingCollection.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol);
        bindingCollection.addLowerTypeBound(t3, unionTypeSymbol);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("string"), false),
                varBinding("$t2", t2, null, asList("string"), false),
                varBinding("$t3", t3, asList("{as T2}"), null, false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasUpperBoundToConvertibleTypeInUnionWithInt_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addVariable("$t3", new TypeVariableReference(t3));
        bindingCollection.addUpperTypeBound(t1, fooType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(intType, convertibleTypeSymbol);
        bindingCollection.addUpperTypeBound(t3, unionTypeSymbol);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("Foo"), false),
                varBinding("$t2", t2, null, asList("Foo"), false),
                varBinding("$t3", t3, null, asList("(int | {as T2})"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasLowerBoundToConvertibleTypeInUnionWithInt_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addVariable("$t3", new TypeVariableReference(t3));
        bindingCollection.addUpperTypeBound(t1, fooType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindingCollection.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(intType, convertibleTypeSymbol);
        bindingCollection.addLowerTypeBound(t3, unionTypeSymbol);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("Foo"), false),
                varBinding("$t2", t2, null, asList("Foo"), false),
                varBinding("$t3", t3, asList("int", "{as T2}"), null, false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_AfterCopyAndHasBoundToConvertibleType_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindings = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings.bind(convertibleTypeSymbol, asList(t1));
        bindings.addUpperTypeBound(t3, convertibleTypeSymbol);

        //act
        IBindingCollection bindingCollection = createBindingCollection((BindingCollection) bindings);
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false),
                varBinding("$t3", t3, null, asList("{as T2}"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_AfterCopyAndHasBoundToConvertibleTypeInUnion_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindings = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(convertibleTypeSymbol);
        bindings.addUpperTypeBound(t3, unionTypeSymbol);

        //act
        IBindingCollection bindingCollection = createBindingCollection((BindingCollection) bindings);
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false),
                varBinding("$t3", t3, null, asList("{as T2}"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void
    renameTypeVariable_AfterCopyAndHasUpperBoundToConvertibleTypeInUnionWithFloat_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindings = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        bindings.addUpperTypeBound(t1, fooType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(floatType, convertibleTypeSymbol);
        bindings.addUpperTypeBound(t3, unionTypeSymbol);

        //act
        IBindingCollection bindingCollection = createBindingCollection((BindingCollection) bindings);
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("Foo"), false),
                varBinding("$t2", t2, null, asList("Foo"), false),
                varBinding("$t3", t3, null, asList("(float | {as T2})"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void
    renameTypeVariable_AfterCopyAndHasLowerBoundToConvertibleTypeInIntersectionWithFloat_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IBindingCollection bindings = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        bindings.addUpperTypeBound(t1, fooType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleTypeSymbol();
        bindings.bind(convertibleTypeSymbol, asList(t1));
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol(floatType, convertibleTypeSymbol);
        bindings.addLowerTypeBound(t3, intersectionTypeSymbol);

        //act
        IBindingCollection bindingCollection = createBindingCollection((BindingCollection) bindings);
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("Foo"), false),
                varBinding("$t2", t2, null, asList("Foo"), false),
                varBinding("$t3", t3, asList("(float & {as T2})"), null, false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void
    renameTypeVariable_AfterCopyTwiceAndHasConvertibleInBothBoundsInMultipleContainers_ConvertibleTypeIsRenamedAsWell
    () {
        //pre act - necessary for arrange
        IBindingCollection bindings = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        String t4 = "T4";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        bindings.addVariable("$t4", new TypeVariableReference(t4));
        bindings.addUpperTypeBound(t1, fooType);
        bindings.addUpperTypeBound(t2, interfaceBType);

        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol();
        bindings.bind(asT1, asList(t1));
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol(floatType, asT1);
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol(intType, intersectionTypeSymbol);
        bindings.addUpperTypeBound(t3, unionTypeSymbol);

        IUnionTypeSymbol unionTypeSymbol2 = createUnionTypeSymbol(interfaceSubAType, asT1);
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionTypeSymbol(arrayType, unionTypeSymbol2);
        bindings.addLowerTypeBound(t4, intersectionTypeSymbol2);

        //act
        IBindingCollection bindingCollection1 = createBindingCollection((BindingCollection) bindings);
        IBindingCollection bindingCollection = createBindingCollection((BindingCollection) bindingCollection1);
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("Foo"), false),
                varBinding("$t2", t2, null, asList("Foo"), false),
                varBinding("$t3", t3, null, asList("((float & {as T2}) | int)"), false),
                varBinding("$t4", t4, asList("((ISubA | {as T2}) & array)"), null, false)
        ));
    }

    //see TINS-484 renaming a convertible type points to same type variable
    @Test
    public void renameTypeVariable_HasUpperBoundToConvertibleWhichPointsToNewTypeVariable_ConvertibleTypeIsRemoved() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addUpperTypeBound(t1, intType);
        bindingCollection.addUpperTypeBound(t2, interfaceBType);

        IConvertibleTypeSymbol asT2 = createConvertibleTypeSymbol();
        bindingCollection.bind(asT2, asList(t2));
        bindingCollection.addUpperTypeBound(t1, asT2);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("int", "IB"), false),
                varBinding("$t2", t2, null, asList("int", "IB"), false)
        ));
    }

    //see TINS-484 renaming a convertible type points to same type variable
    @Test
    public void
    renameTypeVariable_NewTypeVariableWithConvertibleInUpperWhichPointsToOldTypeVariable_ConvertibleTypeIsRemoved() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        bindingCollection.addUpperTypeBound(t1, intType);
        bindingCollection.addUpperTypeBound(t2, interfaceBType);
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol();
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(t2, asT1);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, asList("int", "IB"), false),
                varBinding("$t2", t2, null, asList("int", "IB"), false)
        ));
    }

    //see TINS-484 renaming a convertible type points to same type variable
    @Test
    public void
    renameTypeVariable_NewTypeVariableWithOnlyConvertibleInUpperWhichPointsToOldTypeVariable_UpperIsNullAfterwards() {
        //pre act - necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        bindingCollection.addVariable("$t1", new TypeVariableReference(t1));
        bindingCollection.addVariable("$t2", new TypeVariableReference(t2));
        IConvertibleTypeSymbol asT1 = createConvertibleTypeSymbol();
        bindingCollection.bind(asT1, asList(t1));
        bindingCollection.addUpperTypeBound(t2, asT1);

        //act
        bindingCollection.mergeFirstIntoSecond(t1, t2);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false)
        ));
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
