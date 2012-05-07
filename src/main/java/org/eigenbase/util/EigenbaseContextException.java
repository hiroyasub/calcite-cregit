begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
comment|/**  * Exception which contains information about the textual context of the causing  * exception.  */
end_comment

begin_class
specifier|public
class|class
name|EigenbaseContextException
extends|extends
name|EigenbaseException
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * SerialVersionUID created with JDK 1.5 serialver tool. Prevents      * incompatible class conflict when serialized from JDK 1.5-built server to      * JDK 1.4-built client.      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|54978888153560134L
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|int
name|posLine
decl_stmt|;
specifier|private
name|int
name|posColumn
decl_stmt|;
specifier|private
name|int
name|endPosLine
decl_stmt|;
specifier|private
name|int
name|endPosColumn
decl_stmt|;
specifier|private
name|String
name|originalStatement
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a new EigenbaseContextException object. This constructor is for      * use by the generated factory.      *      * @param message error message      * @param cause underlying cause, must not be null      */
specifier|public
name|EigenbaseContextException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
argument_list|(
name|message
argument_list|,
name|cause
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new EigenbaseContextException object.      *      * @param message error message      * @param cause underlying cause, must not be null      * @param posLine 1-based start line number      * @param posColumn 1-based start column number      * @param endPosLine 1-based end line number      * @param endPosColumn 1-based end column number      */
specifier|public
name|EigenbaseContextException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|int
name|posLine
parameter_list|,
name|int
name|posColumn
parameter_list|,
name|int
name|endPosLine
parameter_list|,
name|int
name|endPosColumn
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|cause
operator|!=
literal|null
operator|)
assert|;
name|setPosition
argument_list|(
name|posLine
argument_list|,
name|posColumn
argument_list|,
name|endPosLine
argument_list|,
name|endPosColumn
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new EigenbaseContextException object. This constructor is for      * use by the generated factory.      *      * @param message error message      * @param cause underlying cause, must not be null      * @param inputText is the orginal SQL statement, may be null      */
specifier|public
name|EigenbaseContextException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|String
name|inputText
parameter_list|)
block|{
name|this
argument_list|(
name|message
argument_list|,
name|cause
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|originalStatement
operator|=
name|inputText
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Sets a textual position at which this exception was detected.      *      * @param posLine 1-based line number      * @param posColumn 1-based column number      */
specifier|public
name|void
name|setPosition
parameter_list|(
name|int
name|posLine
parameter_list|,
name|int
name|posColumn
parameter_list|)
block|{
name|this
operator|.
name|posLine
operator|=
name|posLine
expr_stmt|;
name|this
operator|.
name|posColumn
operator|=
name|posColumn
expr_stmt|;
name|this
operator|.
name|endPosLine
operator|=
name|posLine
expr_stmt|;
name|this
operator|.
name|endPosColumn
operator|=
name|posColumn
expr_stmt|;
block|}
comment|/**      * Sets a textual range at which this exception was detected.      *      * @param posLine 1-based start line number      * @param posColumn 1-based start column number      * @param endPosLine 1-based end line number      * @param endPosColumn 1-based end column number      */
specifier|public
name|void
name|setPosition
parameter_list|(
name|int
name|posLine
parameter_list|,
name|int
name|posColumn
parameter_list|,
name|int
name|endPosLine
parameter_list|,
name|int
name|endPosColumn
parameter_list|)
block|{
name|this
operator|.
name|posLine
operator|=
name|posLine
expr_stmt|;
name|this
operator|.
name|posColumn
operator|=
name|posColumn
expr_stmt|;
name|this
operator|.
name|endPosLine
operator|=
name|endPosLine
expr_stmt|;
name|this
operator|.
name|endPosColumn
operator|=
name|endPosColumn
expr_stmt|;
block|}
comment|/**      * @return 1-based line number, or 0 for missing position information      */
specifier|public
name|int
name|getPosLine
parameter_list|()
block|{
return|return
name|posLine
return|;
block|}
comment|/**      * @return 1-based column number, or 0 for missing position information      */
specifier|public
name|int
name|getPosColumn
parameter_list|()
block|{
return|return
name|posColumn
return|;
block|}
comment|/**      * @return 1-based ending line number, or 0 for missing position information      */
specifier|public
name|int
name|getEndPosLine
parameter_list|()
block|{
return|return
name|endPosLine
return|;
block|}
comment|/**      * @return 1-based ending column number, or 0 for missing position      * information      */
specifier|public
name|int
name|getEndPosColumn
parameter_list|()
block|{
return|return
name|endPosColumn
return|;
block|}
comment|/**      * @return the input string that is associated with the context      */
specifier|public
name|String
name|getOriginalStatement
parameter_list|()
block|{
return|return
name|originalStatement
return|;
block|}
comment|/**      * @param originalStatement - String to associate with the current context      */
specifier|public
name|void
name|setOriginalStatement
parameter_list|(
name|String
name|originalStatement
parameter_list|)
block|{
name|this
operator|.
name|originalStatement
operator|=
name|originalStatement
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End EigenbaseContextException.java
end_comment

end_unit

