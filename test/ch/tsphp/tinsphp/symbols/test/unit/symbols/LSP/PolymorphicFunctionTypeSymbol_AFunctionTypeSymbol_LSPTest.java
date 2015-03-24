/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IConstraintSolver;
import ch.tsphp.tinsphp.common.symbols.IFunctionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbol;
import ch.tsphp.tinsphp.common.symbols.ITypeVariableSymbolWithRef;
import ch.tsphp.tinsphp.symbols.PolymorphicFunctionTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.AFunctionTypeSymbolTest;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PolymorphicFunctionTypeSymbol_AFunctionTypeSymbol_LSPTest extends AFunctionTypeSymbolTest
{
    @Override
    protected IFunctionTypeSymbol createFunctionTypeSymbol(
            String name,
            List<String> parameterIds,
            ITypeSymbol parentTypeSymbol) {

        List<ITypeVariableSymbolWithRef> parameters = new ArrayList<>();
        for (String parameterId : parameterIds) {
            ITypeVariableSymbolWithRef parameter = mock(ITypeVariableSymbolWithRef.class);
            when(parameter.getName()).thenReturn(parameterId);
            parameters.add(parameter);
        }

        return new PolymorphicFunctionTypeSymbol(
                name, parameters, null, mock(ITypeVariableSymbolWithRef.class), new ArrayDeque<ITypeVariableSymbol>(),
                mock(ISymbolFactory.class), mock(IConstraintSolver.class));
    }
}
