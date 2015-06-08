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
        function.manuallySimplified(set("T1", "T2", "T3"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> T3 \\ int <: T1 <: num, T2 <: bool, (int | T1 | T2) <: T3"));
    }

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
        function.manuallySimplified(set("T1", "T2", "T3"), 0, false);
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> T3 \\ int <: T1 <: num, T2 <: bool, (int | T1 | T2) <: T3"));
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
        return new OverloadBindings(symbolFactory, typeHelper);
    }


    protected IFunctionType createFunction(
            String theName, IOverloadBindings theOverloadBindings, List<IVariable> theParameterVariables) {
        return new FunctionType(theName, theOverloadBindings, theParameterVariables);
    }
}
