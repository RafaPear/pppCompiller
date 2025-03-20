class Optimizer(val generated: String, val vars: List<Var>) {

    var regList: Array<Int> = Array<Int>(8) { 0 }

    fun optimize(){
        val lst = format()

        println(lst)

        for (i in lst.indices){
            for (j in lst[i].indices){
                if (lst[i][j] == "MOV"){
                    if (lst[i][j+1].contains('R')){
                        if (lst[i][j+2].contains('#'))
                            regList[regId(lst[i][j+1])] = IntVal(lst[i][j+2])
                    }
                }
            }
        }

        println(regList.toList().toString())
    }

    private fun regId(value: String): Int{
        return value.drop(1).dropLast(1).toInt()
    }

    private fun IntVal(value: String): Int{
        return value.drop(1).toInt()
    }

    private fun format(): List<List<String>>{
        var split = generated.split('\n').map { it ->
            it.split(' ').toMutableList()
        }.toMutableList()

        for (i in split.indices) {
            for (j in split[i].indices) {
                split[i][j] = split[i][j].dropWhile { it ->
                    it.isWhitespace()
                }
            }
        }

        return split
    }

    private fun findVar(token: Token): Var{
        return vars.find { it -> it.name == token.value} ?: error("No variable called ${token.value} at line ${token.line}")
    }
}