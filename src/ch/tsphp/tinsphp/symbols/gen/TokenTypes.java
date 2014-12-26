/* 
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.tinsphp.symbols.gen;

/**
 * This class contains all token types specified by TinsPHP's parser.
 *
 * Do not change this class, it is automatically generated and your changes would be lost with the next update.
 * If you want to change some tokens then be invited to do it directly in the grammar file of the parser (TinsPHP.g)
 */
 public final class TokenTypes
{

    public static final int LogicXorWeak = 4;
    public static final int LogicOrWeak = 5;
    public static final int LogicAndWeak = 6;
    public static final int Assign = 7;
    public static final int BitwiseAndAssign = 8;
    public static final int BitwiseOrAssign = 9;
    public static final int BitwiseXorAssign = 10;
    public static final int PlusAssign = 11;
    public static final int MinusAssign = 12;
    public static final int DotAssign = 13;
    public static final int MultiplyAssign = 14;
    public static final int DivideAssign = 15;
    public static final int ModuloAssign = 16;
    public static final int ShiftLeftAssign = 17;
    public static final int ShiftRightAssign = 18;
    public static final int CAST_ASSIGN = 19;
    public static final int QuestionMark = 20;
    public static final int LogicOr = 21;
    public static final int LogicAnd = 22;
    public static final int BitwiseOr = 23;
    public static final int BitwiseXor = 24;
    public static final int BitwiseAnd = 25;
    public static final int Equal = 26;
    public static final int Identical = 27;
    public static final int NotEqual = 28;
    public static final int NotIdentical = 29;
    public static final int GreaterEqualThan = 30;
    public static final int GreaterThan = 31;
    public static final int LessEqualThan = 32;
    public static final int LessThan = 33;
    public static final int ShiftLeft = 34;
    public static final int ShiftRight = 35;
    public static final int Minus = 36;
    public static final int Plus = 37;
    public static final int Dot = 38;
    public static final int Multiply = 39;
    public static final int Divide = 40;
    public static final int Modulo = 41;
    public static final int Instanceof = 42;
    public static final int CAST = 43;
    public static final int PRE_DECREMENT = 44;
    public static final int PRE_INCREMENT = 45;
    public static final int At = 46;
    public static final int BitwiseNot = 47;
    public static final int LogicNot = 48;
    public static final int UNARY_MINUS = 49;
    public static final int UNARY_PLUS = 50;
    public static final int New = 51;
    public static final int Clone = 52;
    public static final int POST_DECREMENT = 53;
    public static final int POST_INCREMENT = 54;
    public static final int ARRAY_ACCESS = 55;
    public static final int FIELD_ACCESS = 56;
    public static final int CLASS_STATIC_ACCESS = 57;
    public static final int FUNCTION_CALL = 58;
    public static final int METHOD_CALL = 59;
    public static final int METHOD_CALL_POSTFIX = 60;
    public static final int METHOD_CALL_STATIC = 61;
    public static final int Exit = 62;
    public static final int Bool = 63;
    public static final int Int = 64;
    public static final int Float = 65;
    public static final int String = 66;
    public static final int TypeArray = 67;
    public static final int Null = 68;
    public static final int This = 69;
    public static final int CONSTANT = 70;
    public static final int ACTUAL_PARAMETERS = 71;
    public static final int Abstract = 72;
    public static final int Arrow = 73;
    public static final int As = 74;
    public static final int BINARY = 75;
    public static final int BLOCK = 76;
    public static final int BLOCK_CONDITIONAL = 77;
    public static final int Backslash = 78;
    public static final int Break = 79;
    public static final int CLASS_BODY = 80;
    public static final int CLASS_MODIFIER = 81;
    public static final int CLASS_STATIC_ACCESS_VARIABLE_ID = 82;
    public static final int CONSTANT_DECLARATION = 83;
    public static final int CONSTANT_DECLARATION_LIST = 84;
    public static final int Case = 85;
    public static final int Cast = 86;
    public static final int Catch = 87;
    public static final int Class = 88;
    public static final int Colon = 89;
    public static final int Comma = 90;
    public static final int Comment = 91;
    public static final int Const = 92;
    public static final int Construct = 93;
    public static final int Continue = 94;
    public static final int DECIMAL = 95;
    public static final int DEFAULT_NAMESPACE = 96;
    public static final int Default = 97;
    public static final int Destruct = 98;
    public static final int Do = 99;
    public static final int Dollar = 100;
    public static final int DoubleColon = 101;
    public static final int EXPONENT = 102;
    public static final int EXPRESSION = 103;
    public static final int EXPRESSION_LIST = 104;
    public static final int Echo = 105;
    public static final int Else = 106;
    public static final int Extends = 107;
    public static final int FIELD = 108;
    public static final int FIELD_MODIFIER = 109;
    public static final int FUNCTION_MODIFIER = 110;
    public static final int Final = 111;
    public static final int For = 112;
    public static final int Foreach = 113;
    public static final int Function = 114;
    public static final int HEXADECIMAL = 115;
    public static final int INTERFACE_BODY = 116;
    public static final int Identifier = 117;
    public static final int If = 118;
    public static final int Implements = 119;
    public static final int Interface = 120;
    public static final int LeftCurlyBrace = 121;
    public static final int LeftParenthesis = 122;
    public static final int LeftSquareBrace = 123;
    public static final int METHOD_DECLARATION = 124;
    public static final int METHOD_MODIFIER = 125;
    public static final int MinusMinus = 126;
    public static final int NAMESPACE_BODY = 127;
    public static final int Namespace = 128;
    public static final int NotEqualAlternative = 129;
    public static final int OCTAL = 130;
    public static final int ObjectOperator = 131;
    public static final int PARAMETER_DECLARATION = 132;
    public static final int PARAMETER_LIST = 133;
    public static final int PARAMETER_TYPE = 134;
    public static final int Parent = 135;
    public static final int ParentColonColon = 136;
    public static final int PlusPlus = 137;
    public static final int Private = 138;
    public static final int ProtectThis = 139;
    public static final int Protected = 140;
    public static final int Public = 141;
    public static final int Return = 142;
    public static final int RightCurlyBrace = 143;
    public static final int RightParenthesis = 144;
    public static final int RightSquareBrace = 145;
    public static final int STRING_DOUBLE_QUOTED = 146;
    public static final int STRING_SINGLE_QUOTED = 147;
    public static final int SWITCH_CASES = 148;
    public static final int Self = 149;
    public static final int SelfColonColon = 150;
    public static final int Semicolon = 151;
    public static final int Static = 152;
    public static final int Switch = 153;
    public static final int TYPE = 154;
    public static final int TYPE_MODIFIER = 155;
    public static final int TYPE_NAME = 156;
    public static final int Throw = 157;
    public static final int Try = 158;
    public static final int TypeAliasBool = 159;
    public static final int TypeAliasFloat = 160;
    public static final int TypeAliasFloat2 = 161;
    public static final int TypeAliasInt = 162;
    public static final int TypeBool = 163;
    public static final int TypeFloat = 164;
    public static final int TypeInt = 165;
    public static final int TypeMixed = 166;
    public static final int TypeObject = 167;
    public static final int TypeResource = 168;
    public static final int TypeString = 169;
    public static final int USE_DECLARATION = 170;
    public static final int Use = 171;
    public static final int VARIABLE_DECLARATION = 172;
    public static final int VARIABLE_DECLARATION_LIST = 173;
    public static final int VariableId = 174;
    public static final int Void = 175;
    public static final int While = 176;
    public static final int Whitespace = 177;
    public static final int False = 178;
    public static final int PhpEnd = 179;
    public static final int PhpStart = 180;
    public static final int True = 181;

    private TokenTypes() {
    }
}