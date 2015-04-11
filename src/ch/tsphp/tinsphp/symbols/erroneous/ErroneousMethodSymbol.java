/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class ErroneousMethodSymbol from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.tinsphp.common.inference.constraints.IBinding;
import ch.tsphp.tinsphp.common.inference.constraints.IFunctionType;
import ch.tsphp.tinsphp.common.inference.constraints.IIntersectionConstraint;
import ch.tsphp.tinsphp.common.symbols.IMinimalVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.IVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.erroneous.IErroneousMethodSymbol;

import java.util.List;


public class ErroneousMethodSymbol extends AErroneousScopedSymbol implements IErroneousMethodSymbol
{

    public static final String ERROR_MESSAGE = "ErroneousMethodSymbol is not a real method.";

    public ErroneousMethodSymbol(ITSPHPAst ast, String name, TSPHPException exception) {
        super(ast, name, exception);
    }

    @Override
    public void addParameter(IVariableSymbol variableSymbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public List<IVariableSymbol> getParameters() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public IMinimalVariableSymbol getReturnVariable() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean isAlwaysCasting() {
        return true;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isFalseable() {
        return true;
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public List<IIntersectionConstraint> getLowerBoundConstraints() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public List<IIntersectionConstraint> getUpperBoundConstraints() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public List<IBinding> getBindings() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void addLowerBoundConstraint(IIntersectionConstraint constraint) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void addUpperBoundConstraint(IIntersectionConstraint constraint) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setBindings(List<IBinding> bindings) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void addOverload(IFunctionType overload) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public List<IFunctionType> getOverloads() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }
}
