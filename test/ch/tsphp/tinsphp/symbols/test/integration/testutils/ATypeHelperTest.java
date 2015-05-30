/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.testutils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.core.IConversionsProvider;
import ch.tsphp.tinsphp.common.scopes.IScopeHelper;
import ch.tsphp.tinsphp.common.symbols.IConvertibleTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IModifierHelper;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.ConvertibleTypeSymbol;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.SymbolFactory;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;
import org.junit.Ignore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.tsphp.tinsphp.common.utils.Pair.pair;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public abstract class ATypeHelperTest extends ATypeTest
{

    protected IUnionTypeSymbol createUnion(ITypeSymbol... typeSymbols) {
        IUnionTypeSymbol unionTypeSymbol = createUnionTypeSymbol();
        for (ITypeSymbol typeSymbol : typeSymbols) {
            unionTypeSymbol.addTypeSymbol(typeSymbol);
        }
        return unionTypeSymbol;
    }

    protected IUnionTypeSymbol createUnionTypeSymbol() {
        return createUnionTypeSymbol(typeHelper);
    }

    protected IUnionTypeSymbol createUnionTypeSymbol(ITypeHelper typeHelper) {
        return new UnionTypeSymbol(typeHelper);
    }


    protected IIntersectionTypeSymbol createIntersectionType(ITypeSymbol... typeSymbols) {
        IIntersectionTypeSymbol intersectionTypeSymbol = createIntersectionTypeSymbol();
        for (ITypeSymbol typeSymbol : typeSymbols) {
            intersectionTypeSymbol.addTypeSymbol(typeSymbol);
        }
        return intersectionTypeSymbol;
    }

    protected IIntersectionTypeSymbol createIntersectionTypeSymbol() {
        return createIntersectionTypeSymbol(typeHelper);
    }

    protected IIntersectionTypeSymbol createIntersectionTypeSymbol(ITypeHelper theTypeHelper) {
        return new IntersectionTypeSymbol(theTypeHelper);
    }

    protected IConvertibleTypeSymbol createConvertibleType(
            ITypeSymbol typeSymbol, ISymbolFactory theSymbolFactory, ITypeHelper theTypeHelper) {
        ConvertibleTypeSymbol convertibleTypeSymbol = createConvertibleType(theSymbolFactory, theTypeHelper);
        convertibleTypeSymbol.addLowerTypeBound(typeSymbol);
        convertibleTypeSymbol.addUpperTypeBound(typeSymbol);
        return convertibleTypeSymbol;
    }

    protected ConvertibleTypeSymbol createConvertibleType() {
        return createConvertibleType(symbolFactory, typeHelper);
    }

    protected ConvertibleTypeSymbol createConvertibleType(ISymbolFactory theSymbolFactory, ITypeHelper theTypeHelper) {
        return new ConvertibleTypeSymbol(
                new OverloadBindings(theSymbolFactory, theTypeHelper));
    }

    @SafeVarargs
    protected final Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> createConversions(
            Pair<ITypeSymbol, List<ITypeSymbol>>... pairs) {

        Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversionsMap = new HashMap<>();
        for (Pair<ITypeSymbol, List<ITypeSymbol>> element : pairs) {
            Map<String, Pair<ITypeSymbol, IConversionMethod>> conversions = new HashMap<>();
            for (ITypeSymbol toType : element.second) {
                conversions.put(toType.getAbsoluteName(), pair(toType, mock(IConversionMethod.class)));
            }
            conversionsMap.put(element.first.getAbsoluteName(), conversions);
        }

        return conversionsMap;
    }

    protected ISymbolFactory createSymbolFactory(
            IScopeHelper theScopeHelper,
            IModifierHelper theModifierHelper,
            ITypeHelper theTypeHelper) {
        return new SymbolFactory(theScopeHelper, theModifierHelper, theTypeHelper);
    }


    protected ITypeHelper createTypeHelperAndInit(
            Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> implicitConversions,
            Map<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> explicitConversions) {
        ITypeHelper typeHelper = new TypeHelper();
        IConversionsProvider conversionsProvider = mock(IConversionsProvider.class);
        when(conversionsProvider.getImplicitConversions()).thenReturn(implicitConversions);
        when(conversionsProvider.getExplicitConversions()).thenReturn(explicitConversions);
        typeHelper.setConversionsProvider(conversionsProvider);
        typeHelper.setMixedTypeSymbol(mixedType);
        return typeHelper;
    }

    protected ITypeHelper createTypeHelperAndInit() {
        return createTypeHelperAndInit(
                new HashMap<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>>(),
                new HashMap<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>>());
    }
}
