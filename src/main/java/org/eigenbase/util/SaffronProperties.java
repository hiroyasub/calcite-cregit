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
name|security
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

begin_comment
comment|/**  * Provides an environment for debugging information, et cetera, used by  * saffron.  *  *<p>{@link #getIntProperty} and {@link #getBooleanProperty} are convenience  * methods.</p>  *  *<p>It is a singleton, accessed via the {@link #instance} method. It is  * populated from System properties if saffron is invoked via a<code>  * main()</code> method, from a<code>javax.servlet.ServletContext</code> if  * saffron is invoked from a servlet, and so forth. If there is a file called  *<code>"saffron.properties"</code> in the current directory, it is read too.  *</p>  *  *<p>Every property used in saffron code must have a member in this class. The  * member must be public and final, and be of type {@link  * org.eigenbase.util.property.Property} or some subtype. The javadoc comment  * must describe the name of the property (for example,  * "net.sf.saffron.connection.PoolSize") and the default value, if any.<em>  * Developers, please make sure that this remains so!</em></p>  */
end_comment

begin_class
specifier|public
class|class
name|SaffronProperties
extends|extends
name|Properties
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * The singleton properties object.      */
specifier|private
specifier|static
name|SaffronProperties
name|properties
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * The string property "saffron.class.dir" is the path of the directory to      * compile classes to.      */
specifier|public
specifier|final
name|StringProperty
name|classDir
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.class.dir"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.java.compiler.class" is the name of the Java      * compiler to use. It must implement {@link      * org.eigenbase.javac.JavaCompiler}. The default value is      * "JP.ac.tsukuba.openjava.SunJavaCompiler".      */
specifier|public
specifier|final
name|StringProperty
name|javaCompilerClass
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.java.compiler.class"
argument_list|,
literal|"JP.ac.tsukuba.openjava.SunJavaCompiler"
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.package.name" is the package in which to      * include temporary classes. The default is "saffron.runtime".      */
specifier|public
specifier|final
name|StringProperty
name|packageName
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.package.name"
argument_list|,
literal|"saffron.runtime"
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.java.dir" is the directory to generate      * temporary java files to. The default is {@link #classDir the class root}.      */
specifier|public
specifier|final
name|StringProperty
name|javaDir
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.java.dir"
argument_list|,
literal|null
argument_list|)
block|{
specifier|public
name|String
name|getDefaultValue
parameter_list|()
block|{
return|return
name|classDir
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**      * The string property "saffron.java.compiler.args" is the argument string      * for the {@link #javaCompilerClass java compiler}. {@link      * org.eigenbase.javac.JavaCompilerArgs#setString} describes how these      * arguments are interpreted.      */
specifier|public
specifier|final
name|StringProperty
name|javaCompilerArgs
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.java.compiler.args"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * The boolean property "saffron.stupid" determines whether to optimize      * variable assignments. If it is true, records are assigned to a variable      * even if they are never used. Default is false.      */
specifier|public
specifier|final
name|BooleanProperty
name|stupid
init|=
operator|new
name|BooleanProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.stupid"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**      * The integer property "saffron.debug.level" determines how much debugging      * information is printed. The default, 0, means no debugging.      */
specifier|public
specifier|final
name|IntegerProperty
name|debugLevel
init|=
operator|new
name|IntegerProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.debug.level"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.debug.out" is the name of the file to send      * debugging information to.<code>"out"</code> (the default), means send to      * {@link System#out};<code>"err"</code> means send to {@link System#err}.      */
specifier|public
specifier|final
name|StringProperty
name|debugOut
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.debug.out"
argument_list|,
literal|"out"
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.test.Name" is used by {@link      * net.sf.farrago.test.FarragoTestCase} to filter tests.      */
specifier|public
specifier|final
name|StringProperty
name|testName
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.test.Name"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.test.Class" is used by {@link      * net.sf.farrago.test.FarragoTestCase} to filter tests.      */
specifier|public
specifier|final
name|StringProperty
name|testClass
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.test.Class"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.test.Suite" is used by {@link      * net.sf.farrago.test.FarragoTestCase} to filter tests.      */
specifier|public
specifier|final
name|StringProperty
name|testSuite
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.test.Suite"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.test.everything" is used by {@link      * net.sf.farrago.test.FarragoTestCase} to filter tests.      */
specifier|public
specifier|final
name|BooleanProperty
name|testEverything
init|=
operator|new
name|BooleanProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.test.everything"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.test.jdbc.url" is the URL of the JDBC      * database which contains the EMP and DEPT tables used for testing.      */
specifier|public
specifier|final
name|StringProperty
name|testJdbcUrl
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.test.jdbc.url"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.test.jdbc.drivers" is a comma-separated list      * of class names to be used as JDBC drivers.      */
specifier|public
specifier|final
name|StringProperty
name|testJdbcDrivers
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.test.jdbc.drivers"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * The boolean property "saffron.opt.allowInfiniteCostConverters" determines      * whether the optimizer will consider adding converters of infinite cost in      * order to convert a relational expression from one calling convention to      * another. The default value is<code>true</code>.      */
specifier|public
specifier|final
name|BooleanProperty
name|allowInfiniteCostConverters
init|=
operator|new
name|BooleanProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.opt.allowInfiniteCostConverters"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.default.charset" is the name of the default      * character set. The default is "ISO-8859-1". It is used in {@link      * org.eigenbase.sql.validate.SqlValidator}.      */
specifier|public
specifier|final
name|StringProperty
name|defaultCharset
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.default.charset"
argument_list|,
literal|"ISO-8859-1"
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.default.nationalcharset" is the name of the      * default national character set which is used with the N'string' construct      * which may or may not be different from the {@link #defaultCharset}. The      * default is "ISO-8859-1". It is used in {@link      * org.eigenbase.sql.SqlLiteral#SqlLiteral}      */
specifier|public
specifier|final
name|StringProperty
name|defaultNationalCharset
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.default.nationalcharset"
argument_list|,
literal|"ISO-8859-1"
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.default.collation.name" is the name of the      * default collation. The default is "ISO-8859-1$en_US". Used in {@link      * org.eigenbase.sql.SqlCollation} and {@link      * org.eigenbase.sql.SqlLiteral#SqlLiteral}      */
specifier|public
specifier|final
name|StringProperty
name|defaultCollation
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.default.collation.name"
argument_list|,
literal|"ISO-8859-1$en_US"
argument_list|)
decl_stmt|;
comment|/**      * The string property "saffron.default.collation.strength" is the strength      * of the default collation. The default is "primary". Used in {@link      * org.eigenbase.sql.SqlCollation} and {@link      * org.eigenbase.sql.SqlLiteral#SqlLiteral}      */
specifier|public
specifier|final
name|StringProperty
name|defaultCollationStrength
init|=
operator|new
name|StringProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.default.collation.strength"
argument_list|,
literal|"primary"
argument_list|)
decl_stmt|;
comment|/**      * The boolean property "saffron.calc.comments.generate" determines if to      * generate comments in calculator programs in order to make debugging      * easier. The default is "true". Used in {@link      * org.eigenbase.sql.SqlCollation} and {@link      * org.eigenbase.sql.SqlLiteral#SqlLiteral}      */
specifier|public
specifier|final
name|BooleanProperty
name|generateCalcProgramComments
init|=
operator|new
name|BooleanProperty
argument_list|(
name|this
argument_list|,
literal|"saffron.calc.comments.generate"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * This constructor is private; please use {@link #instance} to create a      * {@link SaffronProperties}.      */
specifier|private
name|SaffronProperties
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Retrieves the singleton instance of {@link SaffronProperties}.      */
specifier|public
specifier|static
name|SaffronProperties
name|instance
parameter_list|()
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|SaffronProperties
argument_list|()
expr_stmt|;
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
name|properties
operator|.
name|loadSaffronProperties
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
comment|/**      * Adds all saffron-related properties found in the source list. This means      * all properties whose names start with "saffron." or "net.sf.saffron." The      * added properties can replace existing properties.      *      * @param source a Properties list      */
specifier|public
name|void
name|loadSaffronProperties
parameter_list|(
name|Properties
name|source
parameter_list|)
block|{
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
block|}
comment|/**      * Retrieves a boolean property. Returns<code>true</code> if the property      * exists, and its value is<code>1</code>,<code>true</code> or<code>      * yes</code>; returns<code>false</code> otherwise.      */
specifier|public
name|boolean
name|getBooleanProperty
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|getBooleanProperty
argument_list|(
name|key
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Retrieves a boolean property, or a default value if the property does not      * exist. Returns<code>true</code> if the property exists, and its value is      *<code>1</code>,<code>true</code> or<code>yes</code>; the default value      * if it does not exist;<code>false</code> otherwise.      */
specifier|public
name|boolean
name|getBooleanProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"1"
argument_list|)
operator|||
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
operator|||
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
return|;
block|}
comment|/**      * Retrieves an integer property. Returns -1 if the property is not found,      * or if its value is not an integer.      */
specifier|public
name|int
name|getIntProperty
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|String
name|value
init|=
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|i
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
return|return
name|i
return|;
block|}
block|}
end_class

begin_comment
comment|// End SaffronProperties.java
end_comment

end_unit

