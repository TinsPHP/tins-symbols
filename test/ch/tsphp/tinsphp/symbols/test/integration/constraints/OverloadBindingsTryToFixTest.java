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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        collection.addVariable("$a", new TypeVariableReference(ta));
        collection.addVariable("$b", new TypeVariableReference(tb));
        collection.addVariable(
                RETURN_VARIABLE_NAME, new FixedTypeVariableReference(new TypeVariableReference(tReturn)));
        collection.addLowerTypeBound(ta, intType);
        collection.addLowerTypeBound(tb, floatType);
        collection.addLowerTypeBound(tReturn, boolType);
        collection.addUpperTypeBound(tReturn, boolType);

        //act
        collection.tryToFix(new HashSet<String>());

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        collection.addVariable("$a", new TypeVariableReference(ta));
        collection.addVariable("$b", new TypeVariableReference(tb));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(ta, intType);
        collection.addLowerTypeBound(tb, floatType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        collection.addVariable("$a", new TypeVariableReference(ta));
        collection.addVariable("$b", new TypeVariableReference(tb));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(ta, intType);
        collection.addLowerRefBound(tb, new TypeVariableReference(ta));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tc = "Tc";
        String td = "Td";
        String tReturn = "Treturn";

        collection.addVariable("$a", new TypeVariableReference(ta));
        collection.addVariable("$b", new TypeVariableReference(tb));
        collection.addVariable("$c", new TypeVariableReference(tc));
        collection.addVariable("$d", new TypeVariableReference(td));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(ta, intType);
        collection.addLowerRefBound(tb, new TypeVariableReference(ta));
        collection.addLowerRefBound(tc, new TypeVariableReference(tb));
        collection.addLowerRefBound(td, new TypeVariableReference(tc));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(td));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String t1 = "T1";
        String ta = "Ta";
        String tReturn = "Treturn";

        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable($x, new TypeVariableReference(t1));
        collection.addVariable($y, new TypeVariableReference(t1));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ta, new TypeVariableReference(t1));
        collection.addUpperTypeBound(t1, numType);
        collection.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(t1);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange

        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, intType);
        collection.addUpperTypeBound(tx, numType);
        collection.addLowerRefBound(tx, new TypeVariableReference(ty));
        collection.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("num"), asList("num"), true),
                varBinding($y, ty, asList("num"), asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    @Test
    public void tryToFix_YIsLowerRefOfXAndXIsReturned_ReturnIsTxAndHasTyAsLowerRef() {
        //corresponds: function foo($x, $y){ $x + 1; $x = $y; return $x;}

        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, intType);
        collection.addUpperTypeBound(tx, numType);
        collection.addLowerRefBound(tx, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), asList("num"), false),
                varBinding($y, ty, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), asList("num"), false)
        ));
    }

    @Test
    public void tryToFix_YIsLowerRefOfBAndBIsLowerRefOfXAndXIsReturned_ReturnIsTxAndHasTyAsLowerRefAndBIsTy() {
        //corresponds: function foo($x, $y){ $x + 1; $b = $y; $x = $b; return $x;}

        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $b = "$b";
        String tx = "Tx";
        String ty = "Ty";
        String tb = "Tb";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($b, new TypeVariableReference(tb));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, intType);
        collection.addUpperTypeBound(tx, numType);
        collection.addLowerRefBound(tx, new TypeVariableReference(tb));
        collection.addLowerRefBound(tb, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable($b, new TypeVariableReference(tb));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ta, new TypeVariableReference(ty));
        collection.addLowerRefBound(tx, new TypeVariableReference(ta));
        collection.addLowerRefBound(tb, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable($b, new TypeVariableReference(tb));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(ta, intType);
        collection.addLowerRefBound(ta, new TypeVariableReference(ty));
        collection.addLowerRefBound(tx, new TypeVariableReference(ta));
        collection.addLowerRefBound(tb, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable($b, new TypeVariableReference(tb));
        collection.addVariable($c, new TypeVariableReference(tc));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(ta, intType);
        collection.addLowerRefBound(ta, new TypeVariableReference(ty));
        collection.addLowerRefBound(tc, new TypeVariableReference(ta));
        collection.addLowerRefBound(tx, new TypeVariableReference(tc));
        collection.addLowerRefBound(tb, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable($b, new TypeVariableReference(tb));
        collection.addVariable($c, new TypeVariableReference(tc));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tc, intType);
        collection.addLowerRefBound(ta, new TypeVariableReference(ty));
        collection.addLowerRefBound(tc, new TypeVariableReference(ta));
        collection.addLowerRefBound(tx, new TypeVariableReference(tc));
        collection.addLowerRefBound(tb, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable($b, new TypeVariableReference(tb));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(ta, intType);
        collection.addLowerRefBound(ta, new TypeVariableReference(ty));
        collection.addLowerRefBound(tx, new TypeVariableReference(ta));
        collection.addLowerRefBound(tb, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("mixed"), asList("mixed"), true),
                varBinding($y, ty, asList("mixed"), asList("mixed"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerConstantReturn_AllAreConstant() {
        //corresponds: function foo($x){ $a = $x; return 1;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ta, new TypeVariableReference(tx));
        collection.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("mixed"), asList("mixed"), true),
                varBinding($a, ta, asList("mixed"), asList("mixed"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), asList("int"), true)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerAndParamReturned_VariableUnifiesWithParam() {
        //corresponds: function foo($x){ $a = $x; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ta, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding($a, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerAndSameLowerTypeAndParamReturned_UnifiesWithParameter() {
        //corresponds: function foo($x){ $a = 1; $x = 1; $a = $x; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ta, new TypeVariableReference(tx));
        collection.addLowerTypeBound(tx, intType);
        collection.addLowerTypeBound(ta, intType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($a, tx, asList("int"), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }


    @Test
    public void tryToFix_VariableHasParamAsLowerAndDifferentLowerTypeAndParamReturned_DoesNotUnifyWithParameter() {
        //corresponds: function foo($x){ $a = 1.3; $x = 1; $a = $x; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ta, new TypeVariableReference(tx));
        collection.addLowerTypeBound(tx, intType);
        collection.addLowerTypeBound(ta, floatType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($a, ta, asList("float", "int", "@" + tx), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }

    @Test
    public void tryToFix_ParamXAndYAndBothAreReturned_ReturnHasBothAsLowerRef() {
        //corresponds: function foo($x, $y){ if($x){return $x;} return $y;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addUpperTypeBound(tx, boolType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, null, asList("bool"), false),
                varBinding($y, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@" + tx, "@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_SemiConstantReturn_ReturnHasTypeAndParamAsLowerBound() {
        //corresponds: function foo($x, $y){ if($x){return 1;} return $y;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addUpperTypeBound(tx, boolType);
        collection.addLowerTypeBound(tReturn, intType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, stringType);
        collection.addUpperTypeBound(tx, stringType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), asList("string"), true)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    @Test
    public void tryToFix_SameLowerAndUpperTypeBoundWithUpperRefToOtherParamWhichHasSameUpper_FixOnlyOne() {
        //corresponds:
        // function foo($x, $y){
        //   $x = $y; $x = 'h'; expectsString($x); expectsString($y); return $x; return $y;
        // }
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, stringType);
        collection.addUpperTypeBound(tx, stringType);
        collection.addUpperTypeBound(ty, stringType);
        collection.addLowerRefBound(tx, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                varBinding($y, ty, null, asList("string"), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string", "@Ty"), null, false)
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, stringType);
        collection.addUpperTypeBound(tx, stringType);
        collection.addUpperTypeBound(ty, stringType);
        collection.addLowerRefBound(ty, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(ty, stringType);
        collection.addUpperTypeBound(ty, stringType);
        collection.addUpperTypeBound(tx, stringType);
        collection.addLowerRefBound(tx, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String tx = "Tx";
        String ty = "Ty";
        String ta = "Ta";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, stringType);
        collection.addUpperTypeBound(tx, stringType);
        collection.addUpperTypeBound(ty, stringType);
        collection.addLowerRefBound(ty, new TypeVariableReference(ta));
        collection.addLowerRefBound(ta, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, stringType);
        collection.addUpperTypeBound(tx, stringType);
        collection.addLowerRefBound(ty, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("string"), asList("string"), true),
                varBinding($y, ty, asList("string"), null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, asList("string"), null, false)
        ));
    }

    @Test
    public void tryToFix_Recursion_DoesNotHaveSelfReference() {
        //corresponds: function endless($x){ $x = endless($x); return $x;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tx, new TypeVariableReference(tReturn));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithLocalVariableIndirection_DoesNotHaveSelfReference() {
        //corresponds: function endless($x){ $a = endless($x); $x = $a; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $a = "$a";
        String ta = "Ta";

        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tx, new TypeVariableReference(ta));
        collection.addLowerRefBound(ta, new TypeVariableReference(tReturn));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding($a, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithParamIndirection_DoesNotHaveCyclicRef() {
        //corresponds: function endless($x, $y){ $y = endless($x,$y); $x = $y; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";

        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(tx, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, ty, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithDoubleParamIndirection_DoesNotHaveCyclicRef() {
        //corresponds: function endless($x, $y, $z){ $y = endless($x,$y,$z); $z = $y; $x = $z; return $x;}
        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($z, new TypeVariableReference(tz));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(tz, new TypeVariableReference(ty));
        collection.addLowerRefBound(tx, new TypeVariableReference(tz));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($z, new TypeVariableReference(tz));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerTypeBound(tx, intType);
        collection.addUpperTypeBound(tx, numType);
        collection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(tz, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($z, new TypeVariableReference(tz));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(tx, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(tz, new TypeVariableReference(ty));
        collection.addLowerRefBound(tz, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String fCallBar1 = "bar()@2|1";
        String tBar1 = "Tb";
        String fCallBar2 = "bar()@2|4";
        String tBar2 = "Tb";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        collection.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));
        collection.addLowerRefBound(tBar1, new TypeVariableReference(tx));
        collection.addLowerRefBound(tBar2, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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


        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($z, new TypeVariableReference(tz));
        collection.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        collection.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addUpperTypeBound(tx, boolType);
        collection.addUpperTypeBound(ty, boolType);
        collection.addLowerRefBound(tBar1, new TypeVariableReference(tz));
        collection.addLowerRefBound(tBar1, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        collection.addLowerRefBound(tBar2, new TypeVariableReference(tz));
        collection.addLowerRefBound(tBar2, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($z, new TypeVariableReference(tz));
        collection.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        collection.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        collection.addVariable(fCallBar3, new TypeVariableReference(tBar3));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addUpperTypeBound(tx, boolType);
        collection.addUpperTypeBound(ty, boolType);
        collection.addLowerRefBound(tBar1, new TypeVariableReference(tz));
        collection.addLowerRefBound(tBar1, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        collection.addLowerRefBound(tBar2, new TypeVariableReference(tz));
        collection.addLowerRefBound(tBar2, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));
        collection.addLowerRefBound(tBar3, new TypeVariableReference(tz));
        collection.addLowerRefBound(tBar3, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tBar3));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable($b, new TypeVariableReference(tb));
        collection.addVariable($c, new TypeVariableReference(tc));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        collection.addLowerRefBound(ta, new TypeVariableReference(tb));
        collection.addLowerRefBound(ta, new TypeVariableReference(tc));
        collection.addLowerRefBound(tb, new TypeVariableReference(tx));
        collection.addLowerRefBound(tb, new TypeVariableReference(ty));
        collection.addLowerRefBound(tc, new TypeVariableReference(tx));
        collection.addLowerRefBound(tc, new TypeVariableReference(ty));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ta));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String plus = "+@1|2";
        String tPlus = "Tplus";
        String tReturn = "Treturn";

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(plus, new TypeVariableReference(tPlus));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        collection.bind(asTPlus, asList(tPlus));
        collection.addUpperTypeBound(tx, asTPlus);
        collection.addUpperTypeBound(ty, asTPlus);
        collection.addUpperTypeBound(tPlus, numType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(plus, new TypeVariableReference(tPlus));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        collection.bind(asTPlus, asList(tPlus));
        collection.addUpperTypeBound(tx, asTPlus);
        collection.addUpperTypeBound(ty, asTPlus);
        collection.addUpperTypeBound(tPlus, numType);
        collection.addLowerRefBound(ta, new TypeVariableReference(tPlus));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ta));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($z, new TypeVariableReference(tz));
        collection.addVariable(plus, new TypeVariableReference(tPlus));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        collection.bind(asTPlus, asList(tPlus));
        collection.addUpperTypeBound(tz, boolType);
        collection.addUpperTypeBound(tx, asTPlus);
        collection.addUpperTypeBound(ty, asTPlus);
        collection.addUpperTypeBound(tPlus, numType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
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
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable($z, new TypeVariableReference(tz));
        collection.addVariable(plus, new TypeVariableReference(tPlus));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        collection.bind(asTPlus, asList(tPlus));
        collection.addUpperTypeBound(tz, mixedType);
        collection.addUpperTypeBound(tx, asTPlus);
        collection.addUpperTypeBound(ty, asTPlus);
        collection.addUpperTypeBound(tPlus, numType);
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        collection.addLowerRefBound(tReturn, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, null, asList("{as " + tPlus + "}"), false),
                varBinding($y, ty, null, asList("{as " + tPlus + "}"), false),
                varBinding($z, tz, asList("mixed"), asList("mixed"), true),
                varBinding(plus, tPlus, null, asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@" + tPlus, "@" + tx, "@" + ty), null, false)
        ));
    }

    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithConvertibleWithoutReturn_AllAreFixed() {
        //corresponds:
        //  function foo($x, $y){ $a = $x + $y; return false;}
        // with overload {as T} x {as T} -> T

        //pre-act necessary for arrange
        IOverloadBindings collection = createOverloadBindings();

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

        collection.addVariable($x, new TypeVariableReference(tx));
        collection.addVariable($y, new TypeVariableReference(ty));
        collection.addVariable(plus, new TypeVariableReference(tPlus));
        collection.addVariable($a, new TypeVariableReference(ta));
        collection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleType();
        collection.bind(asTPlus, asList(tPlus));
        collection.addUpperTypeBound(tx, asTPlus);
        collection.addUpperTypeBound(ty, asTPlus);
        collection.addUpperTypeBound(tPlus, numType);
        collection.addLowerRefBound(ta, new TypeVariableReference(tPlus));
        collection.addLowerTypeBound(tReturn, boolType);

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        collection.tryToFix(parameterTypeVariables);

        assertThat(collection, withVariableBindings(
                varBinding($x, tx, asList("{as " + tPlus + "}"), asList("{as " + tPlus + "}"), true),
                varBinding($y, ty, asList("{as " + tPlus + "}"), asList("{as " + tPlus + "}"), true),
                varBinding(plus, tPlus, asList("num"), asList("num"), true),
                varBinding($a, ta, asList("num"), asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("bool"), asList("bool"), true)
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


