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
operator|.
name|fun
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
name|type
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * An operator which performs set operations on multisets, such as "MULTISET  * UNION ALL".  *  *<p>Not to be confused with {@link SqlMultisetValueConstructor} or {@link  * SqlMultisetQueryConstructor}.  *  *<p>todo: Represent the ALL keyword to MULTISET UNION ALL etc. as a hidden  * operand. Then we can obsolete this class.  *  * @author Wael Chatila  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SqlMultisetSetOperator
extends|extends
name|SqlBinaryOperator
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|boolean
name|all
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlMultisetSetOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|prec
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|OTHER
argument_list|,
name|prec
argument_list|,
literal|true
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiNullableMultiset
argument_list|,
name|SqlTypeStrategies
operator|.
name|otiFirstKnown
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcMultisetX2
argument_list|)
expr_stmt|;
name|this
operator|.
name|all
operator|=
name|all
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlMultisetSetOperator.java
end_comment

end_unit

