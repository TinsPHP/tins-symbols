/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IClassTypeSymbol;
import ch.tsphp.tinsphp.symbols.ClassTypeSymbol;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassTypeSymbolTest
{

    @Test
    public void getParent_NothingDefined_ReturnsNull() {
        //no arrange necessary

        IClassTypeSymbol symbol = createClassTypeSymbol();
        IClassTypeSymbol result = symbol.getParent();

        assertThat(result, is(nullValue()));
    }

    private IClassTypeSymbol createClassTypeSymbol() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.getName()).thenReturn("dummy");
        return createClassTypeSymbol(
                mock(IScopeHelper.class),
                mock(ITSPHPAst.class),
                new ModifierSet(),
                "foo",
                mock(IScope.class),
                typeSymbol);
    }

    protected IClassTypeSymbol createClassTypeSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet modifiers,
            String name,
            IScope enclosingScope,
            ITypeSymbol parentTypeSymbol) {
        return new ClassTypeSymbol(scopeHelper, definitionAst, modifiers, name, enclosingScope, parentTypeSymbol);
    }
}
