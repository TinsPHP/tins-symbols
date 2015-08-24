/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.constraints.BindingCollection;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static ch.tsphp.tinsphp.common.TinsPHPConstants.RETURN_VARIABLE_NAME;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.BindingCollectionMatcher.varBinding;
import static ch.tsphp.tinsphp.symbols.test.integration.testutils.BindingCollectionMatcher.withVariableBindings;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

public class BindingCollectionTryToFixTest extends ATypeHelperTest
{
    @Test
    public void tryToFix_NoParams_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = 2.2; return true;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        bindingCollection.addVariable("$a", new TypeVariableReference(ta));
        bindingCollection.addVariable("$b", new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ta, intType);
        bindingCollection.addLowerTypeBound(tb, floatType);
        bindingCollection.addLowerTypeBound(tReturn, boolType);
        bindingCollection.addUpperTypeBound(tReturn, boolType);

        //act
        bindingCollection.tryToFix(new HashSet<String>());

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", ta, asList("int"), null, true),
                varBinding("$b", tb, asList("float"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("bool"), null, true)
        ));
    }

    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaIndirection_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = 2.2; return $b;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        bindingCollection.addVariable("$a", new TypeVariableReference(ta));
        bindingCollection.addVariable("$b", new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ta, intType);
        bindingCollection.addLowerTypeBound(tb, floatType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", ta, asList("int"), null, true),
                varBinding("$b", tb, asList("float"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("float"), null, true)
        ));
    }

    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaDoubleIndirection_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = $a; return $b;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        bindingCollection.addVariable("$a", new TypeVariableReference(ta));
        bindingCollection.addVariable("$b", new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ta, intType);
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", ta, asList("int"), null, true),
                varBinding("$b", tb, asList("int"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }

    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaDoubleIndirection2_AllVariablesAreConstant() {
        //corresponds: function foo(){ $b = 1; $a = $b; return $a;}

        //same as before but changed $a with $b


        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tReturn = "Treturn";

        bindingCollection.addVariable("$a", new TypeVariableReference(ta));
        bindingCollection.addVariable("$b", new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tb, intType);
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tb));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ta));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", ta, asList("int"), null, true),
                varBinding("$b", tb, asList("int"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }


    //see TINS-386 function with constant return via indirection
    @Test
    public void tryToFix_NoParamsReturnViaMultipleIndirection_AllVariablesAreConstant() {
        //corresponds: function foo(){ $a = 1; $b = $a; $c = $b; $d = $c; return $d;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String ta = "Ta";
        String tb = "Tb";
        String tc = "Tc";
        String td = "Td";
        String tReturn = "Treturn";

        bindingCollection.addVariable("$a", new TypeVariableReference(ta));
        bindingCollection.addVariable("$b", new TypeVariableReference(tb));
        bindingCollection.addVariable("$c", new TypeVariableReference(tc));
        bindingCollection.addVariable("$d", new TypeVariableReference(td));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ta, intType);
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tc, new TypeVariableReference(tb));
        bindingCollection.addLowerRefBound(td, new TypeVariableReference(tc));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(td));
        Set<String> parameterTypeVariables = new HashSet<>();

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding("$a", ta, asList("int"), null, true),
                varBinding("$b", tb, asList("int"), null, true),
                varBinding("$c", tc, asList("int"), null, true),
                varBinding("$d", td, asList("int"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }

    //see TINS-449 unused ad-hoc polymorphic parameters
    @Test
    public void tryToFix_UnusedAdHocParamsAssignedToLocalConstantReturn_LocalHasNotMixedAsType() {
        //corresponds: function foo($x, $y){ $a = $x + $y; return $1;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String t1 = "T1";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable($x, new TypeVariableReference(t1));
        bindingCollection.addVariable($y, new TypeVariableReference(t1));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(t1));
        bindingCollection.addUpperTypeBound(t1, numType);
        bindingCollection.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(t1);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, t1, null, asList("num"), true),
                varBinding($y, t1, null, asList("num"), true),
                varBinding($a, ta, asList("num"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }


    @Test
    public void tryToFix_YIsLowerRefOfXAndConstantReturn_AllVariablesAreFixed() {
        //corresponds: function foo($x, $y){ $x + 1; $x = $y; return 1;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange

        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("num"), true),
                varBinding($y, ty, null, asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }

    @Test
    public void tryToFix_YIsLowerRefOfXAndXIsReturned_ReturnIsTxAndHasTyAsLowerRef() {
        //corresponds: function foo($x, $y){ $x + 1; $x = $y; return $x;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), asList("num"), false),
                varBinding($y, ty, null, asList("num", "@" + tx), false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), asList("num"), false)
        ));
    }

    @Test
    public void tryToFix_YIsLowerRefOfBAndBIsLowerRefOfXAndXIsReturned_ReturnIsTxAndHasTyAsLowerRefAndBIsTy() {
        //corresponds: function foo($x, $y){ $x + 1; $b = $y; $x = $b; return $x;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $b = "$b";
        String tx = "Tx";
        String ty = "Ty";
        String tb = "Tb";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($b, new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(tb));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), asList("num"), false),
                varBinding($y, ty, null, asList("num", "@" + tx), false),
                varBinding($b, ty, null, asList("num", "@" + tx), false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), asList("num"), false)
        ));
    }

    @Test
    public void tryToFix_YIsLowerALowerXLowerBAndBIsReturned_ReturnIsTxAndHasTyAsLowerRefAndAIsTyAndBIsTx() {
        //corresponds: function foo($x, $y){ $a = $y; $x = $a; $b = $x; return $x;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable($b, new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("@" + ty), null, false),
                varBinding($y, ty, null, asList("@" + tx), false),
                varBinding($a, ty, null, asList("@" + tx), false),
                varBinding($b, tx, asList("@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_YIsLowALowXLowBLowRtnAndAHasDiffLowTypeThanY_TyLowTxAndRtnIsTxAndTyLowTaAndBIsTx() {
        //corresponds: function foo($x, $y){$a = 1; $a = $y; $x = $a; $b = $x; return $x;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable($b, new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ta, intType);
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), null, false),
                varBinding($y, ty, null, asList("@" + tx, "@" + ta), false),
                varBinding($a, ta, asList("int", "@" + ty), null, false),
                varBinding($b, tx, asList("int", "@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_YIsLowALowCLowXLowBLowRtnAndAHasDiffLowBoundThanY_TyLowTxTcTaAndRtnIsTxAndBIsTx() {
        //corresponds: function foo($x, $y){$a = 1; $a = $y; $c = $a; $x = $c $b = $x; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable($b, new TypeVariableReference(tb));
        bindingCollection.addVariable($c, new TypeVariableReference(tc));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ta, intType);
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tc, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(tc));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), null, false),
                varBinding($y, ty, null, asList("@" + tx, "@" + ta, "@" + tc), false),
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
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable($b, new TypeVariableReference(tb));
        bindingCollection.addVariable($c, new TypeVariableReference(tc));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tc, intType);
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tc, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(tc));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), null, false),
                varBinding($y, ty, null, asList("@" + tx, "@" + tc), false),
                varBinding($a, ty, null, asList("@" + tx, "@" + tc), false),
                varBinding($b, tx, asList("int", "@" + ty), null, false),
                varBinding($c, tc, asList("int", "@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), null, false)
        ));
    }


    @Test
    public void tryToFix_CircularRefWithParamAndOtherParamIsLower_Unifies() {
        //corresponds: function foo($x, $y){$a = 1; $a = $y; $a = $x; $x = $a; $b = $x; return $x;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable($b, new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ta, intType);
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tb));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int", "@" + ty), null, false),
                varBinding($y, ty, null, asList("@" + tx, "@" + ta), false),
                varBinding($a, ta, asList("int", "@" + ty), null, false),
                varBinding($b, tx, asList("int", "@" + ty), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int", "@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_HasTwoParamsDoesNothing_AllAreConstant() {
        //corresponds: function foo($x, $y){ return 1;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("mixed"), true),
                varBinding($y, ty, null, asList("mixed"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerConstantReturn_AllAreConstant() {
        //corresponds: function foo($x){ $a = $x; return 1;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tx));
        bindingCollection.addLowerTypeBound(tReturn, intType);
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("mixed"), true),
                varBinding($a, ta, asList("mixed"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerAndParamReturned_VariableUnifiesWithParam() {
        //corresponds: function foo($x){ $a = $x; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding($a, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    @Test
    public void tryToFix_VariableHasParamAsLowerAndSameLowerTypeAndParamReturned_UnifiesWithParameter() {
        //corresponds: function foo($x){ $a = 1; $x = 1; $a = $x; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tx));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addLowerTypeBound(ta, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($a, tx, asList("int"), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }


    @Test
    public void tryToFix_VariableHasParamAsLowerAndDifferentLowerTypeAndParamReturned_DoesNotUnifyWithParameter() {
        //corresponds: function foo($x){ $a = 1.3; $x = 1; $a = $x; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $a = "$a";
        String tx = "Tx";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tx));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addLowerTypeBound(ta, floatType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("@" + ta), false),
                varBinding($a, ta, asList("float", "int", "@" + tx), null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), asList("@" + ta), false)
        ));
    }

    @Test
    public void tryToFix_ParamXAndYAndBothAreReturned_ReturnHasBothAsLowerRef() {
        //corresponds: function foo($x, $y){ if($x){return $x;} return $y;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, boolType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("bool", "@" + tReturn), false),
                varBinding($y, ty, null, asList("@" + tReturn), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@" + tx, "@" + ty), null, false)
        ));
    }

    @Test
    public void tryToFix_SemiConstantReturn_ReturnHasTypeAndParamAsLowerBound() {
        //corresponds: function foo($x, $y){ if($x){return 1;} return $y;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, boolType);
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("bool"), true),
                varBinding($y, ty, null, asList("@" + tReturn), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "@" + ty), null, false)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    @Test
    public void tryToFix_SameLowerAndUpperTypeBound_FixIt() {
        //corresponds: function foo($x){ $x = 'h'; expectsString($x); return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(tx, stringType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), null, true)
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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(ty, stringType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("string"), true),
                //see TINS-500 multiple return and same lower as param upper - why Ty is fixed
                varBinding($y, ty, null, asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), null, true)
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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(ty, stringType);
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("string"), true),
                varBinding($y, ty, null, asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), null, true)
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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ty, stringType);
        bindingCollection.addUpperTypeBound(ty, stringType);
        bindingCollection.addUpperTypeBound(tx, stringType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("string"), true),
                varBinding($y, ty, null, asList("string"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), null, true)
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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String $a = "$a";
        String tx = "Tx";
        String ty = "Ty";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(ty, stringType);
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("string"), true),
                varBinding($y, ty, null, asList("string"), true),
                varBinding($a, ta, asList("string"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("string"), null, true)
        ));
    }

    //see TINS-415 same lower and upper type bound equals fix type
    @Test
    public void tryToFix_SameLowerAndUpperTypeBoundWithLowerRefToOtherParamWhichHasDifferentUpper_FixOnlyOne() {
        //corresponds: function foo($x, $y){ $y = $x; $x = 'h'; expectsString($x); return $x; return $y; }
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String $y = "$y";
        String tx = "Tx";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, stringType);
        bindingCollection.addUpperTypeBound(tx, stringType);
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("string"), true),
                varBinding($y, ty, asList("string"), null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, asList("string"), null, false)
        ));
    }

    //see TINS-519 fix indirect recursive and self ref
    @Test
    public void tryToFix_Recursion_DoesNotHaveSelfReference() {
        //corresponds: function endless($x){ $x = endless($x); return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(tReturn));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    //see TINS-519 fix indirect recursive and self ref
    @Test
    public void tryToFix_RecursionWithLocalVariableIndirection_DoesNotHaveSelfReference() {
        //corresponds: function endless($x){ $a = endless($x); $x = $a; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $a = "$a";
        String ta = "Ta";

        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tReturn));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding($a, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }

    //see TINS-519 fix indirect recursive and self ref
    @Test
    public void tryToFix_RecursionWithLocalVariableIndirectionAndHasParamAsLower_DoesNotHaveSelfReference() {
        //corresponds: function endless($x){ $a = endless($x); $x = $a; $a = $x; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $a = "$a";
        String ta = "Ta";

        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tReturn));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, null, false),
                varBinding($a, tx, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tx, null, null, false)
        ));
    }


    //see TINS-519 fix indirect recursive and self ref
    @Test
    public void tryToFix_RecursionWithParamIndirection_DoesNotHaveCyclicRef() {
        //corresponds: function endless($x, $y){ $y = endless($x,$y); $x = $y; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";

        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, ty, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithDoubleParamIndirection_DoesNotHaveCyclicRef() {
        //corresponds: function endless($x, $y, $z){ $y = endless($x,$y,$z); $z = $y; $x = $z; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, ty, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding($z, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, null, null, false)
        ));
    }

    @Test
    public void tryToFix_RecursionWithTripleParamIndirection_DoesNotHaveCyclicRef() {
        //corresponds: function endless($x, $y, $z, $a){ $y = endless($x,$y,$z); $z = $y; $a = $z; $x = $a; return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String $a = "$a";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);
        parameterTypeVariables.add(ta);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, ty, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding($z, ty, null, null, false),
                varBinding($a, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, null, null, false)
        ));
    }

    //see TINS-519 fix indirect recursive and self ref
    @Test
    public void tryToFix_RecursionWithDoubleParamIndirectionAndTwoReturn_DoesNotHaveCyclicRef() {
        //corresponds: function foo($x, $y, $z){ if($x > 0){ $y = foo($x -1,$y,$z); $z = $y; return $z;} return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("num", "@" + ty), false),
                varBinding($y, ty, asList("int", "@Tx"), null, false),
                varBinding($z, ty, asList("int", "@Tx"), null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, asList("int", "@Tx"), null, false)
        ));
    }

    //see TINS-519 fix indirect recursive and self ref
    @Test
    public void tryToFix_TwoRecursions_DoesNotHaveCyclicRef() {
        //corresponds: function foo($x, $y, $z){ $y = foo(...); $x = foo(...); $z = $y; $z = $x; return $z;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";

        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, ty, null, null, false),
                varBinding($y, ty, null, null, false),
                varBinding($z, ty, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, null, null, false)
        ));
    }

    //see TINS-519 fix indirect recursive and self ref
    @Test
    public void tryToFix_ThreeRecursions_DoesNotHaveCyclicRef() {
        //corresponds:
        // function foo($x, $y, $z, $a, $b){
        //   $y = foo(...); $x = foo(...); $b = foo(...);
        // $z = $a; $a = $b; $z = $y; $z = $x; return $z;
        // }
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String $a = "$a";
        String ta = "Ta";
        String $b = "$b";
        String tb = "Tb";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable($b, new TypeVariableReference(tb));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tb));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ta));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);
        parameterTypeVariables.add(ta);
        parameterTypeVariables.add(tb);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tb, null, null, false),
                varBinding($y, tb, null, null, false),
                varBinding($z, tb, null, null, false),
                varBinding($a, tb, null, null, false),
                varBinding($b, tb, null, null, false),
                varBinding(RETURN_VARIABLE_NAME, tb, null, null, false)
        ));
    }


    //see TINS-519 fix indirect recursive - self ref removed when should not
    @Test
    public void tryToFix_IndirectRecursiveWithSelfRef_DoesNotHaveCycle() {
        //corresponds:
        // function foo($x, $y, $z){ if($x > 0){ $y = foo($x - 1, $y, $z); $z = $y; return $z; } return $x; }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int", "@" + ty), false),
                varBinding($y, ty, asList("@" + tx), null, false),
                varBinding($z, ty, asList("@" + tx), null, false),
                varBinding(RETURN_VARIABLE_NAME, ty, asList("@" + tx), null, false)
        ));
    }

    //see TINS-519 fix indirect recursive - self ref removed when should not
    @Test
    public void tryToFix_IndirectRecursiveWithSelfRefAndUpperTypeBound_DoesNotHaveCycle() {
        //corresponds:
        // function foo($x, $y, $z){
        //   if($x > 0){
        //     $y = foo($x - 1, $y + (1 + 1.4), $z);
        //     $z = $y;
        //     return $z;
        //   }
        //   return $x;
        // }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(ty, numType);
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int", "@" + ty), false),
                varBinding($y, ty, asList("@" + tx), asList("num"), false),
                varBinding($z, ty, asList("@" + tx), asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, ty, asList("@" + tx), asList("num"), false)
        ));
    }

    //see TINS-519 fix indirect recursive - self ref removed when should not
    @Test
    public void tryToFix_IndirectRecursiveWithSelfRefAndLowerOfOtherParam_DoesNotHaveCycle() {
        //corresponds:
        // function foo($x, $y, $z, $a){
        //   if($x > 0){
        //     $y = foo($x - 1, $y + (1 + 1.4), $z);
        //     $z = $y;
        //     $a = $y;
        //     return $z;
        //   }
        //   return $x;
        // }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $z = "$z";
        String tz = "Tz";
        String $a = "$a";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(ty, numType);
        bindingCollection.addLowerRefBound(ty, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tz, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);
        parameterTypeVariables.add(ta);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int", "@" + ty), false),
                varBinding($y, ty, asList("@" + tx), asList("num"), false),
                varBinding($z, ty, asList("@" + tx), asList("num"), false),
                varBinding($a, ta, null, asList("mixed"), true),
                varBinding(RETURN_VARIABLE_NAME, ty, asList("@" + tx), asList("num"), false)
        ));
    }

    //see TINS-463 multiple return and ConcurrentModificationException
    @Test
    public void tryToFix_ParametricReturnDueToParametricFunctionCall_NoConcurrentModificationException() {
        //corresponds: function foo($x){ return bar($x); return bar($x);} function bar($x){return $x;}
        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String fCallBar1 = "bar()@2|1";
        String tBar1 = "Tb";
        String fCallBar2 = "bar()@2|4";
        String tBar2 = "Tb";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        bindingCollection.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));
        bindingCollection.addLowerRefBound(tBar1, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tBar2, new TypeVariableReference(tx));
        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
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
        //  function foo($x, $y, $z){ if($x){return bar($y, $z);} return bar($y, $z);}
        //  function bar($x, $y){ if($x){return $x;} return $y;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

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


        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        bindingCollection.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, boolType);
        bindingCollection.addUpperTypeBound(ty, boolType);
        bindingCollection.addLowerRefBound(tBar1, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tBar1, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        bindingCollection.addLowerRefBound(tBar2, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tBar2, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("bool"), true),
                varBinding($y, ty, null, asList("bool", "@" + tBar1, "@" + tBar2, "@" + tReturn), false),
                varBinding($z, tz, null, asList("@" + tBar1, "@" + tBar2, "@" + tReturn), false),
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
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(fCallBar1, new TypeVariableReference(tBar1));
        bindingCollection.addVariable(fCallBar2, new TypeVariableReference(tBar2));
        bindingCollection.addVariable(fCallBar3, new TypeVariableReference(tBar3));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, boolType);
        bindingCollection.addUpperTypeBound(ty, boolType);
        bindingCollection.addLowerRefBound(tBar1, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tBar1, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tBar1));
        bindingCollection.addLowerRefBound(tBar2, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tBar2, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tBar2));
        bindingCollection.addLowerRefBound(tBar3, new TypeVariableReference(tz));
        bindingCollection.addLowerRefBound(tBar3, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tBar3));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("bool"), true),
                varBinding($y, ty, null, asList("bool", "@" + tBar1, "@" + tBar2, "@" + tBar3, "@" + tReturn), false),
                varBinding($z, tz, null, asList("@" + tBar1, "@" + tBar2, "@" + tBar3, "@" + tReturn), false),
                varBinding(fCallBar1, tBar1, asList("@" + ty, "@" + tz), null, false),
                varBinding(fCallBar2, tBar2, asList("@" + ty, "@" + tz), null, false),
                varBinding(fCallBar3, tBar3, asList("@" + ty, "@" + tz), null, false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@Ty", "@Tz"), null, false)
        ));
    }

    //see TINS-463 multiple return and ConcurrentModificationException
    @Test
    public void tryToFix_LocalVariableWithMultipleLowerRefs_NoConcurrentModificationException() {
        //corresponds:
        //  function foo($x, $y){
        //    $a = $b; $b = $x; $b = $y;
        //    $a = $c; $c = $x; $c = $y;
        //    return $a;
        //  }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable($b, new TypeVariableReference(tb));
        bindingCollection.addVariable($c, new TypeVariableReference(tc));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tb));
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tc));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tb, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tc, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tc, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ta));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("@" + ta, "@" + tb, "@" + tc, "@" + tReturn), false),
                varBinding($y, ty, null, asList("@" + ta, "@" + tb, "@" + tc, "@" + tReturn), false),
                varBinding($a, ta, asList("@" + tx, "@" + ty), null, false),
                varBinding($b, tb, asList("@" + tx, "@" + ty), null, false),
                varBinding($c, tc, asList("@" + tx, "@" + ty), null, false),
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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String plus = "+@1|2";
        String tPlus = "Tplus";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(plus, new TypeVariableReference(tPlus));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleTypeSymbol();
        bindingCollection.bind(asTPlus, asList(tPlus));
        bindingCollection.addUpperTypeBound(tx, asTPlus);
        bindingCollection.addUpperTypeBound(ty, asTPlus);
        bindingCollection.addUpperTypeBound(tPlus, numType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("{as " + tPlus + "}"), true),
                varBinding($y, ty, null, asList("{as " + tPlus + "}"), true),
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
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(plus, new TypeVariableReference(tPlus));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleTypeSymbol();
        bindingCollection.bind(asTPlus, asList(tPlus));
        bindingCollection.addUpperTypeBound(tx, asTPlus);
        bindingCollection.addUpperTypeBound(ty, asTPlus);
        bindingCollection.addUpperTypeBound(tPlus, numType);
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tPlus));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ta));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("{as " + tPlus + "}"), true),
                varBinding($y, ty, null, asList("{as " + tPlus + "}"), true),
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
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(plus, new TypeVariableReference(tPlus));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleTypeSymbol();
        bindingCollection.bind(asTPlus, asList(tPlus));
        bindingCollection.addUpperTypeBound(tz, boolType);
        bindingCollection.addUpperTypeBound(tx, asTPlus);
        bindingCollection.addUpperTypeBound(ty, asTPlus);
        bindingCollection.addUpperTypeBound(tPlus, numType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("{as " + tPlus + "}", "@" + tReturn), false),
                varBinding($y, ty, null, asList("{as " + tPlus + "}"), true),
                varBinding($z, tz, null, asList("bool"), true),
                varBinding(plus, tPlus, null, asList("num", "@" + tReturn), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("@" + tPlus, "@" + tx), null, false)
        ));
    }


    //see TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_PlusWithConvertibleAndReturnTxAndTy_ReturnIsNotFixedIsTPlusAndTxAndTy() {
        //corresponds:
        //  function foo($x, $y, $a){ if($a > 10){ return $x + $y;} else if ($a <10){return $x;} return $y; }
        // with + overload {as T} x {as T} -> T

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String $a = "$a";
        String ta = "Ta";
        String plus = "+@1|2";
        String tPlus = "Tplus";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(plus, new TypeVariableReference(tPlus));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleTypeSymbol();
        bindingCollection.bind(asTPlus, asList(tPlus));
        bindingCollection.addUpperTypeBound(ta, mixedType);
        bindingCollection.addUpperTypeBound(tx, asTPlus);
        bindingCollection.addUpperTypeBound(ty, asTPlus);
        bindingCollection.addUpperTypeBound(tPlus, numType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tPlus));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("{as " + tPlus + "}", "@" + tReturn), false),
                varBinding($y, ty, null, asList("{as " + tPlus + "}", "@" + tReturn), false),
                varBinding($a, ta, asList("mixed"), null, true),
                varBinding(plus, tPlus, null, asList("num", "@" + tReturn), false),
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
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(plus, new TypeVariableReference(tPlus));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTPlus = createConvertibleTypeSymbol();
        bindingCollection.bind(asTPlus, asList(tPlus));
        bindingCollection.addUpperTypeBound(tx, asTPlus);
        bindingCollection.addUpperTypeBound(ty, asTPlus);
        bindingCollection.addUpperTypeBound(tPlus, numType);
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tPlus));
        bindingCollection.addLowerTypeBound(tReturn, boolType);

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("{as num}"), true),
                varBinding($y, ty, null, asList("{as num}"), true),
                varBinding(plus, tPlus, null, asList("num"), true),
                varBinding($a, ta, asList("num"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("bool"), null, true)
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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String e1 = "+@1|4";
        String te1 = "Te1";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable(e1, new TypeVariableReference(te1));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = createConvertibleTypeSymbol();
        bindingCollection.bind(asTe1, asList(te1));
        bindingCollection.addUpperTypeBound(tx, asTe1);
        bindingCollection.addUpperTypeBound(te1, numType);
        bindingCollection.addLowerTypeBound(tReturn, intType);

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("{as num}"), true),
                varBinding(e1, te1, null, asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
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
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(e1, new TypeVariableReference(te1));
        bindingCollection.addVariable(e2, new TypeVariableReference(te2));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTe1 = createConvertibleTypeSymbol();
        bindingCollection.bind(asTe1, asList(te1));
        bindingCollection.addUpperTypeBound(tx, asTe1);
        IConvertibleTypeSymbol asTe2 = createConvertibleTypeSymbol();
        bindingCollection.bind(asTe2, asList(te2));
        bindingCollection.addUpperTypeBound(ty, asTe2);
        bindingCollection.addUpperTypeBound(te1, numType);
        bindingCollection.addUpperTypeBound(te2, numType);
        bindingCollection.addLowerTypeBound(tReturn, intType);

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("{as num}"), true),
                varBinding($y, ty, null, asList("{as num}"), true),
                varBinding(e1, te1, null, asList("num"), true),
                varBinding(e2, te2, null, asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String e1 = "+@1|4";
        String te1 = "T1";
        String localReturn = "return@1|2";
        String tLocalReturn = "T2";
        String tReturn = "T3";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable(e1, new TypeVariableReference(te1));
        bindingCollection.addVariable(localReturn, new TypeVariableReference(tLocalReturn));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addLowerTypeBound(te1, intType);
        bindingCollection.addLowerTypeBound(tReturn, boolType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        bindingCollection.addLowerRefBound(tLocalReturn, new TypeVariableReference(te1));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int"), true),
                varBinding(e1, te1, asList("int"), null, true),
                varBinding(localReturn, tLocalReturn, asList("int"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "bool"), null, true)
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
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String e1 = "+@1|4";
        String te1 = "T3";
        String localReturn = "return@1|2";
        String tLocalReturn = "T2";
        String tReturn = "T1";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable(e1, new TypeVariableReference(te1));
        bindingCollection.addVariable(localReturn, new TypeVariableReference(tLocalReturn));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addLowerTypeBound(te1, intType);
        bindingCollection.addLowerTypeBound(tReturn, boolType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te1));
        bindingCollection.addLowerRefBound(tLocalReturn, new TypeVariableReference(te1));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int"), true),
                varBinding(e1, te1, asList("int"), null, true),
                varBinding(localReturn, tLocalReturn, asList("int"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "bool"), null, true)
        ));
    }

    //see TINS-497 fixing recursive functions leaves param non-fixed
    @Test
    public void tryToFix_RecursiveAndReturnHasFixedBound_AllAreFixed() {
        //corresponds: function fac($n){ return $n > 0 ? $n * fac($n-1) : $n;}
        // where int x int -> int was taken for $n - 1

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addUpperTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }


    //see TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_MultipleReturnIntAndTxWhereTxHasUpperInt_AllAreFixed() {
        //corresponds: function foo($x){ if($x-1 > 0){return 1;} return $x;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }

    //see TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_MultipleReturnIntAndTxAndTyWhereTxHasUpperIntAndTyUpperFloat_TxFixedAndReturnHasTyAsLower() {
        //corresponds: function fac($x, $y){ if($x - 1 > 0){return 1;} else if($y - 1.5 > 0){return $y;} return $x;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(ty, floatType);
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int"), true),
                varBinding($y, ty, null, asList("float", "@" + tReturn), false),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "@" + ty), null, false)
        ));
    }

    //see TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_MultipleReturnIntAndTxAndTaWhereTxHasUpperIntAndTaIsFloat_AllFixed() {
        //corresponds:
        // function fac($x){ $a = 1.5; if($x - 1 > 0){return 1;} else if($a - 1.5 > 0){return $a;} return $x;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $a = "$a";
        String ta = "Ta";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addLowerTypeBound(ta, floatType);
        bindingCollection.addUpperTypeBound(ta, floatType);
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ta));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("int"), true),
                varBinding($a, ta, asList("float"), null, true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int", "float"), null, true)
        ));
    }

    //see TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_TyIsLowerOfTxAndTxLowerIsIntAndTyUpperIsIntAndReturnLowerIsIntAndReturnIsTxTy_TyIsFixed() {
        //corresponds:
        // function foo($x, $y){ $x = $y; $x = 1; $y + 1; return $x; return $y; }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(ty, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($y, ty, null, asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }

    //see TINS-500 multiple return and same lower as param upper
    @Test
    public void tryToFix_TyIsLowerOfTxAndTyUpperIsIntAndReturnLowerIsIntAndReturnIsTxTy_TyIsFixed() {
        //corresponds:
        // function foo($x, $y){ $x = $y; $y + 1; return $x; return $y; }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(ty, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(ty));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($y, ty, null, asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }

    //see TINS-521 return with two non-fixed convertible types as lower turns out mixed
    //see also TINS-485 fixing functions with convertible types
    @Test
    public void tryToFix_TwoPlusFirstIsTxAsTAndSecondIsAsTxAsT_T1xAsT1xAsT2ReturnT2WhereT1LowerT2AndT2LowerNum() {
        //corresponds:
        // function foo($x, $y, $z){ return $x + $y + $z; }
        // where overload for e1 = $x + $y is
        //   T x {as T} -> T \ T <: num
        // and overload for e2 = e1 + $z;
        //   {as T} x {as T} -> T \ T <: num

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

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

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable($z, new TypeVariableReference(tz));
        bindingCollection.addVariable(e1, new TypeVariableReference(tx));
        bindingCollection.addVariable(e2, new TypeVariableReference(te2));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        IConvertibleTypeSymbol asTx = symbolFactory.createConvertibleTypeSymbol();
        bindingCollection.bind(asTx, asList(tx));
        IConvertibleTypeSymbol asTe2 = symbolFactory.createConvertibleTypeSymbol();
        bindingCollection.bind(asTe2, asList(te2));
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.addUpperTypeBound(te2, numType);
        bindingCollection.addUpperTypeBound(ty, asTx);
        bindingCollection.addUpperTypeBound(tx, asTe2);
        bindingCollection.addUpperTypeBound(tz, asTe2);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te2));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);
        parameterTypeVariables.add(tz);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("num", "@" + te2), false),
                varBinding($y, ty, null, asList("{as " + tx + "}"), true),
                varBinding($z, tz, null, asList("{as " + te2 + "}"), true),
                varBinding(e1, tx, null, asList("num", "@" + te2), false),
                varBinding(e2, te2, asList("@" + tx), asList("num"), false),
                varBinding(RETURN_VARIABLE_NAME, te2, asList("@" + tx), asList("num"), false)
        ));
    }

    //see TINS-530 NullPointer when type parameter does not contribute to return
    @Test
    public void tryToFix_ParamIsIndirectLowerOfReturnAndUpperIsSubtypeOfLowerReturn_DoesNotProduceANullPointer() {
        //corresponds:
        // function foo($x, $y){ $x = $y; $y + 1; return $x; return 1; return 1.5; }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(ty, intType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tReturn, numType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int"), asList("@" + tReturn), false),
                varBinding($y, ty, null, asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("num", "@" + tx), null, false)
        ));
    }

    //see TINS-530 NullPointer when type parameter does not contribute to return
    @Test
    public void tryToFix_ParamIsIndirectLowerOfReturnAndUpperIsSameAsLowerOfReturn_DoesNotProduceANullPointer() {
        //corresponds:
        // function foo($x, $y){ $x = $y; $y + 1; return $x; return 1; }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addUpperTypeBound(ty, intType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerTypeBound(tReturn, intType);
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($y, ty, null, asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }

    //see TINS-530 NullPointer when type parameter does not contribute to return
    @Test
    public void tryToFix_ParamIsIndirectLowerOfReturnAndHasFixedType_DoesNotProduceANullPointer() {
        //corresponds:
        // function foo($x, $y){ $x = $y; $y + 1; $y = 1; return $x; }

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $y = "$y";
        String ty = "Ty";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($y, new TypeVariableReference(ty));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(ty, intType);
        bindingCollection.addUpperTypeBound(ty, intType);
        bindingCollection.addLowerRefBound(tx, new TypeVariableReference(ty));
        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(tx));

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);
        parameterTypeVariables.add(ty);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, asList("int"), null, false),
                varBinding($y, ty, null, asList("int"), true),
                varBinding(RETURN_VARIABLE_NAME, tx, asList("int"), null, false)
        ));
    }

    //see TINS-520 merge type parameters
