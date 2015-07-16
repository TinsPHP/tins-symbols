/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.TinsPHPConstants;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.constraints.BindingCollection;
import ch.tsphp.tinsphp.symbols.constraints.FunctionType;
import ch.tsphp.tinsphp.symbols.constraints.Variable;
import ch.tsphp.tinsphp.symbols.scopes.ScopeHelper;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FunctionTypeTest extends ATypeTest
{

    @Test
    public void getSignature_SimplifiedNoParamsReturnsInt_ReturnsEmptyParamArrowInt() {
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T")));
        bindingCollection.addLowerTypeBound("T", intType);
        bindingCollection.addUpperTypeBound("T", intType);

        IFunctionType function = createFunction("foo", bindingCollection, new ArrayList<IVariable>());
        function.manuallySimplified(new HashSet<String>(), 0, false);
        String result = function.getSignature();

        assertThat(result, is("() -> int"));
    }

    @Test
    public void getSignature_IntArrowFloat_ReturnsSignatureWithParams() {
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable("$expr", new FixedTypeVariableReference(new TypeVariableReference("T1")));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T2")));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", intType);
        bindingCollection.addLowerTypeBound("T2", floatType);
        bindingCollection.addUpperTypeBound("T2", floatType);
        IVariable expr = new Variable("$expr");

        IFunctionType function = createFunction("foo", bindingCollection, asList(expr));
        function.manuallySimplified(new HashSet<String>(), 0, false);
        String result = function.getSignature();

        assertThat(result, is("int -> float"));
    }

    @Test
    public void getSignature_Identity_ReturnsTArrowT() {
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable("$expr", new TypeVariableReference("T"));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference("T"));
        IVariable expr = new Variable("$expr");

        IFunctionType function = createFunction("foo", bindingCollection, asList(expr));
        function.manuallySimplified(set("T"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T -> T"));
    }

    @Test
    public void getSignature_Assign_ReturnsT1xT2ArrowT1AndT2LowerT1() {
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable("$lhs", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$rhs", new TypeVariableReference("T2"));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference("T1"));
        bindingCollection.addLowerRefBound("T1", new TypeVariableReference("T2"));
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", bindingCollection, asList(lhs, rhs));
        function.manuallySimplified(set("T1", "T2"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> T1 \\ T2 <: T1"));
    }

    //see also TINS-516 improve function signature with unions
    @Test
    public void
    getSignature_T1xT2ArrowT3AndIntLowerT1LowerNumAndT2LowerBoolAndT1OrT2LowerT3_ReturnsSignatureWithTypeParameters() {
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable("$lhs", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$rhs", new TypeVariableReference("T2"));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference("T3"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", numType);
        bindingCollection.addUpperTypeBound("T2", boolType);
        bindingCollection.addLowerRefBound("T3", new TypeVariableReference("T1"));
        bindingCollection.addLowerRefBound("T3", new TypeVariableReference("T2"));
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", bindingCollection, asList(lhs, rhs));
        function.manuallySimplified(set("T1", "T2"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> (int | T1 | T2) \\ int <: T1 <: num, T2 <: bool"));
    }

    //see also TINS-516 improve function signature with unions
    @Test
    public void
    getSignature_T1xT2ArrowT3AndIntLowerT1LowerNumAndT2LowerBoolAndT2OrT1LowerT3_ReturnsSignatureWithTypeParameters() {
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable("$lhs", new TypeVariableReference("T1"));
        bindingCollection.addVariable("$rhs", new TypeVariableReference("T2"));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference("T3"));
        bindingCollection.addLowerTypeBound("T1", intType);
        bindingCollection.addUpperTypeBound("T1", numType);
        bindingCollection.addUpperTypeBound("T2", boolType);
        bindingCollection.addLowerRefBound("T3", new TypeVariableReference("T2"));
        bindingCollection.addLowerRefBound("T3", new TypeVariableReference("T1"));
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", bindingCollection, asList(lhs, rhs));
        function.manuallySimplified(set("T1", "T2"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> (int | T1 | T2) \\ int <: T1 <: num, T2 <: bool"));
    }

    @Test
    public void
    getSignature_WithHelperTypeParameterCorrespondsPlusAssign_ReturnsSignatureIncludingHelperTypeParameter() {
        IBindingCollection bindingCollection = createBindingCollection();
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        String tHelper = "T";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addVariable("$rhs", new FixedTypeVariableReference(new TypeVariableReference(tRhs)));
        bindingCollection.addVariable("!help'", new TypeVariableReference(tHelper));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tLhs));
        IConvertibleTypeSymbol asT = symbolFactory.createConvertibleTypeSymbol();
        bindingCollection.bind(asT, Arrays.asList("T"));
        bindingCollection.addLowerRefBound(tLhs, new TypeVariableReference(tHelper));
        bindingCollection.addUpperTypeBound(tLhs, asT);
        bindingCollection.addUpperTypeBound(tRhs, asT);
        bindingCollection.addUpperTypeBound(tHelper, numType);

        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", bindingCollection, asList(lhs, rhs));
        function.manuallySimplified(set(tLhs, tHelper), 0, true);
        String result = function.getSignature();

        assertThat(result, is("Tlhs x {as T} -> Tlhs \\ T <: Tlhs <: {as T}, T <: num"));
    }

    //see TINS-514 function signature calculation NullPointer
    //see also TINS-516 improve function signature with unions
    @Test
    public void getSignature_TxAndTyAndReturnHasTxAndTyAsLowerRef_ReturnsSignatureAccordingly() {
        IBindingCollection bindingCollection = createBindingCollection();
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        String tReturn = "Treturn";
        bindingCollection.addVariable("$lhs", new TypeVariableReference(tLhs));
        bindingCollection.addVariable("$rhs", new FixedTypeVariableReference(new TypeVariableReference(tRhs)));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));

        bindingCollection.addUpperTypeBound(tLhs, numType);
        bindingCollection.addUpperTypeBound(tRhs, stringType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tLhs));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tRhs));

        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", bindingCollection, asList(lhs, rhs));
        function.manuallySimplified(set(tLhs, tRhs), 0, false);
        String result = function.getSignature();

        assertThat(result, is("Tlhs x Trhs -> (Tlhs | Trhs) \\ Tlhs <: num, Trhs <: string"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_Identity_ReturnsIdentitySignature() {
        //corresponds: function foo($x){return $x;}
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V2";
        String tReturn = "V1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        IVariable $x = new Variable("$x");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("T -> T"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_IdentityWithNumAsUpper_ReturnsIdentitySignatureWithConstraint() {
        //corresponds: function foo($x){return $x + 1;}
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V2";
        String tReturn = "V1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        IVariable $x = new Variable("$x");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("T -> T \\ T <: num"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_PlusWithConvertible_UseOnlyT() {
        //corresponds: function foo($x, $y){return $x + $y;}
        //where {as T} x {as T} -> T \ T <: num is used for the + operator
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        String te1 = "V2";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(te1));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = symbolFactory.createConvertibleTypeSymbol();
        bindingCollection.bind(asTe1, Arrays.asList(te1));
        bindingCollection.addUpperTypeBound(tx, asTe1);
        bindingCollection.addUpperTypeBound(ty, asTe1);
        bindingCollection.addUpperTypeBound(te1, numType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("{as T} x {as T} -> T \\ T <: num"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_PlusWithConvertibleWithLowerBound_UseOnlyT() {
        //corresponds: function foo($x, $y){return $x + $y + 1;}
        //where {as T} x {as T} -> T \ T <: num is used for the first + operator
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        String te1 = "V2";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(te1));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = symbolFactory.createConvertibleTypeSymbol();
        bindingCollection.bind(asTe1, Arrays.asList(te1));
        bindingCollection.addUpperTypeBound(tx, asTe1);
        bindingCollection.addUpperTypeBound(ty, asTe1);
        bindingCollection.addLowerTypeBound(te1, intType);
        bindingCollection.addUpperTypeBound(te1, numType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("{as T} x {as T} -> T \\ int <: T <: num"));
    }

    //see TINS-548 hasConvertibleParameters false even though function has
    @Test
    public void simplify_PlusWithConvertibleWithLowerBound_HasConvertibleParameterTypes() {
        //corresponds: function foo($x, $y){return $x + $y + 1;}
        //where {as T} x {as T} -> T \ T <: num is used for the first + operator
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        String te1 = "V2";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(te1));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = symbolFactory.createConvertibleTypeSymbol();
        bindingCollection.bind(asTe1, Arrays.asList(te1));
        bindingCollection.addUpperTypeBound(tx, asTe1);
        bindingCollection.addUpperTypeBound(ty, asTe1);
        bindingCollection.addLowerTypeBound(te1, intType);
        bindingCollection.addUpperTypeBound(te1, numType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x, $y));
        function.simplify();
        boolean result = function.hasConvertibleParameterTypes();

        assertThat(result, is(true));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    //see also TINS-517 param lower of other and both lower of return
    //see also TINS-516 improve function signature with unions
    @Test
    public void simplify_TxAndTyWhereTxLowerTyAndTyLowerTRtnAndIntLowerTRtn_TypeParametersReflectOrderOfParameters() {
        //corresponds: function foo($x, $y){if($y > 10){$y = $x; return $y;} return 1;}
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tx));
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> (int | T2) \\ T1 <: T2"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    //see also TINS-516 improve function signature with unions
    @Test
    public void simplify_TxAndTyWhereTyLowerTxAndTyLowerTRtnAndIntLowerTRtn_TypeParametersReflectOrderOfParameters() {
        //corresponds: function foo($x, $y){if($y > 10){$x = $y; return $y;} return 1;}
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("mixed x T -> (int | T)"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    //see also TINS-517 param lower of other and both lower of return
    @Test
    public void simplify_TxAndTyWhereTyLowerTxAndTxLowerTRtnAndIntLowerTRtn_TypeParametersReflectOrderOfParameters() {
        //corresponds: function foo($x, $y){if($y > 10){$x = $y; return $x;} return 1;}
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> (int | T1) \\ T2 <: T1"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_AsTe1UpperTxAndTe1LowerReturn_HelperTypeVariableIsRenamed() {
        //corresponds: function foo($x){ if($x + 1 > 0){ return $x; } return []; }
        //where {as T} x {as T} -> T was taken for $x + 1;
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V3";
        String te1 = "V2";
        String tReturn = "V1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(te1));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = symbolFactory.createConvertibleTypeSymbol();
        bindingCollection.bind(asTe1, asList(te1));
        bindingCollection.addUpperTypeBound(te1, intType);
        bindingCollection.addUpperTypeBound(tx, asTe1);
        bindingCollection.addLowerTypeBound(tReturn, arrayType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        IVariable $x = new Variable("$x");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("{as T} -> (array | T) \\ T <: int"));
    }

    //see TINS-548 hasConvertibleParameters false even though function has
    @Test
    public void simplify_AsTe1UpperTxAndTe1LowerReturn_HasConvertibleParameters() {
        //corresponds: function foo($x){ if($x + 1 > 0){ return $x; } return []; }
        //where {as T} x {as T} -> T was taken for $x + 1;
        IBindingCollection bindingCollection = createBindingCollection();
        String tx = "V3";
        String te1 = "V2";
        String tReturn = "V1";
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("+@1|2", new TypeVariableReference(te1));
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = symbolFactory.createConvertibleTypeSymbol();
        bindingCollection.bind(asTe1, asList(te1));
        bindingCollection.addUpperTypeBound(te1, intType);
        bindingCollection.addUpperTypeBound(tx, asTe1);
        bindingCollection.addLowerTypeBound(tReturn, arrayType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        IVariable $x = new Variable("$x");

        IFunctionType function = createFunction("foo", bindingCollection, asList($x));
        function.simplify();
        boolean result = function.hasConvertibleParameterTypes();

        assertThat(result, is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void simplify_CalledTheSecondTime_ThrowsIllegalStateException() {
        IBindingCollection bindingCollection = createBindingCollection();
        bindingCollection.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T")));
        bindingCollection.addLowerTypeBound("T", intType);
        bindingCollection.addUpperTypeBound("T", intType);

        IFunctionType function = createFunction("foo", bindingCollection, new ArrayList<IVariable>());
        function.simplify();
        function.simplify();

        //assert in annotation
    }

    private BindingCollection createBindingCollection() {
        ITypeHelper typeHelper = new TypeHelper();
        typeHelper.setMixedTypeSymbol(mixedType);
        ISymbolFactory symbolFactory = new SymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);
        symbolFactory.setMixedTypeSymbol(mixedType);
        return new BindingCollection(symbolFactory, typeHelper);
    }


    protected IFunctionType createFunction(
            String theName, IBindingCollection theBindingCollection, List<IVariable> theParameterVariables) {
        return new FunctionType(theName, theBindingCollection, theParameterVariables);
    }
}
