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
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>SqlDynamicParam</code> represents a dynamic parameter marker in an  * SQL statement. The textual order in which dynamic parameters appear within an  * SQL statement is the only property which distinguishes them, so this 0-based  * index is recorded as soon as the parameter is encountered.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SqlDynamicParam
extends|extends
name|SqlNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlDynamicParam
parameter_list|(
name|int
name|index
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlDynamicParam
argument_list|(
name|index
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|DYNAMIC_PARAM
return|;
block|}
specifier|public
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setNeedWhitespace
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|validator
operator|.
name|validateDynamicParam
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|Constant
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equalsDeep
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|SqlDynamicParam
operator|)
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
name|this
operator|+
literal|"!="
operator|+
name|node
assert|;
return|return
literal|false
return|;
block|}
name|SqlDynamicParam
name|that
init|=
operator|(
name|SqlDynamicParam
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|index
operator|!=
name|that
operator|.
name|index
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
name|this
operator|+
literal|"!="
operator|+
name|node
assert|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlDynamicParam.java
end_comment

end_unit

