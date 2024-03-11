package org.archipanion.Controller.Api.Cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.jakewharton.picnic.table
import org.archipanion.Model.Config.RootConfig
import java.nio.file.Path
import java.nio.file.Paths

/**
 *
 * @author Ralph Gasser
 * @version 1.0
 */
class ConfigCommand(private val config: RootConfig) : NoOpCliktCommand(
    name = "config",
    help = "Groups commands related to  read and set the config.",
    epilog = "Config related commands usually have the form: <config> <command> <identifier> <value>,, e.g., `config get port` " +
            "Check help for command specific parameters.",
    invokeWithoutSubcommand = true,
    printHelpOnEmptyArgs = true
) {


    init {
        this.subcommands(
            About(this.config),
            Get(this.config),
            Set(this.config)
        )
    }

    /**
     * [CliktCommand] to list all fields in a schema registered with this vitrivr instance.
     */
    inner class About(val config: RootConfig) :
        CliktCommand(name = "about", help = "Lists all fields that are registered within the config.") {

        /**
         * Executes the command.
         */
        override fun run() {
            val table = table {
                cellStyle { border = true; paddingLeft = 1; paddingRight = 1 }
                header {
                    row {
                        cell("Config Name")
                        cell("Path")
                        cell("Value")
                        cell("Datatype")
                        cell("Validity")
                        cell("Example")
                    }
                }
                body {
                    // TODO: Flatten config values and print them here
                    /*                    for (field in this@ConfigCommand.config) {
                                            row {
                                                cell(field.fieldName)
                                                cell(field.analyser::class.java.simpleName)
                                                cell(field.analyser.contentClasses.map { it.simpleName }.joinToString())
                                                cell(field.analyser.descriptorClass.simpleName)
                                                cell(this@SchemaCommand.schema.connection.description())
                                                cell(field.getInitializer().isInitialized())
                                            }
                                        }*/
                }
            }
            println(table)
        }
    }

    /**
     * [CliktCommand] to initialize the schema.
     */
    inner class Get(val config: RootConfig) :
        CliktCommand(name = "get", help = "Initializes the schema using the database connection.") {
        override fun run() {
            println("A value")
        }
    }

    /**
     * [CliktCommand] to start an extraction job.
     */
    inner class Set(val config: RootConfig) :
        CliktCommand(name = "set", help = "Extracts data from a source and stores it in the schema.") {

        /** Path to the configuration file. */
        private val input: Path by option(
            "-c",
            "--config",
            help = "Path to the extraction configuration."
        ).convert { Paths.get(it) }.default(Paths.get("config.json"))

        override fun run() {
            /* Read configuration file. */
            println("Started extraction job with UUID .")
        }
    }
}