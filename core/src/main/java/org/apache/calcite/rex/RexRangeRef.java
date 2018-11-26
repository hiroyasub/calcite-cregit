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
name|rex
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Reference to a range of columns.  *  *<p>This construct is used only during the process of translating a  * {@link org.apache.calcite.sql.SqlNode SQL} tree to a  * {@link org.apache.calcite.rel.RelNode rel}/{@link RexNode rex}  * tree.<em>Regular {@link RexNode rex} trees do not contain this  * construct.</em></p>  *  *<p>While translating a join of EMP(EMPNO, ENAME, DEPTNO) to DEPT(DEPTNO2,  * DNAME) we create<code>RexRangeRef(DeptType,3)</code> to represent the pair  * of columns (DEPTNO2, DNAME) which came from DEPT. The type has 2 columns, and  * therefore the range represents columns {3, 4} of the input.</p>  *  *<p>Suppose we later create a reference to the DNAME field of this  * RexRangeRef; it will return a<code>{@link RexInputRef}(5,Integer)</code>,  * and the {@link org.apache.calcite.rex.RexRangeRef} will disappear.</p>  */
end_comment

begin_class
specifier|public
class|class
name|RexRangeRef
extends|extends
name|RexNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a range reference.    *    * @param rangeType Type of the record returned    * @param offset    Offset of the first column within the input record    */
name|RexRangeRef
parameter_list|(
name|RelDataType
name|rangeType
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|rangeType
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitRangeRef
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexBiVisitor
argument_list|<
name|R
argument_list|,
name|P
argument_list|>
name|visitor
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitRangeRef
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|RexRangeRef
operator|&&
name|type
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|RexRangeRef
operator|)
name|obj
operator|)
operator|.
name|type
argument_list|)
operator|&&
name|offset
operator|==
operator|(
operator|(
name|RexRangeRef
operator|)
name|obj
operator|)
operator|.
name|offset
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|type
argument_list|,
name|offset
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexRangeRef.java
end_comment

end_unit

