/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IVariable;
import ch.tsphp.tinsphp.symbols.constraints.Variable;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class VariableTest
{
    @Test
    public void getName_Standard_IsOnePassedByConstructor() {
        String name = "$dummy";

        IVariable variable = createVariable(name);
        String result = variable.getName();

        assertThat(result, is(name));
    }

    @Test
    public void getAbsoluteName_Standard_IsNamePassedByConstructor() {
        String name = "$dummy";

        IVariable variable = createVariable(name);
        String result = variable.getAbsoluteName();

        assertThat(result, is(name));
    }

    @Test
    public void getType_Standard_ReturnsNull() {
        //no arrange necessary

        IVariable variable = createVariable("dummy");
        ITypeSymbol result = variable.getType();

        assertThat(result, is(nullValue()));
    }

    protected IVariable createVariable(String theName) {
        return new Variable(theName);
    }
}
