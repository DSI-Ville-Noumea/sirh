package nc.mairie.spring.dao.mapper.metier.parametrage;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.mairie.spring.dao.metier.parametrage.SPBASEDao;
import nc.mairie.spring.domain.metier.parametrage.SPBASE;

import org.springframework.jdbc.core.ResultSetExtractor;

public class SPBASEResultSetExtractor implements ResultSetExtractor<Object> {

	@Override
	public Object extractData(ResultSet rs) throws SQLException {
		SPBASE base = new SPBASE();
		base.setCdBase(rs.getString(SPBASEDao.CHAMP_CDBASE));
		base.setNbasHH(rs.getDouble(SPBASEDao.CHAMP_NBASHH));
		base.setLiBase(rs.getString(SPBASEDao.CHAMP_LIBASE));
		base.setNbhSa(rs.getDouble(SPBASEDao.CHAMP_NBAHSA));
		base.setNbhDi(rs.getDouble(SPBASEDao.CHAMP_NBAHDI));
		base.setNbhLu(rs.getDouble(SPBASEDao.CHAMP_NBAHLU));
		base.setNbhMa(rs.getDouble(SPBASEDao.CHAMP_NBAHMA));
		base.setNbhMe(rs.getDouble(SPBASEDao.CHAMP_NBAHME));
		base.setNbhJe(rs.getDouble(SPBASEDao.CHAMP_NBAHJE));
		base.setNbhVe(rs.getDouble(SPBASEDao.CHAMP_NBAHVE));
		base.setNbasCH(rs.getDouble(SPBASEDao.CHAMP_NBASCH));
		return base;
	}
}
