/*======================================================================
MetaDataToDb transfers meta data from SIARD files to databases. 
Application : Siard2
Description : Transfers meta data from SIARD files to databases.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 29.08.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.cmd;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import ch.admin.bar.siard2.api.MetaAttribute;
import ch.admin.bar.siard2.api.MetaColumn;
import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaSchema;
import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.MetaTrigger;
import ch.admin.bar.siard2.api.MetaType;
import ch.admin.bar.siard2.api.MetaUniqueKey;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.enterag.sqlparser.SqlLiterals;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.EU;
import ch.enterag.utils.background.Progress;
import ch.enterag.utils.jdbc.BaseDatabaseMetaData;
import ch.enterag.utils.logging.IndentLogger;

/*====================================================================*/
/** Transfers meta data from databases to SIARD files and back.
 @author Hartwig Thomas
 */
public class MetaDataToDb
  extends MetaDataBase
{
  /** logger */  
  private static IndentLogger _il = IndentLogger.getIndentLogger(MetaDataToDb.class.getName());
  private ArchiveMapping _am = null;
  public ArchiveMapping getArchiveMapping() { return _am; }
  private int _iMaxTableNameLength = -1;
  private int _iMaxColumnNameLength = -1;
  private Progress _progress = null;
  private int _iTablesCreated = -1;
  private int _iTables = -1;
  private int _iTablesPercent = -1;

	public boolean _cubrid = false;
	public String	 _ardb;
	public String  _todb;
	
  /*------------------------------------------------------------------*/
  /** increment the number or tables created, issuing a notification,
   * when a percent is reached.
   */
  private void incTablesCreated()
  {
    _iTablesCreated++;
    if ((_progress != null) && (_iTables > 0) && ((_iTablesCreated % _iTablesPercent) == 0))
    {
      int iPercent = (int)((100*_iTablesCreated)/_iTables);
      _progress.notifyProgress(iPercent);
    }
  } /* incTablesCreated */
  
  /*------------------------------------------------------------------*/
  /** check if cancel was requested.
   * @return true, if cancel was requested.
   */
  private boolean cancelRequested()
  {
    boolean bCancelRequested = false;
    if (_progress != null)
      bCancelRequested = _progress.cancelRequested();
    return bCancelRequested;
  } /* cancelRequested */

  /*------------------------------------------------------------------*/
  /** create an attribute definition for a CREATE TYPE statement from
   * attribute meta data.
   * @param ma attribute meta data
   * @param tm mapping of names in type.
   * @return SQL fragment defining the attribute.
   */
  private String createAttribute(MetaAttribute ma, TypeMapping tm)
  {
    StringBuilder sbSql = new StringBuilder(
      SqlLiterals.formatId(tm.getMappedAttributeName(ma.getName())));
    sbSql.append(" ");
    MetaType mt = ma.getMetaType();
    if (mt == null)
    {
      sbSql.append(ma.getType());
      if (ma.getCardinality() >= 0)
        sbSql.append(" ARRAY["+String.valueOf(ma.getCardinality())+"]");
    }
    else
    {
      SchemaMapping sm = _am.getSchemaMapping(ma.getTypeSchema());
      QualifiedId qiType = new QualifiedId(null,
        sm.getMappedSchemaName(), sm.getMappedTypeName(ma.getTypeName()));
      sbSql.append(qiType.format());
    }
    return sbSql.toString();
  } /* createMetaAttribute */

  /*------------------------------------------------------------------*/
  /** create a CREATE TYPE statement from type meta data and execute it.
   * @param mt type meta data.
   * @param sm mapping of names in schema.
   * @throws SQLException if a database error occurred.
   */
  private void createType(MetaType mt, SchemaMapping sm)
    throws SQLException
  {
    _il.enter(mt.getName());
    TypeMapping tm = sm.getTypeMapping(mt.getName());
    QualifiedId qiType = new QualifiedId(null,
      sm.getMappedSchemaName(),tm.getMappedTypeName());
    /* type was not dropped if drop was not necessary */
    if (!existsType(qiType.getSchema(), qiType.getName()))
    {
      CategoryType cat = mt.getCategoryType();
      if ((cat != CategoryType.DISTINCT) || supportsDistincts())
      {
        StringBuilder sbSql = new StringBuilder("CREATE TYPE "+qiType.format());
        sbSql.append(" AS ");
        if (cat == CategoryType.DISTINCT)
          sbSql.append(mt.getBase());
        else
        {
          // TODO: handle super type once we have an example!
          sbSql.append("(");
          for (int iAttribute = 0; iAttribute < mt.getMetaAttributes(); iAttribute++)
          {
            if (iAttribute > 0)
              sbSql.append(",\r\n");
            MetaAttribute ma = mt.getMetaAttribute(iAttribute);
            sbSql.append(createAttribute(ma,tm));
          }
          sbSql.append(")");
          if (!mt.isFinal())
            sbSql.append(" NOT");
          sbSql.append(" FINAL\r\n");
          if (!mt.isInstantiable())
            sbSql.append(" NOT");
          sbSql.append(" INSTANTIABLE");
        }
        /* now execute it */
        Statement stmt = _dmd.getConnection().createStatement();
        stmt.setQueryTimeout(_iQueryTimeoutSeconds);
        stmt.executeUpdate(sbSql.toString());
        stmt.close();
      }
    }
    _il.exit();
  } /* createType */

  /*------------------------------------------------------------------*/
  /** create all types of a schema.
   * @param ms schema meta data.
   * @param sm mapping of names in schema.
   * @throws SQLException if a database error occurred.
   */
  private void createTypes(MetaSchema ms, SchemaMapping sm)
    throws SQLException
  {
    _il.enter(ms.getName());
    if (supportsUdts() || supportsDistincts())
    {
      for (int iType = 0; iType < ms.getMetaTypes(); iType++)
      {
        MetaType mt = ms.getMetaType(iType);
        CategoryType cat = mt.getCategoryType();
        if (((cat == CategoryType.DISTINCT) && supportsDistincts()) ||
            ((cat == CategoryType.UDT) && supportsUdts()))
          createType(mt, sm);
      }
    }
    _il.exit();
  } /* createTypes */
  
  /*------------------------------------------------------------------*/
  /** check, whether a type exists in the database.
   * @param sMangledSchema schema name.
   * @param sMangledType type name.
   * @return true, if type exists.
   * @throws SQLException if a database error occurred.
   */
  private boolean existsType(String sMangledSchema, String sMangledType)
    throws SQLException
  {
    boolean bExists = false;
    ResultSet rs = _dmd.getUDTs(null, 
      ((BaseDatabaseMetaData)_dmd).toPattern(sMangledSchema), 
      ((BaseDatabaseMetaData)_dmd).toPattern(sMangledType), 
      new int[]{ Types.STRUCT, Types.DISTINCT });
    while (rs.next())
      bExists = true;
    rs.close();
    return bExists;
  } /* existsType */
  
  /*------------------------------------------------------------------*/
  /** drop all types that will be created within a schema.
   * @param ms schema meta data
   * @param sm mapping of names in schema.
   * @throws SQLException if a database error occurred.
   */
  private void dropTypes(MetaSchema ms, SchemaMapping sm)
    throws IOException, SQLException
  {
    _il.enter(ms.getName());
    if (supportsUdts())
    {
      Set<String> setTypes = new HashSet<String>();
      for (int iType = 0; iType < ms.getMetaTypes(); iType++)
      {
        MetaType mt = ms.getMetaType(iType);
        CategoryType cat = mt.getCategoryType();
        if (supportsDistincts() || (cat != CategoryType.DISTINCT))
          setTypes.add(mt.getName());
      }
      while (!setTypes.isEmpty())
      {
        for (Iterator<String> iterType = setTypes.iterator(); iterType.hasNext(); )
        {
          String sTypeName = iterType.next();
          TypeMapping tm = sm.getTypeMapping(sTypeName);
          if (existsType(sm.getMappedSchemaName(),tm.getMappedTypeName()))
          {
            MetaType mt = ms.getMetaType(sTypeName);
            if (matchType(mt, sm))
              iterType.remove();
            else
            {
              QualifiedId qiType = new QualifiedId(null,
                sm.getMappedSchemaName(),tm.getMappedTypeName());
              String sSql = "DROP TYPE "+qiType.format()+" RESTRICT";
              _il.event(sSql);
              Statement stmt = _dmd.getConnection().createStatement();
              stmt.setQueryTimeout(_iQueryTimeoutSeconds);
              try 
              {
                stmt.executeUpdate(sSql);
                iterType.remove();
              }
              catch(SQLException se) {}
              finally { stmt.close(); }
            }
          }
          else
            iterType.remove();
        }
      }
    }
    _il.exit();
  } /* dropTypes */

  /*------------------------------------------------------------------*/
  /** create a column definition for a CREATE TABLE statement
   * @param mc column meta data
   * @param tm mapping names within table.
   * @return SQL fragment defining the column.
   * @throws IOException if an I/O error occurred.
   */
  private String createColumn(MetaColumn mc, TableMapping tm)
    throws IOException
  {
    StringBuilder sbSql = new StringBuilder();
		String sMappedColumnName = SqlLiterals.formatId(tm.getMappedColumnName(mc.getName()));
		if (_todb.equals("CUBRID"))
		{
			if (!sMappedColumnName.contains("\""))
			{
				sMappedColumnName = "\"" + sMappedColumnName + "\"";
			}
		}
		sbSql.append(sMappedColumnName);
    sbSql.append(" ");
	
    MetaType mt = mc.getMetaType();
    if (mt == null)
		{
			String typeName = mc.getType();
			if (_todb.equals("CUBRID"))
			{
				if (typeName.contains("TIME"))
				{
					if (typeName.contains("TIMESTAMP"))
					{
						/* S: CUBRID의 TIMESTAMP컬럼 타입 최대값 2038년 1월 19일, 그 이상 데이터 오류 발생관련 수정 */
						typeName = "DATETIME";
						/* E: CUBRID의 TIMESTAMP컬럼 타입 최대값 2038년 1월 19일, 그 이상 데이터 오류 발생관련 수정 */
					}
					else
					{
						typeName = "TIME";
					}
				}
				else if (typeName.contains("LOB"))
				{
					typeName = typeName.substring(0, 4);
				}
				else if (typeName.contains("FLOAT") || typeName.contains("DECIMAL") || typeName.contains("NUMERIC"))
				{
					if (mc.getLength() > 38)
					{
						typeName = typeName.substring(0, 5) + "(38";
						if (mc.getScale() > 0)
							typeName = typeName + ", " + mc.getScale();

						typeName = typeName + ")";
					}
				}
				else if (typeName.contains("CHAR"))
				{
					int idx = typeName.indexOf("(");

					if (typeName.contains("VARNCHAR"))
					{
						typeName = "VARCHAR";
					}
					else if (typeName.contains("NCHAR"))
					{
						typeName = "CHAR";
					}
					else
					{
						typeName = typeName.substring(0, idx);
					}

					if (mc.getLength() > 1073741823)
					{
						if (idx >= 0)
							typeName = typeName + "(1073741823)";
					}
					else
					{
						typeName = typeName + "(" + mc.getLength() + ")";
					}
				}
				else if (typeName.contains("BINARY"))
				{
					int idx = typeName.indexOf("(");

					if (typeName.substring(0, idx).contains("VAR"))
					{
						typeName = "BIT VARYING";
					}
					else
					{
						typeName = "BIT";
					}

					if (mc.getLength() >= 1073741823 / 8)
					{
						typeName = typeName + "(1073741823)";
					}
					else
					{
						typeName = typeName + "(" + mc.getLength() * 8 + ")";
					}
				}
			}

			sbSql.append(typeName);
      if (mc.getCardinality() >= 0)
        sbSql.append(" ARRAY["+String.valueOf(mc.getCardinality())+"]");
    }
    else
    {
      CategoryType cat = mt.getCategoryType();
      if ((cat == CategoryType.DISTINCT) && (!supportsDistincts()))
      {
        // for more complex base types we should have to parse the base type first ...
        sbSql.append(mt.getBase());
      }
      else
      {
        SchemaMapping sm = _am.getSchemaMapping(mc.getTypeSchema());
        QualifiedId qiType = new QualifiedId(null,
          sm.getMappedSchemaName(),sm.getMappedTypeName(mc.getTypeName()));
        sbSql.append(qiType.format());
      }
    }
    if (!mc.isNullable())
      sbSql.append(" NOT NULL");
    return sbSql.toString();
  } /* createColumn */

  /*------------------------------------------------------------------*/
  /** get the set of available tables in the database.
   * @return set of tables.
   * @throws SQLException if a database error occurred.
   */
  private Set<QualifiedId> getTables()
    throws SQLException
  {
    Set<QualifiedId> setTables = new HashSet<QualifiedId>();
    ResultSet rs = _dmd.getTables(null, "%", "%", new String[] {"TABLE"});
    while (rs.next())
    {
      String sCatalog = rs.getString("TABLE_CAT");
      String sSchema = rs.getString("TABLE_SCHEM");
      String sTable = rs.getString("TABLE_NAME");
      QualifiedId qiTable = new QualifiedId(sCatalog,sSchema,sTable);
      setTables.add(qiTable);
    }
    rs.close();
    return setTables;
  } /* getTables */
  
  /*------------------------------------------------------------------*/
  /** create a CREATE TABLE statement from table meta data and execute it.
   * @param mt table meta data.
   * @param sm mapping of names in schema.
   * @throws IOException if an I/O error occurred.
   * @throws SQLException if a database error occurred.
   */
  private void createTable(MetaTable mt, SchemaMapping sm)
    throws IOException, SQLException
  {
    _il.enter(mt.getName());
    Set<QualifiedId> setBefore = getTables();
    TableMapping tm = sm.getTableMapping(mt.getName());
    QualifiedId qiTable = new QualifiedId(null,sm.getMappedSchemaName(),tm.getMappedTableName());
		StringBuilder sbSql;

		if (_todb.equals("CUBRID"))
		{
			String name = mt.getName();
			if (!name.contains("\""))
			{
				name = "\"" + name + "\"";
			}
			sbSql = new StringBuilder("  CREATE TABLE " + name + "(\r\n");
		}
		else
			sbSql = new StringBuilder("  CREATE TABLE " + qiTable.format() + "(\r\n");
    List<List<String>> llColumnNames = mt.getColumnNames(supportsArrays(),supportsUdts());
    for (int iExtendedColumn = 0; iExtendedColumn < llColumnNames.size(); iExtendedColumn++)
    {
      List<String> listColumn = llColumnNames.get(iExtendedColumn);
      StringBuilder sbColumnName = new StringBuilder();
      for (int i = 0; i < listColumn.size(); i++)
      {
        if (i > 0)
          sbColumnName.append(".");
        sbColumnName.append(listColumn.get(i));
      }
      if (iExtendedColumn > 0)
        sbSql.append(",\r\n");
      MetaColumn mc = mt.getMetaColumn(sbColumnName.toString());
      if (mc != null)
				sbSql.append("      " + createColumn(mc, tm));
      else
      {
        String sMappedColumnName = tm.getMappedExtendedColumnName(sbColumnName.toString());
        sbSql.append(SqlLiterals.formatId(sMappedColumnName));
        sbSql.append(" ");
        sbSql.append(mt.getType(llColumnNames.get(iExtendedColumn)));
      }
    }
    /* add primary key */
    MetaUniqueKey mpk = mt.getMetaPrimaryKey();
    if (mpk != null)
    {
      StringBuilder sbPrimaryKey = new StringBuilder();
			/* S: 2019.10.23 */
			String dataBaseType = _todb.toUpperCase();
			if (dataBaseType.equals("ORACLE") || dataBaseType.equals("MYSQL") || dataBaseType.equals("SQL SERVER")
			        || dataBaseType.equals("SQLSERVER") || dataBaseType.equals("CUBRID"))
			{
				sbPrimaryKey.append("      CONSTRAINT \"" + mpk.getName() + "\" PRIMARY KEY (");
			}
			else
			{
				sbPrimaryKey.append("      PRIMARY KEY(");
			}
			/* E: 2019.10.23 */
			
			for (int iColumn = 0; iColumn < mpk.getColumns(); iColumn++)
      {
        if (iColumn > 0)
          sbPrimaryKey.append(",");
        String sMappedColumnName = SqlLiterals.formatId(tm.getMappedColumnName(mpk.getColumn(iColumn)));
				if (_todb.equals("CUBRID"))
				{
					if (!sMappedColumnName.contains("\""))
					{
						sMappedColumnName = "\"" + sMappedColumnName + "\"";
					}
				}
        sbPrimaryKey.append(sMappedColumnName);
      }
      sbPrimaryKey.append(")");
      sbSql.append(",\r\n");
      sbSql.append(sbPrimaryKey.toString());
    }
    /* unique and foreign keys are added in the end of upload */
		sbSql.append("\r\n    )\r\n");
    /* now execute it */
    _il.event(sbSql.toString());
    Statement stmt = _dmd.getConnection().createStatement();
    stmt.setQueryTimeout(_iQueryTimeoutSeconds);
    
		/* S: 테이블 생성 스크립트 출력 */
	  System.out.println("\r\n" + sbSql.toString());
    /* E: 테이블 생성 스크립트 출력 */
	  
    stmt.executeUpdate(sbSql.toString());
    stmt.close();
    /* record names of created table and columns */
    Set<QualifiedId> setCreated = getTables();
    setCreated.removeAll(setBefore);
    if (!setCreated.contains(qiTable)) 
    {
      for (Iterator<QualifiedId> iterCreated = setCreated.iterator(); iterCreated.hasNext(); )
        qiTable = iterCreated.next();
      sm.setMappedSchemaName(qiTable.getSchema());
      tm.setMappedTableName(qiTable.getName());
    }
    ResultSet rsColumns = _dmd.getColumns(null, 
      ((BaseDatabaseMetaData)_dmd).toPattern(qiTable.getSchema()), 
      ((BaseDatabaseMetaData)_dmd).toPattern(qiTable.getName()),
      "%");
    while (rsColumns.next())
    {
      String sMappedColumnName = rsColumns.getString("COLUMN_NAME");
      int iOrdinalPosition = rsColumns.getInt("ORDINAL_POSITION");
      List<String> listColumn = llColumnNames.get(iOrdinalPosition-1);
      StringBuilder sbColumnName = new StringBuilder();
      for (int i = 0; i < listColumn.size(); i++)
      {
        if (i > 0)
          sbColumnName.append(".");
        sbColumnName.append(listColumn.get(i));
      }
      String sExtendedColumnName = sbColumnName.toString();
      if (!sMappedColumnName.equals(tm.getMappedColumnName(sExtendedColumnName)))
        tm.putMappedExtendedColumnName(sExtendedColumnName,sMappedColumnName);
    }
    rsColumns.close();
    _il.exit();
  } /* createTable */
	/*------------------------------------------------------------------*/
	/**
	 * create a CREATE TABLE statement from table meta data and execute it.
	 * 
	 * @param mt table meta data.
	 * @param sm mapping of names in schema.
	 * @throws IOException  if an I/O error occurred.
	 * @throws SQLException if a database error occurred.
	 */
	private void createCubridTable(MetaTable mt, SchemaMapping sm) throws IOException, SQLException
	{
		_il.enter(mt.getName());
		TableMapping tm = sm.getTableMapping(mt.getName());
		StringBuilder sbSql = new StringBuilder("");

		List<List<String>> llColumnNames = mt.getColumnNames(supportsArrays(), supportsUdts());
		for (int iExtendedColumn = 0; iExtendedColumn < llColumnNames.size(); iExtendedColumn++)
		{
			List<String> listColumn = llColumnNames.get(iExtendedColumn);
			StringBuilder sbColumnName = new StringBuilder();
			for (int i = 0; i < listColumn.size(); i++)
			{
				if (i > 0)
					sbColumnName.append(".");
				sbColumnName.append(listColumn.get(i));
			}
		}

		String crt = mt.getDescription();

		if (mt.getDescription().indexOf("CONSTRAINT") >= 0)
		{
			String before_constraint = crt.substring(0, crt.indexOf("CONSTRAINT"));
			before_constraint = before_constraint.substring(0, before_constraint.lastIndexOf(","));

			sbSql.append(before_constraint);
			/* add primary key */
			MetaUniqueKey mpk = mt.getMetaPrimaryKey();
			if (mpk != null)
			{
				StringBuilder sbPrimaryKey = new StringBuilder();
				sbPrimaryKey.append("PRIMARY KEY(");
				for (int iColumn = 0; iColumn < mpk.getColumns(); iColumn++)
				{
					if (iColumn > 0)
						sbPrimaryKey.append(",");
					String sMappedColumnName = tm.getMappedColumnName(mpk.getColumn(iColumn));
					sbPrimaryKey.append(SqlLiterals.formatId(sMappedColumnName));
				}
				sbPrimaryKey.append(")");
				sbSql.append(",\r\n");
				sbSql.append(sbPrimaryKey.toString());
			}
			String after_contraint = crt.substring(crt.lastIndexOf(")"));

			sbSql.append(after_contraint);
		}
		else
			sbSql.append(crt);

		/* unique and foreign keys are added in the end of upload */

		/* now execute it */
		_il.event(sbSql.toString());
		Statement stmt = _dmd.getConnection().createStatement();
		stmt.setQueryTimeout(_iQueryTimeoutSeconds);
		stmt.executeUpdate(sbSql.toString());
		stmt.close();

		_il.exit();
	} /* createTable */
  /*------------------------------------------------------------------*/
  /** create all tables of a schema.
   * @param ms schema meta data.
   * @param sm mapping of names in schema.
   * @throws IOException if an I/O error occurred.
   * @throws SQLException if a database error occurred.
   */
  private void createTables(MetaSchema ms, SchemaMapping sm)
    throws IOException, SQLException
  {
    _il.enter(ms.getName());
    for (int iTable = 0; (iTable < ms.getMetaTables()) && (!cancelRequested()); iTable++)
    {
      MetaTable mt = ms.getMetaTable(iTable);
			if (_ardb.equals("CUBRID"))
			{
				if (mt.getName().equalsIgnoreCase("db_serial") || mt.getName().equalsIgnoreCase("_db_stored_procedure"))
					continue;
			}
      QualifiedId qiTable = new QualifiedId(null,mt.getParentMetaSchema().getName(),mt.getName());
      System.out.println("  Table: "+qiTable.format());
			if (_cubrid)
			{
				createCubridTable(mt, sm);
			}
			else
			{
				createTable(mt, sm);
			}
			incTablesCreated();
    }
		if (_cubrid)
		{
			for (int iTable = 0; (iTable < ms.getMetaTables()) && (!cancelRequested()); iTable++)
			{
				MetaTable mt = ms.getMetaTable(iTable);
				createTriggers(mt);
			}
		}
    _il.exit();
  } /* createTables */

  /*------------------------------------------------------------------*/
	/**
	 * create all triggers of a schema.
	 * 
	 * @param ms schema meta data.
	 * @param sm mapping of names in schema.
	 * @throws IOException  if an I/O error occurred.
	 * @throws SQLException if a database error occurred.
	 */
	private void createTriggers(MetaTable mt) throws IOException, SQLException
	{
		_il.enter(mt.getName());

		for (int iTrigger = 0; iTrigger < mt.getMetaTriggers(); iTrigger++)
		{
			MetaTrigger mtr = mt.getMetaTrigger(iTrigger);

			StringBuilder sbSql = new StringBuilder("");
			sbSql.append(mtr.getDescription());

			/* now execute it */
			_il.event(sbSql.toString());
			Statement stmt = _dmd.getConnection().createStatement();
			stmt.setQueryTimeout(_iQueryTimeoutSeconds);
			stmt.executeUpdate(sbSql.toString());
			stmt.close();
		}
		_il.exit();
	} /* createTriggers */
	
  /*------------------------------------------------------------------*/
  /** check, whether table exists in the database.
   * @param sMangledSchema schema name.
   * @param sMangledTable table name.
   * @return true, if table exists.
   * @throws SQLException if a database error occurred.
   */
  private boolean existsTable(String sMangledSchema, String sMangledTable)
    throws SQLException
  {
    boolean bExists = false;
    ResultSet rs = _dmd.getTables(null, 
      ((BaseDatabaseMetaData)_dmd).toPattern(sMangledSchema), 
      ((BaseDatabaseMetaData)_dmd).toPattern(sMangledTable), 
      new String[]{"TABLE"});
    if (rs.next())
      bExists = true;
    rs.close();
    return bExists;
  } /* existsTable */
  
  /*------------------------------------------------------------------*/
  /** drop all tables that will be created within a schema.
   * @param ms schema meta data
   * @param sm mapping of names in schema.
   * @throws SQLException if a database error occurred.
   */
  private void dropTables(MetaSchema ms, SchemaMapping sm)
    throws SQLException
  {
    _il.enter(ms, sm);
    for (int iTable = 0; iTable < ms.getMetaTables(); iTable++)
    {
      String sTableName = ms.getMetaTable(iTable).getName();
      TableMapping tm = sm.getTableMapping(sTableName);
      Statement stmt = _dmd.getConnection().createStatement();
      stmt.setQueryTimeout(_iQueryTimeoutSeconds);
      try
      {
        if (existsTable(sm.getMappedSchemaName(),tm.getMappedTableName()))
        {
          QualifiedId qiTable = new QualifiedId(null,
          sm.getMappedSchemaName(),tm.getMappedTableName());
          
          //20201021 - Addon
          if(_todb.equals("CUBRID")) {
          	qiTable = new QualifiedId(null,
                null,tm.getMappedTableName());
          }
          
          /* CASCADE must always drop! */
          String sSql = "DROP TABLE "+qiTable.format();
          
          //20201021 - Addon
          if(!_todb.equals("CUBRID")) {
          	sSql += " CASCADE";
          }
          System.out.println("  sSql: "+sSql);
          
          _il.event(sSql);
          stmt.executeUpdate(sSql);
          System.out.println("  Dropped: "+qiTable.format());
        }
      }
      catch(SQLException se) { System.out.println("  Could not drop "+tm.getMappedTableName()+" "+EU.getExceptionMessage(se)); } // could not drop this time but maybe next
      finally{ stmt.close(); }
    }
    _il.exit();
  } /* dropTables */

  /*------------------------------------------------------------------*/
  /** check, whether schema exists in the database.
   * @param sMangledSchema schema name.
   * @return true, if schema exists.
   * @throws SQLException if a database error occurred.
   */
  private boolean existsSchema(String sMangledSchema)
    throws SQLException
  {
    boolean bExists = false;
    ResultSet rs = _dmd.getSchemas(null, ((BaseDatabaseMetaData)_dmd).toPattern(sMangledSchema));
    if (rs.next())
      bExists = true;
    rs.close();
    return bExists;
  } /* existsSchema */

  /*------------------------------------------------------------------*/
  /** create the schema if it does not exist.
   * @param ms schema meta data.
   * @param sm mapping of names in schema.
   * @throws SQLException if a database error occurred.
   */
  private void createSchema(MetaSchema ms, SchemaMapping sm)
    throws SQLException
  {
    if (!existsSchema(sm.getMappedSchemaName()))
    {
      String sSql = "CREATE SCHEMA \""+sm.getMappedSchemaName()+"\"";
      _il.event(sSql);
      Statement stmt = _dmd.getConnection().createStatement();
      stmt.setQueryTimeout(_iQueryTimeoutSeconds);
      try 
      { 
        stmt.executeUpdate(sSql);
        stmt.getConnection().commit();
      }
      catch(SQLException se) 
      { 
        _il.exception(se);
        stmt.getConnection().rollback();
        /* rethrow it (only caught for finally clause) */
        throw new SQLException(se.getMessage(),se.getCause());
      }
      finally { stmt.close(); }
    }
  } /* createSchema */
  
  /*------------------------------------------------------------------*/
  /** upload creates all types and tables in the database.
   * @throws IOException if an I/O error occurred.
   * @throws SQLException if a database error occurred.
   */
  public void upload(Progress progress)
    throws IOException, SQLException
  {
    _il.enter();
    System.out.println("Meta Data");
    _progress = progress;
    /* compute number of tables to create */
    _iTables = 0;
    for (int iSchema = 0; iSchema < _md.getMetaSchemas(); iSchema++)
    {
      MetaSchema ms = _md.getMetaSchema(iSchema);
      for (int iTable = 0; iTable < ms.getMetaTables(); iTable++)
        _iTables++;
    }
    _iTablesPercent = (_iTables+99)/100;
    _iTablesCreated = 0;
    for (int iSchema = 0; (iSchema < _md.getMetaSchemas()) && (!cancelRequested()); iSchema++)
    {
      MetaSchema ms = _md.getMetaSchema(iSchema);
      SchemaMapping sm = _am.getSchemaMapping(ms.getName());
      createSchema(ms,sm);
    
	    if (existsSchema(sm.getMappedSchemaName()))
	    {
        dropTables(ms,sm);
        dropTypes(ms,sm);
        createTypes(ms,sm);
        createTables(ms,sm);
      
		    if (_cubrid)
		    {
			    createProcedures(ms, sm);
		    }
	    }
      else
        throw new SQLException("Schema \""+sm.getMappedSchemaName()+"\" could not be created! "+
          "Map \""+ms.getName()+"\" to existing schema.");
	  }	
	
    if (cancelRequested())
      throw new IOException("Upload of meta data cancelled!");
    _dmd.getConnection().commit();
    _il.exit();
  } /* upload */
  
	/*------------------------------------------------------------------*/
	/**
	 * create all procedures of a schema.
	 * 
	 * @param ms schema meta data.
	 * @param sm mapping of names in schema.
	 * @throws IOException  if an I/O error occurred.
	 * @throws SQLException if a database error occurred.
	 */
	private void createProcedures(MetaSchema ms, SchemaMapping sm) throws IOException, SQLException
	{
		// _il.enter(ms.getName());
		String path = java.lang.System.getProperty("java.class.path");

		for (int iRoutine = 0; iRoutine < ms.getMetaRoutines(); iRoutine++)
		{
			String head = "CREATE " + ms.getMetaRoutine(iRoutine).getCharacteristic() + " "
			        + ms.getMetaRoutine(iRoutine).getName() + " (";
			String tail = ms.getMetaRoutine(iRoutine).getDescription();
			String proc_body = head;
			boolean first = true;
			for (int iParam = 0; iParam < ms.getMetaRoutine(iRoutine).getMetaParameters(); iParam++)
			{
				if (!first)
					proc_body = proc_body + ",";
				proc_body = proc_body + " " + ms.getMetaRoutine(iRoutine).getMetaParameter(iParam).getName();
				proc_body = proc_body + " " + ms.getMetaRoutine(iRoutine).getMetaParameter(iParam).getMode();
				proc_body = proc_body + " " + ms.getMetaRoutine(iRoutine).getMetaParameter(iParam).getType();

				first = false;
			}

			proc_body = proc_body + ")" + " " + tail;

			StringBuilder sbSql = new StringBuilder("");
			sbSql.append(proc_body);

			/* now execute it */
			_il.event(sbSql.toString());
			Statement stmt = _dmd.getConnection().createStatement();
			stmt.setQueryTimeout(_iQueryTimeoutSeconds);
			stmt.executeUpdate(sbSql.toString());
			stmt.close();

			System.out.println(proc_body);
		}
		_il.enter(path);
		ms.getMetaView(1);
		_il.exit();
	} /* createProcedures */

  /*------------------------------------------------------------------*/
  /** returns the number of tables that exist in the database and will 
   * be dropped, when upload is executed.
   * @return number of tables that will be dropped, when upload is executed.
   */
  public int tablesDroppedByUpload()
    throws SQLException
  {
    _il.enter();
    int iTablesDropped = 0;
    for (int iSchema = 0; iSchema < _md.getMetaSchemas(); iSchema++)
    {
      MetaSchema ms = _md.getMetaSchema(iSchema);
      SchemaMapping sm = _am.getSchemaMapping(ms.getName());
      for (int iTable = 0; iTable < ms.getMetaTables(); iTable++)
      {
        MetaTable mt = ms.getMetaTable(iTable);
				if (_ardb.equals("CUBRID"))
				{
					if (sm.getMappedTableName(mt.getName()).equalsIgnoreCase("db_serial"))
						continue;
					if (sm.getMappedTableName(mt.getName()).equalsIgnoreCase("_db_stored_procedure"))
						continue;
				}
        if (existsTable(sm.getMappedSchemaName(), sm.getMappedTableName(mt.getName())))
          iTablesDropped++;
      }
    }
    _il.exit(String.valueOf(iTablesDropped));
    return iTablesDropped;
  } /* tablesDroppedByUpload */

  /*------------------------------------------------------------------*/
  /** matchAttributes returns true, if the attributes of the given
   * type meta data match the attributes in the database.
   * @param mt type meta data.
   * @param sm mapping of names in schema.
   * @return true, if the attributes of the type in the database
   *         match the attributes in the meta data.     
   * @throws IOException if an I/O error occurred
   * @throws SQLException if a database error occurred.
   */
  private boolean matchAttributes(MetaType mt, SchemaMapping sm)
    throws IOException, SQLException
  {
    boolean bMatches = true;
    TypeMapping tm = sm.getTypeMapping(mt.getName());
    int iPosition = 0;
    ResultSet rs = _dmd.getAttributes(null, 
      ((BaseDatabaseMetaData)_dmd).toPattern(sm.getMappedSchemaName()), 
      ((BaseDatabaseMetaData)_dmd).toPattern(tm.getMappedTypeName()), "%");
    while (bMatches && rs.next())
    {
      iPosition++;
      String sTypeSchema = rs.getString("TYPE_SCHEM");
      if (!sTypeSchema.equals(_am.getMappedSchemaName(mt.getParentMetaSchema().getName())))
        throw new IOException("Attribute with unexpected type schema found!");
      String sTypeName = rs.getString("TYPE_NAME");
      if (!sTypeName.equals(sm.getMappedTypeName(mt.getName())))
        throw new IOException("Attribute with unexpected type name found");
      String sAttributeName = rs.getString("ATTR_NAME");
      // int iAttrSize = rs.getInt("ATTR_SIZE");
      // int iDecimalDigits = rs.getInt("DECIMAL_DIGITS");
      /* find attribute with this mangled name */
      MetaAttribute ma = null;
      for (int iAttribute = 0; (ma == null) && (iAttribute < mt.getMetaAttributes()); iAttribute++)
      {
        MetaAttribute maTemp = mt.getMetaAttribute(iAttribute);
        if (sAttributeName.equals(tm.getMappedAttributeName(maTemp.getName())))
          ma = maTemp; 
      }
      if (ma != null)
      {
        int iDataType = rs.getInt("DATA_TYPE");
        String sAttrTypeName = rs.getString("ATTR_TYPE_NAME");
        if ((iDataType != Types.DISTINCT) &&
            (iDataType != Types.ARRAY) &&
            (iDataType != Types.STRUCT))
        {
          if (iDataType != ma.getPreType())
            bMatches = false;
        }
        else if (iDataType == Types.ARRAY)
        {
          /* parse array constructor "<base> ARRAY[<n>]" */
          Matcher m = MetaDataFromDb._patARRAY_CONSTRUCTOR.matcher(sTypeName);
          if (m.matches())
          {
            if (!ma.getType().equals(m.group(1)))
              bMatches = false;
            if (ma.getCardinality() != Integer.parseInt(m.group(2)))
              bMatches = false;
          }
          else
            throw new SQLException("Invalid ARRAY constructor for attribute "+ma.getName()+" of type "+mt.getName()+"!");
        }
        else
        {
          try
          {
            QualifiedId qiAttrType = new QualifiedId(sAttrTypeName);
            if (qiAttrType.getSchema() == null)
              qiAttrType.setSchema(sm.getMappedSchemaName());
            /* find schema with this mangled name */
            MetaSchema msAttr = null;
            SchemaMapping smAttr = null;
            for (int iSchema = 0; (msAttr == null) && (iSchema < _md.getMetaSchemas()); iSchema++)
            {
              MetaSchema msTemp = _md.getMetaSchema(iSchema);
              SchemaMapping smTemp = _am.getSchemaMapping(msTemp.getName());
              if (smTemp.getMappedSchemaName().equals(qiAttrType.getSchema()))
              {
                msAttr = msTemp;
                smAttr = smTemp;
              }
            }
            if (msAttr != null)
            {
              /* find type with this mangled name */
              MetaType mtAttr = null;
              for (int iType = 0; (mtAttr == null) && (iType < msAttr.getMetaTypes()); iType++)
              {
                MetaType mtTemp = msAttr.getMetaType(iType);
                if (smAttr.getMappedTypeName(mtTemp.getName()).equals(qiAttrType.getName()))
                  mtAttr = mtTemp;
              }
              if (mtAttr != null)
                bMatches = matchType(mtAttr,smAttr);
              else
                bMatches = false;
            }
            else
              bMatches = false;
          }
          catch(ParseException pe) { throw new SQLException("Type of attribute "+ma.getName()+" of type "+mt.getName()+" could not be parsed!",pe); }
        }
      }
      else
        bMatches = false;
    }
    rs.close();
    if (iPosition < mt.getMetaAttributes())
      bMatches = false;
    return bMatches;
  } /* matchAttributes */

  /*------------------------------------------------------------------*/
  /** matchType returns true, if a type of the given name exists in the 
   * database and has the same relevant properties.
   * @param mt type meta data.
   * @param sm mapping of names in schema.
   * @return true, if the same type exists in the database.
   * @throws IOException if an I/O error occurred
   * @throws SQLException if a database error occurred.
   */
  private boolean matchType(MetaType mt, SchemaMapping sm)
    throws IOException, SQLException
  {
    boolean bMatches = true;
    TypeMapping tm = sm.getTypeMapping(mt.getName());
    CategoryType cat = mt.getCategoryType();
    int iDataType = Types.STRUCT;
    if (cat == CategoryType.DISTINCT)
      iDataType = Types.DISTINCT;
    ResultSet rs = _dmd.getUDTs(null, 
      ((BaseDatabaseMetaData)_dmd).toPattern(sm.getMappedSchemaName()), 
      ((BaseDatabaseMetaData)_dmd).toPattern(tm.getMappedTypeName()), null);
    while (rs.next())
    {
      /* the type only needs to be dropped if its base type 
       * or its attributes are different */
      if (iDataType == rs.getInt("DATA_TYPE"))
      {
        if (iDataType == Types.DISTINCT)
        {
          int iBaseType = rs.getInt("BASE_TYPE");
          if (iBaseType != mt.getBasePreType())
            bMatches = false;
        }
        else
        {
          if (!matchAttributes(mt,sm))
            bMatches = false;
        }
      }
      else
        bMatches = false;
    }
    rs.close();
    return bMatches;
  } /* matchType */
  
  /*------------------------------------------------------------------*/
  /** returns the number of types that exist in the database and will be 
   * dropped, when upload is executed.
   * @return number of tables that will be dropped, when upload is executed.
   * @throws IOException if an I/O error occurred
   * @throws SQLException if a database error occurred.
   */
  public int typesDroppedByUpload()
    throws IOException, SQLException
  {
    _il.enter();
    int iTypesDropped = 0;
    if (supportsUdts())
    {
      for (int iSchema = 0; iSchema < _md.getMetaSchemas(); iSchema++)
      {
        MetaSchema ms = _md.getMetaSchema(iSchema);
        SchemaMapping sm = _am.getSchemaMapping(ms.getName());
        for (int iType = 0; iType < ms.getMetaTypes(); iType++)
        {
          MetaType mt = ms.getMetaType(iType);
          if (!matchType(mt,sm))
            iTypesDropped++;
        }
      }
    }
    _il.exit(String.valueOf(iTypesDropped));
    return iTypesDropped;
  } /* typesDroppedByUpload */

  /*------------------------------------------------------------------*/
  /** constructor
   * @param dmd database meta data.
   * @param md SIARD meta data.
   * @param mapSchemas schema mapping to be used for upload, or empty or null.
   * @throws IOException if an I/O error occurred
   * @throws SQLException if a database error occurred.
   */
  private MetaDataToDb(DatabaseMetaData dmd, MetaData md, Map<String,String> mapSchemas)
    throws IOException, SQLException
  {
    super(dmd,md);
    dmd.getConnection().setAutoCommit(false);
    _iMaxTableNameLength = _dmd.getMaxTableNameLength();
    _iMaxColumnNameLength = _dmd.getMaxColumnNameLength();
    _am = ArchiveMapping.newInstance(supportsArrays(), supportsUdts(), 
      mapSchemas, _md, _iMaxTableNameLength, _iMaxColumnNameLength);
  } /* constructor */
  
  /*------------------------------------------------------------------*/
  /** factory
   * @param dmd database meta data.
   * @param md SIARD meta data.
   * @param mapSchemas schema mapping to be used for upload, or null.
   * @return new instance.
   * @throws IOException if an I/O error occurred
   * @throws SQLException if a database error occurred.
   */
  public static MetaDataToDb newInstance(DatabaseMetaData dmd, MetaData md, Map<String,String> mapSchemas)
    throws IOException, SQLException
  {
    return new MetaDataToDb(dmd,md,mapSchemas);
  } /* factory */
  
} /* class MetaDataTransfer */
