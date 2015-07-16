/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.tinsphp.symbols.erroneous.ErroneousMethodSymbol;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ErroneousMethodSymbolTest
{

    @Test(expected = UnsupportedOperationException.class)
    public void addParameter_Standard_ThrowsUnsupportedOprationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addParameter(mock(IVariableSymbol.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getParameters_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.getParameters();

        //assert in annotation
    }

    @Test
    public void isFinal_Standard_ReturnsFalse() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isFinal();

        assertThat(result, is(false));
    }

    @Test
    public void isAbstract_Standard_ReturnsFalse() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isAbstract();

        assertThat(result, is(false));
    }

    @Test
    public void isStatic_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isStatic();

        assertThat(result, is(true));
    }

    @Test
    public void isAlwaysCasting_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isAlwaysCasting();

        assertThat(result, is(true));
    }

    @Test
    public void isFalseable_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isFalseable();

        assertThat(result, is(true));
    }

    @Test
    public void isNullable_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isNullable();

        assertThat(result, is(true));
    }

    @Test
    public void isPublic_Standard_ReturnsTrue() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isPublic();

        assertThat(result, is(true));
    }

    @Test
    public void isProtected_Standard_ReturnsFalse() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isProtected();

        assertThat(result, is(false));
    }

    @Test
    public void isPrivate_Standard_ReturnsFalse() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        boolean result = methodSymbol.isPrivate();

        assertThat(result, is(false));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getOverloads_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.getOverloads();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addOverload_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addOverload(mock(IFunctionType.class));

        //assert in annotation
    }


    @Test(expected = UnsupportedOperationException.class)
    public void setBindings_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.setBindings(new ArrayList<IBindingCollection>());

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getBindings_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.getBindings();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getConstraints_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.getConstraints();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addConstraints_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.addConstraint(mock(IConstraint.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getReturnVariable_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IErroneousMethodSymbol methodSymbol = createMethodSymbol();
        methodSymbol.getReturnVariable();

        //assert in annotation
    }

    private IErroneousMethodSymbol createMethodSymbol() {
        return createMethodSymbol(mock(ITSPHPAst.class), "foo", new TSPHPException());
    }

    protected IErroneousMethodSymbol createMethodSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
        return new ErroneousMethodSymbol(ast, name, exception);

    }

}
