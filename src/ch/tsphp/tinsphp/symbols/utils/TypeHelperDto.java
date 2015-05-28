/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.utils;

import ch.tsphp.common.symbols.ITypeSymbol;

public class TypeHelperDto
{
    public ITypeSymbol fromType;
    public ITypeSymbol toType;
    boolean shallConsiderImplicitConversions;

    public TypeHelperDto(
            ITypeSymbol theFromType, ITypeSymbol theToType, boolean considerImplicitConversions) {
        fromType = theFromType;
        toType = theToType;
        this.shallConsiderImplicitConversions = considerImplicitConversions;
    }

    public TypeHelperDto(TypeHelperDto dto) {
        fromType = dto.fromType;
        toType = dto.toType;
        shallConsiderImplicitConversions = dto.shallConsiderImplicitConversions;
    }
}