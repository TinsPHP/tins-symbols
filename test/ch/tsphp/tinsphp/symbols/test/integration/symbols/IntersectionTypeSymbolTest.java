/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.symbols;

import ch.tsphp.tinsphp.common.inference.constraints.IBindingCollection;
import ch.tsphp.tinsphp.common.inference.constraints.TypeVariableReference;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.ModifierHelper;
import ch.tsphp.tinsphp.symbols.scopes.ScopeHelper;
import ch.tsphp.tinsphp.symbols.test.integration.testutils.ATypeHelperTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

public class IntersectionTypeSymbolTest extends ATypeHelperTest
{

    @Test
    public void isFixed_ContainsNonFixedIsReplacedByFixed_ReturnsTrue() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol containerTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        String tx = "Tx";
        ISymbolFactory symbolFactory = createSymbolFactory();
        IConvertibleTypeSymbol asTx = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, numType);
        bindingCollection.bind(asTx, asList(tx));
        containerTypeSymbol.addTypeSymbol(asTx);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        asTy.addLowerTypeBound(intType);
        asTy.addUpperTypeBound(intType);

        containerTypeSymbol.addTypeSymbol(asTy);
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, Is.is(true));
    }

    @Test
    public void isFixed_ContainsFixedIsReplaceByNonFixed_ReturnsFalse() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol containerTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        String tx = "Tx";
        ISymbolFactory symbolFactory = createSymbolFactory();
        IConvertibleTypeSymbol asTx = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.bind(asTx, asList(tx));
        containerTypeSymbol.addTypeSymbol(mixedType);

        containerTypeSymbol.addTypeSymbol(asTx);
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, Is.is(false));
    }


    @Test
    public void isFixed_ContainsNonFixedIsFixedFromOutside_ReturnsTrue() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol containerTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        String tx = "Tx";
        ISymbolFactory symbolFactory = createSymbolFactory();
        IConvertibleTypeSymbol asTx = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.bind(asTx, asList(tx));
        containerTypeSymbol.addTypeSymbol(asTx);

        bindingCollection.fixTypeParameter(tx);
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, Is.is(true));
    }

    @Test
    public void isFixed_ContainsTwoNonFixedAndOneIsFixedFromOutside_ReturnsFalse() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol containerTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        ISymbolFactory symbolFactory = createSymbolFactory();
        IConvertibleTypeSymbol asTx = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(ty, stringType);
        bindingCollection.bind(asTx, asList(tx));
        bindingCollection.bind(asTy, asList(ty));
        containerTypeSymbol.addTypeSymbol(asTx);
        containerTypeSymbol.addTypeSymbol(asTy);

        bindingCollection.fixTypeParameter(tx);
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, Is.is(false));
    }

    @Test
    public void isFixed_ContainsNonFixedUnionWithTwoNonFixedTypesAndIsReplacedByFixed_ReturnsTrue() {
        //pre-act necessary for arrange
        IIntersectionTypeSymbol containerTypeSymbol = createIntersectionTypeSymbol();

        //arrange
        String tx = "Tx";
        String ty = "Ty";
        ISymbolFactory symbolFactory = createSymbolFactory();
        IConvertibleTypeSymbol asTx = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        IConvertibleTypeSymbol asTy = createConvertibleTypeSymbol(symbolFactory, typeHelper);
        IBindingCollection bindingCollection = symbolFactory.createBindingCollection();
        bindingCollection.addVariable("$x", new TypeVariableReference(tx));
        bindingCollection.addVariable("$y", new TypeVariableReference(ty));
        bindingCollection.addUpperTypeBound(tx, intType);
        bindingCollection.addUpperTypeBound(ty, stringType);
        bindingCollection.bind(asTx, asList(tx));
        bindingCollection.bind(asTy, asList(ty));
        IUnionTypeSymbol intersectionTypeSymbol = createUnionTypeSymbol(asTx, asTy);
        containerTypeSymbol.addTypeSymbol(intersectionTypeSymbol);

        containerTypeSymbol.addTypeSymbol(intType);
        boolean result = containerTypeSymbol.isFixed();

        assertThat(result, Is.is(true));
    }

    private ISymbolFactory createSymbolFactory() {
        return createSymbolFactory(new ScopeHelper(), new ModifierHelper(), typeHelper);
    }
}
