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
name|adapter
operator|.
name|enumerable
package|;
end_package

begin_comment
comment|/**  * Implements a windowed aggregate function by generating expressions to  * initialize, add to, and get a result from, an accumulator.  * Windowed aggregate is more powerful than regular aggregate since it can  * access rows in the current partition by row indices.  * Regular aggregate can be used to implement windowed aggregate.  *<p>This interface does not define new methods: window-specific  * sub-interfaces are passed when implementing window aggregate.  *  * @see org.apache.calcite.adapter.enumerable.StrictWinAggImplementor  * @see org.apache.calcite.adapter.enumerable.RexImpTable.FirstLastValueImplementor  * @see org.apache.calcite.adapter.enumerable.RexImpTable.RankImplementor  * @see org.apache.calcite.adapter.enumerable.RexImpTable.RowNumberImplementor  */
end_comment

begin_interface
specifier|public
interface|interface
name|WinAggImplementor
extends|extends
name|AggImplementor
block|{
comment|/**    * Allows to access rows in window partition relative to first/last and    * current row.    */
specifier|public
enum|enum
name|SeekType
block|{
comment|/**      * Start of window.      * @see WinAggFrameContext#startIndex()      */
name|START
block|,
comment|/**      * Row position in the frame.      * @see WinAggFrameContext#index()      */
name|SET
block|,
comment|/**      * The index of row that is aggregated.      * Valid only in {@link WinAggAddContext}.      * @see WinAggAddContext#currentPosition()      */
name|AGG_INDEX
block|,
comment|/**      * End of window.      * @see WinAggFrameContext#endIndex()      */
name|END
block|}
name|boolean
name|needCacheWhenFrameIntact
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End WinAggImplementor.java
end_comment

end_unit

