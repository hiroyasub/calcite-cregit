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
name|rex
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link RexSqlConvertletTable}.  *  * @author Sunny Choi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RexSqlReflectiveConvertletTable
implements|implements
name|RexSqlConvertletTable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RexSqlReflectiveConvertletTable
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RexSqlConvertlet
name|get
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
name|RexSqlConvertlet
name|convertlet
decl_stmt|;
specifier|final
name|SqlOperator
name|op
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
comment|// Is there a convertlet for this operator
comment|// (e.g. SqlStdOperatorTable.plusOperator)?
name|convertlet
operator|=
operator|(
name|RexSqlConvertlet
operator|)
name|map
operator|.
name|get
argument_list|(
name|op
argument_list|)
expr_stmt|;
if|if
condition|(
name|convertlet
operator|!=
literal|null
condition|)
block|{
return|return
name|convertlet
return|;
block|}
comment|// Is there a convertlet for this class of operator
comment|// (e.g. SqlBinaryOperator)?
name|Class
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|clazz
init|=
name|op
operator|.
name|getClass
argument_list|()
decl_stmt|;
while|while
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
name|convertlet
operator|=
operator|(
name|RexSqlConvertlet
operator|)
name|map
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
if|if
condition|(
name|convertlet
operator|!=
literal|null
condition|)
block|{
return|return
name|convertlet
return|;
block|}
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
comment|// Is there a convertlet for this class of expression
comment|// (e.g. SqlCall)?
name|clazz
operator|=
name|call
operator|.
name|getClass
argument_list|()
expr_stmt|;
while|while
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
name|convertlet
operator|=
operator|(
name|RexSqlConvertlet
operator|)
name|map
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
if|if
condition|(
name|convertlet
operator|!=
literal|null
condition|)
block|{
return|return
name|convertlet
return|;
block|}
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Registers a convertlet for a given operator instance      *      * @param op Operator instance, say {@link      * org.eigenbase.sql.fun.SqlStdOperatorTable#minusOperator}      * @param convertlet Convertlet      */
specifier|protected
name|void
name|registerOp
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|RexSqlConvertlet
name|convertlet
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|op
argument_list|,
name|convertlet
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RexSqlReflectiveConvertletTable.java
end_comment

end_unit

