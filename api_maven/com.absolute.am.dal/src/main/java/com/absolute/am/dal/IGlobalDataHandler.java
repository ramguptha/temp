package com.absolute.am.dal;

public interface IGlobalDataHandler {
	/**
	 * Return the schema_version of the entire database.
	 * @return global_data.schema_version
	 * @throws Exception
	 */
	public int getDatabaseSchemaVersion() throws Exception;

}
