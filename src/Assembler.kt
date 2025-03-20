class Assembler(val fileName: String) {
    fun assemble(){
        val process = ProcessBuilder("cmd", "/c", "p16as", fileName).start()
        val output = process.inputStream.bufferedReader().readText()

        if (output.isNotBlank()) {
            println("Error: $output")
        } else {
            println("Success: " + process.inputStream.bufferedReader().readText())
        }
    }

    fun run(){
        val process = ProcessBuilder("cmd", "/c", "p16sim", fileName).start()
        val output = process.inputStream.bufferedReader().readText()
        println(output)

        ProcessBuilder("cmd", "/c", "p16dbg").start()
    }

    fun assembleAndRun(){
        assemble()
        run()
    }
}