/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.utils;

import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IIntersectionTypeSymbol;
import ch.tsphp.tinsphp.common.symbols.IUnionTypeSymbol;
import ch.tsphp.tinsphp.common.utils.IOverloadResolver;

import java.util.Collection;
import java.util.Set;

public class OverloadResolver implements IOverloadResolver
{
    private ITypeSymbol mixedTypeSymbol;

    @Override
    public void setMixedTypeSymbol(ITypeSymbol typeSymbol) {
        mixedTypeSymbol = typeSymbol;
    }

    @Override
    public boolean isFirstSameOrSubTypeOfSecond(ITypeSymbol potentialSubType, ITypeSymbol typeSymbol) {
        return getPromotionLevelFromTo(potentialSubType, typeSymbol) != -1;
    }

    @Override
    public boolean isFirstSameOrParentTypeOfSecond(ITypeSymbol potentialParentType, ITypeSymbol typeSymbol) {
        return getPromotionLevelFromTo(typeSymbol, potentialParentType) != -1;
    }

    private int getPromotionLevelFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        if (actualParameterType instanceof IUnionTypeSymbol) {
            return getPromoLevelActualIsUnion((IUnionTypeSymbol) actualParameterType, formalParameterType);
        } else if (actualParameterType instanceof IIntersectionTypeSymbol) {
            return getPromoLevelActualIsIntersection(
                    (IIntersectionTypeSymbol) actualParameterType, formalParameterType);
        }
        return getPromoLevelActualIsNotUnionNorIntersection(actualParameterType, formalParameterType);
    }

    private int getPromoLevelActualIsNotUnionNorIntersection(
            ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        if (formalParameterType instanceof IUnionTypeSymbol) {
            return getPromoLevelFormalIsUnion(actualParameterType, (IUnionTypeSymbol) formalParameterType);
        } else if (formalParameterType instanceof IIntersectionTypeSymbol) {
            return getPromoLevelFormalIsIntersection(
                    actualParameterType, (IIntersectionTypeSymbol) formalParameterType);
        }
        return getPromoLevelWithoutUnionAndIntersection(actualParameterType, formalParameterType);
    }

    private boolean isSameOrSubType(int promotionLevel) {
        return promotionLevel != -1;
    }

    private int getPromoLevelActualIsUnion(IUnionTypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        Collection<ITypeSymbol> typeSymbols = actualParameterType.getTypeSymbols().values();

        // an empty union is the bottom type of all types and hence is a subtype of all types
        int highestPromotionLevel = 1;

        if (!typeSymbols.isEmpty()) {
            highestPromotionLevel = allMustBeSameOrSubTypes(typeSymbols, formalParameterType);
        } else if (isBottomTypeAsWell(formalParameterType)) {
            highestPromotionLevel = 0;
        }

        return highestPromotionLevel;
    }

    private boolean isBottomTypeAsWell(ITypeSymbol formalParameterType) {
        return formalParameterType instanceof IUnionTypeSymbol
                && ((IUnionTypeSymbol) formalParameterType).getTypeSymbols().isEmpty();
    }

    private int getPromoLevelActualIsIntersection(
            IIntersectionTypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        Collection<ITypeSymbol> typeSymbols = actualParameterType.getTypeSymbols().values();

        int highestPromotionLevel;

        if (!typeSymbols.isEmpty()) {
            if (formalParameterType instanceof IIntersectionTypeSymbol) {
                // in this case, each type in the intersection type of the formal parameter must be a parent type of
                // the actual parameter type. Following an example: int & float & bool < int & bool because:
                //   int & float & bool < int
                //   int & float & bool < float
                // Another example which does not meet these criteria int & float < int & string is wrong since:
                //   int & float </ string (is not a subtype)
                //
                Collection<ITypeSymbol> formalParameterTypes
                        = ((IIntersectionTypeSymbol) formalParameterType).getTypeSymbols().values();

                highestPromotionLevel = allMustBeSameOrParentTypes(formalParameterTypes, actualParameterType);
            } else {
                highestPromotionLevel = oneMustBeSameOrSubType(typeSymbols, formalParameterType);
            }
        } else {
            highestPromotionLevel = getPromotionLevelFromTo(mixedTypeSymbol, formalParameterType);
        }

        return highestPromotionLevel;
    }

    private int getPromoLevelFormalIsUnion(ITypeSymbol actualParameterType, IUnionTypeSymbol formalParameterType) {
        //if union is empty, then it cannot be a subtype or the same (if actual was an empty union type as well,
        // then this was already addressed in getPromoLevelActualIsUnion)

        Collection<ITypeSymbol> typeSymbols = formalParameterType.getTypeSymbols().values();
        return oneMustBeSameOrParentType(typeSymbols, actualParameterType);
    }

    private int getPromoLevelFormalIsIntersection(
            ITypeSymbol actualParameterType, IIntersectionTypeSymbol formalParameterType) {

        Collection<ITypeSymbol> typeSymbols = formalParameterType.getTypeSymbols().values();

        int highestPromotionLevel = 0;

        if (!typeSymbols.isEmpty()) {
            //All actual types must be subtypes of formal
            for (ITypeSymbol typeSymbol : typeSymbols) {
                int promotionLevel = getPromotionLevelFromTo(actualParameterType, typeSymbol);
                if (isSameOrSubType(promotionLevel)) {
                    if (highestPromotionLevel < promotionLevel) {
                        highestPromotionLevel = promotionLevel;
                    }
                } else {
                    highestPromotionLevel = -1;
                    break;
                }
            }
        } else {
            // an empty intersection is the top type of all types and hence is a parent type of all types,
            // it is represented by mixed
            highestPromotionLevel = getPromotionLevelFromTo(actualParameterType, mixedTypeSymbol);
        }

        return highestPromotionLevel;
    }

    private int allMustBeSameOrParentTypes(
            Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompareWith) {

        int highestPromotionLevel = 0;

        //All actual types must be the same or a parent type of the formal
        for (ITypeSymbol typeSymbol : typeSymbols) {
            int promotionLevel = getPromotionLevelFromTo(typeSymbolToCompareWith, typeSymbol);
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

    private int allMustBeSameOrSubTypes(
            Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompareWith) {

        int highestPromotionLevel = 0;

        //All actual types must be subtypes of formal
        for (ITypeSymbol typeSymbol : typeSymbols) {
            int promotionLevel = getPromotionLevelFromTo(typeSymbol, typeSymbolToCompareWith);
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

    private int oneMustBeSameOrSubType(Collection<ITypeSymbol> actualParameterTypes, ITypeSymbol formalParameterType) {
        int highestPromotionLevel = -1;

        //One type needs to be a subtype the formal
        for (ITypeSymbol typeSymbol : actualParameterTypes) {
            int promotionLevel = getPromotionLevelFromTo(typeSymbol, formalParameterType);
            if (isSameOrSubType(promotionLevel)) {
                if (highestPromotionLevel < promotionLevel) {
                    highestPromotionLevel = promotionLevel;
                }
            }
        }
        return highestPromotionLevel;
    }

    private int oneMustBeSameOrParentType(Collection<ITypeSymbol> typeSymbols, ITypeSymbol typeSymbolToCompare) {
        int highestPromotionLevel = -1;

        for (ITypeSymbol typeSymbol : typeSymbols) {
            int promotionLevel = getPromotionLevelFromTo(typeSymbolToCompare, typeSymbol);
            if (isSameOrSubType(promotionLevel)) {
                if (highestPromotionLevel < promotionLevel) {
                    highestPromotionLevel = promotionLevel;
                }
            }
        }

        return highestPromotionLevel;
    }

    private int getPromoLevelWithoutUnionAndIntersection(ITypeSymbol actualParameterType,
            ITypeSymbol formalParameterType) {
        int promotionLevel = 0;
        if (actualParameterType != formalParameterType) {
            promotionLevel = -1;
            Set<ITypeSymbol> parentTypes = actualParameterType.getParentTypeSymbols();
            for (ITypeSymbol parentType : parentTypes) {
                int parentPromotionLevel = getPromoLevelWithoutUnionAndIntersection(parentType, formalParameterType);
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
