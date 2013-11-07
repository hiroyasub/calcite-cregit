begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|trace
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
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
name|rel
operator|.
name|RelImplementorImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
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
name|util
operator|.
name|property
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|prepare
operator|.
name|Prepare
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Function2
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Functions
import|;
end_import

begin_comment
comment|/**  * Contains all of the {@link java.util.logging.Logger tracers} used within  * org.eigenbase class libraries.  *  *<h3>Note to developers</h3>  *  *<p>Please ensure that every tracer used in org.eigenbase is added to this  * class as a<em>public static final</em> member called<code>  *<i>component</i>Tracer</code>. For example, {@link #getPlannerTracer} is the  * tracer used by all classes which take part in the query planning process.  *  *<p>The javadoc in this file is the primary source of information on what  * tracers are available, so the javadoc against each tracer member must be an  * up-to-date description of what that tracer does. Be sure to describe what  * {@link Level tracing level} is required to obtain each category of tracing.  *  *<p>In the class where the tracer is used, create a<em>private</em> (or  * perhaps<em>protected</em>)<em>static final</em> member called<code>  * tracer</code>.  *  * @author jhyde  * @version $Id$  * @since May 24, 2004  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|EigenbaseTrace
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * The "org.eigenbase.sql.parser" tracer reports parser events in {@link      * org.eigenbase.sql.parser.SqlParser} and other classes (at level {@link      * Level#FINE} or higher).      */
specifier|public
specifier|static
specifier|final
name|Logger
name|parserTracer
init|=
name|getParserTracer
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Function2
argument_list|<
name|Void
argument_list|,
name|File
argument_list|,
name|String
argument_list|>
argument_list|>
name|DYNAMIC_HANDLER
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Function2
argument_list|<
name|Void
argument_list|,
name|File
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Function2
argument_list|<
name|Void
argument_list|,
name|File
argument_list|,
name|String
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
name|Functions
operator|.
name|ignore2
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * The "org.eigenbase.relopt.RelOptPlanner" tracer prints the query      * optimization process.      *      *<p>Levels:      *      *<ul>      *<li>{@link Level#FINE} prints rules as they fire;      *<li>{@link Level#FINER} prints and validates the whole expression pool      * and rule queue as each rule fires;      *<li>{@link Level#FINEST} prints finer details like rule importances.      *</ul>      */
specifier|public
specifier|static
name|Logger
name|getPlannerTracer
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
name|RelOptPlanner
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * The "net.hydromatic.optiq.prepare.Prepare" tracer prints the generated      * program at level {@link java.util.logging.Level#FINE} or higher.      */
specifier|public
specifier|static
name|Logger
name|getStatementTracer
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
name|Prepare
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * The "org.eigenbase.rel.RelImplementorImpl" tracer reports when      * expressions are bound to variables ({@link Level#FINE})      */
specifier|public
specifier|static
name|Logger
name|getRelImplementorTracer
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
name|RelImplementorImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * The tracer "org.eigenbase.sql.timing" traces timing for various stages of      * query processing.      *      * @see EigenbaseTimingTracer      */
specifier|public
specifier|static
name|Logger
name|getSqlTimingTracer
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"org.eigenbase.sql.timing"
argument_list|)
return|;
block|}
comment|/**      * The "org.eigenbase.sql.parser" tracer reports parse events.      */
specifier|public
specifier|static
name|Logger
name|getParserTracer
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"org.eigenbase.sql.parser"
argument_list|)
return|;
block|}
comment|/**      * The "org.eigenbase.sql2rel" tracer reports parse events.      */
specifier|public
specifier|static
name|Logger
name|getSqlToRelTracer
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"org.eigenbase.sql2rel"
argument_list|)
return|;
block|}
comment|/**      * The "org.eigenbase.jmi.JmiChangeSet" tracer reports JmiChangeSet events.      */
specifier|public
specifier|static
name|Logger
name|getJmiChangeSetTracer
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"org.eigenbase.jmi.JmiChangeSet"
argument_list|)
return|;
block|}
comment|/**      * The "org.eigenbase.util.property.Property" tracer reports errors related      * to all manner of properties.      */
specifier|public
specifier|static
name|Logger
name|getPropertyTracer
parameter_list|()
block|{
return|return
name|Logger
operator|.
name|getLogger
argument_list|(
name|Property
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Thread-local handler that is called with dynamically generated Java code.      * It exists for unit-testing.      * The handler is never null; the default handler does nothing.      */
specifier|public
specifier|static
name|ThreadLocal
argument_list|<
name|Function2
argument_list|<
name|Void
argument_list|,
name|File
argument_list|,
name|String
argument_list|>
argument_list|>
name|getDynamicHandler
parameter_list|()
block|{
return|return
name|DYNAMIC_HANDLER
return|;
block|}
block|}
end_class

begin_comment
comment|// End EigenbaseTrace.java
end_comment

end_unit

