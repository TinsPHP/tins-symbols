/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.inference.constraints.IOverloadResolver;

import java.util.Set;

public class OverloadResolver implements IOverloadResolver
{
    @Override
    public boolean isFirstSameOrSubTypeOfSecond(ITypeSymbol potentialSubType, ITypeSymbol typeSymbol) {
        return getPromotionLevelFromTo(potentialSubType, typeSymbol) != -1;
    }

    @Override
    public boolean isFirstSameOrParentTypeOfSecond(ITypeSymbol potentialParentType, ITypeSymbol typeSymbol) {
        return getPromotionLevelFromTo(typeSymbol, potentialParentType) != -1;
    }

    @Override
    public boolean isSameOrSubType(int promotionLevel) {
        return promotionLevel != -1;
    }

    @Override
    public int getPromotionLevelFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        if (actualParameterType instanceof IUnionTypeSymbol) {
            return getPromoLevelActualIsUnion((IUnionTypeSymbol) actualParameterType, formalParameterType);
        }
        return getPromoLevelActualIsNotUnion(actualParameterType, formalParameterType);
    }

    private int getPromoLevelActualIsNotUnion(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        if (formalParameterType instanceof IUnionTypeSymbol) {
            return getPromoLevelFormalIsUnion(actualParameterType, (IUnionTypeSymbol) formalParameterType);
        }
        return getPromoLevelWithoutUnion(actualParameterType, formalParameterType);
    }

    private int getPromoLevelActualIsUnion(IUnionTypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        // an empty union is the bottom type of all types and hence is a sub-type of all types
        // hence the initial promotion level of 0 in case no type exist within the union
        int highestPromotionLevel = 0;

        //All actual types must be sub-types of formal
        for (ITypeSymbol typeSymbol : actualParameterType.getTypeSymbols().values()) {
            int promotionLevel = getPromoLevelActualIsNotUnion(typeSymbol, formalParameterType);
            if (isSameOrSubType(promotionLevel)) {
                if (highestPromotionLevel < promotionLevel) {
                    highestPromotionLevel = promotionLevel;
                }
            } else {
                highestPromotionLevel = -1;
                break;
            }
        }
        return highestPromotionLevel;
    }

    private int getPromoLevelFormalIsUnion(ITypeSymbol actualParameterType, IUnionTypeSymbol formalParameterType) {
        int highestPromotionLevel = -1;
        //Actual needs to be a sub-type of at least one formal type
        for (ITypeSymbol typeSymbol : formalParameterType.getTypeSymbols().values()) {
            int promotionLevel = getPromoLevelWithoutUnion(actualParameterType, typeSymbol);
            if (isSameOrSubType(promotionLevel)) {
                if (highestPromotionLevel < promotionLevel) {
                    highestPromotionLevel = promotionLevel;
                }
            }
        }
        return highestPromotionLevel;
    }

    private int getPromoLevelWithoutUnion(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        int promotionLevel = 0;
        if (actualParameterType != formalParameterType) {
            promotionLevel = -1;
            Set<ITypeSymbol> parentTypes = actualParameterType.getParentTypeSymbols();
            for (ITypeSymbol parentType : parentTypes) {
                int parentPromotionLevel = getPromoLevelWithoutUnion(parentType, formalParameterType);
                if (parentPromotionLevel != -1) {
                    if (promotionLevel == -1 || promotionLevel > parentPromotionLevel + 1) {
                        //+1 since its not the actual parameter but the parent of the actual
                        promotionLevel = parentPromotionLevel + 1;
                    }
                }

            }
        }
        return promotionLevel;
    }
}
