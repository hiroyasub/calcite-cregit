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
name|runtime
package|;
end_package

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
comment|/**  * A counting semaphore. Conceptually, a semaphore maintains a set of permits.  * Each {@link #acquire()} blocks if necessary until a permit is available, and  * then takes it. Each {@link #release()} adds a permit, potentially releasing a  * blocking acquirer. However, no actual permit objects are used; the Semaphore  * just keeps a count of the number available and acts accordingly.  *  *<p>Semaphores are often used to restrict the number of threads than can  * access some (physical or logical) resource.  *  *<p>Note that JDK 1.5 contains<a  * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/Semaphore.html">  * a Semaphore class</a>. We should obsolete this class when we upgrade.  *  * @author jhyde  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|Semaphore
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|int
name|count
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a Semaphore with the given number of permits.      */
specifier|public
name|Semaphore
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Acquires a permit from this semaphore, blocking until one is available.      */
specifier|public
specifier|synchronized
name|void
name|acquire
parameter_list|()
block|{
comment|// REVIEW (jhyde, 2004/7/23): the JDK 1.5 Semaphore class throws
comment|//   InterruptedException; maybe we should too.
while|while
condition|(
name|count
operator|<=
literal|0
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
comment|// we have control, decrement the count
name|count
operator|--
expr_stmt|;
block|}
comment|/**      * Acquires a permit from this semaphore, if one becomes available within      * the given waiting time.      *      *<p>If timeoutMillisec is less than or equal to zero, does not wait at      * all.      */
specifier|public
specifier|synchronized
name|boolean
name|tryAcquire
parameter_list|(
name|long
name|timeoutMillisec
parameter_list|)
block|{
name|long
name|enterTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|endTime
init|=
name|enterTime
operator|+
name|timeoutMillisec
decl_stmt|;
name|long
name|currentTime
init|=
name|enterTime
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tryAcquire: enter="
operator|+
operator|(
name|enterTime
operator|%
literal|100000
operator|)
operator|+
literal|", timeout="
operator|+
name|timeoutMillisec
operator|+
literal|", count="
operator|+
name|count
operator|+
literal|", this="
operator|+
name|this
operator|+
literal|", date="
operator|+
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|(
name|count
operator|<=
literal|0
operator|)
operator|&&
operator|(
name|currentTime
operator|<
name|endTime
operator|)
condition|)
block|{
comment|// REVIEW (jhyde, 2004/7/23): the equivalent method in the JDK 1.5
comment|//   Semaphore class throws InterruptedException; maybe we should
comment|//   too.
try|try
block|{
comment|// Note that wait(0) means no timeout (wait forever), whereas
comment|// tryAcquire(0) means don't wait
assert|assert
operator|(
name|endTime
operator|-
name|currentTime
operator|)
operator|>
literal|0
operator|:
literal|"wait(0) means no timeout!"
assert|;
name|wait
argument_list|(
name|endTime
operator|-
name|currentTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
name|currentTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"enter="
operator|+
operator|(
name|enterTime
operator|%
literal|100000
operator|)
operator|+
literal|", now="
operator|+
operator|(
name|currentTime
operator|%
literal|100000
operator|)
operator|+
literal|", end="
operator|+
operator|(
name|endTime
operator|%
literal|100000
operator|)
operator|+
literal|", timeout="
operator|+
name|timeoutMillisec
operator|+
literal|", remain="
operator|+
operator|(
name|endTime
operator|-
name|currentTime
operator|)
operator|+
literal|", count="
operator|+
name|count
operator|+
literal|", this="
operator|+
name|this
operator|+
literal|", date="
operator|+
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// we may have either been timed out or notified
comment|// let's check which is the case
if|if
condition|(
name|count
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"false"
argument_list|)
expr_stmt|;
block|}
comment|// lock still not released - we were timed out!
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
comment|// we have control, decrement the count
name|count
operator|--
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Releases a permit, returning it to the semaphore.      */
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|()
block|{
name|count
operator|++
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End Semaphore.java
end_comment

end_unit

