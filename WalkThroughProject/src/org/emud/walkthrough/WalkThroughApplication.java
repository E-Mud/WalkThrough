package org.emud.walkthrough;

import java.util.HashSet;
import java.util.Set;

import org.emud.walkthrough.analysisservice.AnalysisService;
import org.emud.walkthrough.database.ActivitiesDataSource;
import org.emud.walkthrough.database.DataSource;
import org.emud.walkthrough.database.UserDataSource;
import org.emud.walkthrough.stub.StubWebClient;
import org.emud.walkthrough.webclient.WebClient;

import android.app.Application;
import android.content.SharedPreferences;

public class WalkThroughApplication extends Application {
	private WebClient defaultWebClient;
	private DataSource dataSource;
	private String activeUser;
	private static final String APP_PREFERENCES = "WalkThroughPreferences",
			USER_PREFERENCES_SUFIX = "Preferences";
	
	/** (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate(){
		super.onCreate();
		activeUser = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
				.getString("activeUserName", null);
	}
	
	/**
	 * Especifica el usuario que esta usando la aplicación como usuario activo.
	 * 
	 * @param user Nombre de usuario.
	 * @param password Contraseña.
	 * @return True si el usuario se encuentra entre los usuarios registrados en la aplicación y ha pasado a ser usuario activo. False en caso contrario.
	 */
	public boolean setActiveUser(String user, String password){
		Set<String> registeredUsers = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
				.getStringSet("registeredUsers", null);
		if(registeredUsers != null && registeredUsers.contains(user)){
			activeUser = user;
			SharedPreferences.Editor editor = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE).edit();
			editor.putString("activeUserName", activeUser);
			editor.putString("activeUserPassword", password);
			editor.commit();
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Retira al usuario activo. Si no habia ningun usuario activo este método no hace nada.
	 */
	public void unsetActiveUser(){
		if(activeUser != null){
			activeUser = null;
			SharedPreferences.Editor editor = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE).edit();
			editor.remove("activeUserName");
			editor.remove("activeUserPassword");
			editor.commit();
			closeDataSource();
		}
	}

	/**
	 * Devuelve el nombre de usuario del usuario activo.
	 * @return Nombre de usuario del usuario activo o null si no hay usuario activo.
	 */
	public String getActiveUserName(){
		return activeUser;
	}
	
	/**
	 * Devuelve la contraseña del usuario activo.
	 * @return Contraseña del usuario activo o null si no hay usuario activo.
	 */
	public String getActiveUserPassword(){
		return getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
		.getString("activeUserPassword", null);
	}
	
	/**
	 * Devuelve la fuente de datos de usuario del usuario activo.
	 * @return Fuente de datos del usuario activo o null si no hay usuario activo.
	 */
	public UserDataSource getUserDataSource(){
		return getDataSource();
		
	}
	
	/**
	 * Devuelve la fuente de datos de actividades del usuario activo.
	 * @return Fuente de datos de actividades del usuario activo o null si no hay usuario activo.
	 */
	public ActivitiesDataSource getActivitiesDataSource(){
		return getDataSource();
	}
	
	/**
	 * Devuelve la fuente de datos del usuario activo
	 * @return Fuente de datos del usuario activo o null si no hay usuario activo.
	 */
	private DataSource getDataSource(){
		if(activeUser != null){
			if(dataSource == null)
				dataSource = new DataSource(this, activeUser);
			return dataSource;
		}else{
			return null;
		}
	}
	
	/**
	 * Comprueba si cierto usuario aparece como usuario registrado.
	 * @param username Nombre de usuario
	 * @return True si es un usuario registrado. False en caso contrario.
	 */
	public boolean containsRegisteredUser(String username) {
		SharedPreferences registeredPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
		Set<String> registeredUsers = registeredPref.getStringSet("registeredUsers", null);
		
		if(registeredUsers != null){
			return registeredUsers.contains(username);
		}else{
			return false;
		}
	}
	
	/**
	 * Añade un usuario como usuario registrado en la aplicación.
	 * @param user Nombre de usuario.
	 * @return True si ha sido añadido satisfactoriamente. False en caso contrario.
	 */
	public boolean addUser(String user){
		SharedPreferences registeredPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
		Set<String> registeredUsers = registeredPref.getStringSet("registeredUsers", null);
		boolean added;
		
		if(registeredUsers == null){
			registeredUsers = new HashSet<String>();
		}

		added = registeredUsers.add(user);
		
		if(added){
			SharedPreferences.Editor editor = registeredPref.edit();
			editor.putStringSet("registeredUsers", registeredUsers);
			editor.commit();			
		}
		
		return added;
	}
	
