package org.cinchapi.concourse.cli;

import org.junit.Assert;
import org.cinchapi.concourse.test.ClientServerTest;
import org.junit.Test;

public class CommandLineInterfaceTest extends ClientServerTest  {

	/**
	 * Tests passing an invalid password to a test CLI.
	 */
	@Test
	public void testInvalidPassword(){
		// Test that passing a valid password results in a Concourse instance.
		String[] validArgs = {"--password", "admin", "-p", String.valueOf(server.getClientPort())};
		TestCommandLineInterface validTestCLI = new TestCommandLineInterface (new Options(), validArgs);
		Assert.assertNotEquals("CLI with valid login, Concourse instance not null", validTestCLI.getConcourse(), null); 

		// Test that passing an invalid password results in a null Concourse instance.
		String[] invalidArgs = {"--password", "invalid", "-p", String.valueOf(server.getClientPort())};
		TestCommandLineInterface invalidTestCLI = new TestCommandLineInterface (new Options(), invalidArgs);
		Assert.assertEquals ("CLI with invalid login, Concourse instance null", invalidTestCLI.getConcourse(), null);
		Assert.assertEquals ("Invalid login failed", true, invalidTestCLI.isLoginFailed);
		
		// Test that passing an invalid port results in a null Concourse instance.
		String[] invalidPortArgs = {"--password", "admin", "-p", "0"};
		TestCommandLineInterface invalidPortTestCLI = new TestCommandLineInterface (new Options(), invalidPortArgs);
		Assert.assertEquals ("CLI with invalid port, Concourse instance null", invalidPortTestCLI.getConcourse(), null);
	}
	
	/**
	 * Test {@link CommandLineInterface} for testing purposes, simply exists to take
	 * command-line options and to attempt a Concourse connection.
	 * @author hmitchell
	 *
	 */
	private class TestCommandLineInterface extends CommandLineInterface{
				
		protected TestCommandLineInterface(Options options, String[] args) {
			super(options, args);
			setLoginAttemptsRemaining (0);
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
