/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.TSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import org.antlr.runtime.CommonToken;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class ModifierHelperTest
{

    @Test
    public void getModifiers_ModifierIsNull_ReturnsEmptyModifierSet() {
        //no arrange necessary

        IModifierHelper modifierHelper = createModifierHelper();
        IModifierSet result = modifierHelper.getModifiers(null);

        assertThat(result, is(empty()));
    }

    @Test
    public void getModifiers_HasNoChildren_ReturnsEmptyModifierSet() {
        ITSPHPAst ast = new TSPHPAst();

        IModifierHelper modifierHelper = createModifierHelper();
        IModifierSet result = modifierHelper.getModifiers(ast);

        assertThat(result, is(empty()));
    }

    @Test
    public void getModifiers_HasOneChild_ReturnsModifierSetWithOneModifier() {
        int tokenType = 10;
        ITSPHPAst ast = new TSPHPAst();
        ast.addChild(new TSPHPAst(new CommonToken(tokenType)));

        IModifierHelper modifierHelper = createModifierHelper();
        IModifierSet result = modifierHelper.getModifiers(ast);

        assertThat(result.size(), is(1));
        assertThat(result, hasItem(tokenType));
    }

    @Test
    public void getModifiers_HasTwoChildren_ReturnsModifierSetWithTwoModifiers() {
        int tokenType = 10;
        int tokenType2 = 123;
        ITSPHPAst ast = new TSPHPAst();
        ast.addChild(new TSPHPAst(new CommonToken(tokenType)));
        ast.addChild(new TSPHPAst(new CommonToken(tokenType2)));

        IModifierHelper modifierHelper = createModifierHelper();
        IModifierSet result = modifierHelper.getModifiers(ast);

        assertThat(result.size(), is(2));
        assertThat(result, hasItems(tokenType, tokenType2));
    }

    protected IModifierHelper createModifierHelper() {
        return new ModifierHelper();
    }
}
