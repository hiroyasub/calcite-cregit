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
name|test
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
comment|/**  * PersistentPropertyTest tests persistent properties using temporary files.  *  * @author Stephan Zuercher  * @version $Id$  * @since December 3, 2004  */
end_comment

begin_class
specifier|public
class|class
name|PersistentPropertyTest
extends|extends
name|EigenbaseTestCase
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|PersistentPropertyTest
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|testPersistentStringProperty
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|DEFAULT_VALUE
init|=
literal|"default value"
decl_stmt|;
specifier|final
name|String
name|NEW_VALUE
init|=
literal|"new value"
decl_stmt|;
specifier|final
name|String
name|PROP_NAME
init|=
literal|"test.eigenbase.persistent.string"
decl_stmt|;
specifier|final
name|String
name|EXISTING_PROP_NAME1
init|=
literal|"test.eigenbase.existing1"
decl_stmt|;
specifier|final
name|String
name|EXISTING_PROP_VALUE1
init|=
literal|"existing value 1"
decl_stmt|;
specifier|final
name|String
name|EXISTING_PROP_NAME2
init|=
literal|"test.eigenbase.existing2"
decl_stmt|;
specifier|final
name|String
name|EXISTING_PROP_VALUE2
init|=
literal|"existing value 2"
decl_stmt|;
specifier|final
name|String
name|EXISTING_PROP_NAME3
init|=
literal|"test.eigenbase.existing3"
decl_stmt|;
specifier|final
name|String
name|EXISTING_PROP_VALUE3
init|=
literal|"existing value 3"
decl_stmt|;
specifier|final
name|String
name|EXISTING_NEW_VALUE
init|=
literal|"new value for existing prop"
decl_stmt|;
specifier|final
name|String
name|EXISTING_DEFAULT_VALUE
init|=
literal|"existing default value"
decl_stmt|;
name|File
name|tempPropFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"eigenbaseTest"
argument_list|,
literal|".properties"
argument_list|)
decl_stmt|;
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|tempPropFile
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"# Test config file"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|EXISTING_PROP_NAME1
operator|+
literal|"="
operator|+
name|EXISTING_PROP_VALUE1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|EXISTING_PROP_NAME2
operator|+
literal|"="
operator|+
name|EXISTING_PROP_VALUE2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|EXISTING_PROP_NAME3
operator|+
literal|"="
operator|+
name|EXISTING_PROP_VALUE3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|load
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tempPropFile
argument_list|)
argument_list|)
expr_stmt|;
name|StringProperty
name|propertyFileLocation
init|=
operator|new
name|StringProperty
argument_list|(
name|props
argument_list|,
literal|"test.eigenbase.properties"
argument_list|,
name|tempPropFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|PersistentStringProperty
name|persistentProperty
init|=
operator|new
name|PersistentStringProperty
argument_list|(
name|props
argument_list|,
name|PROP_NAME
argument_list|,
name|DEFAULT_VALUE
argument_list|,
name|propertyFileLocation
argument_list|)
decl_stmt|;
name|PersistentStringProperty
name|persistentExistingProperty
init|=
operator|new
name|PersistentStringProperty
argument_list|(
name|props
argument_list|,
name|EXISTING_PROP_NAME2
argument_list|,
name|EXISTING_DEFAULT_VALUE
argument_list|,
name|propertyFileLocation
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_VALUE
argument_list|,
name|persistentProperty
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
name|PROP_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_PROP_VALUE1
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|EXISTING_PROP_NAME1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_PROP_VALUE2
argument_list|,
name|persistentExistingProperty
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_PROP_VALUE2
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|EXISTING_PROP_NAME2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_PROP_VALUE3
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|EXISTING_PROP_NAME3
argument_list|)
argument_list|)
expr_stmt|;
name|persistentProperty
operator|.
name|set
argument_list|(
name|NEW_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NEW_VALUE
argument_list|,
name|persistentProperty
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NEW_VALUE
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|PROP_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|persistentExistingProperty
operator|.
name|set
argument_list|(
name|EXISTING_NEW_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_NEW_VALUE
argument_list|,
name|persistentExistingProperty
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_NEW_VALUE
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|EXISTING_PROP_NAME2
argument_list|)
argument_list|)
expr_stmt|;
comment|// reset properties, location and persistent property (reloads
comment|// properties stored in file)
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|props
operator|.
name|load
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tempPropFile
argument_list|)
argument_list|)
expr_stmt|;
name|propertyFileLocation
operator|=
operator|new
name|StringProperty
argument_list|(
name|props
argument_list|,
literal|"test.eigenbase.properties"
argument_list|,
name|tempPropFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|persistentProperty
operator|=
operator|new
name|PersistentStringProperty
argument_list|(
name|props
argument_list|,
name|PROP_NAME
argument_list|,
name|DEFAULT_VALUE
argument_list|,
name|propertyFileLocation
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NEW_VALUE
argument_list|,
name|persistentProperty
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NEW_VALUE
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|PROP_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_NEW_VALUE
argument_list|,
name|persistentExistingProperty
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_NEW_VALUE
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|EXISTING_PROP_NAME2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_PROP_VALUE1
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|EXISTING_PROP_NAME1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXISTING_PROP_VALUE3
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|EXISTING_PROP_NAME3
argument_list|)
argument_list|)
expr_stmt|;
comment|// delete file if test succeeded
name|tempPropFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PersistentPropertyTest.java
end_comment

end_unit

