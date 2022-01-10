package ch.admin.bar.siard2.altibase;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import static org.junit.Assert.*;

import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;

public class TestAltibaseDatabase
{
  public static final String _sTEST_SCHEMA = "sys";
  private static final String _sTEST_TABLE_SIMPLE = "TALTIBASESIMPLE";

  public static QualifiedId getQualifiedSimpleTable()
  {
    return new QualifiedId(null, null, _sTEST_TABLE_SIMPLE);
  }

  private static final String _sTEST_TABLE_COMPLEX = "TALTIBASECOMPLEX";

  public static QualifiedId getQualifiedComplexTable()
  {
    return new QualifiedId(null, null, _sTEST_TABLE_COMPLEX);
  }

  private static class ColumnDefinition extends TestColumnDefinition
  {
    @Override
    public String getValueLiteral()
    {
      String sValueLiteral = "NULL";
      if (_oValue != null)
      {
        if (_sType.equals("GEOMETRY") ||
                _sType.equals("POINT") ||
                _sType.equals("LINESTRING") ||
                _sType.equals("POLYGON") ||
                _sType.equals("MULTIPOINT") ||
                _sType.equals("MULTILINESTRING") ||
                _sType.equals("MULTIPOLYGON") ||
                _sType.equals("GEOMETRYCOLLECTION"))
          sValueLiteral = "GeomFromText(" + super.getValueLiteral() + ")";
        else if (_sType.equals("BYTE(255)") ||
                _sType.equals(("VARBYTE(255)")))
        {
          sValueLiteral = AltibaseLiterals.formatBytesLiteral((byte[]) _oValue);
        }
        else if (_sType.equals("BIT"))
        {
          sValueLiteral = AltibaseLiterals.formatBitLiteral((String)_oValue);
        }
        else
          sValueLiteral = super.getValueLiteral();
      }
      return sValueLiteral;
    }

    public ColumnDefinition(String sName, String sType, Object oValue)
    {
      super(sName, sType, oValue);
    }
  } /* ColumnDefinition */

  public static int _iPrimarySimple = -1;
  public static int _iCandidateSimple = -1;

  @SuppressWarnings("deprecation")
  private static List<TestColumnDefinition> getCdSimple()
  {
    List<TestColumnDefinition> listCdSimple = new ArrayList<TestColumnDefinition>();

    // Numeric Data Types: Integer Types (Exact Values)
    _iPrimarySimple = listCdSimple.size(); // next column will be primary key column
    listCdSimple.add(new ColumnDefinition("CINT", "INT", Integer.valueOf(1000000)));
    listCdSimple.add(new ColumnDefinition("CINTEGER", "INTEGER", Integer.valueOf(-2147483647)));
    listCdSimple.add(new ColumnDefinition("CSMALLINT", "SMALLINT", Short.valueOf((short) -32767)));
    _iCandidateSimple = listCdSimple.size(); // next column will be primary key column
    listCdSimple.add(new ColumnDefinition("CBIGINT", "BIGINT", Long.valueOf(-2147483648L)));

    // Numeric Data Types: Fixed-Point Types (Exact Values)
    listCdSimple.add(new ColumnDefinition("CNUMERIC_5_2", "NUMERIC(5,2)", BigDecimal.valueOf(12345, 2)));
    listCdSimple.add(new ColumnDefinition("CDECIMAL_15_5", "DECIMAL(15,5)", new BigDecimal("123455679.12345")));

    // Numeric Data Types: Floating-Point Types (Approximate Values)
    listCdSimple.add(new ColumnDefinition("CFLOAT_9", "FLOAT(9)", new Float(Double.valueOf(Math.PI).floatValue())));
    listCdSimple.add(new ColumnDefinition("CDOUBLE", "DOUBLE", Double.valueOf(Math.E)));
    listCdSimple.add(new ColumnDefinition("CREAL", "REAL", new Float(Double.valueOf(Math.PI).floatValue())));

    listCdSimple.add(new ColumnDefinition("CBIT", "BIT", "1"));
    listCdSimple.add(new ColumnDefinition("CBOOL", "CHAR(1)", "0"));

    // Date and Time Types
    listCdSimple.add(new ColumnDefinition("CDATE", "DATE", new Date(2016 - 1900, 10, 30)));

    // CLOB
    listCdSimple.add(new ColumnDefinition("CCLOB", "CLOB", TestUtils.getString(5000)));

    // BLOB
    listCdSimple.add(new ColumnDefinition("CBLOB", "BLOB", TestUtils.getBytes(500)));

    // CHAR/VARCHAR
    listCdSimple.add(new ColumnDefinition("CCHAR_4", "CHAR(4)", TestUtils.getString(3)));
    listCdSimple.add(new ColumnDefinition("CVARCHAR_500", "VARCHAR(500)", TestUtils.getString(255)));

    // NCHAR/NVARCHAR
    listCdSimple.add(new ColumnDefinition("CNCHAR_50", "NCHAR(50)", TestUtils.getString(45)));
    listCdSimple.add(new ColumnDefinition("CNVARCHAR_50", "NVARCHAR(50)", TestUtils.getNString(50)));

    // BINARY/VARBINARY
    listCdSimple.add(new ColumnDefinition("CBYTE", "BYTE(255)", TestUtils.getBytes(100)));
    listCdSimple.add(new ColumnDefinition("CVARBYTE_255", "VARBYTE(255)", TestUtils.getBytes(100)));
    return listCdSimple;
  }

  public static List<TestColumnDefinition> _listCdSimple = getCdSimple();

  public static int _iPrimaryComplex = -1;

  private static List<TestColumnDefinition> getCdComplex()
  {
    List<TestColumnDefinition> listCdComplex = new ArrayList<TestColumnDefinition>();

    _iPrimaryComplex = listCdComplex.size(); // next column will be primary key column
    listCdComplex.add(new ColumnDefinition("CID", "INTEGER", Integer.valueOf(1)));
    /* spatial */
    listCdComplex.add(new ColumnDefinition("CGEOMETRY", "GEOMETRY", "POINT (1 2)"));
    listCdComplex.add(new ColumnDefinition("CPOINT", "POINT", "POINT (1 2)"));
    listCdComplex.add(new ColumnDefinition("CLINESTRING", "LINESTRING", "LINESTRING (0 0, 1 1, 2 2)"));
    listCdComplex.add(new ColumnDefinition("CPOLYGON", "POLYGON", "POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0), (5 5, 7 5, 7 7, 5 7, 5 5))"));
    listCdComplex.add(new ColumnDefinition("CMULTIPOINT", "MULTIPOINT", "MULTIPOINT (1 1, 2 2, 3 3)"));
    listCdComplex.add(new ColumnDefinition("CMULTILINESTRING", "MULTILINESTRING", "MULTILINESTRING ((10 10, 20 20), (15 15, 30 15))"));
    listCdComplex.add(new ColumnDefinition("CMULTIPOLYGON", "MULTIPOLYGON", "MULTIPOLYGON (((0 0, 10 0, 10 10, 0 10, 0 0)), ((5 5, 7 5, 7 7, 5 7, 5 5)))"));
    listCdComplex.add(new ColumnDefinition("CGEOMETRYCOLLECTION", "GEOMETRYCOLLECTION", "GEOMETRYCOLLECTION (POINT (1 1), LINESTRING (0 0, 1 1, 2 2, 3 3, 4 4))"));
    /* complex types */
    return listCdComplex;
  }

  public static List<TestColumnDefinition> _listCdComplex = getCdComplex();

  private Connection _conn;

  public static void grantSchemaUser(Connection conn, String sUser) throws SQLException
  {
    StringBuilder sb = new StringBuilder("GRANT ALL PRIVILEGES TO ");
    sb.append(sUser);
    Statement stmt = conn.createStatement();
    stmt.unwrap(Statement.class).executeUpdate(sb.toString());
    stmt.close();
    conn.commit();
  } /* grantSchemaUser */

