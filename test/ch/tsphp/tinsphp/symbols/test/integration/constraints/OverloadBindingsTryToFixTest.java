/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.FixedTypeVariableReference;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static ch.tsphp.tinsphp.common.TinsPHPConstants.RETURN_VARIABLE_NAME;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.OverloadBindingsMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

public class OverloadBindingsTryToFixTest extends ATypeHelperTest
{
    @Test
    public void tryToFix_NoParams_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = 2.2; return true;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        overloadBindings.addVariable("$a", new TypeVariableReference(ta));
        overloadBindings.addVariable("$b", new TypeVariableReference(tb));
        overloadBindings.addVariable(
                RETURN_VARIABLE_NAME, new FixedTypeVariableReference(new TypeVariableReference(tReturn)));
        overloadBindings.addLowerTypeBound(ta, intType);
        overloadBindings.addLowerTypeBound(tb, floatType);
        overloadBindings.addLowerTypeBound(tReturn, boolType);
        overloadBindings.addUpperTypeBound(tReturn, boolType);

        //act
        overloadBindings.tryToFix(new HashSet<String>());

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$a", ta, asList("int"), asList("int"), true),
                varBinding("$b", tb, asList("float"), asList("float"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("bool"), asList("bool"), true)
        ));
    }

    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaIndirection_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = 2.2; return $b;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        overloadBindings.addVariable("$a", new TypeVariableReference(ta));
        overloadBindings.addVariable("$b", new TypeVariableReference(tb));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(ta, intType);
        overloadBindings.addLowerTypeBound(tb, floatType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$a", ta, asList("int"), asList("int"), true),
                varBinding("$b", tb, asList("float"), asList("float"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("float"), asList("float"), true)
        ));
    }

    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaDoubleIndirection_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = $a; return $b;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        overloadBindings.addVariable("$a", new TypeVariableReference(ta));
        overloadBindings.addVariable("$b", new TypeVariableReference(tb));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(ta, intType);
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$a", ta, asList("int"), asList("int"), true),
                varBinding("$b", tb, asList("int"), asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaMultipleIndirection_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = $a; $c = $b; $d = $c; return $d;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tc = "Tc";
        String td = "Td";
        String tReturn = "Treturn";

        overloadBindings.addVariable("$a", new TypeVariableReference(ta));
        overloadBindings.addVariable("$b", new TypeVariableReference(tb));
        overloadBindings.addVariable("$c", new TypeVariableReference(tc));
        overloadBindings.addVariable("$d", new TypeVariableReference(td));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(ta, intType);
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(tc, new TypeVariableReference(tb));
        overloadBindings.addLowerRefBound(td, new TypeVariableReference(tc));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(td));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding("$a", ta, asList("int"), asList("int"), true),
                varBinding("$b", tb, asList("int"), asList("int"), true),
                varBinding("$c", tc, asList("int"), asList("int"), true),
                varBinding("$d", td, asList("int"), asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }


    //see TINS-449 unused ad-hoc polymorphic parameters
    @Test
    public void tryToFix_UnusedAdHocParamsAssignedToLocalConstantReturn_LocalHasNotMixedAsType() {
        //corresponds: function foo($x, $y){ $a = $x + $y; return $1;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String t1 = "T1";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable($x, new TypeVariableReference(t1));
        overloadBindings.addVariable($y, new TypeVariableReference(t1));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(t1));
        overloadBindings.addUpperTypeBound(t1, numType);
        overloadBindings.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(t1);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, t1, asList("num"), asList("num"), true),
                varBinding($y, t1, asList("num"), asList("num"), true),
                varBinding($a, ta, asList("num"), asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }


    @Test
    public void tryToFix_YIsLowerRefOfXAndConstantReturn_AllVariablesAreFixed() {
        //corresponds: function foo($x, $y){ $x + 1; $x = $y; return 1;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange

        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, intType);
        overloadBindings.addUpperTypeBound(tx, numType);
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ty));
        overloadBindings.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("num"), asList("num"), true),
                varBinding($y, ty, asList("num"), asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    @Test
    public void tryToFix_YIsLowerRefOfXAndXIsReturned_ReturnIsTxAndHasTyAsLowerRef() {
        //corresponds: function foo($x, $y){ $x + 1; $x = $y; return $x;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, intType);
        overloadBindings.addUpperTypeBound(tx, numType);
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), asList("num"), false),
                varBinding($y, ty, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), asList("num"), false)
        ));
    }

    @Test
    public void tryToFix_YIsLowerRefOfBAndBIsLowerRefOfXAndXIsReturned_ReturnIsTxAndHasTyAsLowerRefAndBIsTy() {
        //corresponds: function foo($x, $y){ $x + 1; $b = $y; $x = $b; return $x;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $b = "$b";
        String tx = "Tx";
        String ty = "Ty";
        String tb = "Tb";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($b, new TypeVariableReference(tb));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, intType);
        overloadBindings.addUpperTypeBound(tx, numType);
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(tb));
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), asList("num"), false),
                varBinding($y, ty, null, asList("num"), false),
                varBinding($b, ty, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), asList("num"), false)
        ));
    }

    @Test
    public void tryToFix_YIsLowerALowerXLowerBAndBIsReturned_ReturnIsTxAndHasTyAsLowerRefAndAIsTyAndBIsTx() {
        //corresponds: function foo($x, $y){ $a = $y; $x = $a; $b = $x; return $x;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String $b = "$b";
        String tx = "Tx";
        String ty = "Ty";
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable($b, new TypeVariableReference(tb));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("@" + ty), null, false),
                varBinding($y, ty, null, null, false),
                varBinding($a, ty, null, null, false),
                varBinding($b, tx, asList("@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_YIsLowALowXLowBLowRtnAndAHasDiffLowTypeThanY_TyLowTxAndRtnIsTxAndTyLowTaAndBIsTx() {
        //corresponds: function foo($x, $y){$a = 1; $a = $y; $x = $a; $b = $x; return $x;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String $b = "$b";
        String tx = "Tx";
        String ty = "Ty";
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable($b, new TypeVariableReference(tb));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(ta, intType);
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), null, false),
                varBinding($y, ty, null, null, false),
                varBinding($a, ta, asList("int", "@" + ty), null, false),
                varBinding($b, tx, asList("int", "@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_YIsLowALowCLowXLowBLowRtnAndAHasDiffLowBoundThanY_TyLowTxTcTaAndRtnIsTxAndBIsTx() {
        //corresponds: function foo($x, $y){$a = 1; $a = $y; $c = $a; $x = $c $b = $x; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String $b = "$b";
        String $c = "$c";
        String tx = "Tx";
        String ty = "Ty";
        String ta = "Ta";
        String tb = "Tb";
        String tc = "Tc";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable($b, new TypeVariableReference(tb));
        overloadBindings.addVariable($c, new TypeVariableReference(tc));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(ta, intType);
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tc, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(tc));
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), null, false),
                varBinding($y, ty, null, null, false),
                varBinding($a, ta, asList("int", "@" + ty), null, false),
                varBinding($b, tx, asList("int", "@" + ty), null, false),
                varBinding($c, tc, asList("int", "@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_YIsLowALowCLowXLowBLowRtnAndCHasDiffLowBoundThanY_TyLowTxTcAndRtnIsTxAndBIsTxAndAIsTy() {
        //corresponds: function foo($x, $y){$c = 1; $a = $y; $c = $a; $x = $c; $b = $x; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String $b = "$b";
        String $c = "$c";
        String tx = "Tx";
        String ty = "Ty";
        String ta = "Ta";
        String tb = "Tb";
        String tc = "Tc";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable($b, new TypeVariableReference(tb));
        overloadBindings.addVariable($c, new TypeVariableReference(tc));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tc, intType);
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tc, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(tc));
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), null, false),
                varBinding($y, ty, null, null, false),
                varBinding($a, ty, null, null, false),
                varBinding($b, tx, asList("int", "@" + ty), null, false),
                varBinding($c, tc, asList("int", "@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), null, false)
        ));
    }


    @Test
    public void
    tryToFix_CircularRefWithParamAndOtherParamIsLower_Unifies() {
        //corresponds: function foo($x, $y){$a = 1; $a = $y; $a = $x; $x = $a; $b = $x; return $x;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String $b = "$b";
        String tx = "Tx";
        String ty = "Ty";
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable($b, new TypeVariableReference(tb));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(ta, intType);
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), null, false),
                varBinding($y, ty, null, null, false),
                varBinding($a, ta, asList("int", "@" + ty), null, false),
                varBinding($b, tx, asList("int", "@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_HasTwoParamsDoesNothing_AllAreConstant() {
        //corresponds: function foo($x, $y){ return 1;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("mixed"), asList("mixed"), true),
                varBinding($y, ty, asList("mixed"), asList("mixed"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerConstantReturn_AllAreConstant() {
        //corresponds: function foo($x){ $a = $x; return 1;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tx));
        overloadBindings.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("mixed"), asList("mixed"), true),
                varBinding($a, ta, asList("mixed"), asList("mixed"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerAndParamReturned_VariableUnifiesWithParam() {
        //corresponds: function foo($x){ $a = $x; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding($a, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerAndSameLowerTypeAndParamReturned_UnifiesWithParameter() {
        //corresponds: function foo($x){ $a = 1; $x = 1; $a = $x; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tx));
        overloadBindings.addLowerTypeBound(tx, intType);
        overloadBindings.addLowerTypeBound(ta, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($a, tx, asList("int"), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }


    @Test
    public void tryToFix_VariableHasParamAsLowerAndDifferentLowerTypeAndParamReturned_DoesNotUnifyWithParameter() {
        //corresponds: function foo($x){ $a = 1.3; $x = 1; $a = $x; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tx));
        overloadBindings.addLowerTypeBound(tx, intType);
        overloadBindings.addLowerTypeBound(ta, floatType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($a, ta, asList("float", "int", "@" + tx), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }

    @Test
    public void tryToFix_ParamXAndYAndBothAreReturned_ReturnHasBothAsLowerRef() {
        //corresponds: function foo($x, $y){ if($x){return $x;} return $y;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, boolType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, null, asList("bool"), false),
                varBinding($y, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@" + tx, "@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_SemiConstantReturn_ReturnHasTypeAndParamAsLowerBound() {
        //corresponds: function foo($x, $y){ if($x){return 1;} return $y;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, boolType);
        overloadBindings.addLowerTypeBound(tReturn, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("bool"), asList("bool"), true),
                varBinding($y, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "@" + ty), null, false)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    @Test
    public void tryToFix_SameLowerAndUpperTypeBound_FixIt() {
        //corresponds: function foo($x){ $x = 'h'; expectsString($x); return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, stringType);
        overloadBindings.addUpperTypeBound(tx, stringType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), asList("string"), true)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    //see also TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_SameLowerAndUpperTypeBoundWithUpperRefToOtherParamWhichHasSameUpper_FixOnlyOne() {
        //corresponds:
        // function foo($x, $y){
        //   $x = $y; $x = 'h'; expectsString($x); expectsString($y); return $x; return $y;
        // }
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, stringType);
        overloadBindings.addUpperTypeBound(tx, stringType);
        overloadBindings.addUpperTypeBound(ty, stringType);
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                //see TINS-500 multiple return and same lower as param upper - why Ty is fixed
                varBinding($y, ty, asList("string"), asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), asList("string"), true)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    @Test
    public void tryToFix_SameLowerAndUpperTypeBoundWithLowerRefToOtherParamWhichHasSameUpper_FixBoth() {
        //corresponds:
        // function foo($x, $y){
        //   $y = $x; $x = 'h'; expectsString($x); expectsString($y); return $x; return $y;
        // }
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, stringType);
        overloadBindings.addUpperTypeBound(tx, stringType);
        overloadBindings.addUpperTypeBound(ty, stringType);
        overloadBindings.addLowerRefBound(ty, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                varBinding($y, ty, asList("string"), asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), asList("string"), true)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    @Test
    public void tryToFix_SameLowerAndUpperTypeBoundWithLowerRefToOtherParamWhichHasSameUpper2_FixBoth() {
        //corresponds:
        // function foo($x, $y){
        //   $x = $y; $y = 'h'; expectsString($x); expectsString($y); return $x; return $y;
        // }
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(ty, stringType);
        overloadBindings.addUpperTypeBound(ty, stringType);
        overloadBindings.addUpperTypeBound(tx, stringType);
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                varBinding($y, ty, asList("string"), asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), asList("string"), true)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    @Test
    public void tryToFix_SameLowerAndUpperTypeBoundWithLowerRefToOtherParamWhichHasSameUpperViaLocal_FixBoth() {
        //corresponds:
        // function foo($x, $y){
        //   $y = $a; $a = $x; $x = 'h'; expectsString($x); expectsString($y); return $x; return $y;
        // }
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String tx = "Tx";
        String ty = "Ty";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, stringType);
        overloadBindings.addUpperTypeBound(tx, stringType);
        overloadBindings.addUpperTypeBound(ty, stringType);
        overloadBindings.addLowerRefBound(ty, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                varBinding($y, ty, asList("string"), asList("string"), true),
                varBinding($a, ta, asList("string"), asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), asList("string"), true)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    @Test
    public void tryToFix_SameLowerAndUpperTypeBoundWithLowerRefToOtherParamWhichHasDifferentUpper_FixOnlyOne() {
        //corresponds: function foo($x, $y){ $y = $x; $x = 'h'; expectsString($x); return $x; return $y; }
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, stringType);
        overloadBindings.addUpperTypeBound(tx, stringType);
        overloadBindings.addLowerRefBound(ty, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                varBinding($y, ty, asList("string"), null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, asList("string"), null, false)
        ));
    }

    @Test
    public void tryToFix_Recursion_DoesNotHaveSelfReference() {
        //corresponds: function endless($x){ $x = endless($x); return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(tReturn));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithLocalVariableIndirection_DoesNotHaveSelfReference() {
        //corresponds: function endless($x){ $a = endless($x); $x = $a; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $a = "$a";
        String ta = "Ta";

        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ta));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tReturn));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding($a, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithParamIndirection_DoesNotHaveCyclicRef() {
        //corresponds: function endless($x, $y){ $y = endless($x,$y); $x = $y; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";

        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, ty, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithDoubleParamIndirection_DoesNotHaveCyclicRef() {
        //corresponds: function endless($x, $y, $z){ $y = endless($x,$y,$z); $z = $y; $x = $z; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($z, new TypeVariableReference(tz));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tz, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(tz));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, ty, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding($z, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithDoubleParamIndirectionAndTwoReturn_DoesNotHaveCyclicRef() {
        //corresponds: function foo($x, $y, $z){ if($x > 0){ $y = foo($x -1,$y,$z); $z = $y; return $z;} return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($z, new TypeVariableReference(tz));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerTypeBound(tx, intType);
        overloadBindings.addUpperTypeBound(tx, numType);
        overloadBindings.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tz, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("num"), false),
                varBinding($y, ty, asList("int", "@Tx"), null, false),
                varBinding($z, ty, asList("int", "@Tx"), null, false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "@Tx", "@Ty"), null, false)
        ));
    }

    @Test
    public void tryToFix_TwoRecursions_DoesNotHaveCyclicRef() {
        //corresponds: function foo($x, $y, $z){ $y = foo(...); $x = foo(...); $z = $y; $z = $x; return $z;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($z, new TypeVariableReference(tz));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tx, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tz, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tz, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, ty, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding($z, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, null, null, false)
        ));
    }

    //see TINS-463 multiple return and ConcurrentModificationException
    @Test
    public void tryToFix_ParametricReturnDueToParametricFunctionCall_NoConcurrentModificationException() {
        //corresponds: function foo($x){ return bar($x); return bar($x);} function bar($x){return $x;}
        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String fCallBar1 = "bar()@2|1";
        String tBar1 = "Tb";
        String fCallBar2 = "bar()@2|4";
        String tBar2 = "Tb";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        overloadBindings.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));
        overloadBindings.addLowerRefBound(tBar1, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tBar2, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding(fCallBar1, tx, null, null, false),
                varBinding(fCallBar2, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    //see TINS-463 multiple return and ConcurrentModificationException
    @Test
    public void tryToFix_TwoParametricReturnDueToParametricFunctionCall_NoConcurrentModificationException() {
        //corresponds:
        //  function foo($x, $y, $z){ if($x){return bar($y, z);} return bar($y, $z);}
        //  function bar($x, $y){ if($x){return $x;} return $y;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String fCallBar1 = "bar()@2|1";
        String tBar1 = "Tb1";
        String fCallBar2 = "bar()@2|4";
        String tBar2 = "Tb2";
        String tReturn = "Treturn";


        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($z, new TypeVariableReference(tz));
        overloadBindings.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        overloadBindings.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, boolType);
        overloadBindings.addUpperTypeBound(ty, boolType);
        overloadBindings.addLowerRefBound(tBar1, new TypeVariableReference(tz));
        overloadBindings.addLowerRefBound(tBar1, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        overloadBindings.addLowerRefBound(tBar2, new TypeVariableReference(tz));
        overloadBindings.addLowerRefBound(tBar2, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("bool"), asList("bool"), true),
                varBinding($y, ty, null, asList("bool"), false),
                varBinding($z, tz, null, null, false),
                varBinding(fCallBar1, tBar1, asList("@Ty", "@Tz"), null, false),
                varBinding(fCallBar2, tBar2, asList("@Ty", "@Tz"), null, false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@Ty", "@Tz"), null, false)
        ));
    }

    //see TINS-463 multiple return and ConcurrentModificationException
    @Test
    public void tryToFix_ThreeParametricReturnDueToParametricFunctionCall_NoConcurrentModificationException() {
        //corresponds:
        //  function foo($x,$y,$z){
        //     if($x > 10){ return bar($y, z); } else if($x <= 10){ return bar($y, $z); }
        //     return bar($y,$z);
        //  }
        //  function bar($x, $y){ if($x){return $x;} return $y;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String fCallBar1 = "bar()@2|1";
        String tBar1 = "Tb1";
        String fCallBar2 = "bar()@2|4";
        String tBar2 = "Tb2";
        String fCallBar3 = "bar()@2|6";
        String tBar3 = "Tb3";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($z, new TypeVariableReference(tz));
        overloadBindings.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        overloadBindings.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        overloadBindings.addVariable(fCallBar3, new TypeVariableReference(tBar3));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, boolType);
        overloadBindings.addUpperTypeBound(ty, boolType);
        overloadBindings.addLowerRefBound(tBar1, new TypeVariableReference(tz));
        overloadBindings.addLowerRefBound(tBar1, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        overloadBindings.addLowerRefBound(tBar2, new TypeVariableReference(tz));
        overloadBindings.addLowerRefBound(tBar2, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));
        overloadBindings.addLowerRefBound(tBar3, new TypeVariableReference(tz));
        overloadBindings.addLowerRefBound(tBar3, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tBar3));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("bool"), asList("bool"), true),
                varBinding($y, ty, null, asList("bool"), false),
                varBinding($z, tz, null, null, false),
                varBinding(fCallBar1, tBar1, asList("@Ty", "@Tz"), null, false),
                varBinding(fCallBar2, tBar2, asList("@Ty", "@Tz"), null, false),
                varBinding(fCallBar3, tBar3, asList("@Ty", "@Tz"), null, false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@Ty", "@Tz"), null, false)
        ));
    }

    //see TINS-463 multiple return and ConcurrentModificationException
    @Test
    public void tryToFix_LocalVariableWithMultipleLowerRefs_NoConcurrentModificationException() {
        //corresponds:
        //  function foo($x, $y){ $a = $b; $a = $c; $b = $x; $c = $y; $b = $y; $c = $x; return $a;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $a = "$a";
        String ta = "Ta";
        String $b = "$b";
        String tb = "Tb";
        String $c = "$c";
        String tc = "Tc";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable($b, new TypeVariableReference(tb));
        overloadBindings.addVariable($c, new TypeVariableReference(tc));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tb));
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tc));
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tb, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tc, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tc, new TypeVariableReference(ty));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ta));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding($a, ta, asList("@Tx", "@Ty"), null, false),
                varBinding($b, tb, asList("@Tx", "@Ty"), null, false),
                varBinding($c, tc, asList("@Tx", "@Ty"), null, false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@Tx", "@Ty"), null, false)
        ));
    }

    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithConvertible_ReturnIsNotFixed() {
        //corresponds:
        //  function foo($x, $y){ return $x + $y;}
        // with + overload {as T} x {as T} -> T

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String plus = "+@1|2";
        String tPlus = "Tplus";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(plus, new TypeVariableReference(tPlus));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        overloadBindings.bind(asTPlus, asList(tPlus));
        overloadBindings.addUpperTypeBound(tx, asTPlus);
        overloadBindings.addUpperTypeBound(ty, asTPlus);
        overloadBindings.addUpperTypeBound(tPlus, numType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("{as " + tPlus + "}"), asList("{as " + tPlus + "}"), true),
                varBinding($y, ty, asList("{as " + tPlus + "}"), asList("{as " + tPlus + "}"), true),
                varBinding(plus, tPlus, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, tPlus, null, asList("num"), false)
        ));
    }

    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithConvertibleViaLocal_ReturnIsNotFixed() {
        //corresponds:
        //  function foo($x, $y){ $a = $x + $y; return $a;}
        // with + overload {as T} x {as T} -> T

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String plus = "+@1|2";
        String tPlus = "Tplus";
        String $a = "$a";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(plus, new TypeVariableReference(tPlus));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        overloadBindings.bind(asTPlus, asList(tPlus));
        overloadBindings.addUpperTypeBound(tx, asTPlus);
        overloadBindings.addUpperTypeBound(ty, asTPlus);
        overloadBindings.addUpperTypeBound(tPlus, numType);
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tPlus));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ta));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("{as " + tPlus + "}"), asList("{as " + tPlus + "}"), true),
                varBinding($y, ty, asList("{as " + tPlus + "}"), asList("{as " + tPlus + "}"), true),
                varBinding(plus, tPlus, null, asList("num"), false),
                varBinding($a, tPlus, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, tPlus, null, asList("num"), false)
        ));
    }

    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithConvertibleAndReturnTx_ReturnIsNotFixedIsTPlusAndTx() {
        //corresponds:
        //  function foo($x, $y, $z){ if($z){ return $x + $y;} return $x;}
        // with + overload {as T} x {as T} -> T

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String plus = "+@1|2";
        String tPlus = "Tplus";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($z, new TypeVariableReference(tz));
        overloadBindings.addVariable(plus, new TypeVariableReference(tPlus));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        overloadBindings.bind(asTPlus, asList(tPlus));
        overloadBindings.addUpperTypeBound(tz, boolType);
        overloadBindings.addUpperTypeBound(tx, asTPlus);
        overloadBindings.addUpperTypeBound(ty, asTPlus);
        overloadBindings.addUpperTypeBound(tPlus, numType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, null, asList("{as " + tPlus + "}"), false),
                varBinding($y, ty, asList("{as " + tPlus + "}"), asList("{as " + tPlus + "}"), true),
                varBinding($z, tz, asList("bool"), asList("bool"), true),
                varBinding(plus, tPlus, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@" + tPlus, "@" + tx), null, false)
        ));
    }

    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithConvertibleAndReturnTxAndTy_ReturnIsNotFixedIsTPlusAndTxAndTy() {
        //corresponds:
        //  function foo($x, $y, $z){ if($z > 10){ return $x + $y;} else if ($z <10){return $x;} return $y; }
        // with + overload {as T} x {as T} -> T

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String plus = "+@1|2";
        String tPlus = "Tplus";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable($z, new TypeVariableReference(tz));
        overloadBindings.addVariable(plus, new TypeVariableReference(tPlus));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        overloadBindings.bind(asTPlus, asList(tPlus));
        overloadBindings.addUpperTypeBound(tz, mixedType);
        overloadBindings.addUpperTypeBound(tx, asTPlus);
        overloadBindings.addUpperTypeBound(ty, asTPlus);
        overloadBindings.addUpperTypeBound(tPlus, numType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, null, asList("{as " + tPlus + "}"), false),
                varBinding($y, ty, null, asList("{as " + tPlus + "}"), false),
                varBinding($z, tz, asList("mixed"), asList("mixed"), true),
                varBinding(plus, tPlus, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@" + tPlus, "@" + tx, "@" + ty), null, false)
        ));
    }

    //see TINS-485 fixing functions with convertible types
    //see TINS-487 convertible type which points to fixed parameter can be fixed as well
    @Test
    public void tryToFix_PlusWithConvertibleWithoutReturn_AllAreFixed() {
        //corresponds:
        //  function foo($x, $y){ $a = $x + $y; return false;}
        // with overload {as T} x {as T} -> T

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String plus = "+@1|2";
        String tPlus = "Tplus";
        String $a = "$a";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(plus, new TypeVariableReference(tPlus));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        overloadBindings.bind(asTPlus, asList(tPlus));
        overloadBindings.addUpperTypeBound(tx, asTPlus);
        overloadBindings.addUpperTypeBound(ty, asTPlus);
        overloadBindings.addUpperTypeBound(tPlus, numType);
        overloadBindings.addLowerRefBound(ta, new TypeVariableReference(tPlus));
        overloadBindings.addLowerTypeBound(tReturn, boolType);

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("{as num}"), asList("{as num}"), true),
                varBinding($y, ty, asList("{as num}"), asList("{as num}"), true),
                varBinding(plus, tPlus, asList("num"), asList("num"), true),
                varBinding($a, ta, asList("num"), asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("bool"), asList("bool"), true)
        ));
    }

    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithConvertibleCombinedWithPlusWithTwoConvertible_ReturnStaysParametric() {
        //corresponds:
        //  function foo($x, $y, $z){ return $x + $y + $z; }
        // where:
        //   T x {as T} -> T \ T <: num
        // was applied for e1 = $x + $y and
        //   {as T} x {as T} -> T \ T <: num
        // was applied for  e2 = e1 + $z
        // resulting overload should be: (float | int) x {as (float | int)} x (float | int) -> (float | int)


        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String e1 = "+@1|2";
        String e2 = "+@1|4";
        String te2 = "Te2";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(e1, new TypeVariableReference(tx));
        overloadBindings.addVariable($z, new TypeVariableReference(tz));
        overloadBindings.addVariable(e2, new TypeVariableReference(te2));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTx = createConvertibleType();
        overloadBindings.bind(asTx, asList(tx));
        overloadBindings.addUpperTypeBound(tx, numType);
        overloadBindings.addUpperTypeBound(ty, asTx);
        IConvertibleTypeSymbol asTe2 = createConvertibleType();
        overloadBindings.bind(asTe2, asList(te2));
        overloadBindings.addUpperTypeBound(te2, numType);
        overloadBindings.addUpperTypeBound(tx, asTe2);
        overloadBindings.addUpperTypeBound(tz, asTe2);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(te2));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("num"), asList("num"), true),
                varBinding($y, ty, asList("{as num}"), asList("{as num}"), true),
                varBinding(e1, tx, asList("num"), asList("num"), true),
                varBinding($z, tz, asList("{as Te2}"), asList("{as Te2}"), true),
                varBinding(e2, te2, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, te2, null, asList("num"), false)
        ));
    }


    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithConvertibleWithoutReturn_AllFixed() {
        //corresponds:
        //  function foo($x){ $x + true; return 1; }
        // where:
        //   {as T} x {as T} -> T \ T <: num
        // was applied for e1 = $x + true


        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String e1 = "+@1|4";
        String te1 = "Te1";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable(e1, new TypeVariableReference(te1));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = createConvertibleType();
        overloadBindings.bind(asTe1, asList(te1));
        overloadBindings.addUpperTypeBound(tx, asTe1);
        overloadBindings.addUpperTypeBound(te1, numType);
        overloadBindings.addLowerTypeBound(tReturn, intType);

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("{as num}"), asList("{as num}"), true),
                varBinding(e1, te1, asList("num"), asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithTwoConvertibleWithoutReturn_AllFixed() {
        //corresponds:
        //  function foo($x, $y){ $x + true; $y + null; return 1; }
        // where:
        //   {as T} x {as T} -> T \ T <: num
        // was applied for e1 = $x + true and e2 = $y + null;


        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String e1 = "+@1|4";
        String te1 = "Te1";
        String e2 = "+@1|10";
        String te2 = "Te2";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(e1, new TypeVariableReference(te1));
        overloadBindings.addVariable(e2, new TypeVariableReference(te2));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = createConvertibleType();
        overloadBindings.bind(asTe1, asList(te1));
        overloadBindings.addUpperTypeBound(tx, asTe1);
        IConvertibleTypeSymbol asTe2 = createConvertibleType();
        overloadBindings.bind(asTe2, asList(te2));
        overloadBindings.addUpperTypeBound(ty, asTe2);
        overloadBindings.addUpperTypeBound(te1, numType);
        overloadBindings.addUpperTypeBound(te2, numType);
        overloadBindings.addLowerTypeBound(tReturn, intType);

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("{as num}"), asList("{as num}"), true),
                varBinding($y, ty, asList("{as num}"), asList("{as num}"), true),
                varBinding(e1, te1, asList("num"), asList("num"), true),
                varBinding(e2, te2, asList("num"), asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    //see TINS-495 NullPointer when fixing function
    @Test
    public void tryToFix_LocalWithMultipleUpperRefIsLowerOfReturn1_DoesNotCauseNullPointer() {
        //corresponds:
        //  function bar($x){ if($x > 0){return foo(true, $x-1);} return $x;}
        // where foo has the following overload:
        //   bool x T -> (falseType | T)
        // and int x int -> int was taken for e1 = $x - 1
        // hence the return type of the function call foo is (falseType | int | Te1)

        //same as proceedings method but this time te1 is T1 and tReturn T3 (in one of the methods te1 should be
        // inspected first for renaming)


        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String e1 = "+@1|4";
        String te1 = "T1";
        String localReturn = "return@1|2";
        String tLocalReturn = "T2";
        String tReturn = "T3";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable(e1, new TypeVariableReference(te1));
        overloadBindings.addVariable(localReturn, new TypeVariableReference(tLocalReturn));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, intType);
        overloadBindings.addLowerTypeBound(te1, intType);
        overloadBindings.addLowerTypeBound(tReturn, boolType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        overloadBindings.addLowerRefBound(tLocalReturn, new TypeVariableReference(te1));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("int"), true),
                varBinding(e1, te1, asList("int"), asList("int"), true),
                varBinding(localReturn, tLocalReturn, asList("int"), asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "bool"), asList("(bool | int)"), true)
        ));
    }

    //see TINS-495 NullPointer when fixing function
    //see also TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_LocalWithMultipleUpperRefIsLowerOfReturn2_DoesNotCauseNullPointer() {
        //corresponds:
        //  function bar($x){ if($x > 0){return foo(true, $x-1);} return $x;}
        // where foo has the following overload:
        //   bool x T -> (falseType | T)
        // and int x int -> int was taken for e1 = $x - 1
        // hence the return type of the function call foo is (falseType | int | Te1)

        //same as previous method but this time te1 is T3 and tReturn T1 (in one of the methods te1 should be
        // inspected first for renaming)

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String e1 = "+@1|4";
        String te1 = "T3";
        String localReturn = "return@1|2";
        String tLocalReturn = "T2";
        String tReturn = "T1";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable(e1, new TypeVariableReference(te1));
        overloadBindings.addVariable(localReturn, new TypeVariableReference(tLocalReturn));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, intType);
        overloadBindings.addLowerTypeBound(te1, intType);
        overloadBindings.addLowerTypeBound(tReturn, boolType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        overloadBindings.addLowerRefBound(tLocalReturn, new TypeVariableReference(te1));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("int"), true),
                varBinding(e1, te1, asList("int"), asList("int"), true),
                varBinding(localReturn, tLocalReturn, asList("int"), asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "bool"), asList("(bool | int)"), true)
        ));
    }

    //see TINS-497 fixing recursive functions leaves param non-fixed
    @Test
    public void tryToFix_RecursiveAndReturnHasFixedBound_AllAreFixed() {
        //corresponds: function fac($n){ return $n > 0 ? $n * fac($n-1) : $n;}
        // where int x int -> int was taken for $n - 1

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, intType);
        overloadBindings.addLowerTypeBound(tReturn, intType);
        overloadBindings.addUpperTypeBound(tReturn, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }


    //see TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_MultipleReturnIntAndTxWhereTxHasUpperInt_AllAreFixed() {
        //corresponds: function foo($x){ if($x-1 > 0){return 1;} return $x;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, intType);
        overloadBindings.addLowerTypeBound(tReturn, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    //see TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_MultipleReturnIntAndTxAndTyWhereTxHasUpperIntAndTyUpperFloat_TxFixedAndReturnHasTyAsLower() {
        //corresponds: function fac($x, $y){ if($x - 1 > 0){return 1;} else if($y - 1.5 > 0){return $y;} return $x;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($y, new TypeVariableReference(ty));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, intType);
        overloadBindings.addUpperTypeBound(ty, floatType);
        overloadBindings.addLowerTypeBound(tReturn, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("int"), true),
                varBinding($y, ty, null, asList("float"), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "@" + ty), null, false)
        ));
    }

    //see TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_RecursiveAndReturnHasSameLowerAsLowerRefsUpper2_AllAreFixed() {
        //corresponds: function fac($x){ $a = 1.5; if($x - 1 > 0){return 1;} else if($a - 1.5 > 0){return $a;} return
        // $x;}

        //pre-act necessary for arrange
        IOverloadBindings overloadBindings = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $a = "$a";
        String ta = "Ta";
        String tReturn = "Treturn";

        overloadBindings.addVariable($x, new TypeVariableReference(tx));
        overloadBindings.addVariable($a, new TypeVariableReference(ta));
        overloadBindings.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        overloadBindings.addUpperTypeBound(tx, intType);
        overloadBindings.addLowerTypeBound(ta, floatType);
        overloadBindings.addUpperTypeBound(ta, floatType);
        overloadBindings.addLowerTypeBound(tReturn, intType);
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        overloadBindings.addLowerRefBound(tReturn, new TypeVariableReference(ta));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        overloadBindings.tryToFix(parameterTypeVariables);

        assertThat(overloadBindings, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("int"), true),
                varBinding($a, ta, asList("float"), asList("float"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "float"), asList("(float | int)"), true)
        ));
    }

    private IOverloadBindings createOverloadBindings() {
        return createOverloadBindings(symbolFactory, typeHelper);
    }

    protected IOverloadBindings createOverloadBindings(
            ISymbolFactory symbolFactory, ITypeHelper typeHelper) {
        return new OverloadBindings(symbolFactory, typeHelper);
    }

}


