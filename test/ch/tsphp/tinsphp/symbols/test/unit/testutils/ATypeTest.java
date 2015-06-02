/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.testutils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.IConversionMethod;
import ch.tsphp.tinsphp.common.core.IConversionsProvider;
import ch.tsphp.tinsphp.common.symbols.ISymbolFactory;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.common.utils.Pair;
import ch.tsphp.tinsphp.symbols.ConvertibleTypeSymbol;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.constraints.OverloadBindings;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public abstract class ATypeTest
{
    protected static ITypeSymbol mixedType;
    protected static ITypeSymbol arrayType;
    protected static ITypeSymbol scalarType;
    protected static ITypeSymbol stringType;
    protected static ITypeSymbol numType;
    protected static ITypeSymbol floatType;
    protected static ITypeSymbol intType;
    protected static ITypeSymbol boolType;
    protected static ITypeSymbol nothingType;

    protected static ITypeSymbol interfaceAType;
    protected static ITypeSymbol interfaceSubAType;
    protected static ITypeSymbol interfaceBType;
    protected static ITypeSymbol fooType;

    protected static ISymbolFactory symbolFactory;
    protected static ITypeHelper typeHelper;

    @BeforeClass
    public static void init() {
        mixedType = mock(ITypeSymbol.class);
        when(mixedType.getAbsoluteName()).thenReturn("mixed");

        arrayType = mock(ITypeSymbol.class);
        when(arrayType.getParentTypeSymbols()).thenReturn(set(mixedType));
        when(arrayType.getAbsoluteName()).thenReturn("array");

        scalarType = mock(ITypeSymbol.class);
        when(scalarType.getParentTypeSymbols()).thenReturn(set(mixedType));
        when(scalarType.getAbsoluteName()).thenReturn("scalar");

        stringType = mock(ITypeSymbol.class);
        when(stringType.getParentTypeSymbols()).thenReturn(set(scalarType));
        when(stringType.getAbsoluteName()).thenReturn("string");

        numType = mock(ITypeSymbol.class);
        when(numType.getParentTypeSymbols()).thenReturn(set(scalarType));
        when(numType.getAbsoluteName()).thenReturn("num");

        floatType = mock(ITypeSymbol.class);
        when(floatType.getParentTypeSymbols()).thenReturn(set(numType));
        when(floatType.getAbsoluteName()).thenReturn("float");

        intType = mock(ITypeSymbol.class);
        when(intType.getParentTypeSymbols()).thenReturn(set(numType));
        when(intType.getAbsoluteName()).thenReturn("int");

        boolType = mock(ITypeSymbol.class);
        when(boolType.getParentTypeSymbols()).thenReturn(set(scalarType));
        when(boolType.getAbsoluteName()).thenReturn("bool");

        nothingType = mock(ITypeSymbol.class);
        when(nothingType.getAbsoluteName()).thenReturn("nothing");

        interfaceAType = mock(ITypeSymbol.class);
        when(interfaceAType.getParentTypeSymbols()).thenReturn(set(mixedType));
        when(interfaceAType.getAbsoluteName()).thenReturn("IA");
        when(interfaceAType.canBeUsedInIntersection()).thenReturn(true);

        interfaceSubAType = mock(ITypeSymbol.class);
        when(interfaceSubAType.getParentTypeSymbols()).thenReturn(set(interfaceAType));
        when(interfaceSubAType.getAbsoluteName()).thenReturn("ISubA");
        when(interfaceSubAType.canBeUsedInIntersection()).thenReturn(true);

        interfaceBType = mock(ITypeSymbol.class);
        when(interfaceBType.getParentTypeSymbols()).thenReturn(set(mixedType));
        when(interfaceBType.getAbsoluteName()).thenReturn("IB");
        when(interfaceBType.canBeUsedInIntersection()).thenReturn(true);

        fooType = mock(ITypeSymbol.class);
        when(fooType.getParentTypeSymbols()).thenReturn(set(interfaceSubAType, interfaceBType));
        when(fooType.getAbsoluteName()).thenReturn("Foo");


        typeHelper = new TypeHelper();
        symbolFactory = mock(ISymbolFactory.class);
        typeHelper.setMixedTypeSymbol(mixedType);
        when(symbolFactory.getMixedTypeSymbol()).thenReturn(mixedType);
        when(symbolFactory.createUnionTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new UnionTypeSymbol(typeHelper);
            }
        });
        when(symbolFactory.createIntersectionTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new IntersectionTypeSymbol(typeHelper);
            }
        });
        when(symbolFactory.createConvertibleTypeSymbol()).then(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new ConvertibleTypeSymbol(new OverloadBindings(symbolFactory, typeHelper));
            }
        });
    }

    @Before
    public void setUp() {
        IConversionsProvider conversionsProvider = mock(IConversionsProvider.class);
        HashMap<String, Map<String, Pair<ITypeSymbol, IConversionMethod>>> conversions = new HashMap<>();
        when(conversionsProvider.getImplicitConversions()).thenReturn(conversions);
        when(conversionsProvider.getExplicitConversions()).thenReturn(conversions);
        typeHelper.setConversionsProvider(conversionsProvider);
    }

    protected static HashSet<ITypeSymbol> set(ITypeSymbol... symbols) {
        return new HashSet<>(Arrays.asList(symbols));
    }
}
