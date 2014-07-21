package org.emud.walkthrough.database;

import java.util.GregorianCalendar;
import java.util.List;

import org.emud.content.observer.Subject;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.model.Result;

/**
 * @author alberto
 *
 */
public interface ActivitiesDataSource {
	
	/**
	 * Devuelve el Subject asociado con los datos de las actividades y sus resultados. Se usará este Subject para notificar de
	 * inserciones, borrados y/ actualizaciones sobre la fuente de datos.
	 * @return Objecto Subject asociado a la fuente de datos
	 */
	public Subject getActivitiesSubject();

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<WalkActivity> getActivities(GregorianCalendar startDate, GregorianCalendar endDate);


	/**
	 * Crea una nueva actividad en la fuente de datos.
	 * @param act Actividad a crear.
	 * @return Identificador unico de la actividad en la fuente de datos.
	 */
	public long createNewActivity(WalkActivity act);
	
	/**
	 * Devuelve una lista con los resultados asociados a una actividad.
	 * @param activity_id Identifiacdor unico de la actividad en la fuente de datos.
	 * @return Lista de resultados asociados a la actividad.
	 * @deprecated
	 */
	public List<Result> getActivityResults(long activity_id);
	
	/**
	 * Devuelve una actividad junto con sus resultados asociados
	 * @param activity_id Identifiacdor unico de la actividad en la fuente de datos.
	 * @return Actividad identificada o null si no existe actividad con ese identificador.
	 */
	public WalkActivity getActivity(long activity_id);
	
	/**
	 * Actualiza los datos de una actividad
	 * @param activity_id Identificador unico de la actividad en la fuente de datos.
	 * @param activity Nueva actividad.
	 */
	public void updateActivity(long activity_id, WalkActivity activity);
	
	/**
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Result> getResults(int type, GregorianCalendar startDate, GregorianCalendar endDate);
	
	/**
	 * @param type
	 * @return
	 */
	public List<Result> getResults(int type);
}