  public static void dropUser(Connection conn, String user) throws SQLException
  {
    StringBuilder sb = new StringBuilder("DROP USER ");
    sb.append(user);
    sb.append(" CASCADE");
    try
    {
      Statement stmt = conn.createStatement();
      stmt.unwrap(Statement.class).executeUpdate(sb.toString());
      stmt.close();
      conn.commit();
    }
    catch (SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* dropUser */

  public static void createUser(Connection conn, String sUser, String password) throws SQLException
  {
    StringBuilder sb = new StringBuilder("CREATE USER ");
    sb.append(sUser);
    sb.append(" IDENTIFIED BY ");
    sb.append(password);
    Statement stmt = conn.createStatement();
    stmt.unwrap(Statement.class).executeUpdate(sb.toString());
    stmt.close();
    conn.commit();
  } /* createUser */

  public TestAltibaseDatabase(AltibaseConnection connAltibase)
          throws SQLException
  {
    _conn = connAltibase.unwrap(Connection.class);
    _conn.setAutoCommit(false);
    drop();
    create();
  } /* constructor */

  private void drop()
  {
    deleteTables();
    dropTables();
  } /* drop */

  private void executeDrop(String sSql)
  {
    try
    {
      Statement stmt = _conn.createStatement();
      stmt.executeUpdate(sSql);
      stmt.close();
      _conn.commit();
    } catch (SQLException se)
    {
      System.out.println(EU.getExceptionMessage(se));
    }
  } /* executeDrop */

  private void deleteTables()
  {
    deleteTable(getQualifiedSimpleTable());
    deleteTable(getQualifiedComplexTable());
  } /* deleteTables */

  private void deleteTable(QualifiedId qiTable)
  {
    executeDrop("DELETE FROM " + qiTable.format());
  } /* deleteTable */

  private void dropTables()
  {
    dropTable(getQualifiedSimpleTable());
    dropTable(getQualifiedComplexTable());
  } /* dropTables */

  private void dropTable(QualifiedId qiTable)
  {
    executeDrop("DROP TABLE " + qiTable.format());
  } /* dropTable */

  private void create()
          throws SQLException
  {
    createTables();
    insertTables();
  } /* create */

  private void executeCreate(String sSql)
          throws SQLException
  {
    Statement stmt = _conn.createStatement();
    stmt.executeUpdate(sSql);
    stmt.close();
    _conn.commit();
  } /* executeCreate */

  private void createTables()
          throws SQLException
  {
    createTable(getQualifiedSimpleTable(), _listCdSimple,
            Arrays.asList(new String[]{_listCdSimple.get(_iPrimarySimple).getName()}),
            Arrays.asList(new String[]{_listCdSimple.get(_iCandidateSimple).getName()}));
    createTable(getQualifiedComplexTable(), _listCdComplex,
            Arrays.asList(new String[]{_listCdComplex.get(_iPrimaryComplex).getName()}),
            null);
  } /* createTables */

  private void createTable(QualifiedId qiTable, List<TestColumnDefinition> listCd,
                           List<String> listPrimary, List<String> listUnique)
          throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("CREATE TABLE ");
    sbSql.append(qiTable.format());
    sbSql.append("\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn);
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      sbSql.append(tcd.getName());
      sbSql.append(" ");
      sbSql.append(tcd.getType());
    }
    if (listPrimary != null)
    {
      sbSql.append(",\r\n  CONSTRAINT PK");
      sbSql.append(qiTable.getName());
      sbSql.append(" PRIMARY KEY(");
      sbSql.append(SqlLiterals.formatIdentifierCommaList(listPrimary));
      sbSql.append(")");
    }
    if (listUnique != null)
    {
      sbSql.append(",\r\n  CONSTRAINT UK");
      sbSql.append(qiTable.getName());
      sbSql.append(" UNIQUE(");
      sbSql.append(SqlLiterals.formatIdentifierCommaList(listUnique));
      sbSql.append(")");
    }
    sbSql.append("\r\n)");
    executeCreate(sbSql.toString());
  } /* createTable */

  private void insertTables()
          throws SQLException
  {
    insertTable(getQualifiedSimpleTable(), _listCdSimple);
    insertTable(getQualifiedComplexTable(), _listCdComplex);
  } /* insertTables */

  private void insertTable(QualifiedId qiTable, List<TestColumnDefinition> listCd)
          throws SQLException
  {
    StringBuilder sbSql = new StringBuilder("INSERT INTO ");
    sbSql.append(qiTable.format());
    sbSql.append("\r\n(\r\n  ");
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn);
      if (tcd.getValue() != null)
      {
        if (iColumn > 0)
          sbSql.append(",\r\n  ");
        sbSql.append(tcd.getName());
      }
    }
    sbSql.append("\r\n)\r\nVALUES\r\n(\r\n  ");
    List<Object> listLobs = new ArrayList<Object>();
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      TestColumnDefinition tcd = listCd.get(iColumn);
      if (tcd.getValue() != null)
      {
        if (iColumn > 0)
          sbSql.append(",\r\n  ");
        String sLiteral = tcd.getValueLiteral();
        if (sLiteral.length() < 1000)
          sbSql.append(sLiteral);
        else
        {
          sbSql.append("?");
          listLobs.add(tcd.getValue());
        }
      }
    }
    sbSql.append("\r\n)");
    PreparedStatement pstmt = _conn.prepareStatement(sbSql.toString());
    for (int iLob = 0; iLob < listLobs.size(); iLob++)
    {
      Object o = listLobs.get(iLob);
      if (o instanceof String)
      {
        Reader rdrClob = new StringReader((String) o);
        pstmt.setCharacterStream(iLob + 1, rdrClob);

      }
      else if (o instanceof byte[])
      {
        InputStream isBlob = new ByteArrayInputStream((byte[]) o);
        pstmt.setBinaryStream(iLob + 1, isBlob);
      }
      else
        throw new SQLException("Invalid LOB type " + o.getClass().getName() + "!");
    }
    int iResult = pstmt.executeUpdate();
    assertSame("Insert failed!", 1, iResult);
    pstmt.close();
    _conn.commit();
  } /* insertTable */

} /* TestAltibaseDatabase */
