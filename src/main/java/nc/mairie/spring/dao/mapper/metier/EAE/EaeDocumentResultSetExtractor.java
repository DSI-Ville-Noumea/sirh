package nc.mairie.spring.dao.mapper.metier.EAE;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.EAE.EaeDocumentDao;
import nc.mairie.spring.domain.metier.EAE.EaeDocument;

import org.springframework.jdbc.core.ResultSetExtractor;

public class EaeDocumentResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		EaeDocument docu = new EaeDocument();
		docu.setIdEaeDocument(rs.getInt(EaeDocumentDao.CHAMP_ID_EAE_DOCUMENT));
		docu.setIdCampagneEae(rs.getInt(EaeDocumentDao.CHAMP_ID_CAMPAGNE_EAE));
		docu.setIdDocument(rs.getInt(EaeDocumentDao.CHAMP_ID_DOCUMENT));
		docu.setTypeDocument(rs.getString(EaeDocumentDao.CHAMP_TYPE_DOCUMENT));

		return docu;
	}
}
