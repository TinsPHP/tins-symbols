/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;
import ch.tsphp.tinsphp.symbols.ModifierSet;
import ch.tsphp.tinsphp.symbols.PrimitiveTypeNames;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.utils.OverloadResolver;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class UnionTypeSymbolTest
{

    @Test
    public void getTypeSymbols_Standard_ReturnsSetPassedByConstructor() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.NULL, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.FALSE, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.TRUE, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.INT, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.FLOAT, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.NUM, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.STRING, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.SCALAR, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.ARRAY, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.RESOURCE, mock(ITypeSymbol.class));
        types.put(PrimitiveTypeNames.MIXED, mock(ITypeSymbol.class));

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol(types);
        Map<String, ITypeSymbol> result = typeSymbol.getTypeSymbols();

        assertThat(result, is(types));
    }

    @Test
    public void isFalseable_ContainsFalseType_ReturnsTrue() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.FALSE, mock(ITypeSymbol.class));

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol(types);
        boolean result = typeSymbol.isFalseable();

        assertThat(result, is(true));
    }

    @Test
    public void isFalseable_DoesNotContainFalseType_ReturnsFalse() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.BOOL, mock(ITypeSymbol.class));

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol(types);
        boolean result = typeSymbol.isFalseable();

        assertThat(result, is(false));
    }

    @Test
    public void isFalseable_ContainsNullType_ReturnsTrue() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.NULL, mock(ITypeSymbol.class));

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol(types);
        boolean result = typeSymbol.isNullable();

        assertThat(result, is(true));
    }

    @Test
    public void isNullable_DoesNotContainNullType_ReturnsFalse() {
        Map<String, ITypeSymbol> types = new HashMap<>();
        types.put(PrimitiveTypeNames.BOOL, mock(ITypeSymbol.class));

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol(types);
        boolean result = typeSymbol.isNullable();

        assertThat(result, is(false));
    }

    //TODO move to ALazyTypeSymbolTest
    @Test
    public void getName_Standard_ReturnsQuestionMark() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        String result = typeSymbol.getName();

        assertThat(result, is("?"));
    }

    //TODO move to ALazyTypeSymbolTest
    @Test
    public void getAbsoluteName_Standard_ReturnsQuestionMark() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        String result = typeSymbol.getAbsoluteName();

        assertThat(result, is("?"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getParentTypeSymbols_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.getParentTypeSymbols();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefaultValue_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.getDefaultValue();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.addModifier(123);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeModifier_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.removeModifier(123);

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.getModifiers();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setModifiers_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.setModifiers(new ModifierSet());

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getDefinitionAst_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.getDefinitionAst();

        //assert in annotation
    }



    @Test(expected = UnsupportedOperationException.class)
    public void getDefinitionScope_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.getDefinitionScope();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setDefinitionScope_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.setDefinitionScope(mock(IScope.class));

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.getType();

        //assert in annotation
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setType_Standard_ThrowsUnsupportedOperationException() {
        //no arrange necessary

        IUnionTypeSymbol typeSymbol = createUnionTypeSymbol();
        typeSymbol.setType(mock(ITypeSymbol.class));

        //assert in annotation
    }

    private IUnionTypeSymbol createUnionTypeSymbol() {
        return createUnionTypeSymbol(new OverloadResolver());
    }

    private IUnionTypeSymbol createUnionTypeSymbol(Map<String, ITypeSymbol> typeSymbols) {
        return createUnionTypeSymbol(new OverloadResolver(), typeSymbols);
    }

    protected IUnionTypeSymbol createUnionTypeSymbol(IOverloadResolver overloadResolver) {
        return new UnionTypeSymbol(overloadResolver);
    }

    protected IUnionTypeSymbol createUnionTypeSymbol(
            IOverloadResolver overloadResolver, Map<String, ITypeSymbol> typeSymbols) {
        return new UnionTypeSymbol(overloadResolver, typeSymbols);
    }

}
