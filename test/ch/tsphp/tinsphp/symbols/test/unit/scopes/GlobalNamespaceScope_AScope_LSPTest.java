/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

/*
 * This class is based on the class GlobalNamespaceScope_AScope_LSPTest from the TSPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.scopes;

import ch.tsphp.common.IScope;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.symbols.scopes.AScope;
import ch.tsphp.tinsphp.symbols.scopes.GlobalNamespaceScope;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class GlobalNamespaceScope_AScope_LSPTest extends AScopeTest
{

    @Override
    protected AScope createScope(IScopeHelper scopeHelper, String name, IScope enclosingScope) {
        return new GlobalNamespaceScope(scopeHelper, name);
    }

    @Override
    @Test
    public void getEnclosingScope_Standard_ReturnsScopePassedInConstructor() {
        // different behaviour - returns always null
        // yet, does not really violate the Liskov Substitution Principle
        // since it returns the enclosing scope (which is always null for a GlobalNamespaceScope)

        IScope enclosingScope = mock(IScope.class);

        AScope scope = createScope(mock(IScopeHelper.class), "foo", enclosingScope);
        IScope result = scope.getEnclosingScope();

        assertNull(result);
    }
}
