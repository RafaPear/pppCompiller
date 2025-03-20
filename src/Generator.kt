class Generator(val tokens: List<Token>, val vars: List<Var>) {

    var purgeTokens: MutableList<Token> = tokens.toMutableList()
    var usedTokens: MutableList<Token> = mutableListOf()
    var generated: String = ""
    var usedID: MutableList<Int> = mutableListOf()
    var usedLABEL: MutableList<String> = mutableListOf()

    val VAR_REGISTER: Int = 7
    var VAR_REGISTER_VALUE: Int = 0
    val BUFF_REGISTER: Int = 6
    val MAX_VAR_ID: Int = 5
    var opCount = 0

    fun generate(): String {
        while (purgeTokens.isNotEmpty()){
            if (purgeTokens[0].type == TokenType.LET) {
                consume()
                generated += generateVAR()
            }
            else if (purgeTokens[0].type.name in Stopper.SEMI.name){
                consume()
            }
            else
                break
        }
        return generated
    }

    private fun generateVAR(): String{
        var buff = ""
        val thisVar = vars.find { it -> it.name == purgeTokens[0].value} ?: error("Variable error")
        val token = purgeTokens[0]
        consume()

        buff += generateEXPR()

        if (thisVar.id !in usedID) {
            usedID.add(thisVar.id)
        }
        if (usedID.last() > MAX_VAR_ID){
            error("Out of usable registers at variable name '${token.value}' on line ${token.line}")
        }

        if (opCount > 1)
            buff += "\tMOV R${thisVar.id}, R${VAR_REGISTER}\n"
        else
            buff += "\tMOV R${thisVar.id}, #${VAR_REGISTER_VALUE}\n"
        return buff
    }

    private fun generateEXPR(): String{
        var buff = ""

        while (purgeTokens[0].type.name in Operator.entries.map { it.name } || purgeTokens[0].type == TokenType.INT || purgeTokens[0].type == TokenType.VAR) {
            when (purgeTokens[0].type) {
                TokenType.INT -> buff += generateINT()
                TokenType.VAR -> buff += generateINT()
                TokenType.PLUS -> buff += generateADD()
                TokenType.MINUS -> buff += generateSUB()
                TokenType.TIMES -> {}
                TokenType.DIV -> {}
                else -> break
            }
            opCount++
        }

        return buff
    }

    private fun generateINT(): String{
        var buff = ""

        if (purgeTokens[0].type == TokenType.INT) {
            if (opCount >= 1)
                buff += "\tMOV R${VAR_REGISTER}, #${purgeTokens[0].value}\n"
            VAR_REGISTER_VALUE = purgeTokens[0].value?.toInt() ?: error("What")
            consume()
        }
        else if (purgeTokens[0].type == TokenType.VAR) {
            if (opCount >= 1)
                buff += "\tMOV R${VAR_REGISTER}, R${findVar(purgeTokens[0]).id}\n"
            VAR_REGISTER_VALUE = findVar(purgeTokens[0]).id
            consume()
        }
        return buff
    }

    private fun generateADD(): String{
        var buff = ""
        consume()
        val a = purgeTokens[0]
        consume()

        if (a.type == TokenType.INT)
            buff += "\tADD R${VAR_REGISTER}, R${VAR_REGISTER}, #${a.value}\n"
        else if (a.type == TokenType.VAR) {
            buff += "\tADD R${VAR_REGISTER}, R${VAR_REGISTER}, R${findVar(a).id}\n"
        }

        return buff
    }

    private fun generateSUB(): String{
        var buff = ""

        consume()
        val a = purgeTokens[0]
        consume()

        if (a.type == TokenType.INT)
            buff += "\tSUB R${VAR_REGISTER}, R${VAR_REGISTER}, #${a.value}\n"
        else if (a.type == TokenType.VAR) {
            buff += "\tSUB R${VAR_REGISTER}, R${VAR_REGISTER}, R${findVar(a).id}\n"
        }
        return buff
    }

    private fun generateTIMES(): String{

        var buff = ""
        consume()
        val a = purgeTokens[0]
        consume()

        if (a.type == TokenType.INT)
            buff += "\tADD R${VAR_REGISTER}, R${VAR_REGISTER}, #${a.value}\n"
        else if (a.type == TokenType.VAR) {
            buff += "\tADD R${VAR_REGISTER}, R${VAR_REGISTER}, R${findVar(a).id}\n"
        }

        return buff

//        if:
//            mov r3, #0
//        cmp r0, r3
//        beq if_end
//                cmp r0, r1
//        bhs while
//            mov r3, r1
//        mov r1, r0
//        mov r0, r3
//        while:
//            mov r3, #0
//        cmp r3, r1
//        bhs while_end
//                add r2, r2, r0
//        sub r1, r1, #1
//        b while
//            while_end:
//        if_end:
//        b .
    }
//
//    // TODO: generateDIV
//    private fun generateDIV(): String{
//
//
//    }

    private fun isLabelUsed(value: String): Boolean{
        return if (usedLABEL.contains(value))
            true
        else
            false
    }

    private fun findVar(token: Token): Var{
        return vars.find { it -> it.name == token.value} ?: error("No variable called ${token.value} at line ${token.line}")
    }

    private fun consume(from: Int = 0, n: Int = 1){
        for (i in 1..n) {
            usedTokens += purgeTokens[from]
            purgeTokens -= purgeTokens[from]
        }
    }
}