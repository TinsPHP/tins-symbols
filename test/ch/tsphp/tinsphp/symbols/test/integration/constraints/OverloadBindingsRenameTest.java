/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;

import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class OverloadBindingsRenameTest extends ATypeHelperTest
{
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

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasUpperBoundToConvertibleType_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        collection.bind(convertibleTypeSymbol, asList(t1));
        collection.addUpperTypeBound(t3, convertibleTypeSymbol);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false),
                varBinding("$t3", t3, null, asList("{as T2}"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasLowerBoundToConvertibleType_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        collection.bind(convertibleTypeSymbol, asList(t1));
        collection.addLowerTypeBound(t3, convertibleTypeSymbol);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false),
                varBinding("$t3", t3, asList("{as T2}"), null, false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasUpperBoundToConvertibleTypeInUnion_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        collection.addUpperTypeBound(t1, stringType);
        collection.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnion(convertibleTypeSymbol);
        collection.addUpperTypeBound(t3, unionTypeSymbol);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, asList("string"), false),
                varBinding("$t2", t2, null, asList("string"), false),
                varBinding("$t3", t3, null, asList("{as T2}"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasLowerBoundToConvertibleTypeInUnion_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        collection.addUpperTypeBound(t1, stringType);
        collection.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnion(convertibleTypeSymbol);
        collection.addLowerTypeBound(t3, unionTypeSymbol);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, asList("string"), false),
                varBinding("$t2", t2, null, asList("string"), false),
                varBinding("$t3", t3, asList("{as T2}"), null, false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasUpperBoundToConvertibleTypeInUnionWithInt_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addVariable("$t3", new TypeVariableReference(t3));
        collection.addUpperTypeBound(t1, fooType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        collection.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnion(intType, convertibleTypeSymbol);
        collection.addUpperTypeBound(t3, unionTypeSymbol);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, asList("Foo"), false),
                varBinding("$t2", t2, null, asList("Foo"), false),
                varBinding("$t3", t3, null, asList("(int | {as T2})"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_HasLowerBoundToConvertibleTypeInUnionWithInt_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addVariable("$t3", new TypeVariableReference(t3));
        collection.addUpperTypeBound(t1, fooType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        collection.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnion(intType, convertibleTypeSymbol);
        collection.addLowerTypeBound(t3, unionTypeSymbol);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, asList("Foo"), false),
                varBinding("$t2", t2, null, asList("Foo"), false),
                varBinding("$t3", t3, asList("int", "{as T2}"), null, false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_AfterCopyAndHasBoundToConvertibleType_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IOverloadBindings bindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindings.bind(convertibleTypeSymbol, asList(t1));
        bindings.addUpperTypeBound(t3, convertibleTypeSymbol);

        //act
        IOverloadBindings collection = createOverloadBindings((OverloadBindings) bindings);
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false),
                varBinding("$t3", t3, null, asList("{as T2}"), false)
        ));
    }

    //see TINS-483 rename a type variable and convertible types
    @Test
    public void renameTypeVariable_AfterCopyAndHasBoundToConvertibleTypeInUnion_ConvertibleTypeIsRenamedAsWell() {
        //pre act - necessary for arrange
        IOverloadBindings bindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindings.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnion(convertibleTypeSymbol);
        bindings.addUpperTypeBound(t3, unionTypeSymbol);

        //act
        IOverloadBindings collection = createOverloadBindings((OverloadBindings) bindings);
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings bindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        bindings.addUpperTypeBound(t1, fooType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindings.bind(convertibleTypeSymbol, asList(t1));
        IUnionTypeSymbol unionTypeSymbol = createUnion(floatType, convertibleTypeSymbol);
        bindings.addUpperTypeBound(t3, unionTypeSymbol);

        //act
        IOverloadBindings collection = createOverloadBindings((OverloadBindings) bindings);
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings bindings = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        String t3 = "T3";
        bindings.addVariable("$t1", new TypeVariableReference(t1));
        bindings.addVariable("$t2", new TypeVariableReference(t2));
        bindings.addVariable("$t3", new TypeVariableReference(t3));
        bindings.addUpperTypeBound(t1, fooType);
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        bindings.bind(convertibleTypeSymbol, asList(t1));
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionType(floatType, convertibleTypeSymbol);
        bindings.addLowerTypeBound(t3, intersectionTypeSymbol);

        //act
        IOverloadBindings collection = createOverloadBindings((OverloadBindings) bindings);
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings bindings = createOverloadBindings();

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

        IConvertibleTypeSymbol asT1 = createConvertibleType();
        bindings.bind(asT1, asList(t1));
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionType(floatType, asT1);
        IUnionTypeSymbol unionTypeSymbol = createUnion(intType, intersectionTypeSymbol);
        bindings.addUpperTypeBound(t3, unionTypeSymbol);

        IUnionTypeSymbol unionTypeSymbol2 = createUnion(interfaceSubAType, asT1);
        IIntersectionTypeSymbol intersectionTypeSymbol2 = createIntersectionType(arrayType, unionTypeSymbol2);
        bindings.addLowerTypeBound(t4, intersectionTypeSymbol2);

        //act
        IOverloadBindings collection1 = createOverloadBindings((OverloadBindings) bindings);
        IOverloadBindings collection = createOverloadBindings((OverloadBindings) collection1);
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addUpperTypeBound(t1, intType);
        collection.addUpperTypeBound(t2, interfaceBType);

        IConvertibleTypeSymbol asT2 = createConvertibleType();
        collection.bind(asT2, asList(t2));
        collection.addUpperTypeBound(t1, asT2);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, asList("int", "IB"), false),
                varBinding("$t2", t2, null, asList("int", "IB"), false)
        ));
    }

    //see TINS-484 renaming a convertible type points to same type variable
    @Test
    public void
    renameTypeVariable_NewTypeVariableWithConvertibleInUpperWhichPointsToOldTypeVariable_ConvertibleTypeIsRemoved() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        collection.addUpperTypeBound(t1, intType);
        collection.addUpperTypeBound(t2, interfaceBType);
        IConvertibleTypeSymbol asT1 = createConvertibleType();
        collection.bind(asT1, asList(t1));
        collection.addUpperTypeBound(t2, asT1);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, asList("int", "IB"), false),
                varBinding("$t2", t2, null, asList("int", "IB"), false)
        ));
    }

    //see TINS-484 renaming a convertible type points to same type variable
    @Test
    public void
    renameTypeVariable_NewTypeVariableWithOnlyConvertibleInUpperWhichPointsToOldTypeVariable_UpperIsNullAfterwards() {
        //pre act - necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String t1 = "T1";
        String t2 = "T2";
        collection.addVariable("$t1", new TypeVariableReference(t1));
        collection.addVariable("$t2", new TypeVariableReference(t2));
        IConvertibleTypeSymbol asT1 = createConvertibleType();
        collection.bind(asT1, asList(t1));
        collection.addUpperTypeBound(t2, asT1);

        //act
        collection.renameTypeVariable(t1, t2);

        assertThat(collection, withVariableBindings(
                varBinding("$t1", t2, null, null, false),
                varBinding("$t2", t2, null, null, false)
        ));
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