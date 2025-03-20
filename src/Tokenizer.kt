enum class TokenType(val value: String?){
    LET("let"),
    VAR(null),
    EQUALS("="),
    INT(null),
    SEMI(";"),
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIV("/")
}

enum class Operator {
    SEMI,
    PLUS,
    MINUS,
    TIMES,
    DIV
}

enum class Stopper {
    SEMI
}

data class Token(val type: TokenType, val value: String? = null, val line: Int)

class Tokenizer(val src: String) {

    fun tokenize(): List<Token> {
        var buff = ""
        var tokens: List<Token> = emptyList()

        var i = 0
        while (i < src.length) {
            val c = src[i]
            if (c.isLetter()){
                buff += c
                i++
                while (i < src.length && src[i].isLetterOrDigit()){
                    buff += src[i]
                    i++
                }
                i--

                if (buff == TokenType.LET.value){
                    tokens += Token(TokenType.LET, line = src.getLine(i))
                    buff = ""
                }
                else {
                    tokens += Token(TokenType.VAR, buff, line = src.getLine(i))
                    buff = ""
                }
            }
            else if (c.isDigit()){
                buff += c
                i++
                while (i < src.length && src[i].isDigit()){
                    buff += src[i]
                    i++
                }
                i--
                tokens += Token(TokenType.INT, buff, line = src.getLine(i))
                buff = ""
            }
            else if (c == TokenType.EQUALS.value?.toCharArray()[0]){
                tokens += Token(TokenType.EQUALS, line = src.getLine(i))
                buff = ""
            }
            else if (c == TokenType.PLUS.value?.toCharArray()[0]){
                tokens += Token(TokenType.PLUS, line = src.getLine(i))
                buff = ""
            }
            else if (c == TokenType.MINUS.value?.toCharArray()[0]){
                tokens += Token(TokenType.MINUS, line = src.getLine(i))
                buff = ""
            }
            else if (c == TokenType.TIMES.value?.toCharArray()[0]){
                tokens += Token(TokenType.TIMES, line = src.getLine(i))
                buff = ""
            }
            else if (c == TokenType.DIV.value?.toCharArray()[0]){
                tokens += Token(TokenType.DIV, line = src.getLine(i))
                buff = ""
            }
            else if (c == TokenType.SEMI.value?.toCharArray()[0]){
                tokens += Token(TokenType.SEMI, line = src.getLine(i))
            }
            else if (c.isWhitespace()){
                buff = ""
            }
            else {
                error("Something went wrong")
            }
        i++
        }

        return tokens
    }
}

fun String.getLine(index: Int): Int{
    var counter = 1
    for (i in index downTo 0) {
        if (this[i] == '\n') counter++
    }
    return counter
}

fun List<Token>.findFirstStopper(from: Int = 0): Int{
    return from + this.drop(from).indexOfFirst { value -> value.type.name in Stopper.entries.map { it.name }}
}