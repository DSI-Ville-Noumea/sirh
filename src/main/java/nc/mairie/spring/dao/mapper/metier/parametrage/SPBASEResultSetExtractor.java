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
		base.setNbasHH(rs.getInt(SPBASEDao.CHAMP_NBASHH));
		base.setCdcbas(rs.getInt(SPBASEDao.CHAMP_CDCBAS));
		base.setLiBase(rs.getString(SPBASEDao.CHAMP_LIBASE));
		base.setNbhSa(rs.getInt(SPBASEDao.CHAMP_NBHSA));
		base.setNbhDi(rs.getInt(SPBASEDao.CHAMP_NBHDI));
		base.setNbhLu(rs.getInt(SPBASEDao.CHAMP_NBHLU));
		base.setNbhMa(rs.getInt(SPBASEDao.CHAMP_NBHMA));
		base.setNbhMe(rs.getInt(SPBASEDao.CHAMP_NBHME));
		base.setNbhJe(rs.getInt(SPBASEDao.CHAMP_NBHJE));
		base.setNbhVe(rs.getInt(SPBASEDao.CHAMP_NBHVE));
		base.setNbasCH(rs.getInt(SPBASEDao.CHAMP_NBASCH));
		return base;
	}
}
