import sun.util.logging.resources.logging
import java.io.File
import kotlin.system.exitProcess

class Compiler(val args: Array<String>) {
    var outName = "main.s"
    var run = false
    var assemble = false

    fun compile(){
        if (args[0] == "-h"){
            println("------------------ HELP MENU ------------------")
            println("\t Usage: -f fileName.ppp")
            println("                     Args                      ")
            println("\t Output file name: -o \"new name\"")
            exitProcess(0)
        }

        var path: String =
            if (args[0] == "-f") {
                args[1]
            }
            else
                error("No file specified. Specify a file with '-f fileName.ppp'.")

        var i = 2
        while (i in 2..args.indices.last()) {
            if (args[i] == "-o"){
                i++
                outName = args[i]
            }
            else if (args[i] == "-r"){
                run = true
            }
            else if (args[i] == "-a"){
                assemble = true
            }
            i++
        }



        println("INFO:\tTokenizing")
        val tokens = Tokenizer(File(path).readText()).tokenize()
        println(tokens)

        println("INFO:\tParsing")
        val parser = Parser(tokens)
        val parsed = parser.parse()
        println(parsed)

        println("INFO:\tGenerating")
        val generated = Generator(parsed,parser.varList).generate()

        println("INFO:\tOptimizing")
        val optimized = Optimizer(generated,parser.varList).optimize()

        println("INFO:\tCreating file $outName")
        File(outName).printWriter().use { out ->
            out.print(generated)
        }
        println("INFO:\tCreated file $outName")


        if (assemble){
            if (run){
                println("INFO:\tAssembling and Running")
                Assembler(outName).assembleAndRun()
            }
            else {
                println("INFO:\tAssembling")
                Assembler(outName).assemble()
            }
        }
        else if (run){
           error("Can't run without assembling first. Run with argument '-a' ")
        }

        println("INFO:\tFile compiled to p16 assembly successfully.")

        exitProcess(0)
    }
}