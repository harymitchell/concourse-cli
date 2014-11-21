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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.cinchapi.concourse.config.ConcourseClientPreferences;

import com.beust.jcommander.Parameter;

/**
 * Each member variable represents the options that can be passed to the main
 * method of a CLI. Each CLI should subclass this and specify the appropriate
 * parameters.
 * 
 * <p>
 * See http://jcommander.org/ for more information.
 * <p>
 * 
 * @author jnelson
 */
public class Options {

    /**
     * A handler for the client preferences that <em>may</em> exist in the
     * user's home directory.
     */
    private ConcourseClientPreferences prefsHandler = null;

    {
        String file = System.getProperty("user.home") + File.separator
                + "concourse_client.prefs";
        if(Files.exists(Paths.get(file))) { // check to make sure that the
                                            // file exists first, so we
                                            // don't create a blank one if
                                            // it doesn't
            prefsHandler = ConcourseClientPreferences.load(file);
        }
    }
    
    @Parameter(names = { "--help" }, help = true, hidden = true)
    public boolean help;

    @Parameter(names = { "-h", "--host" }, description = "The hostname where the Concourse Server is located")
    public String host = prefsHandler != null ? prefsHandler.getHost()
            : "localhost";

    @Parameter(names = { "-p", "--port" }, description = "The port on which the Concourse Server is listening")
    public int port = prefsHandler != null ? prefsHandler.getPort() : 1717;

    @Parameter(names = { "-u", "--username" }, description = "The username with which to connect")
    public String username = prefsHandler != null ? prefsHandler.getUsername()
            : "admin";

    @Parameter(names = "--password", description = "The password", password = false, hidden = true)
    public String password = prefsHandler != null ? new String(
            prefsHandler.getPassword()) : null;

    @Parameter(names = { "-e", "--environment" }, description = "The environment of the Concourse Server to use")
    public String environment = prefsHandler != null ? prefsHandler
            .getEnvironment() : "";

    @Parameter(names = "--prefs", description = "Path to the concourse_client.prefs file")
    public String prefs;

}
