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
name|avatica
operator|.
name|metrics
operator|.
name|dropwizard3
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Meter
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
comment|/**  * Dropwizard metrics implementation of {@link org.apache.calcite.avatica.metrics.Meter}.  */
end_comment

begin_class
specifier|public
class|class
name|DropwizardMeter
implements|implements
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|metrics
operator|.
name|Meter
block|{
specifier|private
specifier|final
name|Meter
name|meter
decl_stmt|;
specifier|public
name|DropwizardMeter
parameter_list|(
name|Meter
name|meter
parameter_list|)
block|{
name|this
operator|.
name|meter
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|meter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|()
block|{
name|this
operator|.
name|meter
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|this
operator|.
name|meter
operator|.
name|mark
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DropwizardMeter.java
end_comment

end_unit

