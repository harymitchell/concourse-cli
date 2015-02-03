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

import org.junit.Assert;
import org.cinchapi.concourse.test.ClientServerTest;
import org.junit.Test;

public class CommandLineInterfaceTest extends ClientServerTest {

    /**
     * Tests passing an invalid password to a test CLI.
     */
    @Test
    public void testInvalidPassword() {
        // Test that passing a valid password results in a Concourse instance.
        String[] validArgs = { "--password", "admin", "-p",
                String.valueOf(server.getClientPort()) };
        TestCommandLineInterface validTestCLI = new TestCommandLineInterface(
                new Options(), validArgs);
        Assert.assertNotEquals(
                "CLI with valid login, Concourse instance not null",
                validTestCLI.getConcourse(), null);

        // Test that passing an invalid password results in a null Concourse
        // instance.
        String[] invalidArgs = { "--password", "invalid", "-p",
                String.valueOf(server.getClientPort()) };
        TestCommandLineInterface invalidTestCLI = new TestCommandLineInterface(
                new Options(), invalidArgs);
        Assert.assertEquals("CLI with invalid login, Concourse instance null",
                invalidTestCLI.getConcourse(), null);
        Assert.assertEquals("Invalid login failed", true,
                invalidTestCLI.isLoginFailed);

        // Test that passing an invalid port results in a null Concourse
        // instance.
        String[] invalidPortArgs = { "--password", "admin", "-p", "0" };
        TestCommandLineInterface invalidPortTestCLI = new TestCommandLineInterface(
                new Options(), invalidPortArgs);
        Assert.assertEquals("CLI with invalid port, Concourse instance null",
                invalidPortTestCLI.getConcourse(), null);
    }

    /**
     * Test {@link CommandLineInterface} for testing purposes, simply exists to
     * take
     * command-line options and to attempt a Concourse connection.
     * 
     * @author hmitchell
     *
     */
    private class TestCommandLineInterface extends CommandLineInterface {

        protected TestCommandLineInterface(Options options, String[] args) {
            super(options, args);
            setLoginAttemptsRemaining(0);
            connectToConcourse();
        }

        @Override
        protected void doTask() {
            // does nothing.
        }

    }

    @Override
    protected String getServerVersion() {
        return "0.3.4";
    }
}
