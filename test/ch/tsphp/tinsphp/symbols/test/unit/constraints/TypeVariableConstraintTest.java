/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.constraints;

import ch.tsphp.tinsphp.symbols.constraints.TypeVariableConstraint;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TypeVariableConstraintTest
{
    @Test
    public void getId_Standard_ReturnsTypeVariableWithPrefixAt() {
        String typeVariable = "T";

        TypeVariableConstraint constraint = createTypeVariableConstraint(typeVariable);
        String result = constraint.getId();

        assertThat(result, is("@T"));
    }

    @Test
    public void getTypeVariable_Standard_ReturnsOnePassedByConstructor() {
        String typeVariable = "T";

        TypeVariableConstraint constraint = createTypeVariableConstraint(typeVariable);
        String result = constraint.getTypeVariable();

        assertThat(result, is("T"));
    }

    @Test
    public void setAndGetTypeVariable_SetANewTypeVariable_ReturnsTheNewTypeVariable() {
        String typeVariable = "T";

        TypeVariableConstraint constraint = createTypeVariableConstraint(typeVariable);
        constraint.setTypeVariable("T1");
        String result = constraint.getTypeVariable();

        assertThat(result, is("T1"));
    }

    @Test
    public void equals_SameTypeVariable_ReturnsTrue() {
        String typeVariable = "T";

        TypeVariableConstraint constraint = createTypeVariableConstraint(typeVariable);
        boolean result = constraint.equals(createTypeVariableConstraint("T"));

        assertThat(result, is(true));
    }

    @Test
    public void equals_DifferentTypeVariable_ReturnsFalse() {
        String typeVariable = "T";

        TypeVariableConstraint constraint = createTypeVariableConstraint(typeVariable);
        boolean result = constraint.equals(createTypeVariableConstraint("T1"));

        assertThat(result, is(false));
    }

    @Test
    public void hashCode_Standard_SameAsHashCodeOfTypeVariable() {
        String typeVariable = "T";

        TypeVariableConstraint constraint = createTypeVariableConstraint(typeVariable);
        int result = constraint.hashCode();

        assertThat(result, is(typeVariable.hashCode()));
    }

    protected TypeVariableConstraint createTypeVariableConstraint(String typeVariable) {
        return new TypeVariableConstraint(typeVariable);
    }

}
