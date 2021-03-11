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
name|interpreter
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Enumerator
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|rel
operator|.
name|core
operator|.
name|TableFunctionScan
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|rex
operator|.
name|RexCall
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
name|rex
operator|.
name|RexNode
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
name|schema
operator|.
name|Function
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
name|schema
operator|.
name|impl
operator|.
name|TableFunctionImpl
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
name|validate
operator|.
name|SqlUserDefinedTableFunction
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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Interpreter node that implements a  * {@link TableFunctionScan}.  */
end_comment

begin_class
specifier|public
class|class
name|TableFunctionScanNode
implements|implements
name|Node
block|{
specifier|private
specifier|final
name|Scalar
name|scalar
decl_stmt|;
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
specifier|private
specifier|final
name|Sink
name|sink
decl_stmt|;
specifier|private
specifier|final
name|Function1
argument_list|<
name|?
argument_list|,
name|Row
argument_list|>
name|mapFn
decl_stmt|;
specifier|private
name|TableFunctionScanNode
parameter_list|(
name|Compiler
name|compiler
parameter_list|,
name|TableFunctionScan
name|rel
parameter_list|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|rel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
name|this
operator|.
name|scalar
operator|=
name|compiler
operator|.
name|compile
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|rel
operator|.
name|getCall
argument_list|()
argument_list|)
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|compiler
operator|.
name|createContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|compiler
operator|.
name|sink
argument_list|(
name|rel
argument_list|)
expr_stmt|;
if|if
condition|(
name|rowType
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
operator|&&
name|rel
operator|.
name|getElementType
argument_list|()
operator|!=
name|Object
index|[]
operator|.
name|class
condition|)
block|{
name|this
operator|.
name|mapFn
operator|=
operator|(
name|Function1
argument_list|<
name|Object
argument_list|,
name|Row
argument_list|>
operator|)
name|Row
operator|::
name|of
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|mapFn
operator|=
operator|(
name|Function1
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|,
name|Row
argument_list|>
operator|)
name|Row
operator|::
name|asCopy
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|Object
name|o
init|=
name|scalar
operator|.
name|execute
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Enumerable
condition|)
block|{
for|for
control|(
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|final
name|Enumerator
argument_list|<
name|Row
argument_list|>
name|enumerator
init|=
operator|(
operator|(
name|Enumerable
operator|)
name|o
operator|)
operator|.
name|select
argument_list|(
name|mapFn
argument_list|)
operator|.
name|enumerator
argument_list|()
init|;
name|enumerator
operator|.
name|moveNext
argument_list|()
condition|;
control|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Creates a TableFunctionScanNode. */
specifier|static
name|TableFunctionScanNode
name|create
parameter_list|(
name|Compiler
name|compiler
parameter_list|,
name|TableFunctionScan
name|rel
parameter_list|)
block|{
name|RexNode
name|call
init|=
name|rel
operator|.
name|getCall
argument_list|()
decl_stmt|;
if|if
condition|(
name|call
operator|instanceof
name|RexCall
condition|)
block|{
name|SqlOperator
name|operator
init|=
operator|(
operator|(
name|RexCall
operator|)
name|call
operator|)
operator|.
name|getOperator
argument_list|()
decl_stmt|;
if|if
condition|(
name|operator
operator|instanceof
name|SqlUserDefinedTableFunction
condition|)
block|{
name|Function
name|function
init|=
operator|(
operator|(
name|SqlUserDefinedTableFunction
operator|)
name|operator
operator|)
operator|.
name|function
decl_stmt|;
if|if
condition|(
name|function
operator|instanceof
name|TableFunctionImpl
condition|)
block|{
return|return
operator|new
name|TableFunctionScanNode
argument_list|(
name|compiler
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"cannot convert table function scan "
operator|+
name|rel
operator|.
name|getCall
argument_list|()
operator|+
literal|" to enumerable"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit
