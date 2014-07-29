package nc.mairie.spring.dao.utils;

import java.util.List;

public interface ISirhDao {

	<T> List<T> getListe(Class<T> T) throws Exception;

	void supprimerObject(Integer id) throws Exception;

	<T> T chercherObject(Class<T> T, Integer id) throws Exception;
}
