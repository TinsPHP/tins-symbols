/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols.LSP;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IRecordTypeSymbol;
import ch.tsphp.tinsphp.symbols.ClassTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.symbols.APolymorphicTypeSymbolTest;

public class ClassTypeSymbol_APolymorphicTypeSymbol_LSPTest extends APolymorphicTypeSymbolTest
{
    protected IRecordTypeSymbol createPolymorphicTypeSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            IModifierSet modifiers,
            String name,
            IScope enclosingScope,
            ITypeSymbol parentTypeSymbol) {
        return new ClassTypeSymbol(scopeHelper, definitionAst, modifiers, name, enclosingScope, parentTypeSymbol);
    }
}
