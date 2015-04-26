/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.TypeVariableNames;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.FunctionType;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.constraints.Variable;
import ch.tsphp.tinsphp.symbols.scopes.ScopeHelper;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FunctionTypeTest extends ATypeTest
{

    @Test
    public void getSignature_FixedNoParamsReturnsInt_ReturnsEmptyParamArrowInt() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable(TypeVariableNames.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T")));
        overloadBindings.addLowerTypeBound("T", intType);
        overloadBindings.addUpperTypeBound("T", intType);

        IFunctionType function = createFunction("foo", overloadBindings, new ArrayList<IVariable>());
        function.fix();
        String result = function.getSignature();

        assertThat(result, is("() -> int"));
    }

    @Test
    public void getSignature_IntArrowFloat_ReturnsSignatureWithParams() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$expr", new FixedTypeVariableReference(new TypeVariableReference("T1")));
        overloadBindings.addVariable(TypeVariableNames.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T2")));
        overloadBindings.addLowerTypeBound("T1", intType);
        overloadBindings.addUpperTypeBound("T1", intType);
        overloadBindings.addLowerTypeBound("T2", floatType);
        overloadBindings.addUpperTypeBound("T2", floatType);
        IVariable expr = new Variable("$expr");

        IFunctionType function = createFunction("foo", overloadBindings, asList(expr));
        function.fix();
        String result = function.getSignature();

        assertThat(result, is("int -> float"));
    }

    @Test
    public void
    getSignature_T1xT2ArrowT3AndT1IsIntOrFloatAndT2LowerIBAndIAAndT3IsString_ReturnsSignatureWithTypeParameters() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$lhs", new FixedTypeVariableReference(new TypeVariableReference("T1")));
        overloadBindings.addVariable("$rhs", new TypeVariableReference("T2"));
        overloadBindings.addVariable(TypeVariableNames.RETURN_VARIABLE_NAME,
                new FixedTypeVariableReference(new TypeVariableReference("T3")));
        IUnionTypeSymbol intOrFloat = new UnionTypeSymbol(new OverloadResolver());
        intOrFloat.addTypeSymbol(intType);
        intOrFloat.addTypeSymbol(floatType);
        IIntersectionTypeSymbol iBAndIA = new IntersectionTypeSymbol(new OverloadResolver());
        iBAndIA.addTypeSymbol(interfaceAType);
        iBAndIA.addTypeSymbol(interfaceBType);
        overloadBindings.addLowerTypeBound("T1", intOrFloat);
        overloadBindings.addUpperTypeBound("T2", iBAndIA);
        overloadBindings.addLowerTypeBound("T3", stringType);
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", overloadBindings, asList(lhs, rhs));
        function.fix();
        String result = function.getSignature();

        assertThat(result, is("(float | int) x T2 -> string \\ T2 < (IA & IB)"));
    }

    @Test
    public void
    getSignature_T1xT2ArrowT3AndIntLowerT1LowerNumAndT2LowerBoolAndT1OrT2LowerT3_ReturnsSignatureWithTypeParameters() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$lhs", new TypeVariableReference("T1"));
        overloadBindings.addVariable("$rhs", new TypeVariableReference("T2"));
        overloadBindings.addVariable(TypeVariableNames.RETURN_VARIABLE_NAME, new TypeVariableReference("T3"));
        overloadBindings.addLowerTypeBound("T1", intType);
        overloadBindings.addUpperTypeBound("T1", numType);
        overloadBindings.addUpperTypeBound("T2", boolType);
        overloadBindings.addLowerRefBound("T3", new TypeVariableReference("T1"));
        overloadBindings.addLowerRefBound("T3", new TypeVariableReference("T2"));
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", overloadBindings, asList(lhs, rhs));
        function.fix();
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> T3 \\ int < T1 < num, T2 < bool, (int | T1 | T2) < T3"));
    }

    @Test
    public void
    getSignature_T1xT2ArrowT3AndIntLowerT1LowerNumAndT2LowerBoolAndT2OrT1LowerT3_ReturnsSignatureWithTypeParameters() {
        IOverloadBindings overloadBindings = createOverloadBindings();
        overloadBindings.addVariable("$lhs", new TypeVariableReference("T1"));
        overloadBindings.addVariable("$rhs", new TypeVariableReference("T2"));
        overloadBindings.addVariable(TypeVariableNames.RETURN_VARIABLE_NAME, new TypeVariableReference("T3"));
        overloadBindings.addLowerTypeBound("T1", intType);
        overloadBindings.addUpperTypeBound("T1", numType);
        overloadBindings.addUpperTypeBound("T2", boolType);
        overloadBindings.addLowerRefBound("T3", new TypeVariableReference("T2"));
        overloadBindings.addLowerRefBound("T3", new TypeVariableReference("T1"));
        IVariable lhs = new Variable("$lhs");
        IVariable rhs = new Variable("$rhs");

        IFunctionType function = createFunction("foo", overloadBindings, asList(lhs, rhs));
        function.fix();
        String result = function.getSignature();

        assertThat(result, is("T1 x T2 -> T3 \\ int < T1 < num, T2 < bool, (int | T1 | T2) < T3"));
    }

    private OverloadBindings createOverloadBindings() {
        IOverloadResolver overloadResolver = new OverloadResolver();
        overloadResolver.setMixedTypeSymbol(mixedType);
        ISymbolFactory symbolFactory = new SymbolFactory(new ScopeHelper(), new ModifierHelper(), overloadResolver);
        return new OverloadBindings(symbolFactory, overloadResolver);
    }


    protected IFunctionType createFunction(
            String theName, IOverloadBindings theOverloadBindings, List<IVariable> theParameterVariables) {
        return new FunctionType(theName, theOverloadBindings, theParameterVariables);
    }
}
