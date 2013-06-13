begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|test
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * ConcurrentTestCommandExecutor is a thread that executes a sequence of  * {@link ConcurrentTestCommand commands} on a JDBC connection.  */
end_comment

begin_class
class|class
name|ConcurrentTestCommandExecutor
extends|extends
name|Thread
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * The id for this thread.      */
specifier|private
name|Integer
name|threadId
decl_stmt|;
comment|/**      * JDBC URL to connect with.      */
specifier|private
name|String
name|jdbcURL
decl_stmt|;
comment|/**      * JDBC Connection properties.      */
specifier|private
name|Properties
name|jdbcProps
decl_stmt|;
comment|/**      * Command sequence for this thread.      */
specifier|private
name|Iterator
name|commands
decl_stmt|;
comment|/**      * Used to synchronize command execution.      */
specifier|private
name|Sync
name|synchronizer
decl_stmt|;
comment|/**      * JDBC connection for commands.      */
specifier|private
name|Connection
name|connection
decl_stmt|;
comment|/**      * Current JDBC Statement. May be null.      */
specifier|private
name|Statement
name|statement
decl_stmt|;
comment|/**      * First exception thrown by the thread.      */
specifier|private
name|Throwable
name|error
decl_stmt|;
comment|/**      * Location of {@link #error}.      */
specifier|private
name|String
name|when
decl_stmt|;
comment|/**      * Debugging print stream. May be null.      */
specifier|private
specifier|final
name|PrintStream
name|debugPrintStream
decl_stmt|;
comment|/**      * Command throwing error *      */
specifier|private
name|ConcurrentTestCommand
name|errorCommand
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Constructs a ConcurrentTestCommandExecutor with the given thread      * ID, JDBC URL, commands and synchronization object.      *      * @param threadId the thread ID (see {@link      * ConcurrentTestCommandGenerator})      * @param threadName the thread's name      * @param jdbcURL the JDBC URL to connect to      * @param jdbcProps JDBC Connnection properties (user, password, etc.)      * @param commands the sequence of commands to execute -- null elements      * indicate no-ops      * @param synchronizer synchronization object (may not be null);      * @param debugPrintStream if non-null a PrintStream to use for debugging      * output (may help debugging thread synchronization issues)      */
name|ConcurrentTestCommandExecutor
parameter_list|(
name|int
name|threadId
parameter_list|,
name|String
name|threadName
parameter_list|,
name|String
name|jdbcURL
parameter_list|,
name|Properties
name|jdbcProps
parameter_list|,
name|Iterator
name|commands
parameter_list|,
name|Sync
name|synchronizer
parameter_list|,
name|PrintStream
name|debugPrintStream
parameter_list|)
block|{
name|this
operator|.
name|threadId
operator|=
operator|new
name|Integer
argument_list|(
name|threadId
argument_list|)
expr_stmt|;
name|this
operator|.
name|jdbcURL
operator|=
name|jdbcURL
expr_stmt|;
name|this
operator|.
name|jdbcProps
operator|=
name|jdbcProps
expr_stmt|;
name|this
operator|.
name|commands
operator|=
name|commands
expr_stmt|;
name|this
operator|.
name|synchronizer
operator|=
name|synchronizer
expr_stmt|;
name|this
operator|.
name|debugPrintStream
operator|=
name|debugPrintStream
expr_stmt|;
name|this
operator|.
name|setName
argument_list|(
literal|"Command Executor "
operator|+
name|threadName
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Executes the configured commands.      */
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|connection
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|jdbcURL
argument_list|,
name|jdbcProps
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|supportsTransactions
argument_list|()
condition|)
block|{
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|handleError
argument_list|(
name|t
argument_list|,
literal|"during connect"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// stepNumber is used to reconstitute the original step
comment|// numbers passed by the test case author.
name|int
name|stepNumber
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|commands
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ConcurrentTestCommand
name|command
init|=
operator|(
name|ConcurrentTestCommand
operator|)
name|commands
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|command
operator|instanceof
name|ConcurrentTestCommandGenerator
operator|.
name|AutoSynchronizationCommand
operator|)
condition|)
block|{
name|stepNumber
operator|++
expr_stmt|;
block|}
comment|//  if (debugPrintStream != null) {
comment|//      debugPrintStream.println(Thread.currentThread().getName()
comment|//                               + ": Step "
comment|//                               + stepNumber
comment|//                               + ": "
comment|//                               + System.currentTimeMillis());
comment|//  }
comment|// synchronization commands are always executed, lest we deadlock
name|boolean
name|isSync
init|=
name|command
operator|instanceof
name|ConcurrentTestCommandGenerator
operator|.
name|SynchronizationCommand
decl_stmt|;
if|if
condition|(
name|isSync
operator|||
operator|(
operator|(
name|connection
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|command
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|error
operator|==
literal|null
operator|)
operator|)
condition|)
block|{
try|try
block|{
name|command
operator|.
name|execute
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|handleError
argument_list|(
name|t
argument_list|,
literal|"during step "
operator|+
name|stepNumber
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|supportsTransactions
argument_list|()
condition|)
block|{
name|connection
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|handleError
argument_list|(
name|t
argument_list|,
literal|"during connection close"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Handles details of an exception during execution.      */
specifier|private
name|void
name|handleError
parameter_list|(
name|Throwable
name|error
parameter_list|,
name|String
name|when
parameter_list|,
name|ConcurrentTestCommand
name|command
parameter_list|)
block|{
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
name|this
operator|.
name|when
operator|=
name|when
expr_stmt|;
name|this
operator|.
name|errorCommand
operator|=
name|command
expr_stmt|;
if|if
condition|(
name|debugPrintStream
operator|!=
literal|null
condition|)
block|{
name|debugPrintStream
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|when
argument_list|)
expr_stmt|;
name|error
operator|.
name|printStackTrace
argument_list|(
name|debugPrintStream
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Obtains the thread's JDBC connection.      */
specifier|public
name|Connection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
comment|/**      * Obtains the thread's current JDBC statement. May return null.      */
specifier|public
name|Statement
name|getStatement
parameter_list|()
block|{
return|return
name|statement
return|;
block|}
comment|/**      * Sets the thread's current JDBC statement. To clear the JDBC statement use      * {@link #clearStatement()}.      */
specifier|public
name|void
name|setStatement
parameter_list|(
name|Statement
name|stmt
parameter_list|)
block|{
comment|// assert that we don't already have a statement
assert|assert
operator|(
name|statement
operator|==
literal|null
operator|)
assert|;
name|statement
operator|=
name|stmt
expr_stmt|;
block|}
comment|/**      * Clears the thread's current JDBC statement. To set the JDBC statement use      * {@link #setStatement(Statement)}.      */
specifier|public
name|void
name|clearStatement
parameter_list|()
block|{
name|statement
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Retrieves the object used to synchronize threads at a point in the list      * of commands.      */
specifier|public
name|Sync
name|getSynchronizer
parameter_list|()
block|{
return|return
name|synchronizer
return|;
block|}
comment|/**      * Checks whether an exception occurred during execution. If this method      * returns null, the thread's commands all succeeded. If this method returns      * non-null, see {@link #getFailureLocation()} for details on which command      * caused the failure.      */
specifier|public
name|Throwable
name|getFailureCause
parameter_list|()
block|{
return|return
name|error
return|;
block|}
comment|/**      * Returns location (e.g., command number) for exception returned by {@link      * #getFailureCause()}.      */
specifier|public
name|String
name|getFailureLocation
parameter_list|()
block|{
return|return
name|when
return|;
block|}
specifier|public
name|ConcurrentTestCommand
name|getFailureCommand
parameter_list|()
block|{
return|return
name|errorCommand
return|;
block|}
specifier|public
name|Integer
name|getThreadId
parameter_list|()
block|{
return|return
name|threadId
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * Synchronization object that allows multiple      * ConcurrentTestCommandExecutors to execute commands in lock-step.      * Requires that all ConcurrentTestCommandExecutors have the same      * number of commands.      */
specifier|public
specifier|static
class|class
name|Sync
block|{
specifier|private
name|int
name|numThreads
decl_stmt|;
specifier|private
name|int
name|numWaiting
decl_stmt|;
specifier|public
name|Sync
parameter_list|(
name|int
name|numThreads
parameter_list|)
block|{
assert|assert
operator|(
name|numThreads
operator|>
literal|0
operator|)
assert|;
name|this
operator|.
name|numThreads
operator|=
name|numThreads
expr_stmt|;
name|this
operator|.
name|numWaiting
operator|=
literal|0
expr_stmt|;
block|}
specifier|synchronized
name|void
name|waitForOthers
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
operator|++
name|numWaiting
operator|==
name|numThreads
condition|)
block|{
name|numWaiting
operator|=
literal|0
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// REVIEW: SZ 6/17/2004: Need a timeout here --
comment|// otherwise a test case will hang forever if there's
comment|// a deadlock.  The question is, how long should the
comment|// timeout be to avoid falsely detecting deadlocks?
name|wait
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End ConcurrentTestCommandExecutor.java
end_comment

end_unit

