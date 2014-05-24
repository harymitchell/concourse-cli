/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Jeff Nelson, Cinchapi Software Collective
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cinchapi.concourse.cli;

import java.io.IOException;

import org.cinchapi.concourse.config.ConcourseClientPreferences;

import jline.console.ConsoleReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;

/**
 * A {@link CommandLineInterface} is a console tool that interacts with
 * {@link Concourse} via the client interface. This class contains boilerplate
 * logic for grabbing authentication credentials, etc. The implementing class is
 * responsible for establishing a client connection.
 * 
 * @author jnelson
 */
public abstract class CommandLineInterface {

    /**
     * The CLI options.
     */
    protected Options options;

    /**
     * The parser that validates the CLI options.
     */
    protected JCommander parser;

    /**
     * Handler to the console for interactive I/O.
     */
    protected ConsoleReader console;

    /**
     * Construct a new instance.
     * <p>
     * The subclass should call {@link #CommandLineInterface(Options, String[])}
     * from this constructor with an instance of of the appropriate subclass of
     * {@link Options} if necessary.
     * </p>
     * 
     * @param args
     */
    public CommandLineInterface(String[] args) {
        this(new Options(), args);
    }

    /**
     * Construct a new instance that is seeded with an object containing options
     * metadata. The {@code options} will be parsed by {@link JCommander} to
     * configure them appropriately.
     * <p>
     * The subclass should NOT override this constructor. If the subclass
     * defines a custom {@link Options} class, then it only needs to pass those
     * to this super constructor from {@link #CommandLineInterface(String...)}.
     * </p>
     * 
     * @param options
     * @param args - these usually come from the main method
     */
    protected CommandLineInterface(Options options, String... args) {
        try {
            this.parser = new JCommander(options, args);
            this.options = options;
            parser.setProgramName(CaseFormat.UPPER_CAMEL.to(
                    CaseFormat.LOWER_HYPHEN, this.getClass().getSimpleName()));
            this.console = new ConsoleReader();
            if(options.help) {
                parser.usage();
                System.exit(1);
            }
            // must get creds in constructor in case subclass tries to connect
            // to Concourse in its constructor
            if(!Strings.isNullOrEmpty(options.prefs)) {
                ConcourseClientPreferences prefs = ConcourseClientPreferences
                        .load(options.prefs);
                options.username = prefs.getUsername();
                options.password = new String(prefs.getPassword());
                options.host = prefs.getHost();
                options.port = prefs.getPort();
            }
            else if(Strings.isNullOrEmpty(options.password)) {
                options.password = console.readLine("Password: ", '*');
            }
        }
        catch (ParameterException e) {
            die(e.getMessage());
        }
        catch (IOException e) {
            die(e.getMessage());
        }
    }

    /**
     * Run the CLI. This method should only be called from the main method.
     */
    public final int run() {
        try {            
            doTask();
            return 0;
        }
        catch (Exception e) {
            return die(e.getMessage());
        }
    }

    /**
     * Print {@code message} to stderr and exit with a non-zero status.
     * 
     * @param message
     */
    protected int die(String message) {
        System.err.println("ERROR: " + message);
        return 2;
    }

    /**
     * Implement the task. This method is called by the main {@link #run()}
     * method, so the implementer should place all task logic
     * here.
     * <p>
     * DO NOT call {@link System#exit(int)} with '0' from this method
     * </p>
     */
    protected abstract void doTask();

}
