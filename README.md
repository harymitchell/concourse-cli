# concourse-cli

The concourse-cli framework allows third party developers to define **client side** command line interfaces that interact directly with **Concourse**.

## General Information

### Versioning

This is version 1.0.1 of the concourse-config-framework.

This project will be maintained under the [Semantic Versioning](http://semver.org)
guidelines such that release versions will be formatted as `<major>.<minor>.<patch>`
where

* breaking backward compatibility bumps the major,
* new additions while maintaining backward compatibility bumps the minor, and
* bug fixes or miscellaneous changes bumps the patch.

## CLI Options
The first step in creating a CLI is to define the valid `Options`. Options are variables and flags that are passed into the CLI by the caller. We use [JCommander](http://jcommander.org/) to parse CLI options so defining them is very simple using Java annotations.
	
	
	public class SampleOptions extends Options {

    	@Parameter(names = "--allCaps", description = "Flag to enable printing in all caps")
    	public boolean allCaps = false; // this is the default value if the flag is
                                    	// not present

    	@Parameter(names = { "-m", "--message" }, description = "The message to print", required = true)
    	public String message;

	}


The base `Options` class specifies arguments that are common to all Concourse CLIs (i.e. connection info, etc). Support for generating a usage message in the appropriate contexts is built in. All you need to do is define the specific opitions for your CLI and move on :)

## CLI Class
After defining your Options, you must define the class that will do the work associated with your CLI. 

	public class SampleCli extends CommandLineInterface {

    	/**
     	* Construct a new instance.
     	* 
     	* @param args
     	*/
    	public SampleCli(String[] args) {
        	super(new SampleOptions(), args); // I must pass an instance of my CLI's
                                          		// options to the super constructor
    	}

    	@Override
    	protected void doTask() {
        	SampleOptions myOptions = (SampleOptions) options;
        	Concourse concourse = Concourse.connect(myOptions.host, myOptions.port,
                	myOptions.username, myOptions.password); // Each CLI is
                                                         // responsible for
                                                         // establishing its own
                                                         // connection to
                                                         // Concourse
        	System.out.println(concourse.getServerVersion());
        	String output = myOptions.message;
        	if(myOptions.allCaps) {
            	output = output.toUpperCase();
        	}
        	System.out.println(output);
    	}

	}
	
**NOTE:** You should **not** call `System.exit()` from the `doTask()` method because this is handled by the framework. If there is a failure, you should throw a `RuntimeException` from the method. Otherwise, the CLI is considered to have suceeded.


## Shell Script
The CLI can be launched from the command line using Java; however, it is usually desirable to create a shell script that handles proper path setting, etc.

### The main script
Use the basic template below for defining a shell script that run your CLI. **You'll want to change the $CLI and $CLASSPATH definitions**. This script ensures that the proper path is called and that any additional arguments passed to the CLI are passed to the appropriate Java code.

###### sample.sh

	#!/usr/bin/env bash
	
	# Specify the fully qualified name of the java CLI class to run. 
	CLI="com.foo.bar.SampleCli""
	
	# Define the classpath that contains the code necessary for the CLI to execute.
	CLASSPATH="../lib"

	# This config will setup all the enviornment variables and check that
	# pthats are proper. 
	. "`dirname "$0"`/.env"

	# run the program
	exec $JAVACMD -classpath "$CLASSPATH" org.cinchapi.concourse.cli.CommandLineInterfaceRunner $CLI "$@"

### Environment configuration script
Be sure to define a script that properly defines the environment in which the shell script run and sets the appropriate paths. For example, you need to make sure that the **concourse-cli** jar is on the classpath. CLI configuration is highly variable depending on the context of the application, but here is an example of what we use for the shell scripts in the concourse-server project.

######.env
	#!/usr/bin/env bash

	###############################################
	###     Configuration for Concourse CLIs    ###
	###############################################

	warn ( ) {
    	echo "$*"
	}

	die ( ) {
    	echo
    	echo "$*"
    	echo
    	exit 1
	}

	# Use the maximum available, or set MAX_FD != -1 to use that value.
	MAX_FD="maximum"

	# OS specific support (must be 'true' or 'false').
	cygwin=false
	msys=false
	darwin=false
	case "`uname`" in
  	CYGWIN* )
    	cygwin=true
    	;;
  	Darwin* )
    	darwin=true
    	;;
  	MINGW* )
    	msys=true
    	;;
	esac

	# For Cygwin, ensure paths are in UNIX format before anything is touched.
	if $cygwin ; then
    	[ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
	fi

	# Set APP_HOME to the parent directory
	cd "${0%/*}"
	APP_HOME="`pwd -P`/.."
	cd $APP_HOME

	# Determine the Java command to use to start the JVM.
	if [ -n "$JAVA_HOME" ] ; then
    	if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        	# IBM's JDK on AIX uses strange locations for the executables
        	JAVACMD="$JAVA_HOME/jre/sh/java"
    	else
        	JAVACMD="$JAVA_HOME/bin/java"
    	fi
    	if [ ! -x "$JAVACMD" ] ; then
        	die "ERROR: JAVA_HOME is set to an invalid directory: 	$JAVA_HOME

	Please set the JAVA_HOME variable in your environment to match the
	location of your Java installation."
    	fi
	else
    	JAVACMD="java"
    	which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set 	and no 'java' command could be found in your PATH.

	Please set the JAVA_HOME variable in your environment to match the
	location of your Java installation."
	fi

	# Increase the maximum file 	descriptors if we can.
	if [ "$cygwin" = "false" -a "$darwin" = "false" ] ; then
    	MAX_FD_LIMIT=`ulimit -H -n`
    	if [ $? -eq 0 ] ; then
        	if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            	MAX_FD="$MAX_FD_LIMIT"
        	fi
        	ulimit -n $MAX_FD
        	if [ $? -ne 0 ] ; then
            	warn "Could not set maximum file descriptor limit: $MAX_FD"
        	fi
    	else
        	warn "Could not query maximum file descriptor limit: 		$MAX_FD_LIMIT"
    	fi
	fi

	# For Cygwin, switch paths to Windows format before running java
	if $cygwin ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 1 -mindepth 1 -type d 2>/dev/null`
    SEP=""
    for dir in $ROOTDIRSRAW ; do
        ROOTDIRS="$ROOTDIRS$SEP$dir"
        SEP="|"
    done
    OURCYGPATTERN="(^($ROOTDIRS))"
    # Add a user-defined pattern to the cygpath arguments
    if [ "$GRADLE_CYGPATTERN" != "" ] ; then
        OURCYGPATTERN="$OURCYGPATTERN|($GRADLE_CYGPATTERN)"
    fi
    # Now convert the arguments - kludge to limit ourselves to /bin/sh
    i=0
    for arg in "$@" ; do
        CHECK=`echo "$arg"|egrep -c "$OURCYGPATTERN" -`
        CHECK2=`echo "$arg"|egrep -c "^-"`                                 ### Determine if an option

        if [ $CHECK -ne 0 ] && [ $CHECK2 -eq 0 ] ; then                    ### Added a condition
            eval `echo args$i`=`cygpath --path --ignore --mixed "$arg"`
        else
            eval `echo args$i`="\"$arg\""
        fi
        i=$((i+1))
    done
    case $i in
        (0) set -- ;;
        (1) set -- "$args0" ;;
        (2) set -- "$args0" "$args1" ;;
        (3) set -- "$args0" "$args1" "$args2" ;;
        (4) set -- "$args0" "$args1" "$args2" "$args3" ;;
        (5) set -- "$args0" "$args1" "$args2" "$args3" "$args4" ;;
        (6) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" ;;
        (7) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" ;;
        (8) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" ;;
        (9) set -- "$args0" "$args1" "$args2" "$args3" "$args4" "$args5" "$args6" "$args7" "$args8" ;;
    esac
	fi

	# Set CLASSPATH
	CLASSPATH="$APP_HOME/lib/*"
	
**NOTE:** The .env script assumes it and the called CLI are located in a subdirectory of the application home. It also assumes that the *concourse-cli* jar and other resources are located in another subdirectory of the application home called *lib*.