//    @Test
//    public void tryToFix_TxLowerTyAndSameLowerTypeBounds_MergesTypeVariables() {
//        //corresponds:
//        // function foo($x, $y){ return $x + $y + $z; }
//        // where {as T} x {as T} -> T \ T <: num was applied
//
//        //pre-act necessary for arrange
//        IBindingCollection bindingCollection = createBindingCollection();
//
//        //arrange
//        String $x = "$x";
//        String tx = "Tx";
//        String $y = "$y";
//        String ty = "Ty";
//        String $z = "$z";
//        String tz = "Tz";
//        String e1 = "+@1|2";
//        String te1 = "Te1";
//        String e2 = "+@1|4";
//        String te2 = "Te2";
//        String tReturn = "Treturn";
//
//        bindingCollection.addVariable($x, new TypeVariableReference(tx));
//        bindingCollection.addVariable($y, new TypeVariableReference(ty));
//        bindingCollection.addVariable($z, new TypeVariableReference(tz));
//        bindingCollection.addVariable(e1, new TypeVariableReference(te1));
//        bindingCollection.addVariable(e2, new TypeVariableReference(te2));
//        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
//        IConvertibleTypeSymbol asTe1 = createConvertibleTypeSymbol();
//        bindingCollection.bind(asTe1, asList(te1));
//        bindingCollection.addUpperTypeBound(te1, numType);
//        bindingCollection.addUpperTypeBound(tx, asTe1);
//        bindingCollection.addUpperTypeBound(ty, asTe1);
//        IConvertibleTypeSymbol asTe2 = createConvertibleTypeSymbol();
//        bindingCollection.bind(asTe2, asList(te2));
//        bindingCollection.addUpperTypeBound(te2, numType);
//        bindingCollection.addUpperTypeBound(te1, asTe2);
//        bindingCollection.addUpperTypeBound(tz, asTe2);
//        bindingCollection.addLowerRefBound(tReturn, new TypeVariableReference(te2));
//
//        Set<String> parameterTypeVariables = new HashSet<>();
//        parameterTypeVariables.add(tx);
//        parameterTypeVariables.add(ty);
//        parameterTypeVariables.add(tz);
//
//        //act
//        bindingCollection.tryToFix(parameterTypeVariables);
//
//        assertThat(bindingCollection, withVariableBindings(
//                varBinding($x, tx, null, asList("{as " + te2 + "}"), false),
//                varBinding($y, ty, null, asList("{as " + te2 + "}"), false),
//                varBinding($z, tz, null, asList("{as " + te2 + "}"), false),
//                varBinding(e1, te1, null, asList("{as " + te2 + "}"), false),
//                varBinding(e2, te2, null, asList("num"), false),
//                varBinding(RETURN_VARIABLE_NAME, te2, null, asList("num"), false)
//        ));
//    }

    //see TINS-657 fixing convertible type and lower type bound propagation
    @Test
    public void tryToFix_ParamHasConvertibleAndUpperRefDoesNotContributeToReturn_UpperRefIsPropagated() {
        //corresponds:
        // function foo($x){ $a = $x + 1; return 1;}

        //pre-act necessary for arrange
        IBindingCollection bindingCollection = createBindingCollection();

        //arrange
        String $x = "$x";
        String tx = "Tx";
        String $a = "$a";
        String ta = "Ta";
        String plus = "+@2|1";
        String tPlus = "Tplus";
        String tReturn = "Treturn";

        bindingCollection.addVariable($x, new TypeVariableReference(tx));
        bindingCollection.addVariable($a, new TypeVariableReference(ta));
        bindingCollection.addVariable(plus, new TypeVariableReference(tPlus));
        bindingCollection.addVariable(RETURN_VARIABLE_NAME, new TypeVariableReference(tReturn));
        bindingCollection.addLowerTypeBound(tPlus, intType);
        bindingCollection.addUpperTypeBound(tPlus, numType);
        IConvertibleTypeSymbol asTplus = createConvertibleTypeSymbol();
        bindingCollection.bind(asTplus, asList(tPlus));
        bindingCollection.addUpperTypeBound(tx, asTplus);
        bindingCollection.addLowerRefBound(ta, new TypeVariableReference(tPlus));
        bindingCollection.addLowerTypeBound(tReturn, intType);

        Set<String> parameterTypeVariables = new HashSet<>();
        parameterTypeVariables.add(tx);

        //act
        bindingCollection.tryToFix(parameterTypeVariables);

        assertThat(bindingCollection, withVariableBindings(
                varBinding($x, tx, null, asList("{as num}"), true),
                varBinding($a, ta, asList("num"), null, true),
                varBinding(plus, tPlus, null, asList("num"), true),
                varBinding(RETURN_VARIABLE_NAME, tReturn, asList("int"), null, true)
        ));
    }

    private IBindingCollection createBindingCollection() {
        return createBindingCollection(symbolFactory, typeHelper);
    }

    protected IBindingCollection createBindingCollection(
            ISymbolFactory symbolFactory, ITypeHelper typeHelper) {
        return new BindingCollection(symbolFactory, typeHelper);
    }

}



