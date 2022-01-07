package ch.admin.bar.siard2.altibase;

/**
 * Contains all built-in Altibase Types
 * @author YounJung Park
 */
public enum AltibaseType
{
	BIT("bit"),
	BYTE("byte"),
	VARBIT("varbit"),
	VARBYTE("varbyte"),
	BINARY("BINARY"),
	SHORT("short"),
	SMALLINT("smallint"),
	BIGINT("bigint"),
	INT("int"),
	INTEGER("integer"),
	FLOAT("float"),
	REAL("real"),
	DOUBLE("double"),
	DOUBLEPRECISION("double precision"),
	DECIMAL("decimal"),
	NUMERIC("numeric"),
	DATE("date"),
	TIME("time"),
	DATETIME("datetime"),
	TIMESTAMP("timestamp"),
	TIMESTAMPLTZ("timestampltz"),
	TIMESTAMPTZ("timestamptz"),
	DATETIMELTZ("datetimeltz"),
	DATETIMETZ("datetimetz"),
	CHAR("char"),
	NCHAR("nchar"),
	NVARCHAR("nvarchar"),
	STRING("string"),
	VARCHAR("varchar"),
	BLOB("blob"),
	CLOB("clob"),
	ENUM("enum"),
	SET("set"),
	MULTISET("multiset"),
	LIST("list"),
	SEQUENCE("sequence"),
	OBJECT("object");

	private String sTypeName = null;

	/**
	 * constructor
	 * @param _sTypeName the system name of the type
	 */
	AltibaseType(String _sTypeName) {
		sTypeName = _sTypeName;
	} /* constructor */

	/**
	 * Gets the enum constant with a given type name
	 * @param _sTypeName the type name
	 * @return enum constant with the given type name
	 */
	public static AltibaseType getByTypeName(String _sTypeName)
	{
		AltibaseType result = null;
		for(int i = 0; i < AltibaseType.values().length; i++)
		{
			AltibaseType t = AltibaseType.values()[i];
			if(t.getTypeName().equalsIgnoreCase(_sTypeName))
			{
				result = t;
				break;
			}
		}
		return result;
	} /* getByTypeName */

	/**
	 * Gets the type name
	 * @return the type name
	 */
	public String getTypeName() {
		return sTypeName;
	}

} /* enum AltibaseType */
