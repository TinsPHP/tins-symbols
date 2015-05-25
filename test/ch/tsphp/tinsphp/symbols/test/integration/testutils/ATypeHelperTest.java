/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.testutils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.ITypeHelper;
import ch.tsphp.tinsphp.symbols.IntersectionTypeSymbol;
import ch.tsphp.tinsphp.symbols.UnionTypeSymbol;
import ch.tsphp.tinsphp.symbols.test.unit.testutils.ATypeTest;
import ch.tsphp.tinsphp.symbols.utils.TypeHelper;
import org.junit.Ignore;

@Ignore
public abstract class ATypeHelperTest extends ATypeTest
{

    protected IUnionTypeSymbol createUnion(ITypeSymbol... typeSymbols) {
        IUnionTypeSymbol unionTypeSymbol = new UnionTypeSymbol(createTypeHelper());
        for (ITypeSymbol typeSymbol : typeSymbols) {
            unionTypeSymbol.addTypeSymbol(typeSymbol);
        }
        return unionTypeSymbol;
    }

    protected IIntersectionTypeSymbol createIntersectionType(ITypeSymbol... typeSymbols) {
        IIntersectionTypeSymbol intersectionTypeSymbol
                = new IntersectionTypeSymbol(createTypeHelperAndSetMixed());
        for (ITypeSymbol typeSymbol : typeSymbols) {
            intersectionTypeSymbol.addTypeSymbol(typeSymbol);
        }
        return intersectionTypeSymbol;
    }

    protected ITypeHelper createTypeHelperAndSetMixed() {
        ITypeHelper typeHelper = createTypeHelper();
        typeHelper.setMixedTypeSymbol(mixedType);
        return typeHelper;
    }

    protected ITypeHelper createTypeHelper() {
        return new TypeHelper();
    }

}
