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
name|test
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
name|piglet
operator|.
name|Ast
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
name|piglet
operator|.
name|Handler
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
name|piglet
operator|.
name|parser
operator|.
name|ParseException
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
name|piglet
operator|.
name|parser
operator|.
name|PigletParser
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
name|plan
operator|.
name|RelOptUtil
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
name|tools
operator|.
name|PigRelBuilder
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
name|util
operator|.
name|Util
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
name|Function
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
name|collect
operator|.
name|Ordering
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/** Fluent API to perform Piglet test actions. */
end_comment

begin_class
class|class
name|Fluent
block|{
specifier|private
specifier|final
name|String
name|pig
decl_stmt|;
name|Fluent
parameter_list|(
name|String
name|pig
parameter_list|)
block|{
name|this
operator|.
name|pig
operator|=
name|pig
expr_stmt|;
block|}
specifier|private
name|Ast
operator|.
name|Program
name|parseProgram
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
operator|new
name|PigletParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|stmtListEof
argument_list|()
return|;
block|}
specifier|public
name|Fluent
name|explainContains
parameter_list|(
name|String
name|expected
parameter_list|)
throws|throws
name|ParseException
block|{
specifier|final
name|Ast
operator|.
name|Program
name|program
init|=
name|parseProgram
argument_list|(
name|pig
argument_list|)
decl_stmt|;
specifier|final
name|PigRelBuilder
name|builder
init|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|PigRelBuilderTest
operator|.
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|Handler
argument_list|(
name|builder
argument_list|)
operator|.
name|handle
argument_list|(
name|program
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Util
operator|.
name|toLinux
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|builder
operator|.
name|peek
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Fluent
name|returns
parameter_list|(
specifier|final
name|String
name|out
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|returns
argument_list|(
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|assertThat
argument_list|(
name|s
argument_list|,
name|is
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|Fluent
name|returnsUnordered
parameter_list|(
name|String
modifier|...
name|lines
parameter_list|)
throws|throws
name|ParseException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expectedLines
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|immutableSortedCopy
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|lines
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|returns
argument_list|(
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|String
name|s
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actualLines
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|i
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|'\n'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|actualLines
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
name|actualLines
operator|.
name|add
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|sortedCopy
argument_list|(
name|actualLines
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedLines
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|Fluent
name|returns
parameter_list|(
name|Function
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
name|checker
parameter_list|)
throws|throws
name|ParseException
block|{
specifier|final
name|Ast
operator|.
name|Program
name|program
init|=
name|parseProgram
argument_list|(
name|pig
argument_list|)
decl_stmt|;
specifier|final
name|PigRelBuilder
name|builder
init|=
name|PigRelBuilder
operator|.
name|create
argument_list|(
name|PigRelBuilderTest
operator|.
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
operator|new
name|CalciteHandler
argument_list|(
name|builder
argument_list|,
name|sw
argument_list|)
operator|.
name|handle
argument_list|(
name|program
argument_list|)
expr_stmt|;
name|checker
operator|.
name|apply
argument_list|(
name|Util
operator|.
name|toLinux
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Fluent
name|parseContains
parameter_list|(
name|String
name|expected
parameter_list|)
throws|throws
name|ParseException
block|{
specifier|final
name|Ast
operator|.
name|Program
name|program
init|=
name|parseProgram
argument_list|(
name|pig
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Util
operator|.
name|toLinux
argument_list|(
name|Ast
operator|.
name|toString
argument_list|(
name|program
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

begin_comment
comment|// End Fluent.java
end_comment

end_unit

