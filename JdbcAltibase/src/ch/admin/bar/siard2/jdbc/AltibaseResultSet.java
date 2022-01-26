/*======================================================================
AltibaseResultSet implements a wrapped Altibase ResultSet.
Version     : $Id: $
Application : SIARD2
Description : AltibaseResultSet implements a wrapped Altibase ResultSet.
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 31.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.sql.*;

import javax.xml.datatype.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.*;

import ch.enterag.utils.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;

/* =============================================================================== */
/**
 * AltibaseResultSet implements a wrapped Altibase ResultSet
 * @author YounJung Park
 */
public class AltibaseResultSet
		extends BaseResultSet
		implements ResultSet
{
	private AltibaseConnection _conn = null;
	private QualifiedId _qiTable = null;

	/* ------------------------------------------------------------------------ */
	/** Constructor
	 * @param rsWrapped
	 */
	public AltibaseResultSet(ResultSet rsWrapped, AltibaseConnection conn)
			throws SQLException
	{
		super(rsWrapped);
		_conn = conn;
	} /* constructor */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Gets the wrapped Statement
	 */
	@Override
	public Statement getStatement() throws SQLException
	{
		return new AltibaseStatement(super.getStatement(), _conn);
	} /* getStatement */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc}
	 * Gets the wrapped ResultSetMetaData
	 */
	public ResultSetMetaData getMetaData() throws SQLException
	{
		return new AltibaseResultSetMetaData(super.getMetaData());
	} /* getMetaData */

	/* ------------------------------------------------------------------------ */
	private Object mapObject(Object o, int iType) throws SQLException
	{
		if ((o instanceof Integer) && (iType == Types.SMALLINT))
		{
			Integer i = (Integer)o;
			Short sh = Short.valueOf(i.shortValue());
			o = sh;
		}
		else if ((o instanceof Integer) && (iType == Types.TINYINT))
		{
			Integer i = (Integer)o;
			Short sh = Short.valueOf(i.shortValue());
			o = sh;
		}
		return o;
	} /* mapObject */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc} */
	@Override
	public Object getObject(int columnIndex) throws SQLException
	{
		Object o = super.getObject(columnIndex);
		int iType = getMetaData().unwrap(ResultSetMetaData.class).getColumnType(columnIndex);
		String sColumnType = getMetaData().getColumnTypeName(columnIndex);
		int iDisplaySize = getMetaData().getColumnDisplaySize(columnIndex);
		if(sColumnType.equals("GEOMETRY") ||
				sColumnType.equals("POINT") ||
				sColumnType.equals("LINESTRING") ||
				sColumnType.equals("POLYGON") ||
				sColumnType.equals("MULTIPOINT") ||
				sColumnType.equals("MULTILINESTRING") ||
				sColumnType.equals("MULTIPOLYGON") ||
				sColumnType.equals("GEOMETRYCOLLECTION"))
		{
			try { o = getGeometryFromInputStream(new ByteArrayInputStream((byte[]) o)).toText(); }
			catch (Exception e) { throw new SQLException("Parsing of Geometry failed!",e); }
		}

		return o;
	} /* getObject */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException
	{
		Object o = null;
		T oMapped = null;
		int iType = Types.OTHER;
		o = super.getObject(columnIndex);
		String sColumnType = getMetaData().getColumnTypeName(columnIndex);
		if(sColumnType.equals("GEOMETRY") ||
				sColumnType.equals("POINT") ||
				sColumnType.equals("LINESTRING") ||
				sColumnType.equals("POLYGON") ||
				sColumnType.equals("MULTIPOINT") ||
				sColumnType.equals("MULTILINESTRING") ||
				sColumnType.equals("MULTIPOLYGON") ||
				sColumnType.equals("GEOMETRYCOLLECTION"))
		{ // geometry types
			try { o = getGeometryFromInputStream(new ByteArrayInputStream((byte[]) o)).toText(); }
			catch (Exception e) { throw new SQLException("Parsing of Geometry failed!",e); }
		}
		else if (sColumnType.equals("DATE"))
		{
			if (o instanceof Timestamp)
			{
				Date sDate = new Date(((Timestamp)o).getTime());
				o = (T)sDate;
			}
		}
		if (type.isInstance(o))
			oMapped = (T) o;
		else
			oMapped = mapObject(o, iType, type);
		return oMapped;
	} /* getObject */

	/* ------------------------------------------------------------------------ */
	private <T> T mapObject(Object o, int iType, Class<T> type) throws SQLException
	{
		T oMapped = null;
		oMapped = type.cast(mapObject(o, iType));
		return oMapped;
	} /* mapObject */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc} */
	@Override
	public String getString(int columnIndex) throws SQLException
	{
		String result = null;
		String sColumnType = getMetaData().getColumnTypeName(columnIndex);
		if(sColumnType.equals("GEOMETRY") ||
				sColumnType.equals("POINT") ||
				sColumnType.equals("LINESTRING") ||
				sColumnType.equals("POLYGON") ||
				sColumnType.equals("MULTIPOINT") ||
				sColumnType.equals("MULTILINESTRING") ||
				sColumnType.equals("MULTIPOLYGON") ||
				sColumnType.equals("GEOMETRYCOLLECTION"))
		{ // geometry types
			try { result = getGeometryFromInputStream(new ByteArrayInputStream(super.getBytes(columnIndex))).toText(); }
			catch (Exception e) { throw new SQLException("Parsing of Geometry failed!",e); }
		}
		else
			result = super.getString(columnIndex);
		return result;
	} /* getString */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc} */
	@Override
	public String getNString(int columnIndex) throws SQLException
	{
		return getString(columnIndex);
	} /* getNString */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc} */
	@Override
	public Duration getDuration(int columnIndex)
			throws SQLException, SQLFeatureNotSupportedException
	{
		byte[] buf = getBytes(columnIndex);
		Interval iv = SqlLiterals.deserialize(buf, Interval.class);
		return iv.toDuration();
	} /* getDuration */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc} */
	@Override
	public void insertRow() throws SQLException
	{
		super.insertRow();
	} /* insertRow */

	/* ------------------------------------------------------------------------ */
	/** {@inheritDoc} */
	@Override
	public void updateRow() throws SQLException
	{
		super.updateRow();
	} /* updateRow */

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xml) throws SQLException
	{
		String sXML = xml.getString();
		xml.free();
		/*
		 * Check if XML is valid
		 */
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(sXML.getBytes(SU.sUTF8_CHARSET_NAME))));
		}
		catch (ParserConfigurationException e)
		{
			throw new SQLException(e);
		}
		catch (SAXException e)
		{
			throw new SQLException(e);
		}
		catch (IOException e)
		{
			throw new SQLException(e);
		}
		if(doc != null)
		{
			updateString(columnIndex, sXML);
		}
	} /* updateSQLXML */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void updateObject(int columnIndex, Object x)
			throws SQLException
	{
		if (x instanceof SQLXML)
			updateSQLXML(columnIndex, (SQLXML)x);
		else
			super.updateObject(columnIndex,x);
	} /* updateObject */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void updateDuration(int columnIndex, Duration x)
			throws SQLException
	{
		Interval iv = Interval.fromDuration(x);
		byte[] buf = SqlLiterals.serialize(iv);
		updateBytes(columnIndex,buf);
	} /* updateDuration */

	/* ------------------------------------------------------------------------ */
	/** Gets a Object from an InputStream
	 * @param inputStream an input stream
	 * @return a Geometry Object
	 * @throws Exception if the bytes in the input stream could not be parsed
	 */
	private Geometry getGeometryFromInputStream(InputStream inputStream) throws Exception
	{
		Geometry geometry = null;
		if (inputStream != null)
		{
			byte[] buffer = new byte[255];
			int bytesRead = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((bytesRead = inputStream.read(buffer)) != -1)
			{
				baos.write(buffer, 0, bytesRead);
			}
			byte[] geometryAsBytes = baos.toByteArray();

			// first four bytes of the geometry are the SRID
			byte[] sridBytes = new byte[4];
			System.arraycopy(geometryAsBytes, 0, sridBytes, 0, 4);
			boolean bigEndian = (geometryAsBytes[4] == 0x00);

			int srid = 0;
			if (bigEndian)
			{
				for (int i = 0; i < sridBytes.length; i++)
				{
					srid = (srid << 8) + (sridBytes[i] & 0xff);
				}
			}
			else
			{
				for (int i = 0; i < sridBytes.length; i++)
				{
					srid += (sridBytes[i] & 0xff) << (8 * i);
				}
			}

			WKBReader wkbReader = new WKBReader();
			byte[] wkb = new byte[geometryAsBytes.length - 4];
			System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
			geometry = wkbReader.read(wkb);
			geometry.setSRID(srid);
		}

		return geometry;
	} /* getGeometryFromInputStream */

	/* ------------------------------------------------------------------------ */
	/** Reads an inputStream and returns a byte array
	 * @param inputStream the input stream to read
	 * @return a byte array consisting of the same bytes as the input stream
	 */
	private byte[] readByteArray(InputStream inputStream)
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[0xFFFF];
		try
		{
			for(int len; (len = inputStream.read(buf)) != -1; )
			{
				os.write(buf, 0, len);
			}
			os.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return os.toByteArray();
	} /* getByteArrayFromInputStream */

	/* ------------------------------------------------------------------------ */
	/** Reads a string from a reader
	 * @param reader
	 * @return the string read
	 */
	private String readString(Reader reader)
	{
		StringBuilder builder = new StringBuilder();
		try
		{
			int c = -1;
			char[] chars = new char[0xFFFF];
			do
			{
				c = reader.read(chars, 0, chars.length);
				if (c>0)
				{
					builder.append(chars, 0, c);
				}
			}
			while(c > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	} /* getStringFromReader */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public String getCursorName() throws SQLException
	{
		String s = null;
		try { s = super.getCursorName(); }
		catch(SQLFeatureNotSupportedException sfnse) { throw sfnse; }
		catch(SQLException se) { throw new SQLFeatureNotSupportedException("getCursorName not supported!",se); }
		return s;
	} /* getCursorName */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException
	{
		try { super.updateAsciiStream(columnIndex, x); }
		catch(SQLFeatureNotSupportedException sfnse) { throw sfnse; }
		catch(SQLException se) { throw new SQLFeatureNotSupportedException("updateAsciiStream not supported!",se); }
	} /* updateAsciiStream */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void updateAsciiStream(int columnIndex, InputStream x,
								  int length) throws SQLException
	{
		try { super.updateAsciiStream(columnIndex, x, length); }
		catch(SQLFeatureNotSupportedException sfnse) { throw sfnse; }
		catch(SQLException se) { throw new SQLFeatureNotSupportedException("updateAsciiStream not supported!",se); }
	} /* updateAsciiStream */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public void updateAsciiStream(int columnIndex, InputStream x,
								  long length) throws SQLException
	{
		try { super.updateAsciiStream(columnIndex, x, length); }
		catch(SQLFeatureNotSupportedException sfnse) { throw sfnse; }
		catch(SQLException se) { throw new SQLFeatureNotSupportedException("updateAsciiStream not supported!",se); }
	} /* updateAsciiStream */

} /* class AltibaseResultSet */
