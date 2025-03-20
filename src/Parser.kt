data class Var(val id: Int, val name: String, val line: Int)

class Parser(val tokens: List<Token>) {

    var purgeTokens: MutableList<Token> = tokens.toMutableList()
    var varList = mutableListOf<Var>()
    var tokenList = mutableListOf<Token>()
    var nextId = 0
    var m_index = 0

    fun parse(): List<Token> {
        while (purgeTokens.isNotEmpty()){
            if (purgeTokens[0].type == TokenType.LET) {
                parseLET()
            }
        }
        return tokenList
    }

    private fun parseLET() {
        consume()
        if (purgeTokens[0].type == TokenType.VAR){
            parseVar()
            if (purgeTokens[0].type == TokenType.EQUALS){
                consumeAndIgnore()
                parseEXPR()
                if (purgeTokens[0].type == TokenType.SEMI){
                   consume()
                }
                else
                    error("Missing '${TokenType.SEMI.value}' at line ${purgeTokens[0].line} ")
            }
            else
                error("Let statement must have a ${TokenType.EQUALS.value} after the identification on line ${purgeTokens[0].line}.")
        }
        else
            error("Let statement must have an identifier on line ${purgeTokens[0].line}.")
    }

    private fun parseVar() {
        val newVar = Var(nextId++, purgeTokens[0].value.toString(), purgeTokens[0].line)
        if (newVar.name in varList.map { it.name }) {
            error("Variable name \"${newVar.name}\" is already defined.")
        }
        varList.add(newVar)
        consume()
    }

    private fun parseEXPR() {

        // Search Multiplication/Division
        parseTIMESDIV()

        // Search Addition/Subtraction
        parseADDSUB()

        // Search for single Ints
        parseINT()

        // Calculate steps
//        m_index = purgeTokens.findFirstStopper(m_index)
    }

    private fun parseTIMESDIV(){
        for (i in 0 until purgeTokens.findFirstStopper()) {
            if (purgeTokens[i].type.name in Operator.entries.map { it.name }) {
                if (purgeTokens[i].type == TokenType.TIMES || purgeTokens[i].type == TokenType.DIV){
                    if (i-1 >= 0){
                        if (purgeTokens[i-1].type == TokenType.INT || purgeTokens[i-1].type == TokenType.VAR){
                            if (i+1 > purgeTokens.findFirstStopper()){
                                if (purgeTokens[i+1].type == TokenType.INT || purgeTokens[i+1].type == TokenType.VAR){
                                    consume(i-1,3)
                                }
                                else error("Missing integer or variable at line ${purgeTokens[i].line}.")
                            }
                        }
                        else error("Missing integer or variable at line ${purgeTokens[i].line}.")
                    }
                }
            }
        }
    }

    private fun parseADDSUB() {
        var singleEXPR = true

        var i = 0
        while (i < purgeTokens.findFirstStopper()) {
            if (purgeTokens[i].type == TokenType.PLUS || purgeTokens[i].type == TokenType.MINUS) {
                if (i-1 >= 0){
                    if (purgeTokens[i-1].type == TokenType.INT || purgeTokens[i-1].type == TokenType.VAR) {
                        if (i+1 < purgeTokens.findFirstStopper()){
                            if (purgeTokens[i+1].type == TokenType.INT || purgeTokens[i+1].type == TokenType.VAR) {
                                checkIlegalInt(purgeTokens[i-1])
                                checkIlegalInt(purgeTokens[i+1])
                                consume(i-1, 3)
                            }
                            else error("Missing integer or variable at line ${purgeTokens[i].line}")
                            singleEXPR = false
                        }
                        else error("Missing integer or variable at line ${purgeTokens[i].line}.")
                    }
                }
                else if (i+1 < purgeTokens.findFirstStopper()){
                    if (purgeTokens[i+1].type == TokenType.INT || purgeTokens[i+1].type == TokenType.VAR) {
                        checkIlegalInt(purgeTokens[i+1])
                        if (purgeTokens[i].type == TokenType.MINUS && singleEXPR){
                            tokenList += Token(TokenType.INT, "0", purgeTokens[i].line)
                        }
                        consume(i, 2)
                    }
                    else error("Missing integer or variable at line ${purgeTokens[i].line}")
                }
                else
                    consumeAndIgnore(i)
                i = -1
            }
            i++
        }
    }


    private fun parseINT() {
        if (purgeTokens[0].type == TokenType.INT || purgeTokens[0].type == TokenType.VAR) {
            consume()
        }
    }

    private fun checkIlegalInt(token: Token){
        if (token.type == TokenType.INT && !token.value.isNullOrBlank() && (token.value.toInt() < 0 || token.value.toInt() > 15)){
            error("Ilegal int value at line ${token.line}.\nInt values must be bigger or equal than 0 and less than 100.\nYour value is ${token.value}.")
        }
    }

    private fun consume(from: Int = 0, n: Int = 1) {
        for (i in 1..n) {
            tokenList += purgeTokens[from]
            purgeTokens -= purgeTokens[from]
        }
    }

    private fun consumeAndIgnore(from: Int = 0, n: Int = 1){
        for (i in 1..n) {
            purgeTokens -= purgeTokens[from]
        }
    }
}