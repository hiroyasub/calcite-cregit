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
operator|.
name|parser
operator|.
name|parserextensiontesting
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
name|SqlAlter
import|;
end_import

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
name|SqlKind
import|;
end_import

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
name|SqlNode
import|;
end_import

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
name|SqlOperator
import|;
end_import

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
name|SqlSpecialOperator
import|;
end_import

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
name|SqlWriter
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Simple test example of a custom alter system call.  */
end_comment

begin_class
specifier|public
class|class
name|SqlUploadJarNode
extends|extends
name|SqlAlter
block|{
specifier|public
specifier|static
specifier|final
name|SqlOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"UPLOAD JAR"
argument_list|,
name|SqlKind
operator|.
name|OTHER_DDL
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|jarPaths
decl_stmt|;
comment|/** Creates a SqlUploadJarNode. */
specifier|public
name|SqlUploadJarNode
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|String
name|scope
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|jarPaths
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|,
name|scope
argument_list|)
expr_stmt|;
name|this
operator|.
name|jarPaths
operator|=
name|jarPaths
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|OPERATOR
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|jarPaths
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|unparseAlterOperation
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
name|keyword
argument_list|(
literal|"UPLOAD"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"JAR"
argument_list|)
expr_stmt|;
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|jarPath
range|:
name|jarPaths
control|)
block|{
name|jarPath
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

