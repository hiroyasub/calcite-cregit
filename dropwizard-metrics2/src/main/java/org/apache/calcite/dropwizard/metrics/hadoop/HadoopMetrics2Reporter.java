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
name|dropwizard
operator|.
name|metrics
operator|.
name|hadoop
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsRecordBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
operator|.
name|Interns
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
operator|.
name|MetricsRegistry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Counter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Gauge
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Histogram
import|;
end_import

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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|ScheduledReporter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Snapshot
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
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
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentLinkedQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Dropwizard-Metrics {@link com.codahale.metrics.Reporter} which also acts as a Hadoop Metrics2  * {@link MetricsSource}. Configure it like other Reporters.  *  *<pre>  * final HadoopMetrics2Reporter metrics2Reporter = HadoopMetrics2Reporter.forRegistry(metrics)  *     .build(DefaultMetricsSystem.initialize("Phoenix"), // The application-level name  *            "QueryServer", // Component name  *            "Phoenix Query Server", // Component description  *            "General"); // Name for each metric record  * metrics2Reporter.start(30, TimeUnit.SECONDS);  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|HadoopMetrics2Reporter
extends|extends
name|ScheduledReporter
implements|implements
name|MetricsSource
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HadoopMetrics2Reporter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_STRING
init|=
literal|""
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MetricsInfo
name|RATE_UNIT_LABEL
init|=
name|Interns
operator|.
name|info
argument_list|(
literal|"rate_unit"
argument_list|,
literal|"The unit of measure for rate metrics"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MetricsInfo
name|DURATION_UNIT_LABEL
init|=
name|Interns
operator|.
name|info
argument_list|(
literal|"duration_unit"
argument_list|,
literal|"The unit of measure of duration metrics"
argument_list|)
decl_stmt|;
comment|/**    * Returns a new {@link Builder} for {@link HadoopMetrics2Reporter}.    *    * @param registry the registry to report    * @return a {@link Builder} instance for a {@link HadoopMetrics2Reporter}    */
specifier|public
specifier|static
name|Builder
name|forRegistry
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|registry
argument_list|)
return|;
block|}
comment|/**    * A builder to create {@link HadoopMetrics2Reporter} instances.    */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
specifier|final
name|MetricRegistry
name|registry
decl_stmt|;
specifier|private
name|MetricFilter
name|filter
decl_stmt|;
specifier|private
name|TimeUnit
name|rateUnit
decl_stmt|;
specifier|private
name|TimeUnit
name|durationUnit
decl_stmt|;
specifier|private
name|String
name|recordContext
decl_stmt|;
specifier|private
name|Builder
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|)
block|{
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|MetricFilter
operator|.
name|ALL
expr_stmt|;
name|this
operator|.
name|rateUnit
operator|=
name|TimeUnit
operator|.
name|SECONDS
expr_stmt|;
name|this
operator|.
name|durationUnit
operator|=
name|TimeUnit
operator|.
name|MILLISECONDS
expr_stmt|;
block|}
comment|/**      * Convert rates to the given time unit. Defaults to {@link TimeUnit#SECONDS}.      *      * @param rateUnit a unit of time      * @return {@code this}      */
specifier|public
name|Builder
name|convertRatesTo
parameter_list|(
name|TimeUnit
name|rateUnit
parameter_list|)
block|{
name|this
operator|.
name|rateUnit
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|rateUnit
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Convert durations to the given time unit. Defaults to {@link TimeUnit#MILLISECONDS}.      *      * @param durationUnit a unit of time      * @return {@code this}      */
specifier|public
name|Builder
name|convertDurationsTo
parameter_list|(
name|TimeUnit
name|durationUnit
parameter_list|)
block|{
name|this
operator|.
name|durationUnit
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|durationUnit
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Only report metrics which match the given filter. Defaults to {@link MetricFilter#ALL}.      *      * @param filter a {@link MetricFilter}      * @return {@code this}      */
specifier|public
name|Builder
name|filter
parameter_list|(
name|MetricFilter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A "context" name that will be added as a tag on each emitted metric record. Defaults to      * no "context" attribute on each record.      *      * @param recordContext The "context" tag      * @return {@code this}      */
specifier|public
name|Builder
name|recordContext
parameter_list|(
name|String
name|recordContext
parameter_list|)
block|{
name|this
operator|.
name|recordContext
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|recordContext
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Builds a {@link HadoopMetrics2Reporter} with the given properties, making metrics available      * to the Hadoop Metrics2 framework (any configured {@link MetricsSource}s.      *      * @param metrics2System The Hadoop Metrics2 system instance.      * @param jmxContext The JMX "path", e.g. {@code "MyServer,sub=Requests"}.      * @param description A description these metrics.      * @param recordName A suffix included on each record to identify it.      *      * @return a {@link HadoopMetrics2Reporter}      */
specifier|public
name|HadoopMetrics2Reporter
name|build
parameter_list|(
name|MetricsSystem
name|metrics2System
parameter_list|,
name|String
name|jmxContext
parameter_list|,
name|String
name|description
parameter_list|,
name|String
name|recordName
parameter_list|)
block|{
return|return
operator|new
name|HadoopMetrics2Reporter
argument_list|(
name|registry
argument_list|,
name|rateUnit
argument_list|,
name|durationUnit
argument_list|,
name|filter
argument_list|,
name|metrics2System
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|jmxContext
argument_list|)
argument_list|,
name|description
argument_list|,
name|recordName
argument_list|,
name|recordContext
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|MetricsRegistry
name|metrics2Registry
decl_stmt|;
specifier|private
specifier|final
name|MetricsSystem
name|metrics2System
decl_stmt|;
specifier|private
specifier|final
name|String
name|recordName
decl_stmt|;
specifier|private
specifier|final
name|String
name|context
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|private
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Gauge
argument_list|>
argument_list|>
name|dropwizardGauges
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|>
name|dropwizardCounters
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Histogram
argument_list|>
argument_list|>
name|dropwizardHistograms
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Meter
argument_list|>
argument_list|>
name|dropwizardMeters
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Timer
argument_list|>
argument_list|>
name|dropwizardTimers
decl_stmt|;
specifier|private
name|HadoopMetrics2Reporter
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|,
name|TimeUnit
name|rateUnit
parameter_list|,
name|TimeUnit
name|durationUnit
parameter_list|,
name|MetricFilter
name|filter
parameter_list|,
name|MetricsSystem
name|metrics2System
parameter_list|,
name|String
name|jmxContext
parameter_list|,
name|String
name|description
parameter_list|,
name|String
name|recordName
parameter_list|,
name|String
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|registry
argument_list|,
literal|"hadoop-metrics2-reporter"
argument_list|,
name|filter
argument_list|,
name|rateUnit
argument_list|,
name|durationUnit
argument_list|)
expr_stmt|;
name|this
operator|.
name|metrics2Registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|jmxContext
argument_list|,
name|description
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|metrics2System
operator|=
name|metrics2System
expr_stmt|;
name|this
operator|.
name|recordName
operator|=
name|recordName
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|dropwizardGauges
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|dropwizardCounters
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|dropwizardHistograms
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|dropwizardMeters
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|dropwizardTimers
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
expr_stmt|;
comment|// Register this source with the Metrics2 system.
comment|// Make sure this is the last thing done as getMetrics() can be called at any time after.
name|this
operator|.
name|metrics2System
operator|.
name|register
argument_list|(
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|jmxContext
argument_list|)
argument_list|,
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|description
argument_list|)
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|getMetrics
parameter_list|(
name|MetricsCollector
name|collector
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|MetricsRecordBuilder
name|builder
init|=
name|collector
operator|.
name|addRecord
argument_list|(
name|recordName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|context
condition|)
block|{
name|builder
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|snapshotAllMetrics
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|metrics2Registry
operator|.
name|snapshot
argument_list|(
name|builder
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
comment|/**    * Consumes the current metrics collected by dropwizard and adds them to the {@code builder}.    *    * @param builder A record builder    */
name|void
name|snapshotAllMetrics
parameter_list|(
name|MetricsRecordBuilder
name|builder
parameter_list|)
block|{
comment|// Pass through the gauges
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Gauge
argument_list|>
argument_list|>
name|gaugeIterator
init|=
name|dropwizardGauges
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|gaugeIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Entry
argument_list|<
name|String
argument_list|,
name|Gauge
argument_list|>
name|gauge
init|=
name|gaugeIterator
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|MetricsInfo
name|info
init|=
name|Interns
operator|.
name|info
argument_list|(
name|gauge
operator|.
name|getKey
argument_list|()
argument_list|,
name|EMPTY_STRING
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|o
init|=
name|gauge
operator|.
name|getValue
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Figure out which gauge types metrics2 supports and call the right method
if|if
condition|(
name|o
operator|instanceof
name|Integer
condition|)
block|{
name|builder
operator|.
name|addGauge
argument_list|(
name|info
argument_list|,
operator|(
name|int
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Long
condition|)
block|{
name|builder
operator|.
name|addGauge
argument_list|(
name|info
argument_list|,
operator|(
name|long
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Float
condition|)
block|{
name|builder
operator|.
name|addGauge
argument_list|(
name|info
argument_list|,
operator|(
name|float
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Double
condition|)
block|{
name|builder
operator|.
name|addGauge
argument_list|(
name|info
argument_list|,
operator|(
name|double
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Ignoring Gauge ({}) with unhandled type: {}"
argument_list|,
name|gauge
operator|.
name|getKey
argument_list|()
argument_list|,
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|gaugeIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// Pass through the counters
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|>
name|counterIterator
init|=
name|dropwizardCounters
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|counterIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|counter
init|=
name|counterIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|MetricsInfo
name|info
init|=
name|Interns
operator|.
name|info
argument_list|(
name|counter
operator|.
name|getKey
argument_list|()
argument_list|,
name|EMPTY_STRING
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding counter {} {}"
argument_list|,
name|info
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addCounter
argument_list|(
name|info
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|counterIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// Pass through the histograms
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Histogram
argument_list|>
argument_list|>
name|histogramIterator
init|=
name|dropwizardHistograms
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|histogramIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Entry
argument_list|<
name|String
argument_list|,
name|Histogram
argument_list|>
name|entry
init|=
name|histogramIterator
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|Histogram
name|histogram
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|addSnapshot
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|EMPTY_STRING
argument_list|,
name|histogram
operator|.
name|getSnapshot
argument_list|()
argument_list|,
name|histogram
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|histogramIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// Pass through the meter values
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Meter
argument_list|>
argument_list|>
name|meterIterator
init|=
name|dropwizardMeters
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|meterIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Entry
argument_list|<
name|String
argument_list|,
name|Meter
argument_list|>
name|meterEntry
init|=
name|meterIterator
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|meterEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|Meter
name|meter
init|=
name|meterEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|addMeter
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|EMPTY_STRING
argument_list|,
name|meter
operator|.
name|getCount
argument_list|()
argument_list|,
name|meter
operator|.
name|getMeanRate
argument_list|()
argument_list|,
name|meter
operator|.
name|getOneMinuteRate
argument_list|()
argument_list|,
name|meter
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|,
name|meter
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|meterIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// Pass through the timers (meter + histogram)
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Timer
argument_list|>
argument_list|>
name|timerIterator
init|=
name|dropwizardTimers
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|timerIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Entry
argument_list|<
name|String
argument_list|,
name|Timer
argument_list|>
name|timerEntry
init|=
name|timerIterator
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|timerEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|Timer
name|timer
init|=
name|timerEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|Snapshot
name|snapshot
init|=
name|timer
operator|.
name|getSnapshot
argument_list|()
decl_stmt|;
comment|// Add the meter info (mean rate and rate over time windows)
name|addMeter
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|EMPTY_STRING
argument_list|,
name|timer
operator|.
name|getCount
argument_list|()
argument_list|,
name|timer
operator|.
name|getMeanRate
argument_list|()
argument_list|,
name|timer
operator|.
name|getOneMinuteRate
argument_list|()
argument_list|,
name|timer
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|,
name|timer
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
comment|// Count was already added via the meter
name|addSnapshot
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|EMPTY_STRING
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
name|timerIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// Add in metadata about what the units the reported metrics are displayed using.
name|builder
operator|.
name|tag
argument_list|(
name|RATE_UNIT_LABEL
argument_list|,
name|getRateUnit
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|tag
argument_list|(
name|DURATION_UNIT_LABEL
argument_list|,
name|getDurationUnit
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add Dropwizard-Metrics rate information to a Hadoop-Metrics2 record builder, converting the    * rates to the appropriate unit.    *    * @param builder A Hadoop-Metrics2 record builder.    * @param name A base name for this record.    * @param desc A description for the record.    * @param count The number of measured events.    * @param meanRate The average measured rate.    * @param oneMinuteRate The measured rate over the past minute.    * @param fiveMinuteRate The measured rate over the past five minutes    * @param fifteenMinuteRate The measured rate over the past fifteen minutes.    */
specifier|private
name|void
name|addMeter
parameter_list|(
name|MetricsRecordBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|,
name|long
name|count
parameter_list|,
name|double
name|meanRate
parameter_list|,
name|double
name|oneMinuteRate
parameter_list|,
name|double
name|fiveMinuteRate
parameter_list|,
name|double
name|fifteenMinuteRate
parameter_list|)
block|{
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_count"
argument_list|,
name|EMPTY_STRING
argument_list|)
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_mean_rate"
argument_list|,
name|EMPTY_STRING
argument_list|)
argument_list|,
name|convertRate
argument_list|(
name|meanRate
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_1min_rate"
argument_list|,
name|EMPTY_STRING
argument_list|)
argument_list|,
name|convertRate
argument_list|(
name|oneMinuteRate
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_5min_rate"
argument_list|,
name|EMPTY_STRING
argument_list|)
argument_list|,
name|convertRate
argument_list|(
name|fiveMinuteRate
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_15min_rate"
argument_list|,
name|EMPTY_STRING
argument_list|)
argument_list|,
name|convertRate
argument_list|(
name|fifteenMinuteRate
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add Dropwizard-Metrics value-distribution data to a Hadoop-Metrics2 record building, converting    * the durations to the appropriate unit.    *    * @param builder A Hadoop-Metrics2 record builder.    * @param name A base name for this record.    * @param desc A description for this record.    * @param snapshot The distribution of measured values.    * @param count The number of values which were measured.    */
specifier|private
name|void
name|addSnapshot
parameter_list|(
name|MetricsRecordBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|,
name|Snapshot
name|snapshot
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_count"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|addSnapshot
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|desc
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add Dropwizard-Metrics value-distribution data to a Hadoop-Metrics2 record building, converting    * the durations to the appropriate unit.    *    * @param builder A Hadoop-Metrics2 record builder.    * @param name A base name for this record.    * @param desc A description for this record.    * @param snapshot The distribution of measured values.    */
specifier|private
name|void
name|addSnapshot
parameter_list|(
name|MetricsRecordBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|desc
parameter_list|,
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_mean"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|getMean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_min"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|getMin
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_max"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|getMax
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_median"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|getMedian
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_stddev"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|getStdDev
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_75thpercentile"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|get75thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_95thpercentile"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|get95thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_98thpercentile"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|get98thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_99thpercentile"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|get99thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
name|name
operator|+
literal|"_999thpercentile"
argument_list|,
name|desc
argument_list|)
argument_list|,
name|convertDuration
argument_list|(
name|snapshot
operator|.
name|get999thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|report
parameter_list|(
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Gauge
argument_list|>
name|gauges
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|counters
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Histogram
argument_list|>
name|histograms
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Meter
argument_list|>
name|meters
parameter_list|,
name|SortedMap
argument_list|<
name|String
argument_list|,
name|Timer
argument_list|>
name|timers
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Gauge
argument_list|>
name|gauge
range|:
name|gauges
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|dropwizardGauges
operator|.
name|add
argument_list|(
name|gauge
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|counter
range|:
name|counters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|dropwizardCounters
operator|.
name|add
argument_list|(
name|counter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Histogram
argument_list|>
name|histogram
range|:
name|histograms
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|dropwizardHistograms
operator|.
name|add
argument_list|(
name|histogram
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Meter
argument_list|>
name|meter
range|:
name|meters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|dropwizardMeters
operator|.
name|add
argument_list|(
name|meter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Timer
argument_list|>
name|timer
range|:
name|timers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|dropwizardTimers
operator|.
name|add
argument_list|(
name|timer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getRateUnit
parameter_list|()
block|{
comment|// Make it "events per rate_unit" to be accurate.
return|return
literal|"events/"
operator|+
name|super
operator|.
name|getRateUnit
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getDurationUnit
parameter_list|()
block|{
comment|// Make it visible to the tests
return|return
name|super
operator|.
name|getDurationUnit
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|double
name|convertDuration
parameter_list|(
name|double
name|duration
parameter_list|)
block|{
comment|// Make it visible to the tests
return|return
name|super
operator|.
name|convertDuration
argument_list|(
name|duration
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|double
name|convertRate
parameter_list|(
name|double
name|rate
parameter_list|)
block|{
comment|// Make it visible to the tests
return|return
name|super
operator|.
name|convertRate
argument_list|(
name|rate
argument_list|)
return|;
block|}
comment|// Getters visible for testing
name|MetricsRegistry
name|getMetrics2Registry
parameter_list|()
block|{
return|return
name|metrics2Registry
return|;
block|}
name|MetricsSystem
name|getMetrics2System
parameter_list|()
block|{
return|return
name|metrics2System
return|;
block|}
name|String
name|getRecordName
parameter_list|()
block|{
return|return
name|recordName
return|;
block|}
name|String
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Gauge
argument_list|>
argument_list|>
name|getDropwizardGauges
parameter_list|()
block|{
return|return
name|dropwizardGauges
return|;
block|}
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|>
name|getDropwizardCounters
parameter_list|()
block|{
return|return
name|dropwizardCounters
return|;
block|}
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Histogram
argument_list|>
argument_list|>
name|getDropwizardHistograms
parameter_list|()
block|{
return|return
name|dropwizardHistograms
return|;
block|}
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Meter
argument_list|>
argument_list|>
name|getDropwizardMeters
parameter_list|()
block|{
return|return
name|dropwizardMeters
return|;
block|}
name|ConcurrentLinkedQueue
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Timer
argument_list|>
argument_list|>
name|getDropwizardTimers
parameter_list|()
block|{
return|return
name|dropwizardTimers
return|;
block|}
block|}
end_class

begin_comment
comment|// End HadoopMetrics2Reporter.java
end_comment

end_unit

