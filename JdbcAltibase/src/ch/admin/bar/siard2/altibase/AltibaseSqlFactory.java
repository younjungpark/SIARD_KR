package ch.admin.bar.siard2.altibase;

import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.datatype.*;
import ch.enterag.sqlparser.ddl.*;
import ch.enterag.sqlparser.expression.*;
import ch.admin.bar.siard2.altibase.datatype.*;
import ch.admin.bar.siard2.altibase.ddl.*;
import ch.admin.bar.siard2.altibase.expression.*;

/* =============================================================================== */
/**
 * AltibaseSqlFactory implements a wrapped Altibase SqlFactory
 * @author YounJung Park
 */
public class AltibaseSqlFactory extends BaseSqlFactory implements SqlFactory
{

	/* ------------------------------------------------------------------------ */
	/**
	 * Returns a new wrapped predefined type
	 */
	@Override
	public PredefinedType newPredefinedType() {
		return new AltibasePredefinedType(this);
	} /* newPredefinedType */

	/* ------------------------------------------------------------------------ */
	/**
	 * Returns a new wrapped literal
	 */
	@Override
	public Literal newLiteral() {
		return new AltibaseLiteral(this);
	} /* newLiteral */

	/* ------------------------------------------------------------------------ */
	/**
	 * Creates a new wrapped unsigned literal
	 */
	@Override
	public UnsignedLiteral newUnsignedLiteral() {
		return new AltibaseUnsignedLiteral(this);
	} /* newUnsignedLiteral */

	/* ------------------------------------------------------------------------ */
	/**
	 * Creates a new wrapped value expression primary
	 */
	@Override
	public ValueExpressionPrimary newValueExpressionPrimary() {
		return new AltibaseValueExpressionPrimary(this);
	} /* newValueExpressionPrimary */

	/* ------------------------------------------------------------------------ */
	/**
	 * Creates a new DROP SCHEMA statement 
	 */
	@Override
	public DropSchemaStatement newDropSchemaStatement() {
		return new AltibaseDropSchemaStatement(this);
	} /* newDropSchemaStatement */

	@Override
	public CreateSchemaStatement newCreateSchemaStatement()
	{
		return new AltibaseCreateSchemaStatement(this);
	}

	@Override
	public AttributeDefinition newAttributeDefinition()
	{
		return new AltibaseAttributeDefinition(this);
	} /* newAttributeDefinition */

	/* ------------------------------------------------------------------------ */
	/** Creates a new DROP TABLE statement */
	@Override
	public DropTableStatement newDropTableStatement()
	{
		return new AltibaseDropTableStatement(this);
	} /* newDropTableStatement */

	/* ------------------------------------------------------------------------ */
	/** Creates a new DROP VIEW statement */
	@Override
	public DropViewStatement newDropViewStatement()
	{
		return new AltibaseDropViewStatement(this);
	} /* newDropTableStatement */

} /* class AltibaseSqlFactory */