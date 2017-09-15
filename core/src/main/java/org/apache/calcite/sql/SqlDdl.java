begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|parser
operator|.
name|SqlParserPos
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/** Base class for CREATE, DROP and other DDL statements. */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlDdl
extends|extends
name|SqlCall
block|{
comment|/** Use this operator only if you don't have a better one. */
specifier|protected
specifier|static
specifier|final
name|SqlOperator
name|DDL_OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"DDL"
argument_list|,
name|SqlKind
operator|.
name|OTHER_DDL
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SqlOperator
name|operator
decl_stmt|;
comment|/** Creates a SqlDdl. */
specifier|public
name|SqlDdl
parameter_list|(
name|SqlOperator
name|operator
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
name|operator
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|operator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|operator
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlDdl.java
end_comment

end_unit

