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
comment|/**  * SqlCollectionTableOperator is the "table function derived table" operator. It  * converts a table-valued function into a relation, e.g. "<code>SELECT * FROM  * TABLE(ramp(5))</code>".  *  *<p>This operator has function syntax (with one argument), whereas {@link  * SqlStdOperatorTable#explicitTableOperator} is a prefix operator.  *  * @author jhyde, stephan  */
end_comment

begin_class
specifier|public
class|class
name|SqlCollectionTableOperator
extends|extends
name|SqlFunctionalOperator
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|int
name|MODALITY_RELATIONAL
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MODALITY_STREAM
init|=
literal|2
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|int
name|modality
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlCollectionTableOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|modality
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|COLLECTION_TABLE
argument_list|,
literal|200
argument_list|,
literal|true
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiFirstArgType
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcAny
argument_list|)
expr_stmt|;
name|this
operator|.
name|modality
operator|=
name|modality
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|int
name|getModality
parameter_list|()
block|{
return|return
name|modality
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCollectionTableOperator.java
end_comment

end_unit