	/**
	 * Elimina a un usuario como usuario registrado. Esto conlleva también la eliminación de sus fuentes de datos.
	 * Si user era el usuario activo se retirará. 
	 * @param user Nombre de usuario.
	 */
	public void removeUser(String user){
		SharedPreferences registeredPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
		Set<String> registeredUsers = registeredPref.getStringSet("registeredUsers", null);
		if(registeredUsers != null){
			boolean removed = registeredUsers.remove(user);
			if(removed){
				SharedPreferences.Editor editor = registeredPref.edit();
				editor.putStringSet("registeredUsers", registeredUsers);
				editor.commit();
				removeUserDatabase(user);
				if(activeUser != null && activeUser.equals(user))
					unsetActiveUser();
			}
		}
	}	
	
	/**
	 * Elimina la base de datos y las fuentes de datos de un usuario.
	 * @param user Nombre de usuario
	 */
	private void removeUserDatabase(String user) {
		closeDataSource();
		String databaseName = DataSource.buildDatabaseName(user);
		super.deleteDatabase(databaseName);
	}
	
	/**
	 * Cierra la aplicación. Se recomienda llamar a esta función antes de cerrar la aplicación para que se cierre la conexión de las fuentes de datos.
	 * 
	 */
	public void close() {
		closeDataSource();
	}
	
	/**
	 * Cierra la conexión de la fuente de datos activa.
	 */
	private void closeDataSource() {
		if(dataSource != null){
			dataSource.closeDatabase();
			dataSource = null;
		}
	}

	/**
	 * Devuelve el cliente web asociado a la aplicación.
	 * @return Cliente web.
	 */
	public WebClient getDefaultWebClient(){
		return defaultWebClient == null ? defaultWebClient = new StubWebClient() : defaultWebClient;
	}
	
	/**
	 * Devuelve el contacto de emergencia del usuario activo.
	 * @return Identificador único del contacto o -1 en caso de no encontrarse o no haber usuario activo.
	 */
	public long getEmergencyContact(){
		if(activeUser != null){
			SharedPreferences userPrefs = getSharedPreferences(activeUser + USER_PREFERENCES_SUFIX, MODE_PRIVATE);
			
			return userPrefs.getLong("emergencyContactID", -1);
		}else{
			return -1;
		}
	}
	
	/**
	 * Especifica el contacto de emergencia del usuario activo.
	 * @param contactID Identificador único del usuario activo.
	 */
	public void setEmergencyContact(long contactID) {
		if(activeUser != null){
			SharedPreferences userPrefs = getSharedPreferences(activeUser + USER_PREFERENCES_SUFIX, MODE_PRIVATE);
			SharedPreferences.Editor editor = userPrefs.edit();
			
			editor.putLong("emergencyContactID", contactID);
			editor.commit();			
		}
	}
	
	/**
	 * Devuelve el estado del servicio de análisis del usuario activo.
	 * @return Estado del servicio de análisis. Si no hay usuario activo se devolverá SERVICE_NONE.
	 */
	public int getServiceState(){
		if(activeUser != null){
			SharedPreferences userPrefs = getSharedPreferences(activeUser + USER_PREFERENCES_SUFIX, MODE_PRIVATE);
			return userPrefs.getInt("serviceState", AnalysisService.SERVICE_NONE);		
		}else{
			return AnalysisService.SERVICE_NONE;
		}
	}
	
	/**
	 * Especifica el estado del servicio de análisis del usuario activo.
	 * @param state Estado del servicio de análisis.
	 */
	public void setServiceState(int state){
		if(activeUser != null){
			SharedPreferences userPrefs = getSharedPreferences(activeUser + USER_PREFERENCES_SUFIX, MODE_PRIVATE);
			SharedPreferences.Editor editor = userPrefs.edit();
			
			editor.putInt("serviceState", state);
			editor.commit();			
		}
	}
	

	public void setScreenPref(boolean screen) {
		if(activeUser != null){
			SharedPreferences userPrefs = getSharedPreferences(activeUser + USER_PREFERENCES_SUFIX, MODE_PRIVATE);
			SharedPreferences.Editor editor = userPrefs.edit();
			
			editor.putBoolean("screenPref", screen);
			editor.commit();			
		}
	}
	
	public boolean getScreenPref() {
		if(activeUser != null){
			SharedPreferences userPrefs = getSharedPreferences(activeUser + USER_PREFERENCES_SUFIX, MODE_PRIVATE);
			return userPrefs.getBoolean("screenPref", false);		
		}else{
			return false;
		}
	}
}
