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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|*
import|;
end_import

begin_comment
comment|// NOTE:  This class gets compiled independently of everything else so that
end_comment

begin_comment
comment|// resource generation can use reflection.  That means it must have no
end_comment

begin_comment
comment|// dependencies on other Eigenbase code.
end_comment

begin_comment
comment|/**  * Base class for all exceptions originating from Farrago.  *  * @see EigenbaseContextException  */
end_comment

begin_class
specifier|public
class|class
name|EigenbaseException
extends|extends
name|RuntimeException
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * SerialVersionUID created with JDK 1.5 serialver tool. Prevents    * incompatible class conflict when serialized from JDK 1.5-built server to    * JDK 1.4-built client.    */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|1314522633397794178L
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|tracer
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|EigenbaseException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new EigenbaseException object.    *    * @param message error message    * @param cause   underlying cause    */
specifier|public
name|EigenbaseException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
comment|// TODO: Force the caller to pass in a Logger as a trace argument for
comment|// better context.  Need to extend ResGen for this.
name|tracer
operator|.
name|throwing
argument_list|(
literal|"EigenbaseException"
argument_list|,
literal|"constructor"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|tracer
operator|.
name|severe
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End EigenbaseException.java
end_comment

end_unit

