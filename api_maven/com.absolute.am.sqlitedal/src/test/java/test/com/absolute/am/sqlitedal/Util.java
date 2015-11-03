package test.com.absolute.am.sqlitedal;

import java.util.Properties;

import com.absolute.am.dal.DalBase;
import com.absolute.am.dal.IDal;
import com.absolute.am.sqlitedal.Dal;

public class Util {

	/**
	 * Returns an already initialized IDal instance. The runtime properties are initialized with defaults that will work 
	 * for this test suite. The caller owns the returned object when you are finished with it.
	 * @return
	 */
	public static IDal getDal() {
		
		Properties runtimeProperties = new Properties();
		String fileUrlLocation = Util.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String fileLocation = fileUrlLocation.substring(1, fileUrlLocation.length()-1);
		
		runtimeProperties.put(DalBase.PROP_DATABASES_FOLDER,fileLocation + "\\..\\..\\src\\test\\resources\\amdatabases");
		runtimeProperties.put(Dal.PROP_VIEW_CONFIG_FOLDER, fileLocation + "\\..\\..\\src\\test\\resources\\view_config");  
		runtimeProperties.put(DalBase.PROP_WEBAPI_DATABASE_FILE, "src\\test\\resources\\amdatabases\\webapi.db");
		runtimeProperties.put(DalBase.PROP_USER_PREFS_DATABASE_FILE, "src\\test\\resources\\amdatabases\\UserPrefs.db");
		runtimeProperties.put(DalBase.PROP_LOADABLE_EXTENSIONS_FILE, fileLocation + "\\..\\..\\src\\test\\resources\\ABTSQLiteExtension");
		runtimeProperties.put(DalBase.PROP_ADMIN_DB_CFG_NAME, fileUrlLocation + "\\..\\..\\src\\test\\resources\\AdminDatabase.cfg.xml");
		runtimeProperties.put(DalBase.PROP_ENUM_DB_CFG_NAME, fileUrlLocation + "\\..\\..\\src\\test\\resources\\EnumDatabase.cfg.xml");
		runtimeProperties.put(DalBase.PROP_WEBAPI_DATABASE_SUPPORTED_LOCALES, "en,ja");
		
		return getDal(runtimeProperties);
	}	
	
	/**
	 * Returns an IDal instance initialized with the given runtimeProperties. The caller owns the returned object
	 * 
	 * @param runtimeProperties
	 * @return
	 */
	public static IDal getDal(Properties runtimeProperties) {
		Dal dal = new Dal();
		dal.initialize(runtimeProperties, false);
		return dal;
	}
}
