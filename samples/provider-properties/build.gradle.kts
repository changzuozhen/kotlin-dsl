println("befor apply")
apply<GreetingPlugin>()
println("after apply")

fun buildFile(path: String) = layout.buildDirectory.file(path)

configure<GreetingPluginExtension> {

    message.set("Hi from Gradle")

    outputFiles.from(
            buildFile("a.txt"),
            buildFile("b.txt"))
}

open class GreetingPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {

        println("GreetingPlugin apply Add the 'greeting' extension object")
        // Add the 'greeting' extension object
        val greeting = extensions.create(
                "greeting",
                GreetingPluginExtension::class,
                project)

        println("GreetingPlugin apply Add a task that uses the configuration")
        // Add a task that uses the configuration
        tasks {
            register("hello", Greeting::class) {
                group = "Greeting"
                println("hello task run...")
                message.set(greeting.message)
                outputFiles.setFrom(greeting.outputFiles)
            }
        }
    }
}

open class GreetingPluginExtension(project: Project) {

    val message = project.objects.property<String>()

    val outputFiles: ConfigurableFileCollection = project.files()
}

open class Greeting : DefaultTask() {

    @get:Input
    val message = project.objects.property<String>()

    @get:OutputFiles
    val outputFiles: ConfigurableFileCollection = project.files()

    @TaskAction
    fun printMessage() {
        val message = message.get()
        val outputFiles = outputFiles.files
        println("Greeting Task: Writing message '$message' to files $outputFiles")
        logger.info("Greeting Task: Writing message '$message' to files $outputFiles")
        outputFiles.forEach {
            println("output file: $it , message: $message")
            it.writeText(message)
        }
    }
}
