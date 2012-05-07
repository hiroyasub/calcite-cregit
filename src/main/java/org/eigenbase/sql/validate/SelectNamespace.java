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
name|validate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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
comment|/**  * Namespace offered by a subquery.  *  * @author jhyde  * @version $Id$  * @see SelectScope  * @see SetopNamespace  * @since Mar 25, 2003  */
end_comment

begin_class
specifier|public
class|class
name|SelectNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlSelect
name|select
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a SelectNamespace.      *      * @param validator Validate      * @param select Select node      * @param enclosingNode Enclosing node      */
specifier|public
name|SelectNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlSelect
name|select
parameter_list|,
name|SqlNode
name|enclosingNode
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|,
name|enclosingNode
argument_list|)
expr_stmt|;
name|this
operator|.
name|select
operator|=
name|select
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement SqlValidatorNamespace, overriding return type
specifier|public
name|SqlSelect
name|getNode
parameter_list|()
block|{
return|return
name|select
return|;
block|}
specifier|public
name|RelDataType
name|validateImpl
parameter_list|()
block|{
name|validator
operator|.
name|validateSelect
argument_list|(
name|select
argument_list|,
name|validator
operator|.
name|unknownType
argument_list|)
expr_stmt|;
return|return
name|rowType
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|this
operator|.
name|getRowTypeSansSystemColumns
argument_list|()
decl_stmt|;
specifier|final
name|int
name|field
init|=
name|SqlTypeUtil
operator|.
name|findField
argument_list|(
name|rowType
argument_list|,
name|columnName
argument_list|)
decl_stmt|;
specifier|final
name|SqlNodeList
name|selectList
init|=
name|select
operator|.
name|getSelectList
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|selectItem
init|=
name|selectList
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|validator
operator|.
name|getSelectScope
argument_list|(
name|select
argument_list|)
operator|.
name|getMonotonicity
argument_list|(
name|selectItem
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SelectNamespace.java
end_comment

end_unit

