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
name|util
operator|.
name|trace
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
name|util
operator|.
name|NumberUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_comment
comment|/**  * CalciteTimingTracer provides a mechanism for tracing the timing of a call  * sequence at nanosecond resolution.  */
end_comment

begin_class
specifier|public
class|class
name|CalciteTimingTracer
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|DecimalFormat
name|DECIMAL_FORMAT
init|=
name|NumberUtil
operator|.
name|decimalFormat
argument_list|(
literal|"###,###,###,###,###"
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
specifier|private
name|long
name|lastNanoTime
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new timing tracer, publishing an initial event (at elapsed time    * 0).    *    * @param logger     logger on which to log timing events; level FINE will be    *                   used    * @param startEvent event to trace as start of timing    */
specifier|public
name|CalciteTimingTracer
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|String
name|startEvent
parameter_list|)
block|{
if|if
condition|(
operator|!
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|logger
operator|=
literal|null
expr_stmt|;
return|return;
block|}
else|else
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
name|lastNanoTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"{}:  elapsed nanos=0"
argument_list|,
name|startEvent
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Publishes an event with the time elapsed since the previous event.    *    * @param event event to trace    */
specifier|public
name|void
name|traceTime
parameter_list|(
name|String
name|event
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|long
name|newNanoTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
name|newNanoTime
operator|-
name|lastNanoTime
decl_stmt|;
name|lastNanoTime
operator|=
name|newNanoTime
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"{}:  elapsed nanos={}"
argument_list|,
name|event
argument_list|,
name|DECIMAL_FORMAT
operator|.
name|format
argument_list|(
name|elapsed
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End CalciteTimingTracer.java
end_comment

end_unit

