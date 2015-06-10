/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.constraints;

import java.util.Map;
import java.util.Set;

public class PropagationDto
{
    public final String returnTypeVariable;
    public final Set<String> parameterTypeVariables;
    public final Map<String, Set<String>> typeVariablesToVisit;
    public final Set<String> typeParameters;
    public final Set<String> recursiveTypeParameters;
    public final Set<String> removeReturnTypeVariable;

    public PropagationDto(
            String theReturnTypeVariable,
            Set<String> theParameterTypeVariables,
            Map<String, Set<String>> theTypeVariablesToVisit,
            Set<String> theParametricParameterTypeVariables,
            Set<String> theRecursiveParameters,
            Set<String> theRemoveReturnTypeVariable) {
        returnTypeVariable = theReturnTypeVariable;
        parameterTypeVariables = theParameterTypeVariables;
        typeVariablesToVisit = theTypeVariablesToVisit;
        typeParameters = theParametricParameterTypeVariables;
        recursiveTypeParameters = theRecursiveParameters;
        removeReturnTypeVariable = theRemoveReturnTypeVariable;
    }
}
