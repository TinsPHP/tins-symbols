/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.test.integration.testutils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.utils.Pair;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeParameterConstraintsMatcher extends BaseMatcher<Map<String, Set<ITypeSymbol>>>
{
    final Pair<String, Set<String>>[] expConstraints;

    @SafeVarargs
    public static Matcher<? super Map<String, Set<ITypeSymbol>>> isConstraints(Pair<String, Set<String>>... dtos) {
        return new TypeParameterConstraintsMatcher(dtos);
    }


    public TypeParameterConstraintsMatcher(Pair<String, Set<String>>[] theConstraints) {
        expConstraints = theConstraints;
    }

    @Override
    public boolean matches(Object item) {
        Map<String, Set<ITypeSymbol>> constraints = (Map<String, Set<ITypeSymbol>>) item;
        boolean ok = constraints.size() == expConstraints.length;
        if (ok) {
            for (int i = 0; i < expConstraints.length; ++i) {
                Set<ITypeSymbol> typeSymbols = constraints.get(expConstraints[i].first);
                if (typeSymbols == null) {
                    ok = false;
                    break;
                }
                List<String> typeSymbolNames = new ArrayList<>(typeSymbols.size());
                for (ITypeSymbol typeSymbol : typeSymbols) {
                    typeSymbolNames.add(typeSymbol.getAbsoluteName());
                }
                ok = typeSymbolNames.containsAll(expConstraints[i].second);
                if (!ok) {
                    break;
                }
            }
        }
        return ok;
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        Map<String, Set<ITypeSymbol>> constraints = (Map<String, Set<ITypeSymbol>>) item;
        description.appendText("[");
        for (Map.Entry<String, Set<ITypeSymbol>> entry : constraints.entrySet()) {
            description.appendText(entry.getKey()).appendText(":[");
            Iterator<ITypeSymbol> iterator = entry.getValue().iterator();
            if (iterator.hasNext()) {
                ITypeSymbol typeSymbol = iterator.next();
                description.appendText(typeSymbol.getAbsoluteName());
            }
            while (iterator.hasNext()) {
                ITypeSymbol typeSymbol = iterator.next();
                description.appendText(", ").appendText(typeSymbol.getAbsoluteName());
            }
        }
        description.appendText("]");
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("[");
        for (int i = 0; i < expConstraints.length; ++i) {
            if (i != 0) {
                description.appendText(", ");
            }
            description.appendText(expConstraints[i].first).appendText(":")
                    .appendText(expConstraints[i].second.toString());
        }
        description.appendText("]");
    }

}
