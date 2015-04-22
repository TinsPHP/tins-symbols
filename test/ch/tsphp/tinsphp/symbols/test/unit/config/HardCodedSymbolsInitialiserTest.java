/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.config;

import ch.tsphp.tinsphp.common.config.ISymbolsInitialiser;
import ch.tsphp.tinsphp.common.scopes.IScopeFactory;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.config.HardCodedSymbolsInitialiser;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class HardCodedSymbolsInitialiserTest
{

    @Test
    public void getScopeHelper_SecondCall_ReturnsSameAsFirst() {
        ISymbolsInitialiser initialiser = createInitialiser();
        IScopeHelper firstCall = initialiser.getScopeHelper();

        IScopeHelper result = initialiser.getScopeHelper();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getScopeHelper_SecondCallAfterReset_ReturnsSameAsFirstBeforeReset() {
        ISymbolsInitialiser initialiser = createInitialiser();
        IScopeHelper firstCall = initialiser.getScopeHelper();
        initialiser.reset();

        IScopeHelper result = initialiser.getScopeHelper();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getOverloadResolver_SecondCall_ReturnsSameAsFirst() {
        ISymbolsInitialiser initialiser = createInitialiser();
        IOverloadResolver firstCall = initialiser.getOverloadResolver();

        IOverloadResolver result = initialiser.getOverloadResolver();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getOverloadResolver_SecondCallAfterReset_ReturnsSameAsFirstBeforeReset() {
        ISymbolsInitialiser initialiser = createInitialiser();
        IOverloadResolver firstCall = initialiser.getOverloadResolver();
        initialiser.reset();

        IOverloadResolver result = initialiser.getOverloadResolver();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getModifierHelper_SecondCall_ReturnsSameAsFirst() {
        ISymbolsInitialiser initialiser = createInitialiser();
        IModifierHelper firstCall = initialiser.getModifierHelper();

        IModifierHelper result = initialiser.getModifierHelper();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getModifierHelper_SecondCallAfterReset_ReturnsSameAsFirstBeforeReset() {
        ISymbolsInitialiser initialiser = createInitialiser();
        IModifierHelper firstCall = initialiser.getModifierHelper();
        initialiser.reset();

        IModifierHelper result = initialiser.getModifierHelper();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getSymbolFactory_SecondCall_ReturnsSameAsFirst() {
        ISymbolsInitialiser initialiser = createInitialiser();
        ISymbolFactory firstCall = initialiser.getSymbolFactory();

        ISymbolFactory result = initialiser.getSymbolFactory();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getSymbolFactory_SecondCallAfterReset_ReturnsSameAsFirstBeforeReset() {
        ISymbolsInitialiser initialiser = createInitialiser();
        ISymbolFactory firstCall = initialiser.getSymbolFactory();
        initialiser.reset();

        ISymbolFactory result = initialiser.getSymbolFactory();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getScopeFactory_SecondCall_ReturnsSameAsFirst() {
        ISymbolsInitialiser initialiser = createInitialiser();
        IScopeFactory firstCall = initialiser.getScopeFactory();

        IScopeFactory result = initialiser.getScopeFactory();

        assertThat(result, is(firstCall));
    }

    @Test
    public void getScopeFactory_SecondCallAfterReset_ReturnsSameAsFirstBeforeReset() {
        ISymbolsInitialiser initialiser = createInitialiser();
        IScopeFactory firstCall = initialiser.getScopeFactory();
        initialiser.reset();

        IScopeFactory result = initialiser.getScopeFactory();

        assertThat(result, is(firstCall));
    }

    protected ISymbolsInitialiser createInitialiser() {
        return new HardCodedSymbolsInitialiser();
    }
}
