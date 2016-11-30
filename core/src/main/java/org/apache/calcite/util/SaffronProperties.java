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
name|runtime
operator|.
name|Resources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|Resources
operator|.
name|BooleanProp
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|Resources
operator|.
name|Default
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|Resources
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|Resources
operator|.
name|StringProp
import|;
end_import

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
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Provides an environment for debugging information, et cetera, used by  * saffron.  *  *<p>It is a singleton, accessed via the {@link #INSTANCE} object. It is  * populated from System properties if saffron is invoked via a<code>  * main()</code> method, from a<code>javax.servlet.ServletContext</code> if  * saffron is invoked from a servlet, and so forth. If there is a file called  *<code>"saffron.properties"</code> in the current directory, it is read too.  *  *<p>Every property used in saffron code must have a method in this interface.  * The method must return a sub-class of  * {@link org.apache.calcite.runtime.Resources.Prop}. The javadoc  * comment must describe the name of the property (for example,  * "net.sf.saffron.connection.PoolSize") and the default value, if any.<em>  * Developers, please make sure that this remains so!</em>  */
end_comment

begin_interface
specifier|public
interface|interface
name|SaffronProperties
block|{
comment|/**    * The boolean property "saffron.opt.allowInfiniteCostConverters" determines    * whether the optimizer will consider adding converters of infinite cost in    * order to convert a relational expression from one calling convention to    * another. The default value is<code>true</code>.    */
annotation|@
name|Resource
argument_list|(
literal|"saffron.opt.allowInfiniteCostConverters"
argument_list|)
annotation|@
name|Default
argument_list|(
literal|"true"
argument_list|)
name|BooleanProp
name|allowInfiniteCostConverters
parameter_list|()
function_decl|;
comment|/**    * The string property "saffron.default.charset" is the name of the default    * character set. The default is "ISO-8859-1". It is used in    * {@link org.apache.calcite.sql.validate.SqlValidator}.    */
annotation|@
name|Resource
argument_list|(
literal|"saffron.default.charset"
argument_list|)
annotation|@
name|Default
argument_list|(
literal|"ISO-8859-1"
argument_list|)
name|StringProp
name|defaultCharset
parameter_list|()
function_decl|;
comment|/**    * The string property "saffron.default.nationalcharset" is the name of the    * default national character set which is used with the N'string' construct    * which may or may not be different from the {@link #defaultCharset}. The    * default is "ISO-8859-1". It is used in    * {@link org.apache.calcite.sql.SqlLiteral#SqlLiteral}    */
annotation|@
name|Resource
argument_list|(
literal|"saffron.default.nationalcharset"
argument_list|)
annotation|@
name|Default
argument_list|(
literal|"ISO-8859-1"
argument_list|)
name|StringProp
name|defaultNationalCharset
parameter_list|()
function_decl|;
comment|/**    * The string property "saffron.default.collation.name" is the name of the    * default collation. The default is "ISO-8859-1$en_US". Used in    * {@link org.apache.calcite.sql.SqlCollation} and    * {@link org.apache.calcite.sql.SqlLiteral#SqlLiteral}    */
annotation|@
name|Resource
argument_list|(
literal|"saffron.default.collation.name"
argument_list|)
annotation|@
name|Default
argument_list|(
literal|"ISO-8859-1$en_US"
argument_list|)
name|StringProp
name|defaultCollation
parameter_list|()
function_decl|;
comment|/**    * The string property "saffron.default.collation.strength" is the strength    * of the default collation. The default is "primary". Used in    * {@link org.apache.calcite.sql.SqlCollation} and    * {@link org.apache.calcite.sql.SqlLiteral#SqlLiteral}    */
annotation|@
name|Resource
argument_list|(
literal|"saffron.default.collation.strength"
argument_list|)
annotation|@
name|Default
argument_list|(
literal|"primary"
argument_list|)
name|StringProp
name|defaultCollationStrength
parameter_list|()
function_decl|;
name|SaffronProperties
name|INSTANCE
init|=
name|Helper
operator|.
name|instance
argument_list|()
decl_stmt|;
comment|/** Helper class. */
class|class
name|Helper
block|{
specifier|private
name|Helper
parameter_list|()
block|{
block|}
comment|/**      * Retrieves the singleton instance of {@link SaffronProperties}.      */
specifier|static
name|SaffronProperties
name|instance
parameter_list|()
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|// read properties from the file "saffron.properties", if it exists
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"saffron.properties"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
name|properties
operator|.
name|load
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"while reading from "
operator|+
name|file
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// we're in a sandbox
block|}
comment|// copy in all system properties which start with "saffron."
name|Properties
name|source
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|Enumeration
name|keys
init|=
name|source
operator|.
name|keys
argument_list|()
init|;
name|keys
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|keys
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|source
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"saffron."
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
literal|"net.sf.saffron."
argument_list|)
condition|)
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Resources
operator|.
name|create
argument_list|(
name|properties
argument_list|,
name|SaffronProperties
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End SaffronProperties.java
end_comment

end_unit

