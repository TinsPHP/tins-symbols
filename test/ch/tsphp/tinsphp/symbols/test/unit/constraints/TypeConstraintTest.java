/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.TypeConstraint;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypeConstraintTest
{
    @Test
    public void getId_Standard_ReturnsAbsoluteNameOfTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getAbsoluteName()).thenReturn("dummy");

        TypeConstraint constraint = createTypeConstraint(typeSymbol);
        String result = constraint.getId();

        assertThat(result, is("dummy"));
    }

    @Test
    public void getTypeSymbol_Standard_ReturnsOnePassedByConstructor() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);

        TypeConstraint constraint = createTypeConstraint(typeSymbol);
        ITypeSymbol result = constraint.getTypeSymbol();

        assertThat(result, is(typeSymbol));
    }

    @Test
    public void toString_Standard_ReturnsAbsoluteNameOfTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getAbsoluteName()).thenReturn("dummy");

        TypeConstraint constraint = createTypeConstraint(typeSymbol);
        String result = constraint.toString();

        assertThat(result, is("dummy"));
    }

    protected TypeConstraint createTypeConstraint(ITypeSymbol typeSymbol) {
        return new TypeConstraint(typeSymbol);
    }
}
