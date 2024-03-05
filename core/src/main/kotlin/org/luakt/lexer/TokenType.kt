package org.luakt.lexer

enum class TokenType{
    Id,
    Int,
    Num,
    Str,
    Attr,
    Proc,

    Nil,
    True,
    False,

    Local,
    Global,

    Function,
    Return,
    End,
    Goto,

    If,
    Else,
    ElseIf,
    Then,

    While,
    Do,
    For,
    Break,
    Repeat,
    Until,
    In,

    And,
    Or,
    Not,

    Dot,   // .
    Semi,  // ;
    Ques,  // ?
    Comma, // ,
    Wave,  // ~
    Colon, // :
    Excla, // !
    BitOr, // |
    BitAnd,// &

    Add, // +
    Sub, // -
    Mul, // *
    Div, // /
    Mod, // %
    Pow, // ^
    Eq,  // =
    Gt,  // >
    Lt,  // <
    Eqs,   // ==
    NotEq, // ~=
    GtEq,  // >=
    LtEq,  // <=
    Arrow, // ->
    DivNoRem, // //
    Concat,   // ..
    GetLen,   // #
    LShift,   // <<
    RShift,   // >>
    TwoColon, // ::
    VarArg,   // ...

    // ( )
    LParen,
    RParen,

    // [ ]
    LBracket,
    RBracket,

    // { }
    LBrace,
    RBrace;

    override fun toString() : String{
        val keyWord = idToStrMap[this]
        if(keyWord != null) return "'$keyWord'"
        val chTk = when(this){
            LParen -> "("
            RParen -> ")"
            LBracket -> "["
            RBracket -> "]"
            LBrace -> "{"
            RBrace -> "}"
            Dot -> "."
            Semi -> ";"
            Ques -> "?"
            Comma -> ","
            Colon -> ":"
            Excla -> "!"
            BitOr -> "|"
            BitAnd -> "&"
            Add -> "+"
            Sub -> "-"
            Mul -> "*"
            Div -> "/"
            Mod -> "%"
            Pow -> "^"
            Eq -> "="
            Gt -> ">"
            Lt -> "<"
            Eqs -> "=="
            NotEq -> "~="
            GtEq -> ">="
            LtEq -> "<="
            Arrow -> "->"
            DivNoRem -> ""
            Concat -> ".."
            GetLen -> "#"
            LShift -> "<<"
            RShift -> ">>"
            TwoColon -> "::"
            VarArg -> "..."
            else -> ""
        }
        if(chTk.isNotEmpty()) return "'${chTk}'"
        return when(this){
            Id -> "Identifier"
            Int -> "Integer"
            Num -> "Number"
            Str -> "String"
            Attr -> "AttributeMacro"
            Proc -> "ProcedureMacro"
            else -> ""
        }
    }
    companion object{
        private val idToStrMap by lazy(LazyThreadSafetyMode.NONE) {
            Token.identifiers.map { Pair(it.value, it.key) }.toMap()
        }
    }
}