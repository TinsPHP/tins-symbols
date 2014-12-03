/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IScalarTypeSymbol;
import ch.tsphp.tinsphp.symbols.ScalarTypeSymbol;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ScalarTypeSymbolTest
{

    @Test
    public void getDefaultValue_Standard_IsAstWithDefaultTokenTypeAndValue() {
        int defaultValueTokenType = 123;
        String defaultValue = "hello";

        IScalarTypeSymbol symbol = createScalarTypeSymbol(defaultValueTokenType, defaultValue);
        ITSPHPAst result = symbol.getDefaultValue();

        assertThat(result.getType(), is(defaultValueTokenType));
        assertThat(result.getText(), is(defaultValue));
    }

    private IScalarTypeSymbol createScalarTypeSymbol(int defaultValueTokenType, String defaultValue) {
        return createScalarTypeSymbol(
                "foo",
                mock(ITypeSymbol.class),
                defaultValueTokenType,
                defaultValue);
    }

    protected IScalarTypeSymbol createScalarTypeSymbol(
            String name,
            ITypeSymbol parentTypeSymbol,
            int defaultValueTokenType,
            String defaultValue) {
        return new ScalarTypeSymbol(
                name,
                parentTypeSymbol,
                defaultValueTokenType,
                defaultValue);
    }
}
