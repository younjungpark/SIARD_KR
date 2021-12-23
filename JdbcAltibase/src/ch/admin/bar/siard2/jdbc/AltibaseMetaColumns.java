/*======================================================================
Implements the type translation between Altibase and ISO SQL
Version     : $Id: $
Application : SIARD2
Description : Implements the type translation between Altibase and ISO SQL
Platform    : Java 7
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 31.10.2016, Simon Jutz
======================================================================*/
package ch.admin.bar.siard2.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import ch.admin.bar.siard2.altibase.AltibaseType;
import ch.enterag.sqlparser.datatype.enums.PreType;

/* =============================================================================== */
/**
 * Implements the type translation between Altibase and ISO SQL
 * @author YounJung Park
 */
public class AltibaseMetaColumns extends AltibaseResultSet
{
	private static Map<AltibaseType,PreType> mapNAME_ALTIBASE_TO_ISO = new HashMap<AltibaseType,PreType>();

	static
	{
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.BIGINT, PreType.BIGINT);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.BIT, PreType.VARCHAR);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.BYTE, PreType.BINARY);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.VARBYTE, PreType.BINARY);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.BINARY, PreType.BINARY);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.BLOB, PreType.BLOB);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.CLOB, PreType.CLOB);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.CHAR, PreType.CHAR);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.DATE, PreType.DATE);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.DATETIME, PreType.TIMESTAMP);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.DECIMAL, PreType.DECIMAL);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.DOUBLE, PreType.DOUBLE);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.ENUM, PreType.VARCHAR);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.FLOAT, PreType.FLOAT);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.INT, PreType.INTEGER);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.INTEGER, PreType.INTEGER);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.SET, PreType.VARCHAR);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.SMALLINT, PreType.SMALLINT);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.TIME, PreType.TIME);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.TIMESTAMP, PreType.TIMESTAMP);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.NUMERIC, PreType.NUMERIC);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.REAL, PreType.REAL);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.STRING, PreType.VARCHAR);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.VARCHAR, PreType.VARCHAR);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.OBJECT, PreType.VARCHAR);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.NCHAR, PreType.NCHAR);
		mapNAME_ALTIBASE_TO_ISO.put(AltibaseType.NVARCHAR, PreType.NVARCHAR);
	}

	private static final int iMAX_VARCHAR_LENGTH = 32000;
	private int _iDataType = -1;
	private int _iTypeName = -1;
	private int _iPrecision = -1;
	private int _iLength = -1;
	private ResultSet _rsUnwrapped = null;

	/* ------------------------------------------------------------------------ */
	/**
	 * Constructor
	 * @param rsWrapped ResultSet to be wrapped
	 */
	public AltibaseMetaColumns(ResultSet rsWrapped, int iDataType, int iTypeName,
							int iPrecision, int iLength, AltibaseConnection conn) throws SQLException
	{
		super(rsWrapped, conn);
		_iDataType = iDataType;
		_iTypeName = iTypeName;
		_iPrecision = iPrecision;
		_iLength = iLength;
		_rsUnwrapped = unwrap(ResultSet.class);
	} /* constructor */

	/* ------------------------------------------------------------------------ */
	/** Implements the type translation between Altibase and ISO SQL
	 * @param sTypeName original type name.
	 * @return data type from java.sql.Types.
	 */
	static int getDataType(String sTypeName)
	{
		AltibaseType mst = AltibaseType.getByTypeName(sTypeName);
		PreType pt = mapNAME_ALTIBASE_TO_ISO.get(mst);
		return pt.getSqlType();
	} /* getDataType */

	/*------------------------------------------------------------------*/
	/** implements length translation.
	 * @param lColumnSize unwrapped length
	 * @param sTypeName original type name.
	 * @return translated length.
	 */
	static long getColumnSize(long lColumnSize, String sTypeName)
	{
		if (lColumnSize <= 0) // VARCHAR must always have a length > 0!
		{
			int iDataType = getDataType(sTypeName);
			if ((iDataType == Types.VARCHAR) ||
					(iDataType == Types.NVARCHAR) ||
					(iDataType == Types.VARBINARY))
				lColumnSize = iMAX_VARCHAR_LENGTH;
		}
		return lColumnSize;
	} /* getColumnSize */

	/* ------------------------------------------------------------------------ */
	@Override
	public int getInt(int columnIndex) throws SQLException
	{
		int iResult = -1;
		if(columnIndex == _iDataType)
			iResult = getDataType(_rsUnwrapped.getString(_iTypeName));
		else if ((columnIndex == _iPrecision) ||
				(columnIndex == _iLength))
		{
			int iLength = _rsUnwrapped.getInt(_iPrecision);
			if (iLength <= 0)
				iLength = _rsUnwrapped.getInt(_iLength);
			iResult = (int)getColumnSize(
					iLength,
					_rsUnwrapped.getString(_iTypeName));
		}
		else
			iResult = super.getInt(columnIndex);
		return iResult;
	} /* getInt */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc}
	 * Mapped java.sql.Types type is returned in DATA_TYPE.
	 * Original java.sql.Types type can be retrieved by using unwrap.
	 * Column size is adjusted to CHARS rather than BYTES.
	 */
	@Override
	public long getLong(int columnIndex) throws SQLException
	{
		long lResult = -1;
		if ((columnIndex == _iPrecision) ||
				(columnIndex == _iLength))
		{
			long lLength = _rsUnwrapped.getLong(_iPrecision);
			if (lLength <= 0)
				lLength = _rsUnwrapped.getLong(_iLength);
			lResult = getColumnSize(
					lLength,
					_rsUnwrapped.getString(_iTypeName));
		}
		else if (columnIndex == _iDataType)
			lResult = getDataType(_rsUnwrapped.getString(_iTypeName));
		else
			lResult = _rsUnwrapped.getLong(columnIndex);
		return lResult;
	} /* getLong */

	/* ------------------------------------------------------------------------ */
	@Override
	public Object getObject(int columnIndex) throws SQLException
	{
		Object oResult = _rsUnwrapped.getObject(columnIndex);


		if (oResult instanceof Integer)
			oResult = Integer.valueOf(getInt(columnIndex));
		else if (oResult instanceof Long)
			oResult = Long.valueOf(getLong(columnIndex));
		else if (oResult instanceof String)
			oResult = getString(columnIndex);

		if(columnIndex == _iLength)
			oResult = Long.valueOf(getLong(columnIndex));

		return oResult;
	} /* getObject */

	/*------------------------------------------------------------------*/
	/** {@inheritDoc} */
	@Override
	public boolean next() throws SQLException
	{
		return _rsUnwrapped.next();
	} /* next */

} /* class AltibaseMetaColumns */
