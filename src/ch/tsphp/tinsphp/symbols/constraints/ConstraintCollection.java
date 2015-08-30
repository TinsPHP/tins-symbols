/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;

import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraint;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraintCollection;

import java.util.ArrayList;
import java.util.List;

public class ConstraintCollection implements IConstraintCollection
{
    private final List<IConstraint> constraints = new ArrayList<>();
    private List<IBindingCollection> bindings = new ArrayList<>();
    private String absoluteName;

    public ConstraintCollection(String theAbsoluteName) {
        absoluteName = theAbsoluteName;
    }

    @Override
    public String getAbsoluteName() {
        return absoluteName;
    }

    @Override
    public List<IConstraint> getConstraints() {
        return constraints;
    }

    @Override
    public void addConstraint(IConstraint constraint) {
        constraints.add(constraint);
    }

    @Override
    public void addBindingCollection(IBindingCollection bindingCollection) {
        bindings.add(bindingCollection);
    }

    @Override
    public List<IBindingCollection> getBindings() {
        return bindings;
    }

    @Override
    public void setBindings(List<IBindingCollection> theBindings) {
        bindings = theBindings;
    }
}
