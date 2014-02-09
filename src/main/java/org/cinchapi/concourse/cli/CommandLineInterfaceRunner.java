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

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * A single runner that can execute any {@link CommandLineInterface} class that
 * is specified as the first argument. This class is designed to make it easier
 * to create CLI applications.
 * <p>
 * <ol>
 * <li>Write a CLI class that extends {@link CommandLineInterface}. The CLI must
 * define a public constructor that takes a single array of string arguments
 * (i.e. similar to a main method).</li>
 * <li>Write a wrapper script that invokes
 * <code>org.cinchapi.concourse.cli.CommandLineInterfaceRunner &lt;cli&gt; &lt;cli-args&gt;</code>
 * where {@code cli} is the fully name of the CLI class from step 1 and
 * {@code cli-args} are the arguments that should be passed to tht CLI</li>
 * <p>
 * <strong>NOTE:</strong> Be sure to add both CommandLineInterfaceRunner and the
 * CLI class from step 1 to the classpath you feed to the JVM in the shell
 * script.
 * </p>
 * </ol>
 * </p>
 * 
 * @author jnelson
 */
@SuppressWarnings("unchecked")
public final class CommandLineInterfaceRunner {

    /**
     * Run the appropriate CLI program
     * 
     * @param args
     */
    public static void main(String... args) {
        if(args.length == 0) {
            System.err.println("ERROR: Please specify a "
                    + "CommandLineInterface to run");
            System.exit(1);
        }
        String name = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        Constructor<? extends CommandLineInterface> constructor = null;
        try {
            Class<?> clazz = Class.forName(name);
            constructor = (Constructor<? extends CommandLineInterface>) clazz
                    .getConstructor(String[].class);
        }
        catch (Exception e) {
            System.err.println(MessageFormat.format(
                    "Cannot execute CommandLineInterface named {0}", name));
            e.printStackTrace();
            System.exit(1);
        }
        // Not worried about NPE because #constructor is guaranteed to be
        // initialized if we make it this far
        try {
            CommandLineInterface cli = constructor.newInstance((Object) args);
            System.exit(cli.run());
        }
        catch (Exception e) {
            // At this point, the Exception is thrown from the CLI (i.e. the
            // user did not pass in a required arg, etc).
            if(e instanceof ReflectiveOperationException
                    && e.getCause().getMessage() != null) {
                System.err.print(MessageFormat.format("ERROR: {0}", e
                        .getCause().getMessage()));
            }
            System.exit(1);
        }

    }

    private CommandLineInterfaceRunner() {}
}
