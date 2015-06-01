/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.tinsphp.common.inference.constraints.IOverloadBindings;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConvertibleTypeSymbolTest extends ATypeHelperTest
{
    @Test(expected = IllegalArgumentException.class)
    public void renameTypeVariable_IsNotCurrentTypeVariable_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        convertibleTypeSymbol.renameTypeVariable("NonExistingTypeVariable", "T");

        //assert in annotation
    }

    @Test(expected = IllegalStateException.class)
    public void renameTypeVariable_WasNotBound_ThrowsIllegalArgumentException() {
        //no arrange necessary

        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        convertibleTypeSymbol.renameTypeVariable(convertibleTypeSymbol.getTypeVariable(), "T2");

        //assert in annotation
    }

    @Test
    public void renameTypeVariable_WasBound_IsRenamedToGivenNewTypeVariable() {
        //pre-act necessary for arrange
        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();

        //arrange
        IOverloadBindings overloadBindings = new OverloadBindings(symbolFactory, typeHelper);
        overloadBindings.addVariable("$a", new TypeVariableReference("Ta"));
        convertibleTypeSymbol.bindTo(overloadBindings, asList("Ta"));

        //act
        convertibleTypeSymbol.renameTypeVariable(convertibleTypeSymbol.getTypeVariable(), "T2");
        String result = convertibleTypeSymbol.getTypeVariable();

        assertThat(result, is("T2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bindTo_NoTypeVariable_ThrowsIllegalArgumentException() {
        IOverloadBindings overloadBindings = new OverloadBindings(symbolFactory, typeHelper);

        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        convertibleTypeSymbol.bindTo(overloadBindings, new ArrayList<String>());

        //assert in annotation
    }

    @Test(expected = IllegalArgumentException.class)
    public void bindTo_MoreThanOneTypeVariable_ThrowsIllegalArgumentException() {
        IOverloadBindings overloadBindings = new OverloadBindings(symbolFactory, typeHelper);

        IConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType();
        convertibleTypeSymbol.bindTo(overloadBindings, asList("T1", "T2"));

        //assert in annotation
    }


}
