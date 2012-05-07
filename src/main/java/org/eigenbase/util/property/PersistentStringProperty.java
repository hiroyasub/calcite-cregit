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
name|util
operator|.
name|property
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Definition and accessor for a string property that is capable of storing  * itself in a<code>.properties</code> file.  *  * @author Stephan Zuercher  * @version $Id$  * @since December 3, 2004  */
end_comment

begin_class
specifier|public
class|class
name|PersistentStringProperty
extends|extends
name|StringProperty
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// NOTE jvs 2-Oct-2005:  have to avoid dragging in dependencies.
comment|/*     private static final Logger tracer = EigenbaseTrace.getPropertyTracer();      */
specifier|private
specifier|static
specifier|final
name|Logger
name|tracer
init|=
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
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|StringProperty
name|propertyFileLocation
decl_stmt|;
specifier|private
name|PersistentPropertyStorage
name|storage
decl_stmt|;
specifier|private
name|boolean
name|storageInitialized
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a persistent string property.      *      * @param properties Properties object which holds values for this property.      * @param path Name by which this property is serialized to a properties      * file, for example "com.acme.trace.Verbosity".      * @param defaultValue Default value, null if there is no default.      * @param propertyFileLocation Location of the property file where this      * property's value should be persisted.      */
specifier|public
name|PersistentStringProperty
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|defaultValue
parameter_list|,
name|StringProperty
name|propertyFileLocation
parameter_list|)
block|{
name|super
argument_list|(
name|properties
argument_list|,
name|path
argument_list|,
name|defaultValue
argument_list|)
expr_stmt|;
comment|// Delay initialization of storage: the property file location
comment|// may not be initialized until later (e.g. its value may change
comment|// in the constructor of the given properties object).
name|this
operator|.
name|propertyFileLocation
operator|=
name|propertyFileLocation
expr_stmt|;
name|this
operator|.
name|storage
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|storageInitialized
operator|=
literal|false
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Sets the value of this property.      *      *<p/>This method is synchronized to prevent multiple threads from      * attempting to initialize the property storage ({@link #storage})      * simultaneously.      *      * @return The previous value, or the default value if not set.      */
specifier|public
specifier|synchronized
name|String
name|set
parameter_list|(
name|String
name|value
parameter_list|)
block|{
specifier|final
name|String
name|prevValue
init|=
name|super
operator|.
name|set
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|storageInitialized
condition|)
block|{
name|storageInitialized
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|propertyFileLocation
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|tracer
operator|.
name|warning
argument_list|(
literal|"Cannot store property '"
operator|+
name|getPath
argument_list|()
operator|+
literal|"' because storage location is not set"
argument_list|)
expr_stmt|;
return|return
name|prevValue
return|;
block|}
try|try
block|{
name|storage
operator|=
name|PersistentPropertyStorage
operator|.
name|newPersistentPropertyStorage
argument_list|(
name|propertyFileLocation
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|tracer
operator|.
name|warning
argument_list|(
literal|"Unable to initialize persistent property storage for '"
operator|+
name|getPath
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|tracer
operator|.
name|throwing
argument_list|(
literal|"PersistentPropertyStorage"
argument_list|,
literal|"<init>"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|prevValue
return|;
block|}
block|}
if|if
condition|(
name|storage
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|storage
operator|.
name|storeProperty
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|tracer
operator|.
name|warning
argument_list|(
literal|"Unable to persist property '"
operator|+
name|getPath
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|tracer
operator|.
name|throwing
argument_list|(
literal|"PersistentPropertyStorage"
argument_list|,
literal|"set(String)"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|prevValue
return|;
block|}
block|}
end_class

begin_comment
comment|// End PersistentStringProperty.java
end_comment

end_unit

