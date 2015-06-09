/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.TinsPHPConstants;
import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.constraints.FunctionType;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
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
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T")));
        overloadBindings.addLowerTypeBound("T", intType);
        overloadBindings.addUpperTypeBound("T", intType);

        IFunctionType function = createFunction("foo", overloadBindings, new ArrayList<IVariable>());
        function.manuallySimplified(new HashSet<String>(), 0, false);
        String result = function.getSignature();

        assertThat(result, is("() -> int"));
    }

    @Test
    public void getSignature_IntArrowFloat_ReturnsSignatureWithParams() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$expr", new FixedTypeVariableReference(new TypeVariableReference("T1")));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T2")));
        overloadBindings.addLowerTypeBound("T1", intType);
        overloadBindings.addUpperTypeBound("T1", intType);
        overloadBindings.addLowerTypeBound("T2", floatType);
        overloadBindings.addUpperTypeBound("T2", floatType);
        IVariable expr = new Variable("$expr");

        IFunctionType function = createFunction("foo", overloadBindings, asList(expr));
        function.manuallySimplified(new HashSet<String>(), 0, false);
        String result = function.getSignature();

        assertThat(result, is("int -> float"));
    }

    @Test
    public void getSignature_Identity_ReturnsTArrowT() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$expr", new TypeVariableReference("T"));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference("T"));
        IVariable expr = new Variable("$expr");

        IFunctionType function = createFunction("foo", overloadBindings, asList(expr));
        function.manuallySimplified(set("T"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T -> T"));
    }

    @Test
    public void getSignature_Assign_ReturnsT1xT2ArrowT1AndT2LowerT1() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$lhs", new TypeVariableReference("T1"));
        overloadBindings.addVariable("$rhs", new TypeVariableReference("T2"));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference("T1"));
        overloadBindings.addLowerRefBound("T1", new TypeVariableReference("T2"));
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", overloadBindings, asList(lhs, rhs));
        function.manuallySimplified(set("T1", "T2"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> T1 \\ T2 <: T1"));
    }

    //see also TINS-516 improve function signature with unions
    @Test
    public void
    getSignature_T1xT2ArrowT3AndIntLowerT1LowerNumAndT2LowerBoolAndT1OrT2LowerT3_ReturnsSignatureWithTypeParameters() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$lhs", new TypeVariableReference("T1"));
        overloadBindings.addVariable("$rhs", new TypeVariableReference("T2"));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference("T3"));
        overloadBindings.addLowerTypeBound("T1", intType);
        overloadBindings.addUpperTypeBound("T1", numType);
        overloadBindings.addUpperTypeBound("T2", boolType);
        overloadBindings.addLowerRefBound("T3", new TypeVariableReference("T1"));
        overloadBindings.addLowerRefBound("T3", new TypeVariableReference("T2"));
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", overloadBindings, asList(lhs, rhs));
        function.manuallySimplified(set("T1", "T2"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> (int | T1 | T2) \\ int <: T1 <: num, T2 <: bool"));
    }

    //see also TINS-516 improve function signature with unions
    @Test
    public void
    getSignature_T1xT2ArrowT3AndIntLowerT1LowerNumAndT2LowerBoolAndT2OrT1LowerT3_ReturnsSignatureWithTypeParameters() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$lhs", new TypeVariableReference("T1"));
        overloadBindings.addVariable("$rhs", new TypeVariableReference("T2"));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference("T3"));
        overloadBindings.addLowerTypeBound("T1", intType);
        overloadBindings.addUpperTypeBound("T1", numType);
        overloadBindings.addUpperTypeBound("T2", boolType);
        overloadBindings.addLowerRefBound("T3", new TypeVariableReference("T2"));
        overloadBindings.addLowerRefBound("T3", new TypeVariableReference("T1"));
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", overloadBindings, asList(lhs, rhs));
        function.manuallySimplified(set("T1", "T2"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> (int | T1 | T2) \\ int <: T1 <: num, T2 <: bool"));
    }

    @Test
    public void
    getSignature_WithHelperTypeParameterCorrespondsPlusAssign_ReturnsSignatureIncludingHelperTypeParameter() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        String tHelper = "T";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(tLhs));
        overloadBindings.addVariable("$rhs", new FixedTypeVariableReference(new TypeVariableReference(tRhs)));
        overloadBindings.addVariable("!help'", new TypeVariableReference(tHelper));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tLhs));
        IConvertibleTypeSymbol asT = symbolFactory.createConvertibleTypeSymbol();
        overloadBindings.bind(asT, Arrays.asList("T"));
        overloadBindings.addLowerRefBound(tLhs, new TypeVariableReference(tHelper));
        overloadBindings.addUpperTypeBound(tLhs, asT);
        overloadBindings.addUpperTypeBound(tRhs, asT);
        overloadBindings.addUpperTypeBound(tHelper, numType);

        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", overloadBindings, asList(lhs, rhs));
        function.manuallySimplified(set(tLhs, tHelper), 0, true);
        String result = function.getSignature();

        assertThat(result, is("Tlhs x {as T} -> Tlhs \\ T <: Tlhs <: {as T}, T <: num"));
    }

    //see TINS-514 function signature calculation NullPointer
    //see also TINS-516 improve function signature with unions
    @Test
    public void getSignature_TxAndTyAndReturnHasTxAndTyAsLowerRef_ReturnsSignatureAccordingly() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tLhs = "Tlhs";
        String tRhs = "Trhs";
        String tReturn = "Treturn";
        overloadBindings.addVariable("$lhs", new TypeVariableReference(tLhs));
        overloadBindings.addVariable("$rhs", new FixedTypeVariableReference(new TypeVariableReference(tRhs)));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));

        overloadBindings.addUpperTypeBound(tLhs, numType);
        overloadBindings.addUpperTypeBound(tRhs, stringType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tLhs));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tRhs));

        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", overloadBindings, asList(lhs, rhs));
        function.manuallySimplified(set(tLhs, tRhs), 0, false);
        String result = function.getSignature();

        assertThat(result, is("Tlhs x Trhs -> (Tlhs | Trhs) \\ Tlhs <: num, Trhs <: string"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_Identity_ReturnsIdentitySignature() {
        //corresponds: function foo($x){return $x;}
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tx = "V2";
        String tReturn = "V1";
        overloadBindings.addVariable("$x", new TypeVariableReference(tx));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        IVariable $x = new Variable("$x");

        IFunctionType function = createFunction("foo", overloadBindings, asList($x));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("T -> T"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_IdentityWithNumAsUpper_ReturnsIdentitySignatureWithConstraint() {
        //corresponds: function foo($x){return $x + 1;}
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tx = "V2";
        String tReturn = "V1";
        overloadBindings.addVariable("$x", new TypeVariableReference(tx));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, numType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        IVariable $x = new Variable("$x");

        IFunctionType function = createFunction("foo", overloadBindings, asList($x));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("T -> T \\ T <: num"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_PlusWithConvertible_UseOnlyT() {
        //corresponds: function foo($x, $y){return $x + $y;}
        //where {as T} x {as T} -> T \ T <: num is used for the + operator
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        String te1 = "V2";
        overloadBindings.addVariable("$x", new TypeVariableReference(tx));
        overloadBindings.addVariable("$y", new TypeVariableReference(ty));
        overloadBindings.addVariable("+@1|2", new TypeVariableReference(te1));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = symbolFactory.createConvertibleTypeSymbol();
        overloadBindings.bind(asTe1, Arrays.asList(te1));
        overloadBindings.addUpperTypeBound(tx, asTe1);
        overloadBindings.addUpperTypeBound(ty, asTe1);
        overloadBindings.addUpperTypeBound(te1, numType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", overloadBindings, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("{as T} x {as T} -> T \\ T <: num"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    @Test
    public void simplify_PlusWithConvertibleWithLowerBound_UseOnlyT() {
        //corresponds: function foo($x, $y){return $x + $y + 1;}
        //where {as T} x {as T} -> T \ T <: num is used for the first + operator
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        String te1 = "V2";
        overloadBindings.addVariable("$x", new TypeVariableReference(tx));
        overloadBindings.addVariable("$y", new TypeVariableReference(ty));
        overloadBindings.addVariable("+@1|2", new TypeVariableReference(te1));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = symbolFactory.createConvertibleTypeSymbol();
        overloadBindings.bind(asTe1, Arrays.asList(te1));
        overloadBindings.addUpperTypeBound(tx, asTe1);
        overloadBindings.addUpperTypeBound(ty, asTe1);
        overloadBindings.addLowerTypeBound(te1, intType);
        overloadBindings.addUpperTypeBound(te1, numType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", overloadBindings, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("{as T} x {as T} -> T \\ int <: T <: num"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    //see also TINS-517 param lower of other and both lower of return
    //see also TINS-516 improve function signature with unions
    @Test
    public void simplify_TxAndTyWhereTxLowerTyAndTyLowerTRtnAndIntLowerTRtn_TypeParametersReflectOrderOfParameters() {
        //corresponds: function foo($x, $y){if($y > 10){$y = $x; return $y;} return 1;}
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        overloadBindings.addVariable("$x", new TypeVariableReference(tx));
        overloadBindings.addVariable("$y", new TypeVariableReference(ty));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ty, new TypeVariableReference(tx));
        overloadBindings.addLowerTypeBound(tReturn, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", overloadBindings, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> (int | T2) \\ T1 <: T2"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    //see also TINS-516 improve function signature with unions
    @Test
    public void simplify_TxAndTyWhereTyLowerTxAndTyLowerTRtnAndIntLowerTRtn_TypeParametersReflectOrderOfParameters() {
        //corresponds: function foo($x, $y){if($y > 10){$x = $y; return $y;} return 1;}
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        overloadBindings.addVariable("$x", new TypeVariableReference(tx));
        overloadBindings.addVariable("$y", new TypeVariableReference(ty));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ty));
        overloadBindings.addLowerTypeBound(tReturn, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", overloadBindings, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("mixed x T -> (int | T)"));
    }

    //see TINS-403 rename TypeVariables to reflect order of parameters
    //see also TINS-517 param lower of other and both lower of return
    @Test
    public void simplify_TxAndTyWhereTyLowerTxAndTxLowerTRtnAndIntLowerTRtn_TypeParametersReflectOrderOfParameters() {
        //corresponds: function foo($x, $y){if($y > 10){$x = $y; return $x;} return 1;}
        IOverloadBindings overloadBindings = createOverloadBindings();
        String tx = "V3";
        String ty = "V4";
        String tReturn = "V1";
        overloadBindings.addVariable("$x", new TypeVariableReference(tx));
        overloadBindings.addVariable("$y", new TypeVariableReference(ty));
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ty));
        overloadBindings.addLowerTypeBound(tReturn, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        IVariable $x = new Variable("$x");
        IVariable $y = new Variable("$y");

        IFunctionType function = createFunction("foo", overloadBindings, asList($x, $y));
        function.simplify();
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> (int | T1) \\ T2 <: T1"));
    }

    @Test(expected = IllegalStateException.class)
    public void simplify_CalledTheSecondTime_ThrowsIllegalStateException() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable(TinsPHPConstants.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T")));
        overloadBindings.addLowerTypeBound("T", intType);
        overloadBindings.addUpperTypeBound("T", intType);

        IFunctionType function = createFunction("foo", overloadBindings, new ArrayList<IVariable>());
        function.simplify();
        function.simplify();

        //assert in annotation
    }

    private OverloadBindings createOverloadBindings() {
        ITypeHelper typeHelper = new TypeHelper();
        typeHelper.setMixedTypeSymbol(mixedType);
        ISymbolFactory symbolFactory = new SymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);
        symbolFactory.setMixedTypeSymbol(mixedType);
        return new OverloadBindings(symbolFactory, typeHelper);
    }


    protected IFunctionType createFunction(
            String theName, IOverloadBindings theOverloadBindings, List<IVariable> theParameterVariables) {
        return new FunctionType(theName, theOverloadBindings, theParameterVariables);
    }
}
